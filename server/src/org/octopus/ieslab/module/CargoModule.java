package org.octopus.ieslab.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.nutz.dao.Cnd;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.octopus.core.module.AbstractBaseModule;
import org.octopus.ieslab.bean.CargoInfo;
import org.woods.json4excel.J4EColumnType;

@At("/ieslab/cargo")
@Ok("ajax")
@Fail("ajax")
public class CargoModule extends AbstractBaseModule {

    private Log log = Logs.get();

    // ==================================== 到货统计

    private String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    // 全局锁
    public Object analysisLock = new Object();
    public boolean analysisArriveCargo = false;
    public String analysisFileName = null;
    private File tmpDir = new File(System.getProperty("java.io.tmpdir"));
    private File tmpFile = null;
    private String headStr = null;

    @At("/analysis/doing")
    public Object isAnalysising() {
        synchronized (analysisLock) {
            return analysisArriveCargo;
        }
    }

    @At("/analysis/upload")
    public void uploadArriveCargoFile(HttpServletRequest req) {
        synchronized (analysisLock) {
            analysisArriveCargo = true;
        }
        analysisFileName = urlDecode(req.getHeader("fnm"));
        tmpFile = new File(tmpDir, System.currentTimeMillis() + ".xls");
        try {
            Files.write(tmpFile, req.getInputStream());
        }
        catch (IOException e) {
            log.error(e);
        }
    }

