package org.cc.tml;

import java.io.File;

/**
 *
 * @author william
 */
public class TML {

    public static File toFile(String base, String prefix, String id, String suffix) {
        String path = base + "/" + prefix;
        path = path.replaceAll("[\\|.]", "/");
        File pf = new File(path);
        if(!pf.exists()){
            pf.mkdirs();
        }
        return new File(path, id + "." + suffix);
    }
    
}
