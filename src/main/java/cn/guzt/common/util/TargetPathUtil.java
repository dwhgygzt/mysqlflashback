package cn.guzt.common.util;

import cn.guzt.constant.SysConstant;

/**
 * 目标工作目录
 *
 * @author guzt
 */
@SuppressWarnings("unused")
public class TargetPathUtil {

    /**
     * 获取目标工作目录, 用于加载配置文件，导出最终的SQL
     * 先从系统属性参数中 获取 targetpath 这个参数，如果没有则采用默认工作目录
     *
     * @return 工作目录
     */
    public static String getTargetPath() {
        String targetpath = System.getProperty(SysConstant.PROKEY_TARGET_PATH);
        if (targetpath != null && targetpath.trim().length() > 0) {
            return targetpath;
        } else {
            return SysConstant.DEFAULT_TARGET_PATH;
        }
    }
}
