package cn.guzt.common.dto;

/**
 * 闪回相关配置
 *
 * @author guzt
 */
@SuppressWarnings("unused")
public class FlushBackConfig {

    /**
     * 远程连接mysql 还是本地 binlog文件导出， local   remote, 默认remote
     */
    private String dumpType;
    /**
     * 【必填】目标数据库
     */
    private String targetDbName;
    /**
     * 目标主机  dumpType=remote 时必填
     */
    private String targetHost;
    /**
     * 目标端口 dumpType=remote 时必填
     */
    private String targetPort;
    /**
     * MYSQL用户名 dumpType=remote 时必填
     */
    private String targetUser;
    /**
     * MYSQL密码 dumpType=remote 时必填
     */
    private String targetPassword;
    /**
     * 【必填】要闪回处理的 表名称
     */
    private String targetTableName;
    /**
     * 要闪回处理的 表中字段名，如果 dumpType=local时必填.
     * 值格式是：COLUMN1,COLUMN2,COLUMN3,COLUMN4...  一定要按照 information_schema.`COLUMNS` 里面的ORDINAL_POSITION字段顺序
     */
    private String targetTableColumns;
    /**
     * 【必填】勿操作的事件类型  DELETE  INSERT UPDATE 这三种
     */
    private String targetEvent;
    /**
     * 【必填】开始时间点 yyyy-mm-dd hh24:mi:ss
     */
    private String startDatetime;
    /**
     * 【必填】结束时间点 yyyy-mm-dd hh24:mi:ss
     */
    private String stopDatetime;
    /**
     * 【必填】针对的binlog文件 SHOW BINARY LOGS  当dumpType=local 时传递 binlog文件的绝对路径;
     */
    private String binLogFileName;
    /**
     * mysql主目录地址，主要是运行 mysqlbinlog命令
     */
    private String mysqlHome;


    public String getDumpType() {
        return dumpType;
    }

    public void setDumpType(String dumpType) {
        this.dumpType = dumpType;
    }

    public String getTargetTableColumns() {
        return targetTableColumns;
    }

    public void setTargetTableColumns(String targetTableColumns) {
        this.targetTableColumns = targetTableColumns;
    }

    public String getTargetDbName() {
        return targetDbName;
    }

    public void setTargetDbName(String targetDbName) {
        this.targetDbName = targetDbName;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public void setTargetHost(String targetHost) {
        this.targetHost = targetHost;
    }

    public String getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(String targetPort) {
        this.targetPort = targetPort;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }

    public String getTargetPassword() {
        return targetPassword;
    }

    public void setTargetPassword(String targetPassword) {
        this.targetPassword = targetPassword;
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public String getTargetEvent() {
        return targetEvent;
    }

    public void setTargetEvent(String targetEvent) {
        this.targetEvent = targetEvent;
    }

    public String getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }

    public String getStopDatetime() {
        return stopDatetime;
    }

    public void setStopDatetime(String stopDatetime) {
        this.stopDatetime = stopDatetime;
    }

    public String getBinLogFileName() {
        return binLogFileName;
    }

    public void setBinLogFileName(String binLogFileName) {
        this.binLogFileName = binLogFileName;
    }

    public String getMysqlHome() {
        return mysqlHome;
    }

    public void setMysqlHome(String mysqlHome) {
        this.mysqlHome = mysqlHome;
    }
}
