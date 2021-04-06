/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.SearchRequestOperator;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;
import com.unidata.mdm.backend.common.search.types.ServiceSearchType;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Mikhail Mikhailov
 * Search execution context.
 */
public class SearchRequestContext implements SearchContext {

    private static final Integer MAX_PAGE_SIZE = 50000;

    /**
     * Query type name.
     */
    private final String entity;
    /**
     * Score.
     */
    private final Float score;
    /**
     * Search type.
     */
    private final SearchRequestType search;
    /**
     * Return source or not.
     */
    private final boolean source;
    /**
     * Search fields.
     */
    private final List<String> searchFields;
    /**
     * Fields to return.
     */
    private final List<String> returnFields;
    /**
     * Form fields array.
     */
    private final List<FormFieldsGroup> form;
    /**
     * Search facets.
     */
    private final List<FacetName> facets;

    /**
     * Fields which will be manage search result order.
     */
    private final Collection<SortField> sortFields;
    /**
     * The text
     */
    private final String text;
    /**
     * Multiple values to look for using Dis Max query.
     */
    private final List<Object> values;
    /**
     * Max hit count to return.
     */
    private final int count;
    /**
     * Page number, 0 based.
     */
    private final int page;
    /**
     * Return total count or not.
     */
    private final boolean totalCount;
    /**
     * Return total count only and no other results.
     */
    private final boolean countOnly;
    /**
     * Return all paged results, if no query string given.
     */
    private final boolean fetchAll;
    /**
     * Default fields operator.
     */
    private final SearchRequestOperator operator;
    /**
     * Date hint.
     */
    private final Date asOf;
    /**
     * The storage id to use. Overrides the system one.
     */
    private final String storageId;
    /**
     * Skip or add etalon id. Add is the default.
     */
    private final boolean skipEtalonId;

    /**
     * Execute only generated query without sorting and post filtering.
     */
    private final boolean onlyQuery;
    /**
     * Search as you type
     */
    private final boolean sayt;
    /**
     *  Use scroll scan mechanism
     */
    private final boolean scrollScan;
    /**
     * Type of requested entity in index
     */
    private final SearchType type;
    /**
     * Aggregations collection.
     */
    private final Collection<AggregationRequestContext> aggregations;
    /**
     * Netsed path.
     */
    private final String nestedPath;
    /**
     * Routing hints to hit only those shards, which really contain the value.
     */
    private final List<String> routings;
    /**
     *  Shard number use for preferences
     */
    private final Integer shardNumber;
    /**
     * Inner hits
     */
    private final Pair<String, List<String>> innerHits;
    /**
     * Run user exits or not
     */
    private final boolean runExits;
    /**
     * Building constructor.
     * @param builder the builder to use.
     */
    private SearchRequestContext(SearchRequestContextBuilder builder) {
        this.entity = builder.entity;
        this.score = builder.score;
        this.search = builder.search;
        this.source = builder.source;
        this.searchFields = builder.searchFields;
        this.returnFields = builder.returnFields;
        this.form = builder.form;
        this.facets = builder.facets;
        this.sortFields = builder.sortFields;
        this.text = builder.text;
        this.values = builder.values;
        this.page = builder.page;
        this.count = builder.count;
        this.totalCount = builder.totalCount;
        this.countOnly = builder.countOnly;
        this.fetchAll = builder.fetchAll;
        this.operator = builder.operator;
        this.asOf = builder.asOf;
        this.storageId = builder.storageId;
        this.skipEtalonId = builder.skipEtalonId;
        this.onlyQuery = builder.onlyQuery;
        this.type = builder.type;
        this.sayt = builder.sayt;
        this.scrollScan = builder.scrollScan;
        this.aggregations = builder.aggregations;
        this.nestedPath = builder.nestedPath;
        this.routings = builder.routings;
        this.runExits = builder.runExits;
        this.shardNumber = builder.shardNumber;
        this.innerHits = builder.innerHits;
    }

    /**
     * @return the type
     */
    @Override
    public String getEntity() {
        return entity;
    }

    /**
     * @return the score
     */
    public Float getScore() {
        return score;
    }

    /**
     * @return the search
     */
    public SearchRequestType getSearch() {
        return search;
    }

    /**
     * @return the source
     */
    public boolean isSource() {
        return source;
    }

    /**
     * @return the fields
     */
    public List<String> getSearchFields() {
        return searchFields;
    }

