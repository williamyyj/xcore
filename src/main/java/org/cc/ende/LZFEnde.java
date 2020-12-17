/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cc.ende;

import com.ning.compress.lzf.LZFDecoder;
import com.ning.compress.lzf.LZFEncoder;
import com.ning.compress.lzf.LZFException;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author William
 */
public class LZFEnde {

    public static String decoder(byte[] buf, String charsetName){
        byte[] data = new byte[0];
        try {
            data = LZFDecoder.safeDecode(buf);
            return new String(data,charsetName);
        } catch (LZFException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decoder(String text, String charsetName)  {
        return decoder(Base64.u64_decode(text),charsetName);
    }

    public static String decoder(String text)  {
        return decoder(Base64.u64_decode(text),"UTF-8");
    }


    public static String encoder(byte[] buf){
        byte[] data = LZFEncoder.encode(buf);
        return Base64.u64_encode(data);
    }

    public static String encoder(String text, String charsetName)  {
        try {
            return encoder(text.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String encoder(String text)  {
        return encoder(text,"UTF-8");
    }
    
}
