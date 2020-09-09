/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
