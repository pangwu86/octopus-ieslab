package org.octopus.ieslab.module;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Scope;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.nutz.web.query.CndMaker;
import org.nutz.web.query.Query;
import org.nutz.web.query.QueryStr;
import org.octopus.core.Keys;
import org.octopus.core.bean.Document;
import org.octopus.core.bean.User;
import org.octopus.core.module.AbstractBaseModule;
import org.octopus.ieslab.bean.Contract;
import org.octopus.ieslab.bean.ContractAgent;
import org.octopus.ieslab.bean.ContractFile;
import org.octopus.ieslab.bean.ContractProvider;
import org.octopus.ieslab.bean.OrderFile;

@At("/ieslab/contract")
@Ok("ajax")
@Fail("ajax")
@IocBean(create = "init")
public class ContractModule extends AbstractBaseModule {

    private Log log = Logs.get();

    private String CONTRACT_FILE_ORDER = "PU14091500038";
    private int ORDER_LENGTH = CONTRACT_FILE_ORDER.length();
    private int ORDER_NM_MIN_LENTH = ORDER_LENGTH + "(XX)YY.jpg".length();

    private Map<String, String> providerNames = new HashMap<String, String>();
    private Set<String> agentNames = new HashSet<String>();

    private File TMP_DIR = new File(System.getProperty("java.io.tmpdir"));

    public void init() {
        refreshBaseInfoCache();
    }

    @At("/baseinfo/refresh")
    public AjaxReturn refreshBaseInfoCache() {
        providerNames.clear();
        agentNames.clear();
        List<ContractProvider> cpList = dao.query(ContractProvider.class, null);
        for (ContractProvider cp : cpList) {
            providerNames.put(cp.getShortName(), cp.getFullName());
        }
        List<ContractAgent> agList = dao.query(ContractAgent.class, null);
        for (ContractAgent ca : agList) {
            agentNames.add(ca.getName());
        }
        NutMap re = new NutMap();
        re.setv("provider", providerNames);
        re.setv("agent", agentNames);
        return Ajax.ok().setData(re);
    }

    @At("/baseinfo/provider/add")
    public AjaxReturn addProvider(@Param("txt") String txt) {
        String[] lines = Strings.splitIgnoreBlank(txt, "\n");
        for (String line : lines) {
            try {
                String[] pi = Strings.splitIgnoreBlank(line, "\\s");
                log.infof("Add Provider : %s", Strings.join(", ", pi));
                String fnm = pi[0];
                String snm = pi[1];
                ContractProvider cp = dao.fetch(ContractProvider.class,
                                                Cnd.where("shortName", "=", snm));
                if (cp == null) {
                    cp = new ContractProvider();
                    cp.setFullName(fnm);
                    cp.setShortName(snm);
                    dao.insert(cp);
                    providerNames.put(cp.getShortName(), cp.getFullName());
                }
            }
            catch (Exception e) {
                log.error(e);
            }
        }
        return Ajax.ok();
    }

    @At("/baseinfo/agent/add")
    public AjaxReturn addAgent(@Param("txt") String txt) {
        String[] alist = Strings.splitIgnoreBlank(txt, "\n");
        for (String ag : alist) {
            try {
                log.infof("Add Agent : %s", ag);
                ContractAgent ca = dao.fetch(ContractAgent.class, ag);
                if (ca == null) {
                    ca = new ContractAgent();
                    ca.setName(ag);
                    dao.insert(ca);
                    agentNames.add(ca.getName());
                }
            }
            catch (Exception e) {
                log.error(e);
            }
        }
        return Ajax.ok();
    }

