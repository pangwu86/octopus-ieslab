package org.octopus.ieslab.module;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.JsonFormat;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.LoopException;
import org.nutz.lang.Mirror;
import org.nutz.lang.Stopwatch;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.query.CndMaker;
import org.nutz.web.query.Query;
import org.nutz.web.query.QueryStr;
import org.octopus.core.module.AbstractBaseModule;
import org.octopus.ieslab.bean.SS_Table;
import org.octopus.ieslab.bean.SS_TrackProcess_V;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 负责查看与管理sqlserver数据的同步
 * 
 */
@At("/ieslab/sqlserver")
@Ok("ajax")
@Fail("ajax")
@IocBean(create = "init")
public class SqlServerDBModule extends AbstractBaseModule {

    private Log log = Logs.get();

    private Map<String, Class<?>> tableClzMap = new HashMap<String, Class<?>>();
    private Map<String, Dao> daoMap = new HashMap<String, Dao>();

    private String DB_URL = "172.20.10.17:1433";
    private String DB_USER = "supply";
    private String DB_PASSWORD = "sup@14";

    public void init() {
        // TODO 暂时先放在这里吧
        add("ProduceNew.TrackProcess_V", "到货统计相关");
        tableClzMap.put("ProduceNew.TrackProcess_V", SS_TrackProcess_V.class);
    }

    @At("/table/list")
    public QueryResult list(@Param("..") Query q) {
        q.tableSet(dao, SS_Table.class, null);
        QueryResult qr = CndMaker.queryResult(new QueryStr() {
            public void analysisQueryStr(SimpleCriteria sc, String kwd, String... otherQCnd) {
                if (!Strings.isBlank(kwd)) {
                    sc.where()
                      .andLike("remark", kwd, false)
                      .orLike("tableName", kwd, false)
                      .orLike("dbName", kwd, false);
                }
            }
        }, q);
        return qr;
    }

    @At("/table/add")
    public void add(@Param("dt") String dtName, @Param("remark") String remark) {
        String[] dbTable = dtName.split("\\.");
        String dbName = dbTable[0];
        String tableName = dbTable[1];
        SS_Table st = dao.fetch(SS_Table.class,
                                Cnd.where("dbName", "=", dbName).and("tableName", "=", tableName));
        if (st != null) {
            return;
        }
        st = new SS_Table();
        st.setDbName(dbName);
        st.setTableName(tableName);
        st.setRemark(remark);
        st.setStatus(0);
        st.setDnum(0);
        st.setInum(0);
        st.setErrnum(0);
        dao.insert(st);

        // 初始化dao
        if (!daoMap.containsKey(dtName)) {
            DruidDataSource dds = new DruidDataSource();
            dds.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            dds.setUrl("jdbc:sqlserver://" + DB_URL + ";DatabaseName=" + dbName);
            dds.setUsername(DB_USER);
            dds.setPassword(DB_PASSWORD);
            Dao ssDao = new NutDao(dds);
            daoMap.put(dtName, ssDao);
        }
    }

    private int getDBCount(Dao dao, String tableName) {
        Sql sql = Sqls.create(String.format("select count(*) from %s", tableName));
        sql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                int dnum = 0;
                while (rs.next()) {
                    dnum = rs.getInt(1);
                }
                return dnum;
            }
        });
        dao.execute(sql);
        return sql.getInt();
    }

    @At("/table/update")
    public Object update(@Param("dbName") String dbName, @Param("tableName") String tableName) {
        final SS_Table st = dao.fetch(SS_Table.class,
                                      Cnd.where("dbName", "=", dbName).and("tableName",
                                                                           "=",
                                                                           tableName));
        if (st != null) {
            if (st.getStatus() != 0) {
                return "正在更新或出错了";
            }
            String dtName = dbName + "." + tableName;
            final Dao ssDao = daoMap.get(dtName);
            if (ssDao == null) {
                return "Dao没有初始化, 无法执行后续操作";
            }
            final Class<?> tclz = tableClzMap.get(dtName);
            final Mirror<?> tmi = Mirror.me(tclz);
            // 开始更新
            st.setStatus(1);
            st.setDnum(getDBCount(ssDao, tableName));
            st.setInum(0);
            st.setUseTime(0);
            st.setErrnum(0);
            dao.update(st, "status|dnum|errnum|inum|useTime");
            dao.clear(tclz);
            // 后台更新(一般会非常耗时)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Stopwatch watch = Stopwatch.begin();
                    ssDao.each(st.getTableName(), null, new Each<Record>() {
                        @Override
                        public void invoke(int index, Record ele, int length) throws ExitLoop,
                                ContinueLoop, LoopException {
                            try {
                                Object tentity = tmi.born();
                                Set<String> fnms = ele.getColumnNames();
                                for (String fnm : fnms) {
                                    tmi.setValue(tentity, fnm, ele.get(fnm));
                                }
                                dao.insert(tentity);
                                // 计数
                                st.setInum(st.getInum() + 1);
                                if (st.getInum() % 2000 == 0) {
                                    dao.update(st, "inum|errnum");
                                }
                            }
                            catch (Exception e) {
                                log.error("SqlServer-Query Has Error", e);
                                log.errorf("SqlServer-Record : %s", ele.toJson(JsonFormat.nice()));
                                st.setErrnum(st.getErrnum() + 1);
                            }
                        }
                    });
                    // 更新
                    st.setStatus(0);
                    st.setUseTime(watch.stop());
                    st.setUpdateTime(new Date());
                    dao.update(st, "status|inum|errnum|useTime|updateTime");
                }
            }).start();
        }
        return null;
    }

}
