package org.octopus.ieslab.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("t_contract_file")
@TableIndexes({@Index(name = "i_confile_order", fields = {"orderNo"}, unique = false),
               @Index(name = "i_confile_docId", fields = {"docId"}, unique = false),
               @Index(name = "i_confile_fname", fields = {"fname"}, unique = false)})
public class ContractFile {

    @Id
    private long id;

    // 合同号
    private String orderNo;

    // 文件序列, 默认是0
    private int findex;

    // 文件名称
    private String fname;

    // 上传时间
    private Date createTime;

    // 文件的docId
    private String docId;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getFindex() {
        return findex;
    }

    public void setFindex(int findex) {
        this.findex = findex;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
