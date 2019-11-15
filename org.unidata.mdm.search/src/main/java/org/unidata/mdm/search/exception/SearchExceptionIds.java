package org.unidata.mdm.search.exception;

import org.unidata.mdm.system.exception.ExceptionId;

/**
 * @author Mikhail Mikhailov on Oct 1, 2019
 * Search exception IDs.
 */
public final class SearchExceptionIds {
    /**
     * Constructor.
     */
    private SearchExceptionIds() {
        super();
    }
    /**
     * Complex related request is incorrect
     */
    public static final ExceptionId EX_SEARCH_COMPLEX_RELATED_REQUEST_INCORRECT
        = new ExceptionId("EX_SEARCH_COMPLEX_RELATED_REQUEST_INCORRECT", "app.search.complex.related.incorrect");
    /**
     * Cannot parse date from SearchUtils.
     */
    public static final ExceptionId EX_SEARCH_CANNOT_PARSE_DATE
        = new ExceptionId("EX_SEARCH_CANNOT_PARSE_DATE", "app.search.cannot.parse.date");
    /**
     * IO failure from XContentFactory caught..
     */
    public static final ExceptionId EX_SEARCH_MAPPING_IO_FAILURE
        = new ExceptionId("EX_SEARCH_MAPPING_IO_FAILURE", "app.search.mappingIOFailure");
    /**
     * Invalid mapping of unknown type supplied.
     */
    public static final ExceptionId EX_SEARCH_MAPPING_TYPE_UNKNOWN
        = new ExceptionId("EX_SEARCH_MAPPING_TYPE_UNKNOWN", "app.search.mappingUnknownType");
    /**
     * Elasticsearch indexing exception.
     */
    public static final ExceptionId EX_INDEXING_EXCEPTION
        = new ExceptionId("EX_INDEXING_EXCEPTION", "app.search.indexing.exception");
    /**
     * Document build failed.
     */
    public static final ExceptionId EX_SEARCH_DOCUMENT_BUILD_FAILED
        = new ExceptionId("EX_SEARCH_DOCUMENT_BUILD_FAILED", "app.search.updateDocumentFailed");
    /**
     * Elasticsearch exception caught.
     */
    public static final ExceptionId EX_SEARCH_ES_NO_MAPPING_FOUND
        = new ExceptionId("EX_SEARCH_ES_NO_MAPPING_FOUND", "app.search.no.mapping.found");
    /**
     * Elasticsearch exception caught.
     */
    public static final ExceptionId EX_SEARCH_ES_ESC_CAUGHT
        = new ExceptionId("EX_SEARCH_ES_ESC_CAUGHT", "app.search.searchElasticSearchExceptionCaught");
    /**
     * Invalid fields supplied for term query.
     */
    public static final ExceptionId EX_SEARCH_INVALID_TERM_FIELDS
        = new ExceptionId("EX_SEARCH_INVALID_TERM_FIELDS", "app.search.invalidFieldsTermQuery");

    /**
     * Case when filter has incorrect combination.
     */
    public static final ExceptionId EX_SEARCH_UNAVAILABLE_FACETS_COMBINATION
        = new ExceptionId("EX_SEARCH_UNAVAILABLE_FACETS_COMBINATION", "app.search.facets.combination");
    /**
     * Try to mark(update) fields which not linked with search request
     */
    public static final ExceptionId EX_SEARCH_NOT_RELATED_SEARCH_TYPES_IN_MARK_OPERATION
        = new ExceptionId("EX_SEARCH_NOT_RELATED_SEARCH_TYPES_IN_MARK_OPERATION", "app.search.mark.not.linked.search.types");
    /**
     * Mark document failed.
     */
    public static final ExceptionId EX_SEARCH_MARK_DOCUMENT_FAILED
        = new ExceptionId("EX_SEARCH_MARK_DOCUMENT_FAILED", "app.search.markFailed");
    /**
     * Parse date failed
     */
    public static final ExceptionId EX_DATA_CANNOT_PARSE_DATE
            = new ExceptionId("EX_DATA_CANNOT_PARSE_DATE", "app.search.parseDateFailed");
}
