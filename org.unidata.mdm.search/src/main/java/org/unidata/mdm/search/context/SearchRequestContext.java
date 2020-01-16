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

package org.unidata.mdm.search.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.form.FormFieldsGroup;
import org.unidata.mdm.search.type.search.FacetName;
import org.unidata.mdm.search.type.search.SearchRequestOperator;
import org.unidata.mdm.search.type.search.SearchRequestType;
import org.unidata.mdm.search.type.search.SortField;

/**
 * @author Mikhail Mikhailov
 *         Search execution context.
 */
public class SearchRequestContext implements TypedSearchContext {

    public static final int MAX_PAGE_SIZE = 50000;
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
     * Sort field values for last record in last search.
     */
    private final List<Object> searchAfter;
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
     * Use scroll scan mechanism
     */
    private final boolean scrollScan;
    /**
     * The search type
     */
    private final IndexType type;
    /**
     * Aggregations collection.
     */
    private final Collection<AggregationSearchContext> aggregations;
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
    private final List<NestedSearchRequestContext> nestedSearch;
    /**
     * Run user exits or not
     */
    private final boolean runExits;
    /**
     * Score query.
     */
    private boolean rescore;
    /**
     * true - must, false - should
     */
    private final boolean must;
    /**
     * Score for fields.
     */
    private final Map<String, Float> scoreFields;
    /**
     * Building constructor.
     *
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
        this.searchAfter = builder.searchAfter;
        this.shardNumber = builder.shardNumber;
        this.nestedSearch = builder.nestedSearch;
        this.must = builder.must;
        this.scoreFields = builder.scoreFields;
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
     * Form fields array.
     */ /**
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
     * @return the search after values
     */
    public List<Object> getSearchAfter() {
        return searchAfter;
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
    public List<NestedSearchRequestContext> getNestedSearch() {
        return nestedSearch;
    }

    /**
     * Simple mode fields are set.
     *
     * @return true if so
     */
    public boolean isSimple() {
        boolean hasSimpleValues = StringUtils.isNoneBlank(text) || CollectionUtils.isNotEmpty(values);
        return hasSimpleValues && (search == SearchRequestType.QSTRING || CollectionUtils.isNotEmpty(searchFields));
    }

    /**
     * Form mode fields are set.
     *
     * @return true if so
     */
    public boolean isForm() {
        return CollectionUtils.isNotEmpty(form);
    }

    /**
     * Combo mode (simple and form).
     *
     * @return true if so
     */
    public boolean isCombo() {
        return isSimple() && isForm();
    }

    /**
     * @return true if nothing supposed to be search
     */
    public boolean isEmpty() {
        return !isSimple() && !isForm() && nestedSearch == null;
    }

    /**
     * @return true if it is "Search as you type", otherwise false
     */
    public boolean isSayt() {
        return sayt;
    }

    /**
     * @return true if it is nested query / request
     */
    public boolean isNested() {
        return Objects.nonNull(this.nestedPath);
    }

    /**
     * Type of requested entity in index
     */ /**
     * Type of search entity
     *
     * @return search
     */
    public IndexType getType() {
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
    public Collection<AggregationSearchContext> getAggregations() {
        return aggregations;
    }

    /**
     * @return the routings
     */
    public List<String> getRoutings() {
        return routings;
    }


    /**
     * @return the scoreFields
     */
    public Map<String, Float> getScoreFields() {
        return MapUtils.isEmpty(scoreFields) ? Collections.emptyMap() : scoreFields;
    }

    /**
     * @return run exits or not
     */
    public boolean isRunExits() {
        return runExits;
    }

    /**
     * @return the returnEtalon
     */
    public boolean isScoreEnabled() {
        return rescore;
    }

    public void rescore(boolean scoreEnabled) {
        this.rescore = scoreEnabled;
    }

    public boolean isMust(){
        return must;
    }
    /**
     * Search all types, simple searches only.
     * @return builder instance for index operation.
     */
    @Nonnull
    public static SearchRequestContextBuilder builder(@Nonnull String entity) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.entity = entity;
        return builder;
    }
    /**
     * Search all indexes in a particular type.
     * @return general builder builder
     */
    @Nonnull
    public static SearchRequestContextBuilder builder(@Nonnull IndexType type) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.type = type;
        return builder;
    }

