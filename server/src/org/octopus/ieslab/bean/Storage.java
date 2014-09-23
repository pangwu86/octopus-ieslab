package org.octopus.ieslab.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Table;
import org.octopus.core.bean.Bean;
import org.woods.json4excel.annotation.J4EIgnore;
import org.woods.json4excel.annotation.J4EName;

@Table("t_storage")
@J4EName("库存明细|库存")
public class Storage extends Bean {

    @J4EIgnore
    private String impDate;
    @J4EIgnore
    private String mname;
    @J4EName("物料编码")
    private String mcode;
    @J4EName("库存总量")
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double total;
    @J4EName("材料库位")
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double material;
    @J4EName("维护库位")
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double maintenance;
    @J4EName("返修库位")
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double repair;
    @J4EName("次品库位")
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double inferior;
    @J4EName("i6受控库位")
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double i6;

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

    public String getImpDate() {
        return impDate;
    }

    public void setImpDate(String impDate) {
        this.impDate = impDate;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMaterial() {
        return material;
    }

    public void setMaterial(double material) {
        this.material = material;
    }

    public double getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(double maintenance) {
        this.maintenance = maintenance;
    }

    public double getRepair() {
        return repair;
    }

    public void setRepair(double repair) {
        this.repair = repair;
    }

    public double getInferior() {
        return inferior;
    }

    public void setInferior(double inferior) {
        this.inferior = inferior;
    }

    public double getI6() {
        return i6;
    }

    public void setI6(double i6) {
        this.i6 = i6;
    }

}
