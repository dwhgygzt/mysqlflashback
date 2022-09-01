package cn.guzt.binlog;

import cn.guzt.common.dto.FlushBackConfig;
import cn.guzt.common.exception.BusinessException;
import cn.guzt.common.util.TargetPathUtil;
import cn.guzt.constant.SysConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * System.out.println("Java运行时环境版本:" + System.getProperty("java.version"));
 * System.out.println("Java 运行时环境供应商:" + System.getProperty("java.vendor"));
 * System.out.println("Java安装目录:" + System.getProperty("java.home"));
 * System.out.println("操作系统的名称:" + System.getProperty("os.name"));
 * System.out.println("操作系统的版本:" + System.getProperty("os.version"));
 * System.out.println("用户的账户名称:" + System.getProperty("user.name"));
 * System.out.println("用户的主目录:" + System.getProperty("user.home"));
 * System.out.println("用户的当前工作目录:" + System.getProperty("user.dir"));
 * System.out.println("当前的classpath的绝对路径的URI表示法:" + Thread.currentThread().getContextClassLoader().getResource(""));
 * System.out.println("得到的是当前的classpath的绝对URI路径:" + DumpBinLogFile.class.getResource("/"));
 * System.out.println("得到的是当前类DumpFile.class文件的URI目录:" + DumpBinLogFile.class.getResource(""));
 *
 * @author guzt
 */
public class DumpBinLogFile {
    static Logger logger = LoggerFactory.getLogger(DumpBinLogFile.class);


    private static String createMysqlBinCmd(
            FlushBackConfig config, String mysqlbinlogPath, String dumpFilePathStr) {
        String remote = "remote";
        String cmd = mysqlbinlogPath + " ";
        if (remote.equals(config.getDumpType())) {
            cmd = cmd +
                    " --read-from-remote-server " +
                    " --host=" + config.getTargetHost() +
                    " --port=" + config.getTargetPort() +
                    " --user=" + config.getTargetUser() +
                    " --password=" + config.getTargetPassword();
        }
        cmd = cmd +
                " --start-datetime=\"" + config.getStartDatetime() + "\"" +
                " --stop-datetime=\"" + config.getStopDatetime() + "\" " +
                " -r " + dumpFilePathStr +
                " -d " + config.getTargetDbName() +
                " -v " + config.getBinLogFileName();
        return cmd;
    }

    public static String dump(FlushBackConfig config) {
        String mysqlbinlogPath;
        if (config.getMysqlHome() != null
                && !"".equals(config.getMysqlHome())) {
            mysqlbinlogPath = config.getMysqlHome() + File.separator + "bin" + File.separator + "mysqlbinlog";
        } else {
            mysqlbinlogPath = "mysqlbinlog";
        }

        String dumpFilePathStr = TargetPathUtil.getTargetPath() + File.separator
                + System.getProperty(SysConstant.PROKEY_TARGET_UUID)
                + "_原始LOG_"
                + config.getTargetTableName() + "_"
                + config.getTargetEvent() + ".txt";

        // 生成系统命令行执行 mysqlbinlog
        String cmd = createMysqlBinCmd(config, mysqlbinlogPath, dumpFilePathStr);
        int exitVal = -1;
        try {
            logger.info("系统命令行执行 mysqlbinlog： {}", cmd);
            // 在单独的进程中执行指定的外部可执行程序的启动路径或字符串命令
            Process process = Runtime.getRuntime().exec(cmd);
            // 等待子进程完成再往下执行
            exitVal = process.waitFor();
            logger.info("系统命令行执行 mysqlbinlog 结果：{}", (exitVal == 0 ? "成功" : "失败"));
            if (exitVal != 0) {
                // 采用字符流读取缓冲池内容
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.error(line);
                }
            }
        } catch (Exception e) {
            logger.error("转储Binlog文件出现错误", e);
        }

        if (exitVal != 0) {
            BusinessException.createByErrorMsg("程序异常终止");
        }

        File dumpFile = new File(dumpFilePathStr);
        if (dumpFile.getAbsoluteFile().exists()) {
            logger.info("转储Binlog文件 " + dumpFilePathStr + " 成功...");
        } else {
            BusinessException.createByErrorMsg("转储Binlog文件 " + dumpFilePathStr + "未生成，异常退出");
        }

        return dumpFilePathStr;
    }

}
