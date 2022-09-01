package cn.guzt.common.util;

import cn.guzt.common.entity.ColumnEntity;
import cn.guzt.common.exception.BusinessException;
import cn.guzt.constant.SysConstant;

import java.util.Map;

/**
 * 新增闪回sql转换
 *
 * @author guzt
 */
@SuppressWarnings("unused")
public class ConverInsertUtil {


    public static String converFlushBackSql(
            String oldSqlStr, Map<String, ColumnEntity> columnEntityMap, Long maxColOrder) {
        String oldSqlStrTrim = oldSqlStr.trim();
        if (oldSqlStrTrim.startsWith(SysConstant.INSERT_INTO)) {
            return oldSqlStr.replaceFirst(SysConstant.INSERT_INTO, SysConstant.DELETE_FOMR);
        } else if (SysConstant.KEY_SET.equals(oldSqlStrTrim)) {
            return SysConstant.KEY_WHERE;
        } else if (oldSqlStrTrim.startsWith(SysConstant.KEY_COL)) {
            int index = oldSqlStrTrim.indexOf(SysConstant.KEY_SPIT);
            String colKey = oldSqlStrTrim.substring(0, index);
            String colValue = oldSqlStrTrim.substring(index);
            ColumnEntity entity = columnEntityMap.get(colKey);
            if (entity == null) {
                BusinessException.createByErrorMsg("转储出的原始SQL中 " + colKey + " 无法匹配到字段名称");
            }
            // 闪回是对应 WHERE 部分的SQL
            if (SysConstant.KEY_EQ_NULL.equals(colValue)) {
                colValue = SysConstant.KEY_IS_NULL;
            }

            if (SysConstant.FIRST_KEY_COL.equals(colKey)) {
                // 第一个字段
                return "   " + entity.getColumnName() + colValue;
            } else if (SysConstant.KEY_COL.concat(maxColOrder.toString()).equals(colKey)) {
                // 最后一个字段
                return "   AND " + entity.getColumnName() + colValue + ";";
            } else {
                return "   AND " + entity.getColumnName() + colValue;
            }
        } else {
            return oldSqlStr;
        }
    }
}
