package org.cc.json;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.Callable;
import org.cc.log.CCLogger;

public class CCCacheFileItem implements ICCCacheItem<JSONObject>, Callable<ICCCacheItem<JSONObject>> {

    private JSONObject data;
    private final String id;
    private long lastModified;

    public CCCacheFileItem(String id) {
        this.id = id;
        File f = new File(id);
        if (f.exists()) {
            this.lastModified = f.lastModified();
        }
    }

    @Override
    public long lastModified() {
        return this.lastModified;
    }

    @Override
    public JSONObject load() throws Exception {
        File f = new File(id);
        if (f.exists()) {
            if (data == null) {
                CCLogger.debug("Load file : " + f.getAbsolutePath());
                data = loadJSON(f);
            } else if (f.lastModified() > this.lastModified) {
                CCLogger.debug("Reload file : " + f.getAbsolutePath());
                data = loadJSON(f);
            } 
        } else {
            CCLogger.debug("Can't find file : " + f.getAbsolutePath());
        }
        return data;
    }

    @Override
    public void unload() {
        data = null;
    }

    private JSONObject loadJSON(File f) throws Exception {
        Reader reader = new InputStreamReader(new FileInputStream(f), "UTF-8");
        try {
            this.lastModified = f.lastModified();
            JSONTokener tk = new JSONTokener(reader);
            return new JSONObject(tk);
        } finally {
            reader.close();
        }
    }

    @Override
    public ICCCacheItem call() throws Exception {
        return this;
    }

}
