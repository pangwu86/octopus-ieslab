package org.octopus.ieslab.module;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.nutz.lang.random.R;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxReturn;
import org.octopus.core.module.AbstractBaseModule;
import org.octopus.ieslab.bean.TestObj;

@At("/ieslab/test")
@Ok("ajax")
public class TestObjModule extends AbstractBaseModule {

    @At("/hi")
    public Object hi() {
        List<String> sList = new ArrayList<String>();
        sList.add("haha");
        sList.add("test");
        return sList;
    }

    @At("/list")
    public Object queryTestObj() {
        return dao.query(TestObj.class, null);
    }

    @At("/add")
    public Object addTestObj(@Param("name") String name, @Param("age") int age) {
        TestObj tObj = new TestObj();
        tObj.setName(name);
        tObj.setAge(age);
        tObj.setCreateTime(new Date());
        dao.insert(tObj);
        return Ajax.ok();
    }

    @At("/fakedata")
    public AjaxReturn fakeData(@Param("num") int num) {
        for (int i = 0; i < num; i++) {
            TestObj tObj = new TestObj();
            tObj.setName("fake_" + num + "_" + R.UU64());
            tObj.setAge(R.random(1, 100));
            tObj.setCreateTime(new Date());
            dao.insert(tObj);
        }
        return Ajax.ok();
    }

}
