> 记录一下增删改失误操作时，根据转储转换binlog日志生成用于恢复使用的SQL

## 场景说明
- 需要 java 运行环境
- 只针对 DELETE UPDATE  INSERT 三种操作的恢复
- 需要`开启logbin`，即log_bin参数为ON
- 需要 binlog_row_image参数为 `FULL`
- 每次只针对某一张表一个操作进行生成恢复SQL
- 只针对数据量不大的恢复，这里如果数据量比较大或者误操作比较多，
  建议使用Percona XtraBackup 热备恢复整个库至某个时间段，
  如果没有热备看看是否有冷备份，如果都没有备份，那。。。抓紧。。。自首
- 同 mysqlbinlog命令一样： <br/>
  支持远程连接mysql <br/>
  `支持通过本地mysqlbinlog命令解析离线binlog日志文件`

## 使用说明
假设数据库student 有学生信息表 t_stu_info, 执行了错误更新操作：
```mysql
UPDATE t_stu_info a SET a.`NAME` = '张三' ;
```

当遇到误操作时，冷静一下，**`记录下当前时间`**，及时通知相关业务负责人，
通知之前如果能操作数据库的话`先锁表`，不能直接操作及时通知 DBA：

```shell script
mysql> LOCK TABLE tab_xxxxx WRITE;
```

查看并切换binlog日志(非必须切换)，让接下来的其他业务操作写入到新的binlog日志文件中
```shell script
mysql> SHOW BINARY LOGS;

mysql> FLUSH BINARY LOGS; 
```

1. 下载该项目代码
2. maven 编译生成jar（mysqlflashback.jar）  
```shell script
mvn clean package -Dmaven.test.skip=true
```
3. 准备配置文件 FLUSH_BACK_CONFIG.json （参考章节-配置文件）
4. 运行jar程序，`-Dtargetpath`为工作目录路径参数，jar程序需要的配置文件和生成的SQL文件都会放此路径下面，
   如果不传递系统参数 `-Dtargetpath` 则默认的工作目录为jar所在的目录路径
```shell script
G:\> java -jar -Dtargetpath=G:\\flushbackResult   mysqlflashback.jar
```
运行后命令行输出信息如下：
```shell script
G:\>java -jar -Dtargetpath=G:\\flushbackResult   mysqlflashback.jar
16:54:03.014 [main] INFO cn.guzt.Application - 当前工作目录: G:\\flushbackResult
16:54:03.017 [main] INFO cn.guzt.Application - 当前目标文件前缀ID: 1662022443017
16:54:03.142 [main] INFO cn.guzt.common.util.ConfigFleUtil - 加载配置文件 FLUSH_BACK_CONFIG.json：{"binLogFileName":"E:\\mysql8\\data\\mysql-bin.000018","dumpType":"local","mysqlHome":"E:\\mysql8","startDatetime":"2022-09-01 16:47:00","stopDatetime":"2022-09-01 16:50:00","targetDbName":"student","targetEvent":"UPDATE","targetHost":"","targetPassword":"","targetPort":"","targetTableColumns":"ID,NAME,STU_NO,SEX,CLASS_NO,CREATE_TIME,LAST_UPDATE_TIME,RVERSION,REMARK,HOME_ADDRESS","targetTableName":"t_stu_info","targetUser":""}
16:54:03.143 [main] INFO cn.guzt.binlog.DumpBinLogFile - 系统命令行执行 mysqlbinlog： E:\mysql8\bin\mysqlbinlog  --start-datetime="2022-09-01 16:47:00" --stop-datetime="2022-09-01 16:50:00"  -r G:\\flushbackResult\1662022443017_原始LOG_t_stu_info_UPDATE.txt -d student -v E:\mysql8\data\mysql-bin.000018
16:54:03.222 [main] INFO cn.guzt.binlog.DumpBinLogFile - 系统命令行执行 mysqlbinlog 结果：成功
16:54:03.222 [main] INFO cn.guzt.binlog.DumpBinLogFile - 转储Binlog文件 G:\\flushbackResult\1662022443017_原始LOG_t_stu_info_UPDATE.txt 成功...
16:54:03.275 [main] INFO cn.guzt.common.util.JdbcUtil - Mysql8 驱动加载成功...
16:54:03.287 [main] INFO cn.guzt.binlog.DumpBinLogFileToSql - 生成SQL文件 G:\\flushbackResult\1662022443017_原始SQL_t_stu_info_UPDATE.sql OK!
16:54:03.293 [main] INFO cn.guzt.binlog.DumpBinLogFileToSql - 生成SQL文件 G:\\flushbackResult\1662022443017_闪回SQL_t_stu_info_UPDATE.sql OK!

```
最后看到生成三个文件，其中 `1662022443017_闪回SQL_t_stu_info_UPDATE.sql` 为恢复的sql文件.
打开文件可以看到其实就是将binlog记录的操作反着在来一遍：
```sql
-- --------第1个--------
UPDATE `student`.`t_stu_info`
SET
   ID='1',
   NAME='张小乐',
   STU_NO='00565',
   SEX='1',
   CLASS_NO='10023',
   CREATE_TIME='2022-09-01 16:11:20',
   LAST_UPDATE_TIME=NULL,
   RVERSION=0,
   REMARK=NULL,
   HOME_ADDRESS='长江北路23-45号'
WHERE
   ID='1'
   AND NAME='张三'
   AND STU_NO='00565'
   AND SEX='1'
   AND CLASS_NO='10023'
   AND CREATE_TIME='2022-09-01 16:11:20'
   AND LAST_UPDATE_TIME IS NULL
   AND RVERSION=0
   AND REMARK IS NULL
   AND HOME_ADDRESS='长江北路23-45号';
-- --------第2个--------

...此处省略下面内容

```

