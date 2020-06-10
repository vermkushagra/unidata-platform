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

/**
 *
 */
package com.unidata.mdm.api.wsdl.v5;

/**
 * @author Mikhail Mikhailov
 * Operation IDs for SOAP operations in the global request.
 */
public enum SoapOperation {

    /**
     * Auth request.
     */
    REQUEST_AUTHENTICATE,
    /**
     * Request Cleanse.
     */
    REQUEST_CLEANSE,
    /**
     * Request Get (data objects).
     */
    REQUEST_GET,
    /**
     * Request Get All periods objects (data objects).
     */
    REQUEST_GET_ALL_PERIODS,
    /**
     * Request Get info.
     */
    REQUEST_INFO_GET,
    /**
     * Request Get (relation).
     */
    REQUEST_GET_REL,
    /**
     * Check duplicates.
     */
    REQUEST_CHECK_DUPLICATES,
    /**
     * Request get data quality errors.
     */
    REQUEST_GET_DATA_QUALITY_ERRORS,
    /**
     * Request get lookup values.
     */
    REQUEST_GET_LOOKUP_VALUES,
    /**
     * Request Merge.
     */
    REQUEST_MERGE,
    /**
     * Request Join.
     */
    REQUEST_JOIN,
    /**
     * Request Split.
     */
    REQUEST_SPLIT,

    /**
     * Request Merge preview.
     */
    REQUEST_MERGE_PREVIEW,
    /**
     * Request Meta get cleanse function description.
     */
    REQUEST_META_GET_CLEANSE_FUNCTION_DESC,
    /**
     * Request meta get cleanse function list.
     */
    REQUEST_META_GET_CLEANSE_FUNCTION_LIST,
    /**
     * Request search.
     */
    REQUEST_SEARCH,
    /**
     * Request soft delete.
     */
    REQUEST_SOFT_DELETE,
    /**
     * Request soft delete relation.
     */
    REQUEST_SOFT_DELETE_REL,
    /**
     * Request upsert.
     */
    REQUEST_UPSERT,
    /**
     * Request bulk upsert
     */
    REQUEST_BULK_UPSERT,
    /**
     * Request upsert relation.
     */
    REQUEST_UPSERT_REL
}
