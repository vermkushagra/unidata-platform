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

package com.unidata.mdm.backend.service.search.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.routing.Preference;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.AggregationRequestContext;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.common.search.fields.RecordHeaderField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.types.VistoryOperationType;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Mikhail Mikhailov Search agent type.
 */
@Component
public class SearchAgentComponent extends BaseAgentComponent {

    private static final String SCORE_SORT = "_score";
    /**
     * default min score for search
     */
    @Value("${unidata.search.default.min.score:0}")
    private float defaultMinScore;

    /**
     * Transport client to use.
     */
    @Autowired
    private Client client;

    /**
     * Empty args constructor, used by container.
     */
    public SearchAgentComponent() {
        super();
    }

    /**
     * One arg constructor, used by utility.
     */
    public SearchAgentComponent(Client client) {
        super();
        this.client = client;
    }

    /**
     * Parameterized search entry point.
     *
     * @param ctx the context
     * @return {@linkplain SearchResponse} instance
     */
    public SearchResponse parameterizedSearch(final SearchRequestContext ctx) {
        return executeRequest(prepareSearchRequest(ctx,
                createGeneralQueryFromContext(ctx)));
    }

    /**
     * Method for extract all record page by page.
     * @param ctx
     * @return
     */
    public List<SearchResponse> parameterizedScrollScanSearch(final SearchRequestContext ctx) {
        List<SearchResponse> result = new ArrayList<>();
        SearchRequestBuilder searchRequestBuilder = prepareSearchRequest(ctx,
                createGeneralQueryFromContext(ctx));
        SearchResponse scanResponse = executeRequest(searchRequestBuilder);
        int from = ctx.getPage() * ctx.getCount();
        while (true) {
            result.add(scanResponse);

            if (scanResponse.getHits().getHits().length < ctx.getCount()) {
                break;
            }
            from += ctx.getCount();
            searchRequestBuilder.setFrom(from);
            scanResponse = executeRequest(searchRequestBuilder);
        }
        return result;
    }

    public Map<SearchRequestContext, SearchResponse> parameterizedSearch(ComplexSearchRequestContext searchRequest) {

        Map<SearchRequestContext, QueryBuilder> queries = createQueryForComplex(searchRequest);
        if (queries.isEmpty()) {
            return Collections.emptyMap();
        }

        if (searchRequest.getType() == ComplexSearchRequestContext.Type.HIERARCHICAL) {
            SearchRequestContext main = searchRequest.getMainRequest();
            SearchResponse result = executeRequest(prepareSearchRequest(main, queries.get(main)));
            return Collections.singletonMap(main, result);
        } else if (searchRequest.getType() == ComplexSearchRequestContext.Type.MULTI) {

            MultiSearchRequestBuilder msrb = client.prepareMultiSearch()
                    .setIndicesOptions(IndicesOptions.lenientExpandOpen());

            searchRequest.getSupplementary().forEach(ctx -> msrb.add(prepareSearchRequest(ctx, queries.get(ctx))));

            Map<SearchRequestContext, SearchResponse> responses = new HashMap<>(
                    searchRequest.getSupplementary().size());
            MultiSearchResponse response = executeRequest(msrb);

            int i = 0;
            for (SearchRequestContext ctx : searchRequest.getSupplementary()) {
                Item it = response.getResponses()[i++];
                responses.put(ctx, it.getResponse());
            }

            return responses;
        }
        return Collections.emptyMap();
    }

