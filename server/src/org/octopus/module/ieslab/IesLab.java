package org.octopus.module.ieslab;

import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.Dao;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.LoopException;
import org.nutz.lang.Strings;
import org.octopus.bean.ieslab.Material;

public class IesLab {

    private static Map<String, String> MaterialMap = new HashMap<String, String>();

    public static void InitMaterialMap(Dao dao) {
        synchronized (MaterialMap) {
            MaterialMap.clear();
            dao.each(Material.class, null, new Each<Material>() {
                @Override
                public void invoke(int index, Material ele, int length) throws ExitLoop,
                        ContinueLoop, LoopException {
                    MaterialMap.put(ele.getMcode(), ele.getName());
                }
            });
        }
    }

    public static String getMaterialName(String mcode) {
        String mname = MaterialMap.get(mcode);
        if (Strings.isBlank(mname)) {
            mname = "未注册物料";
        }
        return mname;
    }
}
