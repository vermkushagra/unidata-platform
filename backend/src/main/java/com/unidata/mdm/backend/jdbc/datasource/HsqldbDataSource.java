package com.unidata.mdm.backend.jdbc.datasource;

import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Dmitry Kopin on 12.04.2017.
 */
public class HsqldbDataSource extends BasicDataSource {
    @Override
    public synchronized void close() throws SQLException {
        Connection conn = getConnection();
        Statement statement = conn.createStatement();
        statement.executeUpdate("SHUTDOWN");
        statement.close();
        conn.close();

        super.close();
    }
}
