package org.octopus.bean.ieslab;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Table;
import org.octopus.bean.Bean;

@Table("t_storage_total")
public class StorageTotal extends Bean {

    private String impMonth;
    private String mname;
    private String mcode;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double totalIn;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double totalOut;
    private long totalInNum;
    private long totalOutNum;

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