    /**
     * By default search will be applied to etalon data.
     * Note: should be called entity method
     * Note: better will be used other static fabric methods
     *
     * @return general builder builder
     */
    @Nonnull
    public static SearchRequestContextBuilder builder(@Nonnull IndexType type, @Nonnull String entity) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.type = type;
        builder.entity = entity;
        return builder;
    }
    /**
     * By default search will be applied to etalon data.
     * Note: should be called entity method
     * Note: better will be used other static fabric methods
     *
     * @return general builder builder
     */
    @Nonnull
    public static SearchRequestContextBuilder builder(@Nonnull IndexType type, @Nonnull String entity, String storageId) {
        SearchRequestContextBuilder builder = new SearchRequestContextBuilder();
        builder.type = type;
        builder.entity = entity;
        builder.storageId = storageId;
        return builder;
    }
    /**
     * Context builder.
     *
     * @author Mikhail Mikhailov
     */
    public static class SearchRequestContextBuilder {

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
         * Use scroll scan mechanism
         */
        private boolean scrollScan = false;
        /**
         * Netsed path.
         */
        private String nestedPath;
        /**
         * Type of entity
         */
        private IndexType type;
        /**
         * Aggregations collection.
         */
        private Collection<AggregationSearchContext> aggregations;
        /**
         * Routing hints to hit only those shards, which really contain the value.
         */
        private List<String> routings;
        /**
         * run exits or not
         */
        private boolean runExits = false;
        /**
         * Sort field values for last record in last search.
         */
        private List<Object> searchAfter;
        /**
         * Shard number for request
         */
        private Integer shardNumber;
        /**
         * Inner hits
         */
        private List<NestedSearchRequestContext> nestedSearch;
        /**
         * true - must, false - should
         */
        private boolean must = true;
        /**
         * Score for fields.
         */
        private Map<String, Float> scoreFields;
        /**
         * Search type
         */
        private SearchRequestContextBuilder() {
            super();
        }
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
            for (int i = 0; returnFields != null && i < returnFields.size(); i++) {
                returnField(returnFields.get(i));
            }
            return this;
        }

        /**
         * Sets the return fields.
         * @param returnFields the return fields to use
         * @return this
         */
        public SearchRequestContextBuilder returnFields(String... returnFields) {
            for (int i = 0; returnFields != null && i < returnFields.length; i++) {
                returnField(returnFields[i]);
            }
            return this;
        }

        /**
         * Sets the return fields.
         *
         * @param returnField the return fields to use
         * @return this
         */
        public SearchRequestContextBuilder returnField(String returnField) {
            if (returnFields == null) {
                returnFields = new ArrayList<>();
            }
            returnFields.add(returnField);
            return this;
        }

        public List<FormFieldsGroup> getForm() {
            return form;
        }

        public IndexType getType() {
            return type;
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
         *
         * @param facets the facets to use
         * @return this
         */
        public SearchRequestContextBuilder facetsAsStrings(List<String> facets) {
            this.facets = FacetName.fromValues(facets);
            return this;
        }

        /**
         * Sets the search facets.
         *
         * @param facets the facets to use
         * @return this
         */
        public SearchRequestContextBuilder facets(List<FacetName> facets) {
            this.facets = facets;
            return this;
        }

        public SearchRequestContextBuilder addSorting(Collection<SortField> sortFields) {
            this.sortFields = sortFields;
            return this;
        }

        /**
         * Sets the text to look for.
         *
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
         *
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
         *
         * TODO: Kill after check.
         *
         * @param text the text
         * @return this
         */
        @Deprecated
        public SearchRequestContextBuilder saytText(String text) {
            this.text = text;
            this.sayt = true;
            return this;
        }

        /**
         * Sets the values to look for, using dis max query.
         *
         * TODO: Kill after check.
         *
         * @param values the values
         * @return this
         */
        @SuppressWarnings("unchecked")
        @Deprecated
        public <T extends Object> SearchRequestContextBuilder values(List<T> values) {
            this.values = (List<Object>) values;
            return this;
        }

        /**
         * Sets page number, 0 based.
         *
         * @param page the page
         * @return this
         */
        public SearchRequestContextBuilder page(int page) {
            this.page = page;
            return this;
        }

        /**
         * Sets the max count to return.
         * Max count to return can't be more {@value #MAX_PAGE_SIZE}.
         *
         * @param count the count
         * @return this
         */
        public SearchRequestContextBuilder count(int count) {

            this.count = Math.min(MAX_PAGE_SIZE, count);
            return this;
        }

        /**
         * Sets the totalCount field.
         *
         * @param totalCount return totalCount or not.
         * @return this
         */
        public SearchRequestContextBuilder totalCount(boolean totalCount) {
            this.totalCount = totalCount;
            return this;
        }

        /**
         * Sets the countOnly field.
         *
         * @param countOnly return countOnly or not.
         * @return this
         */
        public SearchRequestContextBuilder countOnly(boolean countOnly) {
            this.countOnly = countOnly;
            return this;
        }

        /**
         * Sets the fetchAll field.
         *
         * @param fetchAll return all, if no query string set, or not.
         * @return this
         */
        public SearchRequestContextBuilder fetchAll(boolean fetchAll) {
            this.fetchAll = fetchAll;
            return this;
        }

        /**
         * Sets the operator field.
         *
         * @param operator the operator to set.
         * @return this
         */
        public SearchRequestContextBuilder operator(SearchRequestOperator operator) {
            this.operator = operator;
            return this;
        }

        /**
         * Sets as of date for searches.
         *
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
        public SearchRequestContextBuilder entity(String entityName){
            this.entity = entityName;
            return this;
        }

        /**
         * Overrides default storage id.
         *
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
         *
         * @param skipEtalonId true or false
         * @return self
         */
        public SearchRequestContextBuilder skipEtalonId(boolean skipEtalonId) {
            this.skipEtalonId = skipEtalonId;
            return this;
        }

        /**
         * use scroll scan mechanism
         *
         * @param scrollScan true or false
         * @return self
         */
        public SearchRequestContextBuilder scrollScan(boolean scrollScan) {
            this.scrollScan = scrollScan;
            return this;
        }

        /**
         * Generate nested query with given path.
         *
         * @param nestedPath path
         * @return self
         */
        public SearchRequestContextBuilder nestedPath(String nestedPath) {
            this.nestedPath = nestedPath;
            return this;
        }

        /**
         * Adds aggregations to this search context.
         *
         * @param aggregations the aggregations.
         * @return self
         */
        public SearchRequestContextBuilder aggregations(Collection<AggregationSearchContext> aggregations) {
            this.aggregations = aggregations;
            return this;
        }

        /**
         * Adds routig hints.
         *
         * @param routings the routingHints to set
         * @return self
         */
        public SearchRequestContextBuilder routings(List<String> routings) {
            this.routings = routings;
            return this;
        }

        /**
         * Change run exits flag
         *
         * @param runExits run exits flag
         * @return self
         */
        public SearchRequestContextBuilder runExits(boolean runExits) {
            this.runExits = runExits;
            return this;
        }

        /**
         * Set values for last record in last search
         * @param searchAfter search after values
         * @return self
         */
        public SearchRequestContextBuilder searchAfter(List<Object> searchAfter) {
            this.searchAfter = searchAfter;
            return this;
        }

        public SearchRequestContextBuilder shardNumber(Integer shardNumber) {
            this.shardNumber = shardNumber;
            return this;
        }

        public SearchRequestContextBuilder nestedSearch(NestedSearchRequestContext... searches) {
            for (int i = 0; searches != null && i < searches.length; i++) {
                nestedSearch(searches[i]);
            }
            return this;
        }

        public SearchRequestContextBuilder nestedSearch(Collection<NestedSearchRequestContext> searches) {
            if(searches == null) {
                return this;
            }
            searches.forEach(this::nestedSearch);
            return this;
        }

        public SearchRequestContextBuilder nestedSearch(NestedSearchRequestContext newSearch) {
            if(newSearch == null) {
                return this;
            }
            if(nestedSearch == null){
                nestedSearch = new ArrayList<>();
            }

            nestedSearch.add(newSearch);
            return this;
        }
        public SearchRequestContextBuilder must(boolean must) {
            this.must = must;
            return this;
        }

        public SearchRequestContextBuilder scoreFields(Map<String, Float> scoreFields) {
            this.scoreFields = scoreFields;
            return this;
        }
    }
