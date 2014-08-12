package org.octopus.module.ieslab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.comet.Comet;
import org.octopus.bean.core.User;
import org.octopus.bean.ieslab.Material;
import org.octopus.bean.ieslab.Storage;
import org.octopus.bean.ieslab.StorageInOut;
import org.octopus.module.AbstractBaseModule;
import org.woods.json4excel.J4E;
import org.woods.json4excel.J4EConf;
import org.woods.json4excel.J4EEachRow;

@At("/ieslab/import")
@Ok("ajax")
@Fail("ajax")
@IocBean(create = "init")
public class ImpExpModule extends AbstractBaseModule {

    private Log log = Logs.get();

    public void init() {
        IesLab.InitMaterialMap(dao);
    }

    public Set<String> esApplySet = new HashSet<String>();

    @At("/material")
    @Ok("void")
    public void importMaterial(@Param("path") String mpath,
                               HttpServletResponse resp,
                               HttpSession session) {
        User me = ME(session);
        String key = me.getName() + mpath;
        synchronized (esApplySet) {
            if (esApplySet.contains(key)) {
                log.warn("same es-request from : " + key);
                return;
            }
            esApplySet.add(key);
        }
        File impFile = new File(mpath);
        J4EConf j4eConf = J4EConf.from(ImpExpModule.class.getResourceAsStream("/j4e/ieslab/material.js"));
        long count = 0;
        try {
            List<Material> mlist = J4E.fromExcel(new FileInputStream(impFile),
                                                 Material.class,
                                                 j4eConf);
            log.infof("load excelFile[%s], find %d material-row", impFile.getName(), mlist.size());
            Comet.replyByES(resp, "加载Excel文件 : " + impFile.getName());
            Comet.replyByES(resp, "发现数据共 " + mlist.size() + " 条");
            for (Material ma : mlist) {
                try {
                    dao.insert(ma);
                    count++;
                    Comet.replyByES(resp, "导入 : " + ma.getName());
                }
                catch (Exception e) {
                    log.error(e);
                    log.errorf("error material : \n%s", Json.toJson(ma));
                }
            }
            log.infof("import material, success [%d] and fail [%d]", count, mlist.size() - count);
        }
        catch (FileNotFoundException e) {
            log.error(e);
        }
        IesLab.InitMaterialMap(dao);
        Comet.replyByES(resp, "导入完毕, 成功导入" + count + "条数据.");
        // esApplySet.remove(key);
    }