    /**
     * @return only query
     */
    public boolean isOnlyQuery() {
        return onlyQuery;
    }

    /**
     * @return the returnFields
     */
    public List<String> getReturnFields() {
        return returnFields;
    }

    /**
     * @return the form
     */
    public List<FormFieldsGroup> getForm() {
        return form;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }


    /**
     * @return the values
     */
    public List<Object> getValues() {
        return values;
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * @return the page
     */
    public int getPage() {
        return page;
    }

    /**
     * @return the totalCount
     */
    public boolean isTotalCount() {
        return totalCount;
    }

    /**
     * @return the countOnly
     */
    public boolean isCountOnly() {
        return countOnly;
    }


    /**
     * @return the facets
     */
    public List<FacetName> getFacets() {
        return facets;
    }

    /**
     *
     * @return fields which will be used for result sorting.
     */
    public Collection<SortField> getSortFields() {
        return sortFields;
    }

    /**
     * @return the fetchAll
     */
    public boolean isFetchAll() {
        return fetchAll;
    }

    /**
     * @return the operator
     */
    public SearchRequestOperator getOperator() {
        return operator;
    }


    /**
     * @return the asOf
     */
    public Date getAsOf() {
        return asOf;
    }

    /**
     * @return the storageId
     */
    @Override
    public String getStorageId() {
        return storageId;
    }

    /**
     * @return the skipEtalonId
     */
    public boolean isSkipEtalonId() {
        return skipEtalonId;
    }

    /**
     * @return the scroll scan
     */
    public boolean isScrollScan() {
        return scrollScan;
    }

    /**
     * @return the scroll scan
     */
    public Integer getShardNumber() {
        return shardNumber;
    }

    /**
     * @return the scroll scan
     */
    public Pair<String, List<String>> getInnerHits() {
        return innerHits;
    }

    /**
     * Simple mode fields are set.
     * @return true if so
     */
    public boolean isSimple() {
        boolean hasSimpleValues = StringUtils.isNoneBlank(text) || (values != null && !values.isEmpty());
        return hasSimpleValues && (search == SearchRequestType.QSTRING || (searchFields != null && !searchFields.isEmpty()));
    }

    /**
     * Form mode fields are set.
     * @return true if so
     */
    public boolean isForm() {
        return form != null && !form.isEmpty();
    }

    /**
     * Combo mode (simple and form).
     * @return true if so
     */
    public boolean isCombo() {
        return isSimple() && isForm();
    }

    /**
     * @return true if nothing supposed to be search
     */
    public boolean isEmpty() {
        return !isSimple() && !isForm();
    }

    /**
     *
     * @return true if it is "Search as you type", otherwise false
     */
    public boolean isSayt() {
        return sayt;
    }

    /**
    *
    * @return true if it is nested query / request
    */
    public boolean isNested() {
        return Objects.nonNull(this.nestedPath);
    }

    /**
     * Type of search entity
     * @return search
     */
    public SearchType getType() {
        return type;
    }

    /**
     * @return the nestedPath
     */
    public String getNestedPath() {
        return nestedPath;
    }

    /**
     * @return the aggregations
     */
    public Collection<AggregationRequestContext> getAggregations() {
        return aggregations;
    }

    /**
     * @return the routings
     */
    public List<String> getRoutings() {
        return routings;
    }


    /**
     * @return run exits or not
     */
    public boolean isRunExits() {
        return runExits;
    }

    /**
     * @return builder instance for searching over audit events.
     */
    @Nonnull
    public static SearchRequestContextBuilder forAuditEvents() {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.type = ServiceSearchType.AUDIT;
        builder.entity = ServiceSearchType.AUDIT.getIndexName();
        builder.skipEtalonId = true;
        builder.onlyQuery = true;
        return builder;
    }

    /**
     * @return builder instance for searching over model elements.
     */
    @Nonnull
    public static SearchRequestContextBuilder forModelElements() {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.type = ServiceSearchType.MODEL;
        builder.entity = ServiceSearchType.MODEL.getIndexName();
        builder.skipEtalonId = true;
        builder.onlyQuery = true;
        return builder;
    }

    /**
     * @return builder instance for searching over classifiers elements.
     */
    @Nonnull
    public static SearchRequestContextBuilder forClassifierElements() {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.type = ServiceSearchType.CLASSIFIER;
        builder.entity = ServiceSearchType.CLASSIFIER.getIndexName();
        builder.skipEtalonId = true;
        builder.onlyQuery = true;
        return builder;
    }

    /**
     * @return builder instance for searching over etalon data.
     */
    @Nonnull
    public static SearchRequestContextBuilder forEtalonData(@Nonnull String entity) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.entity = entity;
        builder.type = EntitySearchType.ETALON_DATA;
        return builder;
    }

