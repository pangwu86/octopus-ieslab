package org.octopus.ieslab.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.nutz.web.query.CndMaker;
import org.nutz.web.query.Query;
import org.nutz.web.query.QueryStr;
import org.octopus.core.module.AbstractBaseModule;
import org.octopus.ieslab.bean.Material;
import org.octopus.ieslab.bean.Storage;
import org.octopus.ieslab.bean.StorageInOut;
import org.octopus.ieslab.bean.StorageTotal;
import org.octopus.ieslab.bean.StorageTotalAnalysis;
import org.woods.json4excel.J4E;
import org.woods.json4excel.J4EConf;

@At("/ieslab/storage")
@Ok("ajax")
public class StorageModule extends AbstractBaseModule {

    private Log log = Logs.get();

    @At("/material/list")
    public QueryResult listMaterial(@Param("..") Query q) {
        q.tableSet(dao, Material.class, null);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where().andLike("mcode", kwd, false);
                    sc.where().orLike("name", kwd, false);
                    sc.where().orLike("model", kwd, false);
                    sc.where().orLike("smanager", kwd, false);
                    sc.where().orLike("cateL1", kwd, false);
                    sc.where().orLike("cateL2", kwd, false);
                    sc.where().orLike("cateL3", kwd, false);
                }
            }
        }, q);
        return qr;
    }

    @At("/storage/list")
    public QueryResult listStorage(@Param("..") Query q,
                                   @Param("startDate") String stDate,
                                   @Param("endDate") String endDate) {
        q.tableSet(dao, Storage.class, null);
        q.cndSet(stDate, endDate);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where().andLike("mcode", kwd, false);
                    sc.where().orLike("mname", kwd, false);
                }
                if (otherQCnd != null && otherQCnd.length == 2) {
                    sc.where().and("impDate", ">=", otherQCnd[0]);
                    sc.where().and("impDate", "<=", otherQCnd[1]);
                }
            }
        }, q);
        return qr;
    }

    @At("/storageTotal/month")
    public AjaxReturn anlysisStorageTotalYear(@Param("year") final String year) {
        Sql chkMonth = Sqls.create("select distinct(impMonth) from t_storage_total where impMonth like '"
                                   + year
                                   + ".%'");
        final Map<String, Boolean> mmap = new HashMap<String, Boolean>();
        chkMonth.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                while (rs.next()) {
                    mmap.put(rs.getString(1), true);
                }
                return null;
            }
        });
        dao.execute(chkMonth);
        return Ajax.ok().setData(mmap);
    }

    @At("/storageTotal/anlysis")
    public AjaxReturn anlysisStorageTotalMonthDate(@Param("month") final String month) {
        dao.clear(StorageTotal.class, Cnd.where("impMonth", "=", month));
        // 重新分析数据并插入
        Sql mcodeSql = Sqls.create("select distinct(mcode) from t_storage_inout where impDate >= '"
                                   + month
                                   + ".01' and impDate <= '"
                                   + month
                                   + ".31'");
        final List<String> mcodes = new ArrayList<String>();
        mcodeSql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                while (rs.next()) {
                    mcodes.add(rs.getString(1));
                }
                return null;
            }
        });
        dao.execute(mcodeSql);
        // 获得非重复mcode
        for (String mc : mcodes) {
            Sql totalSql = Sqls.create("select mcode, mname, sum(inCount) as totalIn, sum(outCount) as totalOut from t_storage_inout where mcode = "
                                       + mc);
            totalSql.setCallback(new SqlCallback() {
                @Override
                public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                    while (rs.next()) {
                        final StorageTotal st = new StorageTotal();
                        st.setImpMonth(month);
                        st.setMcode(rs.getString(1));
                        st.setMname(rs.getString(2));
                        st.setTotalIn(rs.getDouble(3));
                        st.setTotalOut(rs.getDouble(4));
                        // 计算数量
                        Sql inNumSql = Sqls.create("select count(inCount) from t_storage_inout where mcode = "
                                                   + st.getMcode()
                                                   + " and inCount != 0");
                        inNumSql.setCallback(new SqlCallback() {
                            @Override
                            public Object invoke(Connection conn, ResultSet rs, Sql sql)
                                    throws SQLException {
                                while (rs.next()) {
                                    st.setTotalInNum(rs.getLong(1));
                                }
                                return null;
                            }
                        });
                        dao.execute(inNumSql);

                        Sql outNumSql = Sqls.create("select count(outCount) from t_storage_inout where mcode = "
                                                    + st.getMcode()
                                                    + " and outCount != 0");
                        outNumSql.setCallback(new SqlCallback() {
                            @Override
                            public Object invoke(Connection conn, ResultSet rs, Sql sql)
                                    throws SQLException {
                                while (rs.next()) {
                                    st.setTotalOutNum(rs.getLong(1));
                                }
                                return null;
                            }
                        });
                        dao.execute(outNumSql);
                        // 插入数据
                        dao.insert(st);
                    }
                    return null;
                }
            });
            dao.execute(totalSql);
        }
        return Ajax.ok();
    }

    private static String TMP_PATH = System.getProperty("java.io.tmpdir");

    @At("/storageTotal/download")
    @Ok("void")
    public void downloadStorageTotal(HttpServletRequest req, HttpServletResponse resp) {
        String excelPath = TMP_PATH + "/库存分析_2013.06-2014.06.xls";
        File excelFile = null;
        try {
            excelFile = Files.createFileIfNoExists(excelPath);
        }
        catch (IOException e2) {
            e2.printStackTrace();
        }
        final List<StorageTotalAnalysis> anList = new ArrayList<StorageTotalAnalysis>();

        dao.each(StorageTotal.class, null, new Each<StorageTotal>() {
            @Override
            public void invoke(int index, StorageTotal st, int length) throws ExitLoop,
                    ContinueLoop, LoopException {
                Material ma = dao.fetch(Material.class, st.getMcode());
                if (ma == null) {
                    ma = new Material();
                    ma.setName("未知物料");
                    ma.setMcode(st.getMcode());
                }
                anList.add(new StorageTotalAnalysis(st, ma));
            }
        });

        log.info("load j4eConf");
        J4EConf j4eConf = J4EConf.from(ImpExpModule.class.getResourceAsStream("/j4e/ieslab/storageTotalAnlysis.js"));

        try {
            log.infof("make excel to %s", excelFile.getAbsolutePath());
            J4E.toExcel(new FileOutputStream(excelFile), anList, j4eConf);
        }
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        // 浏览器强制刷新的话，就一定强制读取
        if (!"no-cache".equals(req.getHeader("Cache-Control"))
            && req.getDateHeader("If-Modified-Since") == excelFile.lastModified()) {
            resp.setStatus(304);
        }
        // 写入返回字节
        else {
            try {
                resp.reset();
                resp.setHeader("Accept-Ranges", "bytes");
                resp.setHeader("Content-Length", excelFile.length() + "");
                String encode = new String(excelFile.getName().getBytes("UTF-8"), "ISO8859-1");
                resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
                resp.setHeader("Content-Type", "application/zip");
                resp.setDateHeader("Last-Modified", excelFile.lastModified());
                Streams.writeAndClose(resp.getOutputStream(), new FileInputStream(excelFile));
                resp.setStatus(200);
                resp.flushBuffer();
                // 删除文件
                // excelFile.delete();
            }
            catch (Exception e) {
                throw Lang.wrapThrow(e);
            }
        }
    }

    @At("/storageTotal/anlysisStorage")
    public AjaxReturn anlysisStorageTotalStorage() {
        dao.each(StorageTotal.class, null, new Each<StorageTotal>() {
            @Override
            public void invoke(int index, final StorageTotal ele, int length) throws ExitLoop,
                    ContinueLoop, LoopException {
                // Sql sMax =
                // Sqls.create("select total from t_storage where mcode = '"
                // + ele.getMcode()
                // + "' order by total desc limit 1");
                // sMax.setCallback(new SqlCallback() {
                // @Override
                // public Object invoke(Connection conn, ResultSet rs, Sql sql)
                // throws SQLException {
                // while (rs.next()) {
                // ele.setStorageMax(rs.getDouble(1));
                // }
                // return null;
                // }
                // });
                // dao.execute(sMax);
                if (ele.getStorageMax() > 0) {
                    Sql daysMax = Sqls.create("select count(distinct(impDate)) from t_storage where mcode = '"
                                              + ele.getMcode()
                                              + "' and total > "
                                              + ele.getStorageMax()
                                              * 0.7f);
                    daysMax.setCallback(new SqlCallback() {
                        @Override
                        public Object invoke(Connection conn, ResultSet rs, Sql sql)
                                throws SQLException {
                            while (rs.next()) {
                                ele.setStorageHighNum(rs.getLong(1));
                            }
                            return null;
                        }
                    });
                    dao.execute(daysMax);
                } else {
                    ele.setStorageHighNum(0);
                }
                dao.update(ele, "storageMax|storageHighNum");
            }
        });
        return Ajax.ok();
    }

    @At("/storageTotal/anlysisAll")
    public AjaxReturn anlysisStorageTotalAll(@Param("delete") boolean delete) {
        // dao.clear(StorageTotal.class, null);
        // 重新分析数据并插入
        Sql mcodeSql = Sqls.create("select distinct(mcode) from t_storage_inout");
        final List<String> mcodes = new ArrayList<String>();
        mcodeSql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                while (rs.next()) {
                    mcodes.add(rs.getString(1));
                }
                return null;
            }
        });
        dao.execute(mcodeSql);
        // 获得非重复mcode
        for (String mc : mcodes) {
            StorageTotal stObj = dao.fetch(StorageTotal.class, Cnd.where("mcode", "=", mc));
            if (stObj != null) {
                log.infof("exist material %s[%s] totalData", stObj.getMcode(), stObj.getMname());
                if (!delete) {
                    continue;
                } else {
                    dao.delete(stObj);
                }
            }
            Sql totalSql = Sqls.create("select mcode, mname, sum(inCount) as totalIn, sum(outCount) as totalOut from t_storage_inout where mcode = "
                                       + mc);
            totalSql.setCallback(new SqlCallback() {
                @Override
                public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                    while (rs.next()) {
                        final StorageTotal st = new StorageTotal();
                        st.setImpMonth("");
                        st.setMcode(rs.getString(1));
                        st.setMname(rs.getString(2));
                        st.setTotalIn(rs.getDouble(3));
                        st.setTotalOut(rs.getDouble(4));
                        // 计算数量
                        Sql inNumSql = Sqls.create("select count(inCount) from t_storage_inout where mcode = "
                                                   + st.getMcode()
                                                   + " and inCount != 0");
                        inNumSql.setCallback(new SqlCallback() {
                            @Override
                            public Object invoke(Connection conn, ResultSet rs, Sql sql)
                                    throws SQLException {
                                while (rs.next()) {
                                    st.setTotalInNum(rs.getLong(1));
                                }
                                return null;
                            }
                        });
                        dao.execute(inNumSql);

                        Sql outNumSql = Sqls.create("select count(outCount) from t_storage_inout where mcode = "
                                                    + st.getMcode()
                                                    + " and outCount != 0");
                        outNumSql.setCallback(new SqlCallback() {
                            @Override
                            public Object invoke(Connection conn, ResultSet rs, Sql sql)
                                    throws SQLException {
                                while (rs.next()) {
                                    st.setTotalOutNum(rs.getLong(1));
                                }
                                return null;
                            }
                        });
                        dao.execute(outNumSql);
                        // 插入数据
                        dao.insert(st);
                    }
                    return null;
                }
            });
            dao.execute(totalSql);
        }
        return Ajax.ok();
    }

    @At("/storageTotal/list")
    public QueryResult listStorageTotal(@Param("..") Query q, @Param("month") String month) {
        q.tableSet(dao, StorageTotal.class, null);
        q.cndSet(month);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where().andLike("mcode", kwd, false);
                    sc.where().orLike("mname", kwd, false);
                }
                if (otherQCnd != null && otherQCnd.length == 1) {
                    sc.where().andEquals("impMonth", otherQCnd[0]);
                }
            }
        }, q);
        return qr;
    }

    @At("/storageInOut/list")
    public QueryResult listStorageInOut(@Param("..") Query q,
                                        @Param("startDate") String stDate,
                                        @Param("endDate") String endDate) {
        q.tableSet(dao, StorageInOut.class, null);
        q.cndSet(stDate, endDate);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where().andLike("mcode", kwd, false);
                    sc.where().orLike("mname", kwd, false);
                }
                if (otherQCnd != null && otherQCnd.length == 2) {
                    sc.where().and("impDate", ">=", otherQCnd[0]);
                    sc.where().and("impDate", "<=", otherQCnd[1]);
                }
            }
        }, q);
        return qr;
    }
}
