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

package org.unidata.mdm.data.type.storage;

/**
 * @author Mikhail Mikhailov
 * Connection pool tweakable properties.
 */
public enum PoolSetting {

    POOL_CLASS_NAME("className", "org.postgresql.xa.PGXADataSource"),
    POOL_UNIQUE_NAME("uniqueName", null), // <- Must be given. Can contain wildcards. Will be post processed.
    POOL_MIN_POOL_SIZE("minPoolSize", "10"),
    POOL_MAX_POOL_SIZE("maxPoolSize", "30"),
    POOL_MAX_IDLE_TIME("maxIdleTime", "60"),
    POOL_MIN_LIFE_TIME("maxLifeTime", "0"),
    POOL_AUTOMATIC_ENLISTING_ENABLED("automaticEnlistingEnabled", Boolean.TRUE.toString()),
    POOL_USE_TM_JOIN("useTmJoin", Boolean.TRUE.toString()),
    POOL_ACQUIRE_INCREMENT("acquireIncrement", "1"),
    POOL_ACQUISITION_TIMEOUT("acquisitionTimeout", "30"),
    POOL_ACQUISITION_INTERVAL("acquisitionInterval", "1"),
    POOL_DEFER_CONNECTION_RELEASE("deferConnectionRelease", Boolean.TRUE.toString()),
    POOL_ALLOW_LOCAL_TRANSACTIONS("allowLocalTransactions", Boolean.TRUE.toString()),
    POOL_TWO_PC_ORDERING_POSITION("twoPcOrderingPosition", "1"),
    POOL_APPLY_TRANSACTION_TIMEOUT("applyTransactionTimeout", Boolean.FALSE.toString()),
    POOL_SHARE_TRANSACTION_CONNECTIONS("shareTransactionConnections", Boolean.FALSE.toString()),
    POOL_DISABLED("disabled", Boolean.FALSE.toString()),
    POOL_IGNORE_RECOVERY_FAILURES("ignoreRecoveryFailures", Boolean.FALSE.toString()),
    POOL_CONNECTION_TEST_QUERY("testQuery", "select 1"),
    POOL_CONNECTION_TEST_TIMEOUT("connectionTestTimeout", "1"),
    POOL_ENABLE_JDBC4_CONNECTION_TEST("enableJdbc4ConnectionTest", Boolean.TRUE.toString()),
    POOL_PREPARED_STATEMENT_CACHE_SIZE("preparedStatementCacheSize", "0"),
    POOL_ISOLATION_LEVEL("isolationLevel", null), // READ_COMMITED
    POOL_CURSOR_HOLDABILITY("cursorHoldability", null),
    POOL_LOCAL_AUTO_COMMIT("localAutoCommit", null),
    POOL_JMX_NAME("jmxName", null),
    // Additional driver specific properties in url format, which will be the part of the url.
    POOL_DRIVER_PROPERTIES("driverProperties", null);

    private PoolSetting(String name, String value) {
        this.propName = name;
        this.defaultValue = value;
    }
    /**
     * The property name.
     */
    private final String propName;
    /**
     * The default value.
     */
    private final String defaultValue;
    /**
     * @return the propName
     */
    public String getPropName() {
        return propName;
    }
    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }
}
