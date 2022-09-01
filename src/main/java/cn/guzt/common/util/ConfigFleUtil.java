package cn.guzt.common.util;

import cn.guzt.common.dto.FlushBackConfig;
import cn.guzt.common.exception.BusinessException;
import cn.guzt.constant.SysConstant;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

/**
 * 定义信息
 *
 * @author guzt
 */
@SuppressWarnings("unused")
public class ConfigFleUtil {

    static Logger logger = LoggerFactory.getLogger(ConfigFleUtil.class);

    public static FlushBackConfig loadConfig() {
        FlushBackConfig flushBackConfig = null;
        File targetConfigPath = new File(TargetPathUtil.getTargetPath());
        if (targetConfigPath.isFile()) {
            BusinessException.createByErrorMsg("目标工作目录 " + targetConfigPath.getPath() + "不是一个目录，请检查");
        }
        File targetConfigFile = new File(
                TargetPathUtil.getTargetPath() + File.separator + SysConstant.FLUSH_BACK_CONFIG_FILE);
        if (!targetConfigFile.getAbsoluteFile().exists() || !targetConfigFile.isFile()) {
            logger.error("配置文件 {} 不存在", targetConfigFile.getPath());
            BusinessException.createByErrorMsg("配置文件 " + targetConfigFile.getPath() + " 不存在");
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(targetConfigFile);
            flushBackConfig = JSONObject.parseObject(fileInputStream, FlushBackConfig.class);
            logger.info("加载配置文件 {}：{}", targetConfigFile.getName(), JSONObject.toJSONString(flushBackConfig));
        } catch (Exception e) {
            logger.error("加载配置文件 {} 异常", targetConfigFile.getName(), e);
            BusinessException.createByErrorMsg("加载配置文件 " + targetConfigFile.getName() + "异常");
        }

        // 判断字段合法性
        checkFlushBackConfig(flushBackConfig);
        // 返回
        return flushBackConfig;
    }

    protected static void checkFlushBackConfig(FlushBackConfig config) {
        String local = "local";
        String remote = "remote";

        notBank(config.getDumpType());
        config.setDumpType(config.getDumpType().trim().toLowerCase());
        if (!local.equals(config.getDumpType()) && !remote.equals(config.getDumpType())) {
            BusinessException.createByErrorMsg("配置参数dumpType的值只能为local或者remote");
        }
        notBank(config.getTargetDbName());
        notBank(config.getTargetTableName());
        notBank(config.getTargetEvent());
        notBank(config.getBinLogFileName());
        notBank(config.getStartDatetime());
        notBank(config.getStopDatetime());
        if (remote.equals(config.getDumpType())) {
            notBank(config.getTargetHost());
            notBank(config.getTargetPort());
            notBank(config.getTargetUser());
            notBank(config.getTargetPassword());
        } else {
            notBank(config.getTargetTableColumns());
        }
    }

    private static void notBank(String obj) {
        if (obj == null || obj.trim().length() == 0) {
            BusinessException.createByErrorMsg("请传递配置参数 " + obj);
        }
    }

}
