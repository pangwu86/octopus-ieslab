package org.octopus.module.ieslab.query;

import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Strings;
import org.nutz.web.query.CndMaker;

public class MaterialCndMaker extends CndMaker {

    @Override
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

}
