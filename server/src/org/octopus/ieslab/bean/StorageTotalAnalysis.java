package org.octopus.ieslab.bean;

import org.woods.json4excel.annotation.J4EName;

@J4EName("库存统计分析")
public class StorageTotalAnalysis {

    public StorageTotalAnalysis(StorageTotal st, Material ma) {
        this.cateL1 = ma.getCateL1();
        this.cateL2 = ma.getCateL2();
        this.cateL3 = ma.getCateL3();
        this.itemNo = ma.getItemNo();
        this.unit = ma.getItemNo();
        this.model = ma.getModel();
        this.smanager = ma.getSmanager();
        this.totalIn = st.getTotalIn();
        this.totalOut = st.getTotalOut();
        this.totalInNum = st.getTotalInNum();
        this.totalOutNum = st.getTotalOutNum();
        this.storageMax = st.getStorageMax();
        this.storageHighNum = st.getStorageHighNum();
        this.mcode = ma.getMcode();
        this.mname = ma.getName();
    }

    @J4EName("物料编码")
    private String mcode;
    @J4EName("物料名称")
    private String mname;
    @J4EName("规格")
    private String model;
    @J4EName("单位")
    private String unit;
    @J4EName("货号")
    private String itemNo;
    @J4EName("大类")
    private String cateL1;
    @J4EName("中类")
    private String cateL2;
    @J4EName("小类")
    private String cateL3;
    @J4EName("库管员")
    private String smanager;
    @J4EName("总入库数量")
    private double totalIn;
    @J4EName("总出库数量")
    private double totalOut;
    @J4EName("总入库次数")
    private long totalInNum;
    @J4EName("总出口次数")
    private long totalOutNum;
    @J4EName("最高库存总量")
    private double storageMax;
    @J4EName("高库存天数")
    private long storageHighNum;

    public String getMcode() {
        return mcode;
    }

    public void setMcode(String mcode) {
        this.mcode = mcode;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getCateL1() {
        return cateL1;
    }

    public void setCateL1(String cateL1) {
        this.cateL1 = cateL1;
    }

    public String getCateL2() {
        return cateL2;
    }

    public void setCateL2(String cateL2) {
        this.cateL2 = cateL2;
    }

    public String getCateL3() {
        return cateL3;
    }

    public void setCateL3(String cateL3) {
        this.cateL3 = cateL3;
    }

    public String getSmanager() {
        return smanager;
    }

    public void setSmanager(String smanager) {
        this.smanager = smanager;
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

}