    @At("/storage")
    @Ok("void")
    public void importStorage(@Param("path") String spath,
                              HttpServletResponse resp,
                              HttpSession session) {
        User me = ME(session);
        String key = me.getName() + spath;
        synchronized (esApplySet) {
            if (esApplySet.contains(key)) {
                log.warn("same es-request from : " + key);
                return;
            }
            esApplySet.add(key);
        }
        File storageDir = new File(spath);
        File[] storageFiles = storageDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xls");
            }
        });
        log.infof("storage dir : %s\nfind %d excels", storageDir, storageFiles.length);
        Comet.replyByES(resp, "在目录" + storageDir + "中, 发现Excel文件" + storageFiles.length + "个");
        for (File sf : storageFiles) {
            log.infof(" %s", sf.getName());
            Comet.replyByES(resp, " **** " + sf.getName());
        }
        J4EConf j4eConfForStorage = J4EConf.from(ImpExpModule.class.getResourceAsStream("/j4e/ieslab/storage.js"));
        J4EConf j4eConfForStorageInOut = J4EConf.from(ImpExpModule.class.getResourceAsStream("/j4e/ieslab/storageInOut.js"));
        for (File sf : storageFiles) {
            log.infof(" import storageFile : %s", sf.getName());
            Comet.replyByES(resp, "开始导入文件 : " + sf.getName());
            final String impDate = sf.getName().substring(0, 10); // yyyy.MM.dd
            j4eConfForStorage.setEachPrepare(new J4EEachRow<Storage>() {
                @Override
                public void doEach(Storage t) {
                    if (Strings.isBlank(t.getImpDate())) {
                        t.setImpDate(impDate);
                    }
                    if (Strings.isBlank(t.getMname())) {
                        t.setMname(IesLab.getMaterialName(t.getMcode()));
                    }
                }
            });
            j4eConfForStorageInOut.setEachPrepare(new J4EEachRow<StorageInOut>() {
                @Override
                public void doEach(StorageInOut t) {
                    if (Strings.isBlank(t.getImpDate())) {
                        t.setImpDate(impDate);
                    }
                    if (Strings.isBlank(t.getMname())) {
                        t.setMname(IesLab.getMaterialName(t.getMcode()));
                    }
                }
            });

            // 判断一下是导入, 库存
            int icount = dao.count(Storage.class, Cnd.where("impDate", "=", impDate));
            if (icount <= 0) {
                try {
                    List<Storage> slist = J4E.fromExcel(new FileInputStream(sf),
                                                        Storage.class,
                                                        j4eConfForStorage);
                    dao.insert(slist);
                    Comet.replyByES(resp, "库存信息 : " + impDate + "成功导入" + impDate + "条数据");
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                log.warnf("%s has imported %d storage-rows", impDate, icount);
                Comet.replyByES(resp, "库存信息 : " + impDate + "的数据已经存在, 共" + impDate + "条");
            }

            // 判断是否导入过, 出入库
            int iocount = dao.count(StorageInOut.class, Cnd.where("impDate", "=", impDate));
            if (iocount <= 0) {
                try {
                    List<StorageInOut> slist = J4E.fromExcel(new FileInputStream(sf),
                                                             StorageInOut.class,
                                                             j4eConfForStorageInOut);
                    dao.insert(slist);
                    Comet.replyByES(resp, "出入库信息 : " + impDate + "成功导入" + impDate + "条数据");
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                log.warnf("%s has imported %d storageInout-rows", impDate, iocount);
                Comet.replyByES(resp, "出入库信息 : " + impDate + "的数据已经存在, 共" + impDate + "条");
            }
        }
        Comet.replyByES(resp, "导入完毕");
        // esApplySet.remove(key);
    }

    public static class ChkStorage2 {
        public ChkStorage2(Sheet sheet) {
            this.sheet = sheet;
            String snm = sheet.getSheetName();
            String[] snmArray = snm.split("\\.");
            if (snmArray.length != 3) {
                snmArray = snm.split("\\．");
            }
            if (snmArray.length != 3) {
                throw new RuntimeException("sheetName has Error, [" + snm + "]");
            }
            String stype = snmArray[2].substring(2);
            snmArray[2] = snmArray[2].substring(0, 2);
            this.impDate = snmArray[0] + "." + snmArray[1] + "." + snmArray[2];
            if (stype.indexOf("库存") != -1) {
                this.isStorage = true;
            }
            if (stype.indexOf("明细") != -1) {
                this.isStorageInout = true;
            }
        }

        public boolean isStorage;
        public boolean isStorageInout;
        public String impDate;
        public Sheet sheet;
    }

    @At("/storage2")
    @Ok("void")
    public void importStorage2(@Param("path") String spath,
                               final HttpServletResponse resp,
                               HttpSession session) {
        User me = ME(session);
        String key = me.getName() + spath;
        synchronized (esApplySet) {
            if (esApplySet.contains(key)) {
                log.warn("same es-request from : " + key);
                return;
            }
            esApplySet.add(key);
        }
        File storageDir = new File(spath);
        File[] storageFiles = storageDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xls") || name.toLowerCase().endsWith(".xlsx");
            }
        });
        log.infof("storage dir : %s\nfind %d excels", storageDir, storageFiles.length);
        Comet.replyByES(resp, "在目录" + storageDir + "中, 发现Excel文件" + storageFiles.length + "个");
        for (File sf : storageFiles) {
            log.infof(" %s", sf.getName());
            Comet.replyByES(resp, " **** " + sf.getName());
        }
        J4EConf j4eConfForStorage = J4EConf.from(ImpExpModule.class.getResourceAsStream("/j4e/ieslab/storage2.js"));
        J4EConf j4eConfForStorageInOut = J4EConf.from(ImpExpModule.class.getResourceAsStream("/j4e/ieslab/storageInOut.js"));
        for (File sf : storageFiles) {
            log.infof(" import storageFile : %s", sf.getName());
            Comet.replyByES(resp, "开始导入文件 : " + sf.getName());
            Workbook wb = null;
            try {
                wb = J4E.loadExcel(new FileInputStream(sf));
            }
            catch (FileNotFoundException e1) {
                e1.printStackTrace();
                continue;
            }
            int sheetNum = wb.getNumberOfSheets();
            for (int i = 0; i < sheetNum; i++) {
                Sheet beChksheet = wb.getSheetAt(i);
                final ChkStorage2 cs = new ChkStorage2(beChksheet);
                log.infof("check Sheet %s", Json.toJson(cs));
                j4eConfForStorage.setEachPrepare(new J4EEachRow<Storage>() {
                    @Override
                    public void doEach(Storage t) {
                        if (Strings.isBlank(t.getImpDate())) {
                            t.setImpDate(cs.impDate);
                        }
                        if (Strings.isBlank(t.getMname())) {
                            t.setMname(IesLab.getMaterialName(t.getMcode()));
                        }
                        t.setMaintenance(0);
                        t.setInferior(0);
                        t.setMaterial(0);
                        t.setRepair(0);
                        t.setI6(0);

                        dao.insert(t);

                        Comet.replyByES(resp,
                                        String.format(" $$ 在%s, 物品%s, 库存为 %f",
                                                      t.getImpDate(),
                                                      t.getMname(),
                                                      t.getTotal()));
                    }
                });
                j4eConfForStorageInOut.setEachPrepare(new J4EEachRow<StorageInOut>() {
                    @Override
                    public void doEach(StorageInOut t) {
                        if (Strings.isBlank(t.getImpDate())) {
                            t.setImpDate(cs.impDate);
                        }
                        if (Strings.isBlank(t.getMname())) {
                            t.setMname(IesLab.getMaterialName(t.getMcode()));
                        }
                        Comet.replyByES(resp, String.format(" $$ 在%s, 物品%s, 入库 %f, 出库 %f",
                                                            t.getImpDate(),
                                                            t.getMname(),
                                                            t.getInCount(),
                                                            t.getOutCount()));
                        dao.insert(t);
                    }
                });

                if (cs.isStorage) {
                    // 判断一下是导入, 库存
                    int icount = dao.count(Storage.class, Cnd.where("impDate", "=", cs.impDate));
                    if (icount <= 0) {
                        try {
                            J4E.fromSheet(cs.sheet, Storage.class, j4eConfForStorage);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        log.warnf("%s has imported %d storage-rows", cs.impDate, icount);
                        Comet.replyByES(resp, "库存信息 : " + cs.impDate + "的数据已经存在, 共" + icount + "条");
                    }
                }

                if (cs.isStorageInout) {
                    // 判断是否导入过, 出入库
                    int iocount = dao.count(StorageInOut.class,
                                            Cnd.where("impDate", "=", cs.impDate));
                    if (iocount <= 0) {
                        try {
                            J4E.fromSheet(cs.sheet, StorageInOut.class, j4eConfForStorageInOut);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        log.warnf("%s has imported %d storageInout-rows", cs.impDate, iocount);
                        Comet.replyByES(resp, "出入库信息 : "
                                              + cs.impDate
                                              + "的数据已经存在, 共"
                                              + iocount
                                              + "条");
                    }
                }

            }
        }
        Comet.replyByES(resp, "导入完毕");
        // esApplySet.remove(key);
    }
}
