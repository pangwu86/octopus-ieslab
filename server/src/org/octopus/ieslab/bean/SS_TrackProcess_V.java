package org.octopus.ieslab.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("ss_trackprocess_v")
@TableIndexes({@Index(name = "ss_tpv_qgd", fields = {"请购单号"}, unique = false)})
public class SS_TrackProcess_V {

    @Id
    private long id;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 请购单号;

    private int 请购单序号;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 合同编号;

    private int 合同序号;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 材料编码;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 材料名称;

    @ColDefine(type = ColType.VARCHAR, width = 500)
    private String 规格型号;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 单位;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double 请购单数;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double 合同执行数;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double 订货单价;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double 到货总数;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double 入库总数;
    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double 采购未完成数;

    private String 业务员;

    private String 申请人;

    private Date 计划制定日;

    private Date 计划到货日;

    private Date 确认到货日;

    private Date 订货日;

    private Date 到货日;

    @ColDefine(type = ColType.FLOAT, width = 20, precision = 2)
    private double 到货数;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 检验单号;

    private int 检验单行号;

    private Date 调整日期;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 调整说明;

    private Date 反馈日期;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 反馈说明;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 关联单据号;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 关联行号;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 采购类型;

    private String 到货;

    private String 入库;

    private String 完成;

    private String 到齐;

    @ColDefine(type = ColType.VARCHAR, width = 100)
    private String 工程号;

    @ColDefine(type = ColType.VARCHAR, width = 200)
    private String 工程名称;

    @ColDefine(type = ColType.TEXT)
    private String i6_供应商名称;

    @ColDefine(type = ColType.TEXT)
    private String 备注;

    private String 请购单关闭;

    private String 合同关闭;

    private String 直发现场;

    private String 指定采购;

    private String 散发;

    private Date 调整时间;

    private int 申请人部门;

    public String get请购单号() {
        return 请购单号;
    }

    public void set请购单号(String 请购单号) {
        this.请购单号 = 请购单号;
    }

    public int get请购单序号() {
        return 请购单序号;
    }

    public void set请购单序号(int 请购单序号) {
        this.请购单序号 = 请购单序号;
    }

    public String get合同编号() {
        return 合同编号;
    }

    public void set合同编号(String 合同编号) {
        this.合同编号 = 合同编号;
    }

    public int get合同序号() {
        return 合同序号;
    }

    public void set合同序号(int 合同序号) {
        this.合同序号 = 合同序号;
    }

    public String get材料编码() {
        return 材料编码;
    }

    public void set材料编码(String 材料编码) {
        this.材料编码 = 材料编码;
    }

    public String get材料名称() {
        return 材料名称;
    }

    public void set材料名称(String 材料名称) {
        this.材料名称 = 材料名称;
    }

    public String get规格型号() {
        return 规格型号;
    }

    public void set规格型号(String 规格型号) {
        this.规格型号 = 规格型号;
    }

    public String get单位() {
        return 单位;
    }

    public void set单位(String 单位) {
        this.单位 = 单位;
    }

    public double get请购单数() {
        return 请购单数;
    }

    public void set请购单数(double 请购单数) {
        this.请购单数 = 请购单数;
    }

    public double get合同执行数() {
        return 合同执行数;
    }

    public void set合同执行数(double 合同执行数) {
        this.合同执行数 = 合同执行数;
    }

    public double get订货单价() {
        return 订货单价;
    }

    public void set订货单价(double 订货单价) {
        this.订货单价 = 订货单价;
    }

    public double get到货总数() {
        return 到货总数;
    }

    public void set到货总数(double 到货总数) {
        this.到货总数 = 到货总数;
    }

    public double get入库总数() {
        return 入库总数;
    }

    public void set入库总数(double 入库总数) {
        this.入库总数 = 入库总数;
    }

    public double get采购未完成数() {
        return 采购未完成数;
    }

    public void set采购未完成数(double 采购未完成数) {
        this.采购未完成数 = 采购未完成数;
    }

    public String get业务员() {
        return 业务员;
    }

    public void set业务员(String 业务员) {
        this.业务员 = 业务员;
    }

    public String get申请人() {
        return 申请人;
    }

    public void set申请人(String 申请人) {
        this.申请人 = 申请人;
    }

    public Date get计划制定日() {
        return 计划制定日;
    }

    public void set计划制定日(Date 计划制定日) {
        this.计划制定日 = 计划制定日;
    }

