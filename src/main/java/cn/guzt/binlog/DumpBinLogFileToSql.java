package cn.guzt.binlog;

import cn.guzt.common.dto.FlushBackConfig;
import cn.guzt.common.entity.ColumnEntity;
import cn.guzt.common.exception.BusinessException;
import cn.guzt.common.util.*;
import cn.guzt.constant.SysConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.OptionalLong;

/**
 * 生成binlog原始操作SQL文件
 *
 * @author guzt
 */
public class DumpBinLogFileToSql {
    static Logger logger = LoggerFactory.getLogger(DumpBinLogFileToSql.class);

    protected static String START_FILTER_STR = "### ";

    protected static String EVENT_INSERT = "INSERT INTO";
    protected static String EVENT_DELETE = "DELETE FROM";
    protected static String EVENT_UPDATE = "UPDATE";

    private static String coverEvent(String targetEvent) {
        switch (targetEvent.toUpperCase()) {
            case "UPDATE":
                return EVENT_UPDATE;
            case "DELETE":
                return EVENT_DELETE;
            case "INSERT":
                return EVENT_INSERT;
            default:
                BusinessException.createByErrorMsg("勿操作的事件类型参数错误，只支持INSERT DELETE UPDATE");
                return "";
        }
    }

    /**
     * binlog文件转成SQL文件
     *
     * @param dumpFilePathStr binlog文件路径
     * @param config          配置
     * @param flushBack       true 转为闪回模式的SQL  false 原始SQL
     */
    public static void dumpFileToSql(String dumpFilePathStr, FlushBackConfig config, boolean flushBack) {
        File dumpFile = new File(dumpFilePathStr);
        if (!dumpFile.getAbsoluteFile().exists()) {
            BusinessException.createByErrorMsg("转储的Binlog文件 " + dumpFile.getName() + " 不存在，异常退出");
        }

        // 值处理这个DML事件相关操作记录
        String eventFilterStr = coverEvent(config.getTargetEvent());
        // 只处理这个数据库相关的操作记录
        String dbFilterStr = config.getTargetDbName();
        // 只处理这个表相关的操作记录
        String tableFilterStr = config.getTargetTableName();

        String binlogDumpToSqlFileName = getSqlFileName(config, flushBack);
        File dumpToSqlFile = new File(binlogDumpToSqlFileName);

        BufferedWriter bw = null;
        BufferedReader br = null;

        int sqlCnt = 0;
        boolean startReadFlag = false;
        boolean sqlEnd;
        String beginPart = "";
        Map<String, ColumnEntity> columnEntityMap = JdbcUtil.listColumnsToMap(config);
        OptionalLong optionalLong = JdbcUtil.listColumns(config).stream().mapToLong(
                ColumnEntity::getOrdinalPosition).max();
        Long maxColOrder = -1L;
        if (optionalLong.isPresent()) {
            maxColOrder = optionalLong.getAsLong();
        }
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(dumpToSqlFile), StandardCharsets.UTF_8));
            br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(dumpFile), StandardCharsets.UTF_8));

            String line = br.readLine();
            while (line != null) {
                if (line.startsWith(START_FILTER_STR)) {
                    if (line.startsWith(START_FILTER_STR + EVENT_INSERT)
                            || line.startsWith(START_FILTER_STR + EVENT_DELETE)
                            || line.startsWith(START_FILTER_STR + EVENT_UPDATE)) {
                        startReadFlag = line.startsWith(START_FILTER_STR + eventFilterStr
                                + " `" + dbFilterStr + "`.`" + tableFilterStr + "`");
                        sqlEnd = startReadFlag;
                    } else {
                        sqlEnd = false;
                    }
                    if (startReadFlag) {
                        String tmpStr = line.replaceFirst(START_FILTER_STR, "");
                        if (SysConstant.KEY_WHERE.equals(tmpStr.trim())) {
                            beginPart = SysConstant.KEY_WHERE;
                        } else if (SysConstant.KEY_SET.equals(tmpStr.trim())) {
                            beginPart = SysConstant.KEY_SET;
                        }

                        if (sqlEnd) {
                            sqlCnt = sqlCnt + 1;
                            bw.append("-- --------第").append(String.valueOf(sqlCnt)).append("个--------\n");
                            beginPart = "";
                        }
                        if (flushBack) {
                            tmpStr = converFlushBackSql(beginPart, tmpStr, eventFilterStr, columnEntityMap, maxColOrder);
                        }
                        bw.append(tmpStr).append("\n");
                    }
                }
                line = br.readLine();
            }

            logger.info("生成SQL文件 {} OK!", binlogDumpToSqlFileName);
        } catch (Exception e) {
            logger.info("生成SQL文件 {} ERROR!", binlogDumpToSqlFileName, e);
            BusinessException.createByErrorMsg("转储为SQL文件时出现异常");
        } finally {
            close(bw, br);
        }
    }

    private static String converFlushBackSql(
            String beginPart,
            String oldSqlStr,
            String eventFilterStr,
            Map<String, ColumnEntity> columnEntityMap,
            Long maxColOrder) {
        if (EVENT_UPDATE.equals(eventFilterStr)) {
            return ConverUpdateUtil.converFlushBackSql(beginPart, oldSqlStr, columnEntityMap, maxColOrder);
        } else if (EVENT_DELETE.equals(eventFilterStr)) {
            return ConverDeleteUtil.converFlushBackSql(oldSqlStr, columnEntityMap, maxColOrder);
        } else if (EVENT_INSERT.equals(eventFilterStr)) {
            return ConverInsertUtil.converFlushBackSql(oldSqlStr, columnEntityMap, maxColOrder);
        } else {
            return "出现无法处理的SQL字符串：" + oldSqlStr;
        }
    }

    private static String getSqlFileName(FlushBackConfig config, boolean flushBack) {
        String targetFix;
        if (flushBack) {
            targetFix = "_闪回SQL_";
        } else {
            targetFix = "_原始SQL_";
        }
        return TargetPathUtil.getTargetPath() + File.separator
                + System.getProperty(SysConstant.PROKEY_TARGET_UUID)
                + targetFix
                + config.getTargetTableName() + "_"
                + config.getTargetEvent() + ".sql";
    }

    private static void close(BufferedWriter bw, BufferedReader br) {
        try {
            if (br != null) {
                br.close();
            }
            if (bw != null) {
                bw.flush();
                bw.close();
            }
        } catch (IOException e) {
            logger.error("转储为SQL文件时，关闭文件流异常 ", e);
        }
    }

}
