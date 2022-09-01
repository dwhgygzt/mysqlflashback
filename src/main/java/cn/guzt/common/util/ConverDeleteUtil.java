package cn.guzt.common.util;

import cn.guzt.common.entity.ColumnEntity;
import cn.guzt.common.exception.BusinessException;
import cn.guzt.constant.SysConstant;

import java.util.Map;

/**
 * 删除闪回sql转换
 *
 * @author guzt
 */
@SuppressWarnings("unused")
public class ConverDeleteUtil {

    public static String converFlushBackSql(
            String oldSqlStr, Map<String, ColumnEntity> columnEntityMap, Long maxColOrder) {
        String oldSqlStrTrim = oldSqlStr.trim();
        if (oldSqlStrTrim.startsWith(SysConstant.DELETE_FOMR)) {
            return oldSqlStr.replaceFirst(SysConstant.DELETE_FOMR, SysConstant.INSERT_INTO);
        } else if (SysConstant.KEY_WHERE.equals(oldSqlStrTrim)) {
            return SysConstant.VALUES;
        } else if (oldSqlStrTrim.startsWith(SysConstant.KEY_COL)) {
            int index = oldSqlStrTrim.indexOf(SysConstant.KEY_SPIT);
            String colKey = oldSqlStrTrim.substring(0, index);
            String colValue = oldSqlStrTrim.substring(index).replaceFirst(SysConstant.KEY_SPIT, "");
            ColumnEntity entity = columnEntityMap.get(colKey);
            if (entity == null) {
                BusinessException.createByErrorMsg("转储出的原始SQL中 " + colKey + " 无法匹配到字段名称");
            }
            if (SysConstant.FIRST_KEY_COL.equals(colKey)) {
                // 第一个字段
                return " ( ".concat(colValue) + ", ";
            } else if (SysConstant.KEY_COL.concat(maxColOrder.toString()).equals(colKey)) {
                // 最后一个字段
                return "   " + colValue + ");";
            } else {
                return "   " + colValue + ", ";
            }
        } else {
            return oldSqlStr;
        }
    }
}
