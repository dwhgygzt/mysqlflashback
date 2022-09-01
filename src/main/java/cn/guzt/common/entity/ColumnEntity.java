package cn.guzt.common.entity;

import javax.persistence.Column;

/**
 * 字段定义表主要字段
 *
 * @author guzt
 */
public class ColumnEntity {

    /**
     * 字段顺序
     */
    @Column(name = "ORDINAL_POSITION")
    private Long ordinalPosition;

    /**
     * 字段名称
     */
    @Column(name = "COLUMN_NAME")
    private String columnName;

    /**
     * value="PRI" 表示主键
     */
    @Column(name = "COLUMN_KEY")
    private String columnKey;

    /**
     * 类型大小 varchar(32)
     */
    @Column(name = "COLUMN_TYPE")
    private String columnType;

    /**
     * 模式名称--一般为数据库名称
     */
    @Column(name = "TABLE_SCHEMA")
    private String tableSchema;

    /**
     * 所归属表
     */
    @Column(name = "TABLE_NAME")
    private String tableName;

    /**
     * 是否可以为NULL YES  NO
     */
    @Column(name = "IS_NULLABLE")
    private String isNullable;

    public Long getOrdinalPosition() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(Long ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getTableSchema() {
        return tableSchema;
    }

    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }
}
