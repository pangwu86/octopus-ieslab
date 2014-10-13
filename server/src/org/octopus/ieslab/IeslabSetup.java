package org.octopus.ieslab;

import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.octopus.core.fs.FsModule;

public class IeslabSetup implements Setup {

    @Override
    public void init(NutConfig nc) {
        FsModule.regModule("ies-contract");
    }

    @Override
    public void destroy(NutConfig nc) {
        // TODO Auto-generated method stub

    }

}
