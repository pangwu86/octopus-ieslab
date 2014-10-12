package org.octopus.ieslab.bean;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.woods.json4excel.annotation.J4EName;

@Table("t_contract_provider")
@TableIndexes({@Index(name = "i_conpro_code", fields = {"code"}, unique = false),
               @Index(name = "i_conpro_snm", fields = {"shortName"}, unique = false),
               @Index(name = "i_conpro_fnm", fields = {"fullName"}, unique = false)})
@J4EName("供应商")
public class ContractProvider {

    @Id
    private long id;

    // 供应商编号
    @J4EName("编号")
    private String code;

    // 简称
    @J4EName("供应商简称")
    private String shortName;

    // 全称
    @J4EName("供应商全称")
    private String fullName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
