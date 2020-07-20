package org.cc.json;

/**
 *
 * @author william
 * @param <E>
 */
public interface ICCCacheItem<E> {

    long lastModified();
    public E load() throws Exception ; 
    public void unload();

}
