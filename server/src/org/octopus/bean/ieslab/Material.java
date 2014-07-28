package org.octopus.bean.ieslab;

import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.woods.json4excel.annotation.J4EName;

@Table("t_material")
@J4EName("物料信息")
public class Material {

    @Name
    @J4EName("物料编码")
    private String mcode;
    @J4EName("物料名称")
    private String name;
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

    public String getMcode() {
        return mcode;
    }

    public void setMcode(String mcode) {
        this.mcode = mcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

}
