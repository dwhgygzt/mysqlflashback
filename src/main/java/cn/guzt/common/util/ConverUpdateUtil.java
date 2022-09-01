package cn.guzt.common.util;

import cn.guzt.common.entity.ColumnEntity;
import cn.guzt.common.exception.BusinessException;
import cn.guzt.constant.SysConstant;

import java.util.Map;

/**
 * 更新闪回sql转换
 *
 * @author guzt
 */
@SuppressWarnings("unused")
public class ConverUpdateUtil {


    public static String converFlushBackSql(
            String beginPart, String oldSqlStr, Map<String, ColumnEntity> columnEntityMap, Long maxColOrder) {
        String oldSqlStrTrim = oldSqlStr.trim();
        if (oldSqlStrTrim.startsWith(SysConstant.KEY_WHERE)) {
            return SysConstant.KEY_SET;
        } else if (oldSqlStrTrim.startsWith(SysConstant.KEY_SET)) {
            return SysConstant.KEY_WHERE;
        } else if (oldSqlStrTrim.startsWith(SysConstant.KEY_COL)) {
            int index = oldSqlStrTrim.indexOf(SysConstant.KEY_SPIT);
            String colKey = oldSqlStrTrim.substring(0, index);
            String colValue = oldSqlStrTrim.substring(index);
            ColumnEntity entity = columnEntityMap.get(colKey);
            if (entity == null) {
                BusinessException.createByErrorMsg("转储出的原始SQL中 " + colKey + " 无法匹配到字段名称");
            }
            if (SysConstant.KEY_SET.equals(beginPart)) {
                // 闪回是对应 WHERE 部分的SQL
                if (SysConstant.KEY_EQ_NULL.equals(colValue)) {
                    colValue = SysConstant.KEY_IS_NULL;
                }
                // 第一个字段
                if (!SysConstant.FIRST_KEY_COL.equals(colKey)) {
                    // 最后一个字段
                    if (SysConstant.KEY_COL.concat(maxColOrder.toString()).equals(colKey)) {
                        return "   AND ".concat(entity.getColumnName()).concat(colValue) + ";";
                    }else {
                        return "   AND ".concat(entity.getColumnName()).concat(colValue);
                    }
                }
            } else if (SysConstant.KEY_WHERE.equals(beginPart)) {
                // 闪回是对应 SET 部分的SQL
                if (!SysConstant.KEY_COL.concat(maxColOrder.toString()).equals(colKey)) {
                    // 最后一个字段
                    colValue = colValue + SysConstant.COMMA;
                }
            }
            return "   ".concat(entity.getColumnName()).concat(colValue);
        } else {
            return oldSqlStr;
        }

    }

    public static void main(String[] args) {
        String str = "@2='李四'";

        System.out.println(str.indexOf("="));

        System.out.println(str.substring(0, str.indexOf("=")));
        System.out.println(str.substring(str.indexOf("=")));
    }
}
