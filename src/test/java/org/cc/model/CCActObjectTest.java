/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.model;

import org.junit.Test;

/**
 *
 * @author william
 */
public class CCActObjectTest {

    @Test
    public void test_actor() {
        String base = System.getProperty("$base", "D:\\Users\\william\\Dropbox\\resources\\project\\stock");
        CCProcObject proc = new CCProcObject(base);
        try {
            CCActObject act = new CCActObject(proc,"twse");
        
        } catch (Exception e) {
            proc.release();
        }
    }
}
