package cn.guzt.common.util;

import cn.guzt.common.dto.FlushBackConfig;
import cn.guzt.common.entity.ColumnEntity;
import cn.guzt.common.exception.BusinessException;
import cn.guzt.constant.SysConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查询表字段定义相关信息
 *
 * @author guzt
 */
@SuppressWarnings("unused")
public class JdbcUtil {

    static Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    static {
        // 只加载一次驱动
        try {
            // 使用 getInstance() 方法来解决不兼容的 JVM
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            logger.info("Mysql8 驱动加载成功...");
        } catch (Exception e) {
            logger.error("Mysql8 驱动加载失败", e);
        }
    }

    /**
     * 获取 jdbc 链接
     *
     * @return 当前对象的 Connection 对象
     */
    protected static Connection getConnection(FlushBackConfig config) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(String.format(SysConstant.JDBC_URL,
                    config.getTargetHost(), config.getTargetPort(), config.getTargetDbName()),
                    config.getTargetUser(), config.getTargetPassword());
        } catch (Exception e) {
            logger.error("获取JDBC链接异常", e);
            BusinessException.createByErrorMsg("获取JDBC链接异常");
        }
        return connection;
    }


    /**
     * 对 sql 语句进行赋值处理
     *
     * @param conn      链接对接
     * @param sql       要执行的 sql
     * @param paramList 要填充的字符按
     */
    protected static PreparedStatement getExecutepts(Connection conn, String sql, List<?> paramList) {
        PreparedStatement pts = null;
        try {
            pts = conn.prepareStatement(sql);
            if (paramList != null && paramList.size() > 0) {
                for (int item = 0; item < paramList.size(); item++) {
                    pts.setObject(item + 1, paramList.get(item));
                }
            }
        } catch (Exception e) {
            logger.error("获取PreparedStatement异常", e);
            BusinessException.createByErrorMsg("获取PreparedStatement异常");
        }
        return pts;
    }

    /**
     * 增删改使用
     *
     * @param config    链接对接
     * @param sql       sql语句
     * @param paramList 填充字段
     * @return 是否成功
     */
    public static Boolean executeSql(FlushBackConfig config, String sql, List<?> paramList) {
        int row = 0;
        Connection conn = getConnection(config);
        PreparedStatement pst = getExecutepts(conn, sql, paramList);
        try {
            row = pst.executeUpdate();
            return row > 0;
        } catch (SQLException e) {
            logger.error("executeSql 异常", e);
            BusinessException.createByErrorMsg("executeSql 异常");
            return row > 0;
        } finally {
            close(null, pst, conn);
        }
    }


    /**
     * 条件查询使用
     *
     * @param config    链接对接
     * @param sql       sql语句
     * @param paramList 填充的字段
     * @param tClass    返回类型
     * @return 查询结果
     */
    public static <T> List<T> query(FlushBackConfig config, String sql, Class<T> tClass, List<?> paramList) {
        Connection conn = getConnection(config);
        PreparedStatement pst = getExecutepts(conn, sql, paramList);
        ResultSet rst = null;
        try {
            rst = pst.executeQuery();
            return getList(rst, tClass);
        } catch (SQLException e) {
            logger.error("条件查询SQL 异常", e);
            BusinessException.createByErrorMsg("条件查询SQL 异常");
            return null;
        } finally {
            close(rst, pst, conn);
        }
    }

    /**
     * 无条件时使用
     *
     * @param config 链接对接
     * @param sql    sql语句
     * @param tClass 返回类型
     * @return 查询结果
     */
    public static <T> List<T> query(FlushBackConfig config, String sql, Class<T> tClass) {
        return query(config, sql, tClass, null);
    }


    /**
     * 将 resultSet 中的数据取出并存储与 List 中返回
     *
     * @param rst    查询结果集
     * @param tClass 返回类型的 class
     * @param <T>    泛型
     * @return List<object>
     */
    protected static <T> List<T> getList(ResultSet rst, Class<T> tClass) {
        List<T> resultList = new ArrayList<>(128);
        // 获取所有定义的属性
        Field[] fields = tClass.getDeclaredFields();
        if (fields.length == 0) {
            BusinessException.createByErrorMsg("类 {} 里面没有字段", tClass.getName());
        }
        try {
            while (rst.next()) {
                List<Object> list = new ArrayList<>();
                // 从ResultSet中读取出值
                for (Field field : fields) {
                    list.add(rst.getObject(field.getAnnotation(Column.class).name()));
                }
                // 将值set进 Entity对象中
                T t = tClass.getConstructor().newInstance();
                for (int index = 0; index < fields.length; index++) {
                    PropertyDescriptor pd = new PropertyDescriptor(fields[index].getName(), tClass);
                    Method writeMethod = pd.getWriteMethod();
                    writeMethod.invoke(t, list.get(index));
                }

                resultList.add(t);
            }
        } catch (Exception e) {
            logger.error("将 resultSet 中的数据取出并存储与 List 中返回 异常", e);
            BusinessException.createByErrorMsg("将 resultSet 中的数据取出并存储与 List 中返回 异常");
        }
        return resultList;
    }

    /**
     * 关闭流, 秉持着先开后关原则
     *
     * @param rst  ignore
     * @param pts  ignore
     * @param conn ignore
     */
    protected static void close(ResultSet rst, PreparedStatement pts, Connection conn) {
        try {
            if (rst != null) {
                rst.close();
            }
            if (pts != null) {
                pts.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.error("关闭Mysql连接 异常", e);
            BusinessException.createByErrorMsg("关闭Mysql连接 异常");
        }
    }

    public static List<ColumnEntity> listColumns(FlushBackConfig flushBackConfig) {
        String remote = "remote";
        if (remote.equals(flushBackConfig.getDumpType())) {
            return JdbcUtil.query(flushBackConfig,
                    SysConstant.SELECT_COLUMNS_SQL,
                    ColumnEntity.class,
                    Arrays.asList(
                            flushBackConfig.getTargetDbName(),
                            flushBackConfig.getTargetTableName()));
        } else {
            String targetTableColumns = flushBackConfig.getTargetTableColumns();
            String spitStr = ",";
            if (targetTableColumns == null || targetTableColumns.trim().length() == 0) {
                BusinessException.createByErrorMsg("当配置参数dumpType为local时请传递参数targetTableColumns");
            }
            List<ColumnEntity> columnEntityList = new ArrayList<>(128);
            Long cntOrder = 1L;
            for (String column : targetTableColumns.split(spitStr)) {
                ColumnEntity entity = new ColumnEntity();
                entity.setOrdinalPosition(cntOrder);
                entity.setColumnName(column);
                entity.setTableName(flushBackConfig.getTargetTableName());
                entity.setTableSchema(flushBackConfig.getTargetDbName());
                columnEntityList.add(entity);
                cntOrder++;
            }
            return columnEntityList;
        }

    }

    /**
     * 返回顺序为key的 字段名称map
     *
     * @param flushBackConfig 配置
     * @return Map
     */
    public static Map<String, ColumnEntity> listColumnsToMap(FlushBackConfig flushBackConfig) {
        List<ColumnEntity> columnEntityList = listColumns(flushBackConfig);
        return columnEntityList.stream().collect(
                Collectors.toMap(item -> "@" + item.getOrdinalPosition(), a -> a, (k1, k2) -> k1));
    }
}
