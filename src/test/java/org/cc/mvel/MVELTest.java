package org.cc.mvel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.cc.CCConst;
import org.cc.CCMap;
import org.cc.model.CCProcObject;
import org.cc.ICCMap;
import org.cc.data.CCData;
import org.cc.tml.node.CGBeanNode;
import org.cc.org.mvel2.templates.CompiledTemplate;
import org.cc.org.mvel2.templates.TemplateCompiler;
import org.cc.org.mvel2.templates.TemplateRuntime;
import org.cc.org.mvel2.templates.res.Node;
import org.cc.tml.TML;
import org.cc.util.CCFunc;
import org.junit.Test;
import org.mvel2.MVEL;

/**
 *
 * @author william
 */
public class MVELTest {

    public void test_evel() {
        ICCMap p = new CCMap();
        p.put("$pkg", "hyweb.jo.webpos");
        p.put("$src", "D:\\HHome\\java\\mbatis");
        MVEL.eval("pkg=$pkg+'.data'; suffix='DO'; tp= $src+'\\\\src\\\\main\\\\java'", p);
        System.out.println(p.toString(4));
    }

    @Test
    public void test_bean() throws Exception {
        Map<String, Class<? extends Node>> custNodes = new HashMap<String, Class<? extends Node>>();
        CCProcObject proc = new CCProcObject(CCConst.base("wpos"));
        CCMap varPool = new CCMap();
        varPool.put("$root", CCConst.root);
        varPool.put("$proc", proc);
        varPool.put("metaId", "psSprayPerson");
        ICCMap m = (ICCMap) CCFunc.apply2("cg.DPJavaBean", proc, varPool);

        custNodes.put("cg", CGBeanNode.class);
        File f = new File(CCConst.root + "/$dp/mvel/java", "bean.java");
       System.out.println(f.getAbsolutePath());
        CompiledTemplate tml = TemplateCompiler.compileTemplate(f, custNodes);
        System.out.println(new String(tml.getTemplate()));
        Object ret = TemplateRuntime.execute(tml, m);

        File tf = TML.toFile(m.asString("tp"), m.asString("pkg"), m.asString("classId"), "java");
        System.out.println(ret);
        CCData.saveText(ret.toString(),tf,"UTF-8");

    }

}