    public Date get计划到货日() {
        return 计划到货日;
    }

    public void set计划到货日(Date 计划到货日) {
        this.计划到货日 = 计划到货日;
    }

    public Date get确认到货日() {
        return 确认到货日;
    }

    public void set确认到货日(Date 确认到货日) {
        this.确认到货日 = 确认到货日;
    }

    public Date get订货日() {
        return 订货日;
    }

    public void set订货日(Date 订货日) {
        this.订货日 = 订货日;
    }

    public Date get到货日() {
        return 到货日;
    }

    public void set到货日(Date 到货日) {
        this.到货日 = 到货日;
    }

    public double get到货数() {
        return 到货数;
    }

    public void set到货数(double 到货数) {
        this.到货数 = 到货数;
    }

    public String get检验单号() {
        return 检验单号;
    }

    public void set检验单号(String 检验单号) {
        this.检验单号 = 检验单号;
    }

    public int get检验单行号() {
        return 检验单行号;
    }

    public void set检验单行号(int 检验单行号) {
        this.检验单行号 = 检验单行号;
    }

    public Date get调整日期() {
        return 调整日期;
    }

    public void set调整日期(Date 调整日期) {
        this.调整日期 = 调整日期;
    }

    public String get调整说明() {
        return 调整说明;
    }

    public void set调整说明(String 调整说明) {
        this.调整说明 = 调整说明;
    }

    public Date get反馈日期() {
        return 反馈日期;
    }

    public void set反馈日期(Date 反馈日期) {
        this.反馈日期 = 反馈日期;
    }

    public String get反馈说明() {
        return 反馈说明;
    }

    public void set反馈说明(String 反馈说明) {
        this.反馈说明 = 反馈说明;
    }

    public String get关联单据号() {
        return 关联单据号;
    }

    public void set关联单据号(String 关联单据号) {
        this.关联单据号 = 关联单据号;
    }

    public String get关联行号() {
        return 关联行号;
    }

    public void set关联行号(String 关联行号) {
        this.关联行号 = 关联行号;
    }

    public String get采购类型() {
        return 采购类型;
    }

    public void set采购类型(String 采购类型) {
        this.采购类型 = 采购类型;
    }

    public String get到货() {
        return 到货;
    }

    public void set到货(String 到货) {
        this.到货 = 到货;
    }

    public String get入库() {
        return 入库;
    }

    public void set入库(String 入库) {
        this.入库 = 入库;
    }

    public String get完成() {
        return 完成;
    }

    public void set完成(String 完成) {
        this.完成 = 完成;
    }

    public String get到齐() {
        return 到齐;
    }

    public void set到齐(String 到齐) {
        this.到齐 = 到齐;
    }

    public String get工程号() {
        return 工程号;
    }

    public void set工程号(String 工程号) {
        this.工程号 = 工程号;
    }

    public String get工程名称() {
        return 工程名称;
    }

    public void set工程名称(String 工程名称) {
        this.工程名称 = 工程名称;
    }

    public String getI6_供应商名称() {
        return i6_供应商名称;
    }

    public void setI6_供应商名称(String i6_供应商名称) {
        this.i6_供应商名称 = i6_供应商名称;
    }

    public String get备注() {
        return 备注;
    }

    public void set备注(String 备注) {
        this.备注 = 备注;
    }

    public String get请购单关闭() {
        return 请购单关闭;
    }

    public void set请购单关闭(String 请购单关闭) {
        this.请购单关闭 = 请购单关闭;
    }

    public String get合同关闭() {
        return 合同关闭;
    }

    public void set合同关闭(String 合同关闭) {
        this.合同关闭 = 合同关闭;
    }

    public String get直发现场() {
        return 直发现场;
    }

    public void set直发现场(String 直发现场) {
        this.直发现场 = 直发现场;
    }

    public String get指定采购() {
        return 指定采购;
    }

    public void set指定采购(String 指定采购) {
        this.指定采购 = 指定采购;
    }

    public String get散发() {
        return 散发;
    }

    public void set散发(String 散发) {
        this.散发 = 散发;
    }

    public Date get调整时间() {
        return 调整时间;
    }

    public void set调整时间(Date 调整时间) {
        this.调整时间 = 调整时间;
    }

    public int get申请人部门() {
        return 申请人部门;
    }

    public void set申请人部门(int 申请人部门) {
        this.申请人部门 = 申请人部门;
    }

}