6. 释放目标表
```shell script
mysql> UNLOCK TABLES;
```
6. 执行闪回sql文件
执行之前，务必仔细查看原sql和要闪回恢复的sql是否是你想要的信息。




## 配置文件
配置文件名称必须为 `FLUSH_BACK_CONFIG.json`，存放路径为系统参数 `-Dtargetpath` 指定的目录路径,
如果不指定Dtargetpath则和jar文件放同目录下。
### 参数说明

|   参数  |  含义     |   是否必填   |
| ---- | ---- | ---- |
|   dumpType   |   远程连接mysql、还是本地离线binlog文件导出   |  必填    |
|   targetDbName   |    目标数据库  |  必填    |
|  targetHost    |   目标数据库地址   |    非必填 dumpType=remote 时必填  |
|   targetPort   |  目标数据库端口    |  非必填  dumpType=remote 时必填  |
| targetUser     |   目标数据库登录用户   |    非必填 dumpType=remote 时必填 |
|   targetPassword   |   目标数据库登录密码   |   非必填  dumpType=remote 时必填 |
|    targetTableName  |   发生误操作的表   |   必填   |
|    targetTableColumns  |  发生误操作的表对应的字段名称，<br/> 值格式是：COLUMN1,COLUMN2,COLUMN3,COLUMN4... <br/> 一定要按照 information_schema.`COLUMNS` 里面的ORDINAL_POSITION字段顺序    |  非必填 当dumpType=local必填   |
|   targetEvent   |  勿操作的事件类型  DELETE  INSERT UPDATE 这三种    |   必填   |
|   startDatetime   | 勿操作开始时间点 yyyy-mm-dd hh24:mi:ss     |   必填   |
|   stopDatetime   |   勿操作结束时间点 yyyy-mm-dd hh24:mi:ss   |   必填   |
|  binLogFileName    | binlog文件名     |  非必填 当dumpType=local 时传递 binlog文件的绝对路径   |
|   mysqlHome   |  本地mysqlbinlog命令所在的home路径,即本地安装mysql客户端的home路径    |  非必填 不填写时确保命令行下可以识别mysqlbinlog命令    |



### 样例 local 离线解析binlog日志
```json
{
  "dumpType": "local",
  "targetDbName": "student",
  "targetHost": "",
  "targetPort": "",
  "targetUser": "",
  "targetPassword": "",
  "targetTableName": "t_stu_info",
  "targetTableColumns": "ID,NAME,STU_NO,SEX,CLASS_NO,CREATE_TIME,LAST_UPDATE_TIME,RVERSION,REMARK,HOME_ADDRESS",
  "targetEvent": "UPDATE",
  "startDatetime": "2022-09-01 16:47:00",
  "stopDatetime": "2022-09-01 16:50:00",
  "binLogFileName": "E:\\mysql8\\data\\mysql-bin.000018",
  "mysqlHome": "E:\\mysql8"
}
```

### 样例 remote 远程连接解析binlog日志

```json
{
  "dumpType": "remote",
  "targetDbName": "student",
  "targetHost": "172.11.231.10",
  "targetPort": "3306",
  "targetUser": "stuadmin",
  "targetPassword": "Duh123@com681_21",
  "targetTableName": "t_stu_info",
  "targetTableColumns": "",
  "targetEvent": "UPDATE",
  "startDatetime": "2022-09-01 16:47:00",
  "stopDatetime": "2022-09-01 16:50:00",
  "binLogFileName": "mysql-bin.000018",
  "mysqlHome": "E:\\mysql8"
}
```