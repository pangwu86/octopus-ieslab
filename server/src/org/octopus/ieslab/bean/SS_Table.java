package org.octopus.ieslab.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("ss_table")
public class SS_Table {

    @Id
    private long id;

    private String dbName;

    private String tableName;

    private String remark;

    private int dnum;

    private int inum;

    private int errnum;

    private long useTime;

    private Date updateTime;

    // 0.更新完毕 1.更新中 2.更新出错
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getDnum() {
        return dnum;
    }

    public void setDnum(int dnum) {
        this.dnum = dnum;
    }

    public int getInum() {
        return inum;
    }

    public void setInum(int inum) {
        this.inum = inum;
    }

    public long getUseTime() {
        return useTime;
    }

    public void setUseTime(long useTime) {
        this.useTime = useTime;
    }

    public int getErrnum() {
        return errnum;
    }

    public void setErrnum(int errnum) {
        this.errnum = errnum;
    }

}
