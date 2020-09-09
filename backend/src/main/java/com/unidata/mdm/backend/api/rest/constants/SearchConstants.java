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

package com.unidata.mdm.backend.api.rest.constants;

public class SearchConstants {
    /**
     * Parameter 'text'.
     */
    public static final String SEARCH_PARAM_TEXT = "text";
    /**
     * Parameter 'fields'.
     */
    public static final String SEARCH_PARAM_FIELDS = "fields";
    /**
     * Parameter 'count'.
     */
    public static final String SEARCH_PARAM_COUNT = "count";
    /**
     * Parameter 'page'.
     */
    public static final String SEARCH_PARAM_PAGE = "page";
    /**
     * Parameter 'request content'.
     */
    public static final String SEARCH_PARAM_EXPORT_REQUEST_CONTENT = "request_content";
    /**
     * Search root path.
     */
    public static final String SEARCH_PATH_SEARCH = "search";
    /**
     * Form search path 'param'.
     */
    public static final String SEARCH_PATH_FORM = "form";
    /**
     * Combo search path 'param'.
     */
    public static final String SEARCH_PATH_COMBO = "combo";
    /**
     * Complex search path 'param'.
     */
    public static final String SEARCH_PATH_COMPLEX = "complex";
    /**
     * Simple search path 'param'.
     */
    public static final String SEARCH_PATH_SIMPLE = "simple";
    /**
     * Search path 'param'.
     */
    public static final String SEARCH_PATH_SAYT = "sayt";
    /**
     * Search path 'xls-export-simple'.
     */
    public static final String SEARCH_PATH_XLS_EXPORT_SIMPLE = "xls-export-simple";
    /**
     * Search path 'xls-export-form'.
     */
    public static final String SEARCH_PATH_XLS_EXPORT_FORM = "xls-export-form";
    /**
     * Search path 'meta'.
     */
    public static final String SEARCH_PATH_META = "meta";
    /**
     * Default number of objects to return (page size), if nothing is set.
     */
    public static final String DEFAULT_OBJ_COUNT_VALUE = "10";
    /**
     * Default page number (offset).
     */
    public static final String DEFAULT_PAGE_NUMBER_VALUE = "0";

    // ========================================== Message block ========================================================

    public static final String SEARCH_BEFORE_USER_EXIT_EXCEPTION = "app.search.before.user.exit.exception";

    public static final String SEARCH_AFTER_USER_EXIT_EXCEPTION = "app.search.after.user.exit.exception";
}
