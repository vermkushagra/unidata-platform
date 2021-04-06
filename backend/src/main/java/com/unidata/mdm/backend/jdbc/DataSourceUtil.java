package com.unidata.mdm.backend.jdbc;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DriverManagerDataSource;

public class DataSourceUtil {

    /**
     * Initializes pooled datasource.
     *
     * @param url the URL
     * @return data source
     */
    public static DataSource initPooledDataSource(@Nonnull String url) {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDescription("UniData pooled [foreign] DS to (" + url + ")");
        comboPooledDataSource.setJdbcUrl(url);
        comboPooledDataSource.setMaxStatements(180);
        comboPooledDataSource.setMinPoolSize(1);
        comboPooledDataSource.setAcquireIncrement(2);
        comboPooledDataSource.setMaxPoolSize(50);
        comboPooledDataSource.setPreferredTestQuery("select 1");
        return comboPooledDataSource;
    }

    /**
     * Initializes pooled datasource.
     *
     * @param url the URL
     * @return data source
     */
    public static DataSource initPooledDataSource(@Nonnull String url, int maxConnections) {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDescription("UniData pooled [foreign] DS to (" + url + ")");
        comboPooledDataSource.setJdbcUrl(url);
        comboPooledDataSource.setMaxStatements(180);
        comboPooledDataSource.setMinPoolSize(1);
        comboPooledDataSource.setAcquireIncrement(2);
        comboPooledDataSource.setMaxPoolSize(maxConnections);
        comboPooledDataSource.setPreferredTestQuery("select 1");
        return comboPooledDataSource;
    }

    /**
     * Initializes single datasource.
     *
     * @param url the URL
     * @return data source
     */
    public static DataSource initSingleDataSource(@Nonnull String url) {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDescription("UniData single connection [foreign] DS to (" + url + ")");
        driverManagerDataSource.setJdbcUrl(url);
        return driverManagerDataSource;
    }
}
