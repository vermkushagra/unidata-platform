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

package com.unidata.mdm.backend.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.sql.DataSource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.dao.DataImportDao;
import com.unidata.mdm.backend.service.data.xlsximport.XLSXHeader;
import com.unidata.mdm.backend.util.CryptUtils;
import com.unidata.mdm.meta.SimpleDataType;

/**
 * The Class DataImportDaoImpl.
 */
@Repository
public class DataImportDaoImpl extends AbstractDaoImpl implements DataImportDao {

    /**
     * Instantiates a new data import dao impl.
     *
     * @param dataSource
     *            the data source
     */
    @Autowired
    public DataImportDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.DataImportDao#saveData(java.util.List,
     * java.lang.String, long, java.lang.String)
     */
    @Override
    public void saveData(
            List<Map<String, Object>> toSave,
            List<XLSXHeader> headers,
            String entityName,
            String sourceSystem,
            String modelVersion,
            String fileName,
            String fileCreationDate
    ) {
        try {
            String etalonId = CryptUtils.toMurmurString("ID");
            String externalId = CryptUtils.toMurmurString("EXTERNAL_ID");
            //TODO: rewrite this query builder next time when it require modification. It's not optimal and hard to maintain.

            @SuppressWarnings("unchecked")
            Map<String, Object>[] params = toSave.toArray(new Map[0]);
            String sb = "INSERT INTO " + IMPORT_SCHEMA + constructTableName(entityName, modelVersion, fileName, fileCreationDate) +
                    "( " + toInsert(headers).stream().collect(Collectors.joining(", _", "_", "")) + ") values(" +
                    ":" + etalonId + "," +
                    "(select coalesce(" +
                    "    (select o.external_id from origins o where o.source_system = '" + sourceSystem + "'" +
                    "     and o.external_id = :" + externalId + " and o.name = '" + entityName + "' and o.is_enrichment = false)," +
                    "    (select o.external_id from origins o where o.source_system = '" + sourceSystem + "'" +
                    "     and o.etalon_id =  CASE WHEN :" + etalonId + "=NULL THEN NULL ELSE :" + etalonId + "::uuid END and o.name = '" + entityName +
                    "' and o.is_enrichment = false)," + "    :" + externalId + ")" + ")," +
                    toInsert(headers).stream().skip(2).collect(Collectors.joining(", :", ":", "")) +
                    ")";
            this.namedJdbcTemplate.batchUpdate(sb, params);
        } catch (DuplicateKeyException exception) {
            throw new DataProcessingException("Duplicated rows are detected",
                    ExceptionId.EX_DATA_XLSX_IMPORT_DUPLICATED_KEYS);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @sees
     * com.unidata.mdm.backend.dao.DataImportDao#createImportTable(java.util.
     * List, java.lang.String, long, java.lang.String)
     */
    @Override
    public void createImportTable(List<XLSXHeader> headers, String entityName, String modelVersion, String fileName,
                                  String fileCreationDate) {
        String createStatement = createTable(headers, entityName, modelVersion, fileName, fileCreationDate);
        this.jdbcTemplate.execute(createStatement);
        for (String indexStatement : createIndexes(headers, entityName, modelVersion, fileName, fileCreationDate)) {
            this.jdbcTemplate.execute(indexStatement);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.dao.DataImportDao#dropImportTable(java.lang.
     * String, long, java.lang.String)
     */
    @Override
    public void dropImportTable(String entityName, String modelVersion, String fileName, String fileCreationDate) {
        StringBuilder sb
                = new StringBuilder("DROP TABLE IF EXISTS ")
                .append(IMPORT_SCHEMA)
                .append(constructTableName(entityName, modelVersion, fileName, fileCreationDate))
                .append(" CASCADE");
        this.jdbcTemplate.update(sb.toString());

    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.DataImportDao#dropImportTable(java.lang.String)
     */
    @Override
    public void dropImportTable(String tableName) {
        StringBuilder sb
                = new StringBuilder("DROP TABLE IF EXISTS ")
                .append(IMPORT_SCHEMA)
                .append(tableName)
                .append(" CASCADE");
        this.jdbcTemplate.update(sb.toString());

    }
    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.dao.DataImportDao#purgeImportTable(java.lang.
     * String, long, java.lang.String)
     */
    @Override
    public void purgeImportTable(String entityName, String modelVersion, String fileName, String fileCreationDate) {
        StringBuilder sb
                = new StringBuilder("TRUNCATE TABLE ")
                .append(IMPORT_SCHEMA)
                .append(constructTableName(entityName, modelVersion, fileName, fileCreationDate))
                .append(" RESTART IDENTITY CASCADE");
        this.jdbcTemplate
                .update(sb.toString());
    }

    /**
     * Construct table name.
     *
     * @param entityName
     *            the entity name
     * @param modelVersion
     *            the model version
     * @param fileName
     *            the file name
     * @param fileCreationDate
     *            the file creation date
     * @return the string
     */
    @Override
    public String constructTableName(String entityName, String modelVersion, String fileName, String fileCreationDate) {
        return CryptUtils.toMurmurString(String.join("_", entityName, modelVersion, fileName, fileCreationDate));
    }

    /**
     * To create.
     *
     * @param headers
     *            the headers
     * @return the list
     */
    private static List<String> toCreate(List<XLSXHeader> headers) {
        List<String> result = new ArrayList<>();
        for (XLSXHeader xlsxHeader : headers) {
            String dbColumn = String.join(" ",
                    "_" + CryptUtils.toMurmurString(xlsxHeader.getSystemHeader()),
                    Objects.nonNull(xlsxHeader.getAttributeHolder()) && xlsxHeader.getAttributeHolder().isArray()
                            ? "text"
                            : typeToSQL(xlsxHeader.getTypeHeader()));
            result.add(dbColumn);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.dao.DataImportDao#getConnectionURL()
     */
    @Override
    public String getConnectionURL() {
        org.apache.tomcat.jdbc.pool.DataSource bsd
                = (org.apache.tomcat.jdbc.pool.DataSource) this.jdbcTemplate
                .getDataSource();
        StringBuilder sb = new StringBuilder()
                .append(bsd.getUrl())
                .append("?user=")
                .append(bsd.getUsername())
                .append("&password=")
                .append(bsd.getPoolProperties().getPassword());
        return sb.toString();
    }

    /**
     * To insert.
     *
     * @param headers
     *            the headers
     * @return the list
     */
    private static List<String> toInsert(List<XLSXHeader> headers) {
        List<String> result = new ArrayList<>();
        for (XLSXHeader xlsxHeader : headers) {
            result.add(CryptUtils.toMurmurString(xlsxHeader.getSystemHeader()));
        }
        return result;
    }

    /**
     * Creates the table.
     *
     * @param headers
     *            the headers
     * @param entityName
     *            the entity name
     * @param modelVersion
     *            the model version
     * @param fileName
     *            the file name
     * @param fileCreationDate
     *            the file creation date
     * @return the string
     */
    private String createTable(List<XLSXHeader> headers, String entityName, String modelVersion, String fileName,
                               String fileCreationDate) {
        StringBuilder sb
                = new StringBuilder("CREATE UNLOGGED TABLE ")
                .append(IMPORT_SCHEMA)
                .append(constructTableName(entityName, modelVersion, fileName, fileCreationDate))
                .append("( id serial NOT NULL, ")
                .append(toCreate(headers).stream().collect(Collectors.joining(", ")))
                .append(")");
        return sb.toString();

    }

    /**
     * Creates unique indexes.
     */
    private List<String> createIndexes(List<XLSXHeader> headers, String entityName, String modelVersion, String fileName,
                                       String fileCreationDate) {
        String etalonId = "_" + CryptUtils.toMurmurString("ID");
        String externalId = "_" + CryptUtils.toMurmurString("EXTERNAL_ID");
        String from = "_" + CryptUtils.toMurmurString("FROM");
        String to = "_" + CryptUtils.toMurmurString("TO");
        String tableName = constructTableName(entityName, modelVersion, fileName, fileCreationDate);

        List<String> result = new ArrayList<>();
        result.add(generateUniqueIndex("IDX_3_UNIQIE_EtId_FROM_TO_" + tableName, tableName,
                Arrays.asList(new String[]{etalonId, from, to}),
                Arrays.asList(new String[]{})));
        result.add(generateUniqueIndex("IDX_2_UNIQIE_EtId_FROM_" + tableName, tableName,
                Arrays.asList(new String[]{etalonId, from}),
                Arrays.asList(new String[]{to})));
        result.add(generateUniqueIndex("IDX_2_UNIQIE_EtId_TO_" + tableName, tableName,
                Arrays.asList(new String[]{etalonId, to}),
                Arrays.asList(new String[]{from})));
        result.add(generateUniqueIndex("IDX_2_UNIQIE_EtId_" + tableName, tableName,
                Arrays.asList(new String[]{etalonId}),
                Arrays.asList(new String[]{to, from})));


        result.add(generateUniqueIndex("IDX_3_UNIQIE_ExID_FROM_TO_" + tableName, tableName,
                Arrays.asList(new String[]{externalId, from, to}),
                Arrays.asList(new String[]{})));
        result.add(generateUniqueIndex("IDX_2_UNIQIE_ExId_FROM_" + tableName, tableName,
                Arrays.asList(new String[]{externalId, from}),
                Arrays.asList(new String[]{to})));
        result.add(generateUniqueIndex("IDX_2_UNIQIE_ExId_TO_" + tableName, tableName,
                Arrays.asList(new String[]{externalId, to}),
                Arrays.asList(new String[]{from})));
        result.add(generateUniqueIndex("IDX_2_UNIQIE_ExId_" + tableName, tableName,
                Arrays.asList(new String[]{externalId}),
                Arrays.asList(new String[]{to, from})));

        return result;
    }

    /**
     * Generate unique postgres index.
     * @param name index name
     * @param tableName table name
     * @param unique unique fields
     * @param nullable nullable fields
     * @return
     */
    private String generateUniqueIndex(String name, String tableName, List<String> unique, List<String> nullable) {
        StringBuilder index
                = new StringBuilder("CREATE UNIQUE INDEX ").append(name).append(" ON ")
                .append(IMPORT_SCHEMA)
                .append(tableName)
                .append(" (  ");

        for (int i = 0; i < unique.size(); i++) {
            if (i > 0) {
                index.append(", ");
            }
            index.append(unique.get(i));
        }

        index.append(")");

        for (int i = 0; i < nullable.size(); i++) {
            if (i == 0) {
                index.append(" WHERE ");
            } else {
                index.append(" AND ");
            }
            index.append(nullable.get(i))
                    .append(" IS NULL");
        }

        return index.toString();
    }

    /**
     * Type to sql.
     *
     * @param dataType
     *            the data type
     * @return the string
     */
    private static String typeToSQL(SimpleDataType dataType) {
        String result;
        switch (dataType) {
            case BOOLEAN:
                result = "boolean";
                break;
            case DATE:
                result = "date";
                break;
            case TIME:
                result = "time";
                break;
            case TIMESTAMP:
                result = "timestamp";
                break;
            case INTEGER:
                result = "bigint";
                break;
            case NUMBER:
                result = "numeric";
                break;
            case STRING:
            default:
                result = "text";
                break;
        }
        return result;
    }
}
