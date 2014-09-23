package org.octopus.ieslab.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Table;
import org.octopus.core.bean.Bean;
import org.woods.json4excel.annotation.J4EIgnore;
import org.woods.json4excel.annotation.J4EName;

@Table("t_storage_inout")
@J4EName("出入库明细")
public class StorageInOut extends Bean {

    @J4EIgnore
    private String impDate;
    @J4EIgnore
    private String mname;
    @J4EName("物料编码")
    private String mcode;
    @J4EName("单据号")
    private String docNo;
    @J4EName("批号")
    private String batchNo;
    @J4EName("货号")
    private String itemNo;
    @J4EName("业务类型")
    private String businessType;
    @J4EName("收数量")
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double inCount;
    @J4EName("发数量")
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double outCount;

    public String getImpDate() {
        return impDate;
    }

    public void setImpDate(String impDate) {
        this.impDate = impDate;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getMcode() {
        return mcode;
    }

    public void setMcode(String mcode) {
        this.mcode = mcode;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public double getInCount() {
        return inCount;
    }

    public void setInCount(double inCount) {
        this.inCount = inCount;
    }

    public double getOutCount() {
        return outCount;
    }

    public void setOutCount(double outCount) {
        this.outCount = outCount;
    }

}