    @At("/baseinfo/agent/list")
    public QueryResult listAgent(@Param("..") Query q) {
        // TODO 稍后加上时间段过滤
        q.tableSet(dao, ContractAgent.class, null);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where().andLike("name", kwd, false);
                }
            }
        }, q);
        return qr;
    }

    @At("/baseinfo/provider/list")
    public QueryResult listProvider(@Param("..") Query q) {
        // TODO 稍后加上时间段过滤
        q.tableSet(dao, ContractProvider.class, null);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where().andLike("shortName", kwd, false);
                    sc.where().andLike("fullName", kwd, false);
                }
            }
        }, q);
        return qr;
    }

    @At("/con/list")
    public QueryResult listContract(@Param("..") Query q) {
        // TODO 稍后加上时间段过滤
        q.tableSet(dao, Contract.class, null);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where().andLike("orderNo", kwd, false);
                    sc.where().orLike("pFullName", kwd, false);
                    sc.where().orLike("pShortName", kwd, false);
                    sc.where().orLike("agent", kwd, false);
                }
            }
        }, q);
        return qr;
    }

    /**
     * 检查合同文件是否存在, 一般是在文件上传前, 拖动到fileUpload里后
     * 
     * @param fnms
     * @return
     */
    @At("/con/check")
    public NutMap checkContract(@Param("fnm") String fnm) {
        NutMap re = new NutMap();
        OrderFile ofile = analysisFile(fnm);
        if (!ofile.isOk()) {
            re.setv("ok", false);
            re.setv("msg", ofile.getError());
            return re;
        }
        // 检查供应商,业务员是否存在
        if (!providerNames.containsKey(ofile.getProvider())) {
            re.setv("ok", false);
            re.setv("msg", "供应商不存在");
            return re;
        }
        if (!agentNames.contains(ofile.getAgent())) {
            re.setv("ok", false);
            re.setv("msg", "业务员不存在");
            return re;
        }
        // 检查合同是否已经上传过了
        Contract con = dao.fetch(Contract.class, ofile.getOrderNo());
        if (con != null) {
            ContractFile confile = dao.fetch(ContractFile.class,
                                             Cnd.where("orderNo", "=", ofile.getOrderNo())
                                                .and("fname", "=", fnm));
            if (confile != null) {
                re.setv("ok", false);
                re.setv("msg", "文件已上传过");
                return re;
            }
        }
        re.setv("ok", true);
        return re;
    }

    private Map<String, File> dwPool = new HashMap<String, File>();

    /**
     * 打包合同文件, 返回临时文件的id, 方便后续下载
     * 
     * 
     * @param olist
     * @param resp
     * @param me
     * @return
     */
    @At("/con/package")
    @Ok("ajax")
    public String packageContract(@Param("olist") String olist,
                                  HttpServletResponse resp,
                                  @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        String[] orders = Strings.splitIgnoreBlank(olist, ",");
        // 创建临时文件夹
        File tarDir = new File(TMP_DIR, "contract_download_" + System.currentTimeMillis());
        log.infof("Contract-MkTmpDir : %s", tarDir.getAbsolutePath());
        Files.createDirIfNoExists(tarDir);
        for (String order : orders) {
            List<ContractFile> cfList = dao.query(ContractFile.class,
                                                  Cnd.where("orderNo", "=", order));
            for (ContractFile cf : cfList) {
                log.infof("Contract-AddContract : %s", cf.getFname());
                File centity = Files.createFileIfNoExists(new File(tarDir, cf.getFname()));
                Document cdoc = fsIO.fetch(cf.getDocId());
                Files.write(centity, fsIO.readBinary(cdoc));
            }
        }
        // 打包
        File pkgFile = new File(TMP_DIR, "合同 " + Times.sDT(new Date()) + ".zip");
        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(pkgFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(tarDir);
        zip.addFileset(fileSet);
        zip.execute();
        // 缓存并返回
        String md5 = Lang.md5(pkgFile);
        dwPool.put(md5, pkgFile);
        return md5;
    }

    @At("/con/download/?")
    @Ok("raw")
    public File downloadContract(String md5, HttpServletResponse resp) {
        File pkgFile = dwPool.remove(md5);
        try {
            String encode = new String((pkgFile.getName()).getBytes("UTF-8"), "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            resp.setHeader("Content-Type", "application/zip");
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        return pkgFile;
    }

    /**
     * 上传文件, 新建一个文件
     * 
     * @param req
     * @param module
     * @param moduleKey
     * @param me
     * @return
     */
    @At("/con/upload")
    @Ok("ajax")
    @Fail("ajax")
    public Document uploadContract(HttpServletRequest req,
                                   @Attr(scope = Scope.SESSION, value = Keys.SESSION_USER) User me) {
        String module = "ies-contract";
        boolean isPrivate = false;
        // 分析名字, 拿到各种需要的数据
        String fnm = urlDecode(req.getHeader("fnm"));
        OrderFile ofile = analysisFile(fnm);
        if (!ofile.isOk()) {
            log.warnf("Contract Upload File Name Is Not OK, %s", fnm);
            return null;
        }
        if (dao.count(ContractFile.class,
                      Cnd.where("orderNo", "=", ofile.getOrderNo()).and("fname", "=", fnm)) > 0) {
            log.warnf("Contract Upload File Exist!, %s", fnm);
            return null;
        }
        Document odir = null;
        // 生成contract
        Contract con = dao.fetch(Contract.class, ofile.getOrderNo());
        if (con == null) {
            // 根据order生成目录
            odir = fsIO.make(module,
                             ofile.getOrderNo(),
                             ofile.getOrderNo(),
                             "dir",
                             isPrivate,
                             me.getName());

            con = new Contract();
            con.setDirId(odir.getId());
            con.setOrderNo(ofile.getOrderNo());
            con.setAgent(ofile.getAgent());
            con.setpShortName(ofile.getProvider());
            con.setpFullName(providerNames.get(ofile.getProvider()));
            con.setFileNum(0);
            con.setCreateTime(new Date());
            dao.insert(con);
        } else {
            odir = fsIO.fetch(con.getDirId());
        }
        // 写入文件
        Document ofdoc = fsIO.make(odir, fnm, null, isPrivate, me.getName());
        try {
            BufferedInputStream ins = Streams.buff(req.getInputStream());
            if (ofdoc.isBinary()) {
                fsIO.writeBinary(ofdoc, ins, me.getName());
            } else {
                fsIO.writeText(ofdoc, ins, me.getName());
            }
            // FIXME 移动到别的地方去, 不要放在这里
            fsExtraMaker.makePreview(ofdoc);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
        // 生成contractfile
        ContractFile conFile = new ContractFile();
        conFile.setDocId(ofdoc.getId());
        conFile.setCreateTime(new Date());
        conFile.setFindex(ofile.getIndex());
        conFile.setFname(ofile.getName());
        conFile.setOrderNo(ofile.getOrderNo());
        dao.insert(conFile);
        // 更新contract
        con.setFileNum(con.getFileNum() + 1);
        dao.update(con, "fileNum");
        return ofdoc;
    }

    private OrderFile analysisFile(String fnm) {
        OrderFile of = new OrderFile();
        of.setOk(false);
        of.setName(fnm);
        try {
            if (fnm.length() < ORDER_NM_MIN_LENTH) {
                of.setError("文件名不符合规范");
            } else {
                String order = fnm.substring(0, ORDER_LENGTH);
                Pattern orderReg = Pattern.compile("^[a-zA-Z0-9]+$");
                if (!orderReg.matcher(order).find()) {
                    of.setError("合同号格式不对");
                } else {
                    of.setOrderNo(order);
                    // 先替换()
                    String other = fnm.substring(ORDER_LENGTH);
                    other = other.replaceAll("（", "(");
                    other = other.replaceAll("）", ")");
                    other = other.replaceAll("．", ".");
                    // 分析 index, 供应商, 后缀
                    Pattern otherMa = Pattern.compile("\\s*(\\d*)\\s*(.+)\\s*\\((.+)\\)\\.([a-zA-Z]+)");
                    Matcher otherMatcher = otherMa.matcher(other);
                    if (!otherMatcher.find()) {
                        of.setError("供应商,业务员等格式不符");
                    } else {
                        String index = otherMatcher.group(1);
                        String provider = otherMatcher.group(2);
                        String agent = otherMatcher.group(3);
                        String ftype = otherMatcher.group(4);
                        if (!Strings.isBlank(index)) {
                            of.setIndex(Integer.valueOf(index));
                        }
                        of.setProvider(provider);
                        of.setAgent(agent);
                        of.setType(ftype);
                        of.setOk(true);
                    }
                }
            }
        }
        catch (Exception e) {
            log.error(e);
            of.setError(e.getMessage());
        }
        // 分析文件名称
        return of;
    }

    private String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

}
