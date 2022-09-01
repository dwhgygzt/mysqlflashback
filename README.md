## 使用说明
mvn clean package -Dmaven.test.skip=true 

## 转储binlog

```shell script
mysql> SHOW BINARY LOGS;
mysql> SHOW MASTER STATUS;

mysql> LOCK TABLE t_tmp_money WRITE;
mysql> UNLOCK TABLES;

> 刷新log日志，自此刻开始产生一个新编号的binlog日志文件
mysql> FLUSH BINARY LOGS; 

mysqlbinlog --start-datetime="2022-07-31 11:10:00" --stop-datetime="2022-07-31 11:20:00" -r ddd.sql -d guohe-wms-test -v  E:\mysql8\data\mysql-bin.000011

mysqlbinlog  --read-from-remote-server --host=localhost --port=3309 --user=root --password=root123  --start-datetime="2022-08-13 11:00:00" --stop-datetime="2022-08-13 11:10:00" -r aaa.sql -d guohe-wms-prod -v  mysql-bin.000012

```


## 配置文件

### local
```json
{
  "dumpType": "local",
  "targetDbName": "student",
  "targetHost": "",
  "targetPort": "",
  "targetUser": "",
  "targetPassword": "",
  "targetTableName": "t_stu_info",
  "targetTableColumns": "ID,NAME,STU_NO,CREATE_TIME,REMARK",
  "targetEvent": "UPDATE",
  "startDatetime": "2022-08-04 15:12:00",
  "stopDatetime": "2022-08-04 15:15:00",
  "binLogFileName": "E:\\mysql8\\data\\mysql-bin.000015",
  "mysqlHome": "E:\\mysql8"
}
```

### remote

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
  "startDatetime": "2022-08-04 11:05:00",
  "stopDatetime": "2022-08-04 11:10:00",
  "binLogFileName": "mysql-bin.000015",
  "mysqlHome": "E:\\mysql8"
}
```