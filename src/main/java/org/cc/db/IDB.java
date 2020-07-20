package org.cc.db;

import java.sql.Connection;
import java.sql.SQLException;
import org.cc.ICCMap;
import org.cc.ICCResource;
import org.cc.type.CCTypes;

/**
 * @author william
 * @param <M> 使用的model
 */
public interface IDB extends ICCResource {

    public Connection connection() throws SQLException;

    public String catalog();

    public String schema();

    public String database();

    public CCTypes types();

    public String to_alias(String text);

    public String to_short(String text);

    public String base();

    public void shutdown() throws Exception;

    public String status();

    public ICCMap cfg();
    

}