    /**
     * Prepare request method.
     *
     * @param query prepared query
     * @param ctx   context
     * @return result
     */
    private SearchRequestBuilder prepareSearchRequest(SearchRequestContext ctx, QueryBuilder query) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);
        int from = ctx.getPage() * ctx.getCount();
        int size = (ctx.getPage() * ctx.getCount() + ctx.getCount()) > getMaxWindowSize()
                ? ctx.getCount() - ((ctx.getPage() * ctx.getCount() + ctx.getCount()) - getMaxWindowSize())
                : ctx.getCount();


        SearchRequestBuilder srb = client.prepareSearch(indexName)
                .setTypes(ctx.getType().getName())
                //.setFetchSource(ctx.isSource())
                .setQuery(query)
                .setMinScore(ctx.getScore() != null ? ctx.getScore() : defaultMinScore)
                .setFrom(from)
                .setSize(size);

        if(ctx.getShardNumber() != null){
            srb.setPreference(Preference.SHARDS.type() + ":" + ctx.getShardNumber().toString());
        }

        if (CollectionUtils.isNotEmpty(ctx.getRoutings())) {
            srb.setRouting(ctx.getRoutings().toArray(new String[ctx.getRoutings().size()]));
        }

        if (!ctx.isOnlyQuery()) {
            addFilters(srb, ctx);
        }

        addSorts(srb, ctx);

        if(CollectionUtils.isNotEmpty(ctx.getSearchAfter())){
            srb.searchAfter(ctx.getSearchAfter().toArray());
            // search after can be used only with from equals 0
            srb.setFrom(0);
        }

        addAggregations(srb, ctx);

        srb.setFetchSource(SearchUtils.extractReturnFields(ctx), null);
        return srb;
    }



    /**
     * Add filters to request.
     *
     * @param srb search request builder to add filters to
     * @param ctx the context to process
     */
    private void addFilters(SearchRequestBuilder srb, SearchRequestContext ctx) {

        List<QueryBuilder> filters = new ArrayList<>();

        TermQueryBuilder activeRecords = QueryBuilders.termQuery(
                RecordHeaderField.FIELD_DELETED.getField(), Boolean.FALSE);

        TermQueryBuilder activePeriods = QueryBuilders.termQuery(
                RecordHeaderField.FIELD_INACTIVE.getField(), Boolean.FALSE);

        Date point = ctx.getAsOf() == null ? new Date() : ctx.getAsOf();

        BoolQueryBuilder asOfDate = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.rangeQuery(RecordHeaderField.FIELD_FROM.getField()).lte(point.getTime()))
                .must(QueryBuilders.rangeQuery(RecordHeaderField.FIELD_TO.getField()).gte(point.getTime()));

        filters.add(asOfDate);
        filters.add(activePeriods);
        if (ctx.getText() == null || !ctx.getText().contains(RecordHeaderField.FIELD_DELETED.getField())) {
            filters.add(activeRecords);
        }

        if (ctx.getFacets() != null && ctx.getFacets().contains(FacetName.FACET_NAME_ACTIVE_ONLY)
                && (ctx.getFacets().contains(FacetName.FACET_NAME_INACTIVE_ONLY)
                || ctx.getFacets().contains(FacetName.FACET_NAME_PENDING_ONLY))) {
            throw new BusinessException("Those facets can't be applied together:" + ctx.getFacets().toString(),
                    ExceptionId.EX_SEARCH_UNAVAILABLE_FACETS_COMBINATION);
        }

        // Data: see all published or own new records.
        // This applies to records only
        if (ctx.getType() == EntitySearchType.ETALON_DATA) {
            String user = SecurityUtils.getCurrentUserName();
            final BoolQueryBuilder published = QueryBuilders
                    .boolQuery()
                    .should(QueryBuilders.termQuery(RecordHeaderField.FIELD_PUBLISHED.getField(), Boolean.TRUE))
                    .should(QueryBuilders.termQuery(RecordHeaderField.FIELD_ORIGINATOR.getField(), user));
            filters.add(published);


            applyFacets(filters, ctx.getFacets(), activeRecords, activePeriods, asOfDate, ctx.isFetchAll(), published);
            addSecurityFilter(filters, ctx);
        }

        if (!filters.isEmpty()) {
            BoolQueryBuilder result = QueryBuilders.boolQuery();
            filters.forEach(result::must);
            srb.setPostFilter(result);
        }
    }

    private void applyFacets(
            List<QueryBuilder> filters, List<FacetName> facets,
            TermQueryBuilder activeRecords,
            TermQueryBuilder activePeriods,
            BoolQueryBuilder asOfDate,
            boolean fetchAll, BoolQueryBuilder published
    ) {

        if (CollectionUtils.isEmpty(facets)) {
            return;
        }

        if (facets.contains(FacetName.FACET_NAME_ACTIVE_ONLY)
                && (facets.contains(FacetName.FACET_NAME_INACTIVE_ONLY) || facets.contains(FacetName.FACET_NAME_PENDING_ONLY))) {
            throw new BusinessException("Those facets can't be applied together:" + facets.toString(), ExceptionId.EX_SEARCH_UNAVAILABLE_FACETS_COMBINATION);
        }

        List<VistoryOperationType> operationTypes = new ArrayList<>();

        facets.stream().filter(Objects::nonNull).forEach(f -> {
            switch (f) {
                case FACET_NAME_ERRORS_ONLY:
                    if(fetchAll) {
                        filters.add(
                                QueryBuilders.nestedQuery(RecordHeaderField.FIELD_DQ_ERRORS.getField(),
                                        QueryBuilders.existsQuery(RecordHeaderField.FIELD_DQ_ERRORS.getField()),
                                        ScoreMode.None)
                        );
                    }
                    break;
                case FACET_NAME_INACTIVE_ONLY:
                    filters.remove(activeRecords);
                    filters.add(QueryBuilders.termQuery(RecordHeaderField.FIELD_DELETED.getField(), Boolean.TRUE));
                    break;
                case FACET_NAME_PENDING_ONLY:
                    filters.remove(published);
                    filters.add(QueryBuilders.termQuery(RecordHeaderField.FIELD_PENDING.getField(), Boolean.TRUE));
                    break;
                case FACET_NAME_ACTIVE_ONLY:
                    filters.add(QueryBuilders.termQuery(RecordHeaderField.FIELD_PENDING.getField(), Boolean.FALSE));
                    break;
                case FACET_NAME_PUBLISHED_ONLY:
                    filters.add(QueryBuilders.termQuery(RecordHeaderField.FIELD_PUBLISHED.getField(), Boolean.TRUE));
                    break;
                case FACET_UN_RANGED:
                    filters.remove(asOfDate);
                    break;
                case FACET_NAME_OPERATION_TYPE_DIRECT:
                    operationTypes.add(VistoryOperationType.DIRECT);
                    break;
                case FACET_NAME_OPERATION_TYPE_CASCADED:
                    operationTypes.add(VistoryOperationType.CASCADED);
                    break;
                case FACET_NAME_INACTIVE_PERIODS:
                    filters.remove(activePeriods);
                    break;
                default:
                    break;
            }
        });

        if (!operationTypes.isEmpty()) {
            filters.add(QueryBuilders.termsQuery(RecordHeaderField.FIELD_OPERATION_TYPE.getField(),
                    operationTypes.stream().map(Enum::name).collect(Collectors.toList())));
        }
    }

    /**
     * Adds aggregations to search request builder.
     * @param srb search request builder
     * @param ctx search request context
     */
    private void addAggregations(SearchRequestBuilder srb, SearchRequestContext ctx) {

        if (CollectionUtils.isEmpty(ctx.getAggregations())) {
            return;
        }

        Collection<AggregationRequestContext> arcs = ctx.getAggregations();
        for (AggregationRequestContext aCtx : arcs) {
            srb.addAggregation(createAggregation(aCtx));
        }
    }

    /**
     * @param srb SearchRequestBuilder
     * @param ctx search context
     */
    private void addSorts(SearchRequestBuilder srb, SearchRequestContext ctx) {
        for (SortField sortField : ctx.getSortFields()) {
            SortOrder sortOrder = sortField.getSortOrder() == SortField.SortOrder.ASC ? SortOrder.ASC : SortOrder.DESC;
            String sortFieldName = sortField.isAnalyzedAttribute() ? stringNonAnalyzedField(sortField.getFieldName()) : sortField.getFieldName();
            srb.addSort(sortFieldName, sortOrder);
        }
        if(ctx.isScoreEnabled()){
            srb.addSort(SCORE_SORT, SortOrder.DESC);
        }
    }

    /**
     * Adds data level security to elastic query.
     *
     * @param filters list with filters
     * @param ctx     search context
     */
    private void addSecurityFilter(final List<QueryBuilder> filters, final SearchRequestContext ctx) {

        List<SecurityLabel> labels = SecurityUtils.getSecurityLabelsForResource(ctx.getEntity());
        if (CollectionUtils.isEmpty(labels)) {
            return;
        }

        final Map<String, List<SecurityLabel>> grouped = labels.stream()
                .collect(Collectors.groupingBy(SecurityLabel::getName));

        final BoolQueryBuilder groupFilter = QueryBuilders.boolQuery();
        for (Entry<String, List<SecurityLabel>> group : grouped.entrySet()) {

            BoolQueryBuilder orFilter = QueryBuilders.boolQuery();
            for (final SecurityLabel sl : group.getValue()) {

                final BoolQueryBuilder filterAnd = QueryBuilders.boolQuery();
                sl.getAttributes().forEach(sla -> {

                    final String entityName = StringUtils.substringBefore(sla.getPath(), ".");
                    final String attrPath = StringUtils.substringAfter(sla.getPath(), ".");
                    if (StringUtils.isNotEmpty(sla.getValue())) {
                        // todo refactoring this check
                        filterAnd.must(
                                QueryBuilders.termQuery(
                                        ensureStringFieldForTermQueries(entityName, attrPath),
                                        sla.getValue()));
                    }
                });

                orFilter.should(filterAnd);
            }

            groupFilter.must(orFilter);
        }

        filters.add(groupFilter);
    }

}