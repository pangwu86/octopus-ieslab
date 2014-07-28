package org.octopus.module.ieslab;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.octopus.bean.ieslab.Material;
import org.octopus.bean.ieslab.Storage;
import org.octopus.bean.ieslab.StorageInOut;
import org.octopus.bean.ieslab.StorageTotal;
import org.octopus.module.AbstractBaseModule;
import org.octopus.module.ieslab.query.MaterialCndMaker;
import org.octopus.module.ieslab.query.StorageCndMaker;
import org.octopus.module.ieslab.query.StorageInOutCndMaker;
import org.octopus.module.ieslab.query.StorageTotalCndMaker;

@At("/ieslab/storage")
@Ok("ajax")
public class StorageModule extends AbstractBaseModule {

    @At("/material/list")
    public AjaxReturn listMaterial(@Param("kwd") String kwd,
                                   @Param("pgnm") int pgnm,
                                   @Param("pgsz") int pgsz,
                                   @Param("orderby") String orderby,
                                   @Param("asc") boolean asc) {
        return Ajax.ok().setData(new MaterialCndMaker().queryResult(dao,
                                                                    Material.class,
                                                                    pgnm,
                                                                    pgsz,
                                                                    orderby,
                                                                    asc,
                                                                    kwd));
    }

    @At("/storage/list")
    public AjaxReturn listStorage(@Param("kwd") String kwd,
                                  @Param("pgnm") int pgnm,
                                  @Param("pgsz") int pgsz,
                                  @Param("orderby") String orderby,
                                  @Param("asc") boolean asc,
                                  @Param("startDate") String stDate,
                                  @Param("endDate") String endDate) {
        return Ajax.ok().setData(new StorageCndMaker().queryResult(dao,
                                                                   Storage.class,
                                                                   pgnm,
                                                                   pgsz,
                                                                   orderby,
                                                                   asc,
                                                                   kwd,
                                                                   stDate,
                                                                   endDate));
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

    @At("/storageTotal/list")
    public AjaxReturn listStorageTotal(@Param("kwd") String kwd,
                                       @Param("pgnm") int pgnm,
                                       @Param("pgsz") int pgsz,
                                       @Param("orderby") String orderby,
                                       @Param("asc") boolean asc,
                                       @Param("month") final String month) {
        return Ajax.ok().setData(new StorageTotalCndMaker().queryResult(dao,
                                                                        StorageTotal.class,
                                                                        pgnm,
                                                                        pgsz,
                                                                        orderby,
                                                                        asc,
                                                                        kwd,
                                                                        month));
    }

    @At("/storageInOut/list")
    public AjaxReturn listStorageInOut(@Param("kwd") String kwd,
                                       @Param("pgnm") int pgnm,
                                       @Param("pgsz") int pgsz,
                                       @Param("orderby") String orderby,
                                       @Param("asc") boolean asc,
                                       @Param("startDate") String stDate,
                                       @Param("endDate") String endDate) {
        return Ajax.ok().setData(new StorageInOutCndMaker().queryResult(dao,
                                                                        StorageInOut.class,
                                                                        pgnm,
                                                                        pgsz,
                                                                        orderby,
                                                                        asc,
                                                                        kwd,
                                                                        stDate,
                                                                        endDate));
    }
}
