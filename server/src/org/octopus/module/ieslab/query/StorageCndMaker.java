package org.octopus.module.ieslab.query;

import org.nutz.dao.util.cri.SimpleCriteria;
import org.nutz.lang.Strings;
import org.nutz.web.query.CndMaker;

public class StorageCndMaker extends CndMaker {

    @Override
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

}
