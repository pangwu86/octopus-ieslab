package org.octopus.ieslab.sqlserver;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class TableCreater {

    private static Log log = Logs.get();

    public static Map<String, String> javaTypeMap = new HashMap<String, String>();

    static {
        javaTypeMap.put("varchar", "String");
        javaTypeMap.put("nvarchar", "String");
        javaTypeMap.put("int", "int");
        javaTypeMap.put("decimal", "double");
        javaTypeMap.put("datetime", "Date");
    }

    public static List<TableColumnDefine> checkTable(Dao dao, String tableName) {
        final List<TableColumnDefine> tcdList = new ArrayList<TableColumnDefine>();
        log.infof("Check Table : %s", tableName);
        Sql ctSql = Sqls.create(String.format("select top 1 * from %s", tableName));
        ctSql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                // 输出列名
                for (int i = 1; i <= columnCount; i++) {
                    TableColumnDefine tcd = new TableColumnDefine();
                    tcd.name = metaData.getColumnName(i);
                    tcd.dpSize = metaData.getColumnDisplaySize(i);
                    tcd.type = metaData.getColumnTypeName(i);
                    tcd.javaTp = javaTypeMap.get(tcd.type);
                    tcdList.add(tcd);
                    log.info(tcd);
                }
                return null;
            }
        });
        dao.execute(ctSql);
        return tcdList;
    }

    public static String javaCode(Dao dao, String tableName) {
        List<TableColumnDefine> stc = TableCreater.checkTable(dao, tableName);
        StringBuilder sb = new StringBuilder();
        for (TableColumnDefine cd : stc) {
            if (cd.javaTp.equals("String") && cd.dpSize > 50) {
                sb.append(String.format("@ColDefine(type = ColType.VARCHAR, width = %d)\n",
                                        cd.dpSize));
            }
            sb.append(String.format("private %s %s;\n", cd.javaTp, cd.name));
        }
        return sb.toString();
    }
}
