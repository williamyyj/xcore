package org.cc.tml.node;

import java.io.File;
import java.util.Map;
import org.cc.CCMap;
import org.cc.model.CCProcObject;
import org.cc.ICCList;
import org.cc.ICCMap;
import org.cc.org.mvel2.templates.CompiledTemplate;
import org.cc.org.mvel2.templates.TemplateRuntime;
import org.cc.org.mvel2.templates.res.Node;
import org.cc.org.mvel2.templates.util.TemplateOutputStream;

import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;

/**
 * @author william
 */
public class CGBeanNode extends CGJavaNode {


    @Override
    public Object eval(TemplateRuntime runtime, TemplateOutputStream appender, Object ctx, VariableResolverFactory factory) {

        CCProcObject proc = getCCProcObject(ctx, factory);
        ICCMap m = new CCMap((Map) MVEL.eval(contents, ctx, factory));
        CompiledTemplate tml = getSubTemplate(ctx, factory, m.asString("tmlId"));
        ICCList list = (ICCList)factory.getVariableResolver("flds").getValue() ;
            
        list.forEach( o ->{
            appender.append(String.valueOf(TemplateRuntime.execute(tml,o,factory)));
        });
    
        return next != null ? next.eval(runtime, appender, ctx, factory) : null;
    }

    @Override
    public boolean demarcate(Node terminatingNode, char[] template) {
        return false;
    }
    
    

}
