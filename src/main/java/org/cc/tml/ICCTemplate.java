package org.cc.tml;

import java.io.InputStream;
import org.cc.ICCMap;

/**
 *
 * @author william
 */
public interface ICCTemplate {

    public void processTemplate(InputStream is, ICCMap m);
    
}