    /**
     * @return builder instance for searching over etalon relations.
     */
    @Nonnull
    public static SearchRequestContextBuilder forEtalonRelation(@Nonnull String entity) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.entity = entity;
        builder.type = EntitySearchType.ETALON_RELATION;
        return builder;
    }

    /**
     * @return builder instance for any type related with entity.
     */
    @Nonnull
    public static SearchRequestContextBuilder forEtalon(@Nonnull EntitySearchType type, @Nonnull String entity) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.entity = entity;
        builder.type = type;
        return builder;
    }

    /**
     * @return builder instance fro index operation.
     */
    @Nonnull
    public static SearchRequestContextBuilder forIndex(@Nonnull String entity) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.entity = entity;
        builder.type = null;
        return builder;
    }

    /**
     * By default search will be applied to etalon data.
     * Note: should be called entity method
     * Note: better will be used other static fabric methods
     * @return general builder builder
     */
    @Nonnull
    public static SearchRequestContextBuilder builder() {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.type = EntitySearchType.ETALON_DATA;
        return builder;
    }
    /**
     * By default search will be applied to etalon data.
     * Note: should be called entity method
     * Note: better will be used other static fabric methods
     * @return general builder builder
     */
    @Nonnull
    public static SearchRequestContextBuilder builder(@Nonnull EntitySearchType type) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.type = type;
        return builder;
    }
    /**
     * By default search will be applied to etalon data.
     * Note: should be called entity method
     * Note: better will be used other static fabric methods
     * @return general builder builder
     */
    @Nonnull
    public static SearchRequestContextBuilder builder(@Nonnull EntitySearchType type, @Nonnull String entity) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.type = type;
        builder.entity = entity;
        return builder;
    }



    /**
     * Context builder.
     * @author Mikhail Mikhailov
     */
    public static class SearchRequestContextBuilder {

        /**
         * Search type
         */
        private SearchRequestContextBuilder() {
            super();
        }
        /**
         * Type to operate on.
         */
        private String entity;
        /**
         * Score.
         */
        private Float score;
        /**
         * Type of the search.
         */
        private SearchRequestType search;
        /**
         * Return source or not.
         */
        private boolean source;
        /**
         * Search fields.
         */
        private List<String> searchFields;
        /**
         * Fields to return.
         */
        private List<String> returnFields;
        /**
         * Form fields array.
         */
        private List<FormFieldsGroup> form;
        /**
         * Search facets.
         */
        private List<FacetName> facets;
        /**
         * Fields which will be manage search result order.
         */
        private Collection<SortField> sortFields = Collections.emptyList();
        /**
         * Multiple values for dis max query.
         */
        private List<Object> values;
        /**
         * The text.
         */
        private String text;
        /**
         * Objects count.
         */
        private int count;
        /**
         * Page number, 0 based.
         */
        private int page;
        /**
         * Return total count or not.
         */
        private boolean totalCount;
        /**
         * Return total count only and no other results.
         */
        private boolean countOnly;
        /**
         * Return all paged results, if no query string given.
         */
        private boolean fetchAll;
        /**
         * Default fields operator.
         */
        private SearchRequestOperator operator;
        /**
         * Date hint.
         */
        private Date asOf;
        /**
         * The storage id to use. Overrides the system one.
         */
        private String storageId;
        /**
         * Skip or add etalon id. Add is the default.
         */
        private boolean skipEtalonId;

        /**
         * Execute only query without post filtering and sorting.
         */
        private boolean onlyQuery = false;
        /**
         * Search as you type
         */
        private boolean sayt = false;
        /**
         *  Use scroll scan mechanism
         */
        private boolean scrollScan = false;
        /**
         * Netsed path.
         */
        private String nestedPath;
        /**
         * Type of entity
         */
        private SearchType type;
        /**
         * Aggregations collection.
         */
        private Collection<AggregationRequestContext> aggregations;
        /**
         * Routing hints to hit only those shards, which really contain the value.
         */
        private List<String> routings;
        /**
         * run exits or not
         */
        private boolean runExits = false;
        /**
         * Shard number for request
         */
        private Integer shardNumber;
        /**
         * Inner hits
         */
        private Pair<String, List<String>> innerHits;
        /**
         * Builds a context from this builder.
         * @return new {@link SearchRequestContext}
         */
        public SearchRequestContext build() {
            return new SearchRequestContext(this);
        }

        /**
         * Sets the score.
         * @param score the score
         * @return this
         */
        public SearchRequestContextBuilder score(float score) {
            this.score = score;
            return this;
        }

        /**
         * Sets the search type.
         * @param search the query type
         * @return this
         */
        public SearchRequestContextBuilder search(SearchRequestType search) {
            this.search = search;
            return this;
        }

        /**
         * Sets the source field.
         * @param source return source or not.
         * @return this
         */
        public SearchRequestContextBuilder source(boolean source) {
            this.source = source;
            return this;
        }

        /**
         * Sets the search fields.
         * @param searchFields the fields to use
         * @return this
         */
        public SearchRequestContextBuilder searchFields(List<String> searchFields) {
            this.searchFields = searchFields;
            return this;
        }

        /**
         * Sets the return fields.
         * @param returnFields the return fields to use
         * @return this
         */
        public SearchRequestContextBuilder returnFields(List<String> returnFields) {
            this.returnFields = returnFields;
            return this;
        }

        /**
         * Sets the search group if specified.
         *
         * @param group  the main group to use
         * @param groups addition groups for search request.
         * @return this
         */
        public SearchRequestContextBuilder form(FormFieldsGroup group, FormFieldsGroup... groups) {
            this.form = CollectionUtils.isEmpty(this.form) ? new ArrayList<>() : this.form;
            if (group != null && !group.isEmpty()) {
                this.form.add(group);
            }
            Arrays.stream(groups)
                  .filter(Objects::nonNull)
                  .filter(gr -> !gr.isEmpty())
                  .collect(Collectors.toCollection(() -> this.form));
            return this;
        }

        /**
         * Sets the search group if specified.
         *
         * @param groups addition groups for search request.
         * @return this
         */
        public SearchRequestContextBuilder form(List<FormFieldsGroup> groups) {
            if (CollectionUtils.isEmpty(groups)) {
                return this;
            }
            this.form = CollectionUtils.isEmpty(this.form) ? new ArrayList<>() : this.form;
            groups.stream()
                  .filter(Objects::nonNull)
                  .filter(gr -> !gr.isEmpty())
                  .collect(Collectors.toCollection(() -> this.form));
            return this;
        }

        public SearchRequestContextBuilder onlyQuery(boolean onlyQuery) {
            this.onlyQuery = onlyQuery;
            return this;
        }

        /**
         * Sets the search facets.
         * @param facets the facets to use
         * @return this
         */
        public SearchRequestContextBuilder facetsAsStrings(List<String> facets) {
            this.facets = FacetName.fromValues(facets);
            return this;
        }

        public SearchRequestContextBuilder addSorting(Collection<SortField> sortFields){
            this.sortFields = sortFields;
            return this;
        }

        /**
         * Sets the search facets.
         * @param facets the facets to use
         * @return this
         */
        public SearchRequestContextBuilder facets(List<FacetName> facets) {
            this.facets = facets;
            return this;
        }

        /**
         * Sets the text to look for.
         * @param text the text
         * @return this
         */
        public SearchRequestContextBuilder text(String text) {
            this.text = text;
            this.sayt = false;
            return this;
        }

        /**
         * Sets the text to look for.
         * @param text the text
         * @param sayt - is sayt text
         * @return this
         */
        public SearchRequestContextBuilder text(String text, boolean sayt) {
            this.text = text;
            this.sayt = sayt;
            return this;
        }

        /**
         * Sets the text to look for(Search as you type)
         * @param text the text
         * @return this
         */
        public SearchRequestContextBuilder saytText(String text) {
            this.text = text;
            this.sayt = true;
            return this;
        }

        /**
         * Sets the values to look for, using dis max query.
         * @param values the values
         * @return this
         */
        @SuppressWarnings("unchecked")
        public<T extends Object> SearchRequestContextBuilder values(List<T> values) {
            this.values = (List<Object>) values;
            return this;
        }

        /**
         * Sets page number, 0 based.
         * @param page the page
         * @return this
         */
        public SearchRequestContextBuilder page(int page) {
            this.page = page;
            return this;
        }

        /**
         * Sets the max count to return.
         * Max count to return can't be more {@value MAX_PAGE_SIZE}.
         * @param count the count
         * @return this
         */
        public SearchRequestContextBuilder count(int count) {

            this.count = Math.min(MAX_PAGE_SIZE, count);
            return this;
        }

        /**
         * Sets the totalCount field.
         * @param totalCount return totalCount or not.
         * @return this
         */
        public SearchRequestContextBuilder totalCount(boolean totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        /**
         * Sets the countOnly field.
         * @param countOnly return countOnly or not.
         * @return this
         */
        public SearchRequestContextBuilder countOnly(boolean countOnly) {
            this.countOnly = countOnly;
            return this;
        }

        /**
         * Sets the fetchAll field.
         * @param fetchAll return all, if no query string set, or not.
         * @return this
         */
        public SearchRequestContextBuilder fetchAll(boolean fetchAll) {
            this.fetchAll = fetchAll;
            return this;
        }

        /**
         * Sets the operator field.
         * @param operator the operator to set.
         * @return this
         */
        public SearchRequestContextBuilder operator(SearchRequestOperator operator) {
            this.operator = operator;
            return this;
        }
        /**
         * Sets as of date for searches.
         * @param asOf the date
         * @return self
         */
        public SearchRequestContextBuilder asOf(Date asOf) {
            this.asOf = asOf;
            return this;
        }

        /**
         * @param entityName - entity name
         * @return self
         */
        @Deprecated
        public SearchRequestContextBuilder entity(String entityName){
            this.entity = entityName;
            return this;
        }

        /**
         * Overrides default storage id.
         * @param storageId the storage id to use
         * @return self
         */
        public SearchRequestContextBuilder storageId(String storageId) {
            this.storageId = storageId;
            return this;
        }

        /**
         * Skip or add etalon ID to return fields. Add is the default.
         * This is used for type, which don't have etalon id.
         * @param skipEtalonId true or false
         * @return self
         */
        public SearchRequestContextBuilder skipEtalonId(boolean skipEtalonId) {
            this.skipEtalonId = skipEtalonId;
            return this;
        }

        /**
         * use scroll scan mechanism
         * @param scrollScan true or false
         * @return self
         */
        public SearchRequestContextBuilder scrollScan(boolean scrollScan) {
            this.scrollScan = scrollScan;
            return this;
        }
        /**
         * Generate nested query with given path.
         * @param nestedPath path
         * @return self
         */
        public SearchRequestContextBuilder nestedPath(String nestedPath) {
            this.nestedPath = nestedPath;
            return this;
        }
        /**
         * Adds aggregations to this search context.
         * @param aggregations the aggregations.
         * @return self
         */
        public SearchRequestContextBuilder aggregations(Collection<AggregationRequestContext> aggregations) {
            this.aggregations = aggregations;
            return this;
        }
        /**
         * Adds routig hints.
         * @param routingHints the routingHints to set
         * @return self
         */
        public SearchRequestContextBuilder routings(List<String> routings) {
            this.routings = routings;
            return this;
        }

        /**
         * Change run exits flag
         * @param runExits run exits flag
         * @return self
         */
        public SearchRequestContextBuilder runExits(boolean runExits) {
            this.runExits = runExits;
            return this;
        }

        public SearchRequestContextBuilder shardNumber(Integer shardNumber) {
            this.shardNumber = shardNumber;
            return this;
        }

        public SearchRequestContextBuilder innerHits(Pair<String, List<String>> innerHits) {
            this.innerHits = innerHits;
            return this;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        StringBuilder b = new StringBuilder()
            .append(getClass().getSimpleName())
            .append(": {")
            .append("entity = ").append(entity == null ? "null" : entity).append(", ")
            .append("score = ").append(score).append(", ")
            .append("search = ").append(search == null ? "null" : search.toString()).append(", ")
            .append("source = ").append(source).append(", ")
            .append("searchFields = ").append(searchFields == null ? "null" : searchFields.toString()).append(", ")
            .append("returnFields = ").append(returnFields == null ? "null" : returnFields.toString()).append(", ")
            .append("text = ").append(text == null ? "null" : text).append(", ")
            .append("count = ").append(count).append(", ")
            .append("page = ").append(page).append(", ")
            .append("totalCount = ").append(totalCount).append(", ")
            .append("countOnly = ").append(countOnly).append(", ")
            .append("}");

        return b.toString();
    }
}