//
//    private static void processSearchRequest(SearchRequestContextBuilder builder){
//        if(EntitySearchType.CLASSIFIER.equals(builder.type)
//                && org.apache.commons.collections4.CollectionUtils.isNotEmpty(builder.form)){
//            for(FormFieldsGroup fieldsGroup: builder.form){
//                Iterator<FormField> i = fieldsGroup.getFormFields().iterator();
//                while (i.hasNext()) {
//                    FormField formField = i.next();
//
//                    if(!formField.getPath().startsWith("$")){
//                        String attrName = StringUtils.substringAfter(formField.getPath(), ".");
//
//                        if(!attrName.startsWith("$")){
//                            String clsName = StringUtils.substringBefore(formField.getPath(), ".");
//                            StringBuilder attrNamePath = new StringBuilder();
//                            StringBuilder attrValuePath = new StringBuilder();
//                            attrNamePath.append(clsName).append(".cls_attrs.$attr_name");
//                            attrValuePath.append(clsName).append(".cls_attrs");
//                            if(formField.getType() == SimpleDataType.NUMBER){
//                                attrValuePath.append(".value_as_double");
//                            } else if(formField.getType() == SimpleDataType.STRING){
//                                attrValuePath.append(".value_as_string");
//                            }
//
//                            FormFieldsGroup nestedGroup = FormFieldsGroup.createAndGroup();
//                            nestedGroup.addFormField(FormField.strictString(attrNamePath.toString(), attrName));
//                            nestedGroup.addFormField(new FormField(formField.getType(),
//                                    attrValuePath.toString(),
//                                    formField.getFormType(),
//                                    formField.getInitialSingleValue(),
//                                    formField.getValues(),
//                                    formField.getRange(),
//                                    formField.getSearchType()
//                            ));
//
//                            builder.nestedSearch(NestedSearchRequestContext.builder(SearchRequestContext.builder()
//                                    .nestedPath(clsName + ".cls_attrs")
//                                    .form(nestedGroup)
//                                    .count(1000)
//                                    .source(false)
//                                    .build())
//                                    .nestedQueryName(attrValuePath.toString())
//                                    .nestedSearchType(NestedSearchRequestContext.NestedSearchType.NESTED_OBJECTS)
//                                    .build());
//
//                            i.remove();
//                        }
//                    }
//                }
//            }
//        }
//    }

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
            .append("searchAfter = ").append(searchAfter == null ? "null" : searchAfter.toString()).append(", ")
            .append("text = ").append(text == null ? "null" : text).append(", ")
            .append("count = ").append(count).append(", ")
            .append("page = ").append(page).append(", ")
            .append("totalCount = ").append(totalCount).append(", ")
            .append("countOnly = ").append(countOnly).append(", ")
            .append("must = ").append(must).append(", ")
            .append("}");

        return b.toString();
    }
}