    @At("/analysis/todb")
    @Ok("void")
    public void analysisArriveCargoFile() {
        dao.clear(CargoInfo.class);
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(new FileInputStream(tmpFile));
        }
        catch (Exception e3) {
            log.error("can't load inputstream for a workbook", e3);
        }
        Sheet sheet = wb.getSheetAt(0);
        Iterator<Row> rlist = sheet.rowIterator();
        boolean firstRow = true;
        int index_htNo = 0;
        int index_qgd = 0;
        int index_qgdNo = 0;
        int col_max = 0;
        int count = 0;
        while (rlist.hasNext()) {
            StringBuilder lc = new StringBuilder();
            Row row = rlist.next();
            // 第一行
            if (firstRow) {
                Iterator<Cell> clist = row.cellIterator();
                // 分析三个重要的列的index
                int index_head = 0;
                while (clist.hasNext()) {
                    Cell cell = clist.next();
                    String cval = cellValue(cell);
                    lc.append(cval).append(",");
                    if ("请购单".equals(cval)) {
                        index_qgd = index_head;
                    } else if ("请购单行号".equals(cval)) {
                        index_qgdNo = index_head;
                    } else if ("合同号".equals(cval)) {
                        index_htNo = index_head;
                    }
                    index_head++;
                    col_max++;
                }
                // 最后加上两天假数据
                lc.append("到货日,到货数量");
                // 插入
                headStr = lc.toString();
                firstRow = false;
                continue;
            } else {
                count++;
                StringBuilder repTxt = new StringBuilder();
                repTxt.append(count).append(". ");
                CargoInfo ci = new CargoInfo();
                int index_head = 0;
                for (int cn = 0; cn < col_max; cn++) {
                    Cell cell = row.getCell(cn);
                    String cval = cellValue(cell);
                    lc.append(cval).append(",");
                    if (index_qgd == index_head) {
                        ci.setQgd(cval);
                        repTxt.append(cval).append("_");
                    } else if (index_qgdNo == index_head) {
                        ci.setQgdNo(cval);
                        repTxt.append(cval).append("_");
                    } else if (index_htNo == index_head) {
                        ci.setHtNo(cval);
                        repTxt.append(cval).append("_");
                    }
                    index_head++;
                }
                ci.setHasHtNo(!Strings.isBlank(ci.getHtNo()));
                ci.setLineContent(lc.substring(0, lc.length() - 1));
                dao.insert(ci);
            }
        }
    }

    private final static String CSV_HEAD = new String(new byte[]{(byte) 0xEF,
                                                                 (byte) 0xBB,
                                                                 (byte) 0xBF});

    // private void appendWrite(File af, String str) {
    // try {
    // Files.appendWrite(af, (str +
    // "\r\n").toString().getBytes(Charsets.UTF_8.toString()));
    // }
    // catch (UnsupportedEncodingException e) {
    // e.printStackTrace();
    // }
    // }

    @At("/analysis/download")
    @Ok("void")
    public void downloadAnalysisArriveCargo(HttpServletResponse resp) {
        final StringBuilder sb = new StringBuilder();
        final File anFile = new File(tmpDir, System.currentTimeMillis() + ".csv");
        Files.createFileIfNoExists(anFile);
        sb.append(headStr).append("\r\n");
        // 查找数据,并添加到文件当中
        dao.each(CargoInfo.class, Cnd.where("hasHtNo", "=", true).asc("id"), new Each<CargoInfo>() {
            @Override
            public void invoke(int index, CargoInfo ele, int length) throws ExitLoop, ContinueLoop,
                    LoopException {
                // TODO 根据 qqd 跟 qqdNo 查询sqlserver数据库
                sb.append(String.format("%s,%s,%s", ele.getLineContent(), "yyyy-MM-dd", "0"))
                  .append("\r\n");
            }
        });
        dao.each(CargoInfo.class,
                 Cnd.where("hasHtNo", "=", false).asc("id"),
                 new Each<CargoInfo>() {
                     @Override
                     public void invoke(int index, CargoInfo ele, int length) throws ExitLoop,
                             ContinueLoop, LoopException {
                         // TODO 根据 qqd 跟 qqdNo 查询sqlserver数据库
                         sb.append(String.format("%s,%s,%s", ele.getLineContent(), "unknow", "0"))
                           .append("\r\n");
                     }
                 });
        synchronized (analysisLock) {
            analysisFileName = null;
            tmpFile = null;
            headStr = null;
            analysisArriveCargo = false;
        }
        try {
            String encode = new String(("到货统计分析.csv").getBytes("UTF-8"), "ISO8859-1");
            resp.setHeader("Content-Disposition", "attachment; filename=" + encode);
            resp.setHeader("Content-Type", "application/vnd.ms-excel");
            OutputStreamWriter osw = new OutputStreamWriter(resp.getOutputStream(), "UTF-8");
            osw.write(CSV_HEAD);
            osw.write(sb.toString());
            osw.flush();
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
        catch (IOException e) {
            throw Lang.wrapThrow(e);
        }
    }

    private String cellValue(Cell c) {
        String cval = _cellValue(c);
        if ("NULL".equalsIgnoreCase(cval) || "".equals(cval)) {
            return " ";
        }
        cval = cval.replaceAll(",", " ");
        cval = cval.replaceAll("，", " ");
        return cval;
    }

    private String _cellValue(Cell c) {
        if (c == null) {
            return "";
        }
        J4EColumnType colType = J4EColumnType.STRING;
        try {
            int cType = c.getCellType();
            switch (cType) {
            case Cell.CELL_TYPE_NUMERIC: // 数字
                if (J4EColumnType.STRING == colType) {
                    // 按照整形来拿, 防止2B的科学计数法
                    DecimalFormat df = new DecimalFormat("0");
                    return df.format(c.getNumericCellValue());
                } else if (J4EColumnType.NUMERIC == colType) {
                    // 按照double数字拿
                    DecimalFormat df = new DecimalFormat("0.00");
                    return df.format(c.getNumericCellValue());
                } else {
                    throw new RuntimeException("WTF, CELL_TYPE_NUMERIC is what!");
                }
                // 按照字符拿
            case Cell.CELL_TYPE_STRING: // 字符串
                return c.getStringCellValue();
            case Cell.CELL_TYPE_BOOLEAN: // boolean
                return String.valueOf(c.getBooleanCellValue());
            case Cell.CELL_TYPE_FORMULA:
                return String.valueOf(c.getNumericCellValue());
            default:
                return c.getStringCellValue();
            }
        }
        catch (Exception e) {
            log.error(String.format("cell [%d, %d] has error, value can't convert to string",
                                    c.getRowIndex(),
                                    c.getColumnIndex()),
                      e);
            return "";
        }
    }
}
