package org.cc.tml;

import java.util.HashMap;
import java.util.Map;
import org.cc.org.mvel2.templates.res.Node;

/**
 * @author william
 */
public class CGTemplate extends CCTemplate {

    protected static Map<String, Class<? extends Node>> custNodes = new HashMap<String, Class<? extends Node>>();
    
    private String root ; 
    
    private String prjId ; 

    public CGTemplate(String root, String prjId) {
        __init__(root,prjId);
    }

    private void __init__(String root, String prjId) {
        this.root = root ;
        this.prjId = prjId;
    }
    


}
