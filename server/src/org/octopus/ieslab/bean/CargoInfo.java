package org.octopus.ieslab.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.woods.json4excel.annotation.J4EName;

@Table("cargo_info")
public class CargoInfo {

    @Id
    private long id;

    private boolean hasHtNo;

    @J4EName("请购单")
    private String qgd;

    @J4EName("请购单行号")
    private String qgdNo;

    @J4EName("合同号")
    private String htNo;

    @ColDefine(type = ColType.TEXT)
    private String lineContent;

    public boolean isHasHtNo() {
        return hasHtNo;
    }

    public void setHasHtNo(boolean hasHtNo) {
        this.hasHtNo = hasHtNo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQgd() {
        return qgd;
    }

    public void setQgd(String qgd) {
        this.qgd = qgd;
    }

    public String getQgdNo() {
        return qgdNo;
    }

    public void setQgdNo(String qgdNo) {
        this.qgdNo = qgdNo;
    }

    public String getHtNo() {
        return htNo;
    }

    public void setHtNo(String htNo) {
        this.htNo = htNo;
    }

    public String getLineContent() {
        return lineContent;
    }

    public void setLineContent(String lineContent) {
        this.lineContent = lineContent;
    }

}
