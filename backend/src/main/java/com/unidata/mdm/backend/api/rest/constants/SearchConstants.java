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
