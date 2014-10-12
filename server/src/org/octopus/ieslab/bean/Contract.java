package org.octopus.ieslab.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.woods.json4excel.annotation.J4EIgnore;
import org.woods.json4excel.annotation.J4EName;

@Table("t_contract")
@TableIndexes({@Index(name = "i_con_agent", fields = {"agent"}, unique = false),
               @Index(name = "i_con_ct", fields = {"createTime"}, unique = false),
               @Index(name = "i_con_psn", fields = {"pShortName"}, unique = false),
               @Index(name = "i_con_pfn", fields = {"pFullName"}, unique = false)})
@J4EName("合同")
public class Contract {

    @Name
    @J4EName("合同号")
    private String orderNo;

    @J4EName("供应商全称")
    private String pFullName;

    @J4EName("供应商简称")
    private String pShortName;

    @J4EName("业务员")
    private String agent;

    @J4EName("上传时间")
    private Date createTime;

    @J4EName("文件数量")
    private int fileNum;

    // 存放的文件夹id
    @J4EIgnore
    private String dirId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getpFullName() {
        return pFullName;
    }

    public void setpFullName(String pFullName) {
        this.pFullName = pFullName;
    }

    public String getpShortName() {
        return pShortName;
    }

    public void setpShortName(String pShortName) {
        this.pShortName = pShortName;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

    public String getDirId() {
        return dirId;
    }

    public void setDirId(String dirId) {
        this.dirId = dirId;
    }

}
