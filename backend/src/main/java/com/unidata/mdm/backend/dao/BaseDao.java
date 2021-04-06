package com.unidata.mdm.backend.dao;

import java.sql.Connection;

/**
 * Dao which contain common request to DB
 */
public interface BaseDao {
    /**
     * Gets entity name by etalon id.
     *
     * @param etalonId the etalon id
     * @return name
     */
    String getEntityNameByEtalonId(String etalonId);

    /**
     * Gets entity name by etalon id.
     *
     * @param originId the etalon id
     * @return name
     */
    String getEntityNameByOriginId(String originId);

    /**
     * Gets entity name by etalon id.
     *
     * @param relationEtalonId the etalon id
     * @return name
     */
    String getEntityNameByRelationFromEtalonId(String relationEtalonId);

    /**
     * Gets entity name by etalon id.
     *
     * @param relationOriginId the etalon id
     * @return name
     */
    String getEntityNameByRelationFromOriginId(String relationOriginId);
    /**
     * Gets the bare connection from the data source.
     * @return connection
     */
    Connection getBareConnection();
}
