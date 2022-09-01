package cn.guzt;

import cn.guzt.binlog.DumpBinLogFile;
import cn.guzt.binlog.DumpBinLogFileToSql;
import cn.guzt.common.dto.FlushBackConfig;
import cn.guzt.common.util.ConfigFleUtil;
import cn.guzt.common.util.TargetPathUtil;
import cn.guzt.constant.SysConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主程序入口
 *
 * @author guzt
 */
public class Application {
    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        logger.info("当前工作目录: {}", TargetPathUtil.getTargetPath());
        System.setProperty(SysConstant.PROKEY_TARGET_UUID, System.currentTimeMillis() + "");
        logger.info("当前目标文件前缀ID: {}", System.getProperty(SysConstant.PROKEY_TARGET_UUID));

        // 加载配置文件
        FlushBackConfig flushBackConfig = ConfigFleUtil.loadConfig();

        // 转储Binlog文件
        String dumpFilePathStr = DumpBinLogFile.dump(flushBackConfig);

        // 生成binlog原始操作SQL文件
        DumpBinLogFileToSql.dumpFileToSql(dumpFilePathStr, flushBackConfig, false);

        // 生成binlog闪回操作SQL文件
        DumpBinLogFileToSql.dumpFileToSql(dumpFilePathStr, flushBackConfig, true);

    }
}
