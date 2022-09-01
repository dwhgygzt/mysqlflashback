package cn.guzt.constant;

/**
 * 项目中的常量
 *
 * @author guzt
 */
public interface SysConstant {

    String KEY_EQ_NULL = "=NULL";
    String KEY_IS_NULL = " IS NULL";

    String KEY_WHERE = "WHERE";

    String VALUES = "VALUES";

    String KEY_SET = "SET";

    String KEY_COL = "@";

    String FIRST_KEY_COL = "@1";

    String KEY_SPIT = "=";

    String COMMA = ",";

    String DELETE_FOMR = "DELETE FROM";

    String INSERT_INTO = "INSERT INTO";

    /**
     * 配置文件的名称
     */
    String FLUSH_BACK_CONFIG_FILE = "FLUSH_BACK_CONFIG.json";

    /**
     * 查询表字段SQL
     */
    String SELECT_COLUMNS_SQL = " SELECT a.ORDINAL_POSITION, a.COLUMN_NAME, a.COLUMN_KEY,a.COLUMN_TYPE, " +
            "a.TABLE_SCHEMA,a.TABLE_NAME,a.IS_NULLABLE " +
            " FROM information_schema.`COLUMNS` a " +
            "WHERE a.TABLE_SCHEMA = ? " +
            "  AND a.TABLE_NAME = ? " +
            "ORDER BY a.ORDINAL_POSITION ";

    /**
     * jdbc URL 模板
     */
    String JDBC_URL = "jdbc:mysql://%s:%s/%s?" +
            "useUnicode=true&characterEncoding=utf-8" +
            "&serverTimezone=Asia/Shanghai&useSSL=false" +
            "&nullCatalogMeansCurrent=true&rewriteBatchedStatements=true";

    /**
     * 默认的工作目标文件夹
     */
    String DEFAULT_TARGET_PATH = System.getProperty("user.dir");


    /**
     * 系统参数 目标工作目录
     */
    String PROKEY_TARGET_PATH = "targetpath";

    /**
     * 系统参数 当前目标文件前缀ID
     */
    String PROKEY_TARGET_UUID = "targetuuid";

}
