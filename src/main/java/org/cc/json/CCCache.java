package org.cc.json;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.File;
import java.util.concurrent.TimeUnit;
import org.cc.log.CCLogger;

/**
 *
 * @author william
 */
public class CCCache {

    protected static LoadingCache<String, ICCCacheItem> _cache;

    public static LoadingCache<String, ICCCacheItem> cache() {
        if (_cache == null) {
            _cache = CacheBuilder.newBuilder()
              .maximumSize(1000) // 記憶體中最多保留 1000 筆資料
              .expireAfterAccess(30, TimeUnit.MINUTES)
              .build(new CacheLoader<String, ICCCacheItem>() {
                  @Override
                  public ICCCacheItem load(String k) throws Exception {
                      throw new RuntimeException("Using get(key, new  Callable<>{} ... ");
                  }
              });
        }
        return _cache;
    }

    public static JSONObject load(File f) {
        if (f.exists()) {
            try {
                String fname = f.getAbsolutePath();
                ICCCacheItem<JSONObject> item = cache().get(fname, new CCCacheFileItem(fname));
                return item.load();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        CCLogger.debug("Can't load " + f.getAbsolutePath() + " \r\n");
        return null;
    }

    public static JSONObject load(String base, String prefix, String id) {
        String fp = (prefix + "/" + id).replace(".", "/") + ".json";
       return load(new File(base,fp));       
    }

    public static JSONObject load(String base, String id) {
        return load(base, "", id);
    }
}
