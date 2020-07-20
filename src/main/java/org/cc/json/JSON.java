package org.cc.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author william
 */
public class JSON {

    public static JSONObject loadFormText(String text)  {
        return loadFormText(new StringReader(text));
    }

    public static JSONObject loadFormText(Reader reader)  {
        try {
            JSONTokener tk = new JSONTokener(reader);
            return new JSONObject(tk);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

}
