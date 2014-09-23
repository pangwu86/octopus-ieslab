package org.octopus.ieslab.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Table;
import org.octopus.core.bean.Bean;
import org.woods.json4excel.annotation.J4EName;

@Table("t_storage_total")
@J4EName("库存统计分析")
public class StorageTotal extends Bean {
    @J4EName("导入月份")
    private String impMonth;
    @J4EName("物料编码")
    private String mcode;
    @J4EName("物料名称")
    private String mname;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    @J4EName("总入库数量")
    private double totalIn;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    @J4EName("总出库数量")
    private double totalOut;
    @J4EName("总入库次数")
    private long totalInNum;
    @J4EName("总出口次数")
    private long totalOutNum;
    @J4EName("最高库存总量")
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double storageMax;
    @J4EName("高库存天数")
    private long storageHighNum;

    public double getStorageMax() {
        return storageMax;
    }

    public void setStorageMax(double storageMax) {
        this.storageMax = storageMax;
    }

    public long getStorageHighNum() {
        return storageHighNum;
    }

    public void setStorageHighNum(long storageHighNum) {
        this.storageHighNum = storageHighNum;
    }

    public long getTotalInNum() {
        return totalInNum;
    }

    public void setTotalInNum(long totalInNum) {
        this.totalInNum = totalInNum;
    }

    public long getTotalOutNum() {
        return totalOutNum;
    }

    public void setTotalOutNum(long totalOutNum) {
        this.totalOutNum = totalOutNum;
    }

    public String getImpMonth() {
        return impMonth;
    }

    public void setImpMonth(String impMonth) {
        this.impMonth = impMonth;
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

    public double getTotalIn() {
        return totalIn;
    }

    public void setTotalIn(double totalIn) {
        this.totalIn = totalIn;
    }

    public double getTotalOut() {
        return totalOut;
    }

    public void setTotalOut(double totalOut) {
        this.totalOut = totalOut;
    }

}
