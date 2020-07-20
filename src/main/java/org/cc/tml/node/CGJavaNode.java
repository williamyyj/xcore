package org.cc.tml.node;

import java.io.File;
import org.cc.model.CCProcObject;
import org.cc.org.mvel2.templates.CompiledTemplate;
import org.cc.org.mvel2.templates.TemplateCompiler;
import org.cc.org.mvel2.templates.TemplateRuntime;
import org.cc.org.mvel2.templates.res.Node;
import org.cc.org.mvel2.templates.util.TemplateOutputStream;
import org.mvel2.integration.VariableResolverFactory;

/**
 *
 * @author william
 */
public class CGJavaNode extends Node {

    @Override
    public Object eval(TemplateRuntime runtime, TemplateOutputStream appender, Object ctx, VariableResolverFactory factory) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean demarcate(Node terminatingNode, char[] template) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public CCProcObject getCCProcObject(Object ctx, VariableResolverFactory factory) {
        return (CCProcObject) factory.getVariableResolver("$proc").getValue();
    }

    public String getCGTemplatePath(Object ctx, VariableResolverFactory factory) {
        return (String) factory.getVariableResolver("$root").getValue() + "/$dp/mvel/java";
    }

    public CompiledTemplate getSubTemplate(Object ctx, VariableResolverFactory factory, String tmlId) {
        // 程式碼產生器先不考量效能
        File f = new File(getCGTemplatePath(ctx, factory), tmlId + ".java");
        System.out.println("===== check f "+f.exists());
         System.out.println("===== check f "+f);
        CompiledTemplate tml = TemplateCompiler.compileTemplate(f);
        return tml;
    }

    
    public Object runSubTemplate(CompiledTemplate tmpleate,Object ctx, VariableResolverFactory factory ){
        return TemplateRuntime.execute(tmpleate, ctx, factory);
    }
}
