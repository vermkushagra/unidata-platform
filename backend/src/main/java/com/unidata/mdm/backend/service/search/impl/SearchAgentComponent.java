package com.unidata.mdm.backend.service.search.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.context.AggregationRequestContext;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.auth.SecurityLabel;
import com.unidata.mdm.backend.common.search.FacetName;
import com.unidata.mdm.backend.common.search.SortField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Mikhail Mikhailov Search agent type.
 */
@Component
public class SearchAgentComponent extends BaseAgentComponent {

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
     * Performs parameterized 'count' on the supplied query and returns the
     * result.
     *
     * @param ctx the context to use
     * @return {@linkplain SearchResponse} instance
     */
    public SearchResponse parameterizedCount(final SearchRequestContext ctx) {
        return executeRequest(prepareCountRequest(ctx,
                createGeneralQueryFromContext(ctx)));
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
     * This is fast realisation for get all entities
     *
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
     * Simple SAYT interface.
     *
     * @param ctx the context
     * @return response
     */
    public SearchResponse parameterizedSayt(final SearchRequestContext ctx) {
        return executeRequest(prepareSearchRequest(ctx, createSaytQueryFromContext(ctx)));
    }

    /**
     * Creates query builder from context.
     *
     * @param ctx the context
     * @return query builder
     */
    private QueryBuilder createSaytQueryFromContext(final SearchRequestContext ctx) {

        if (ctx.isFetchAll()) {
            return null;
        }

        if (ctx.getSearchFields().size() == 1) {
            return QueryBuilders
                    .matchQuery(ctx.getSearchFields().get(0),
                            ctx.getText())
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(Operator.AND)
                    .analyzer(SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
                    .lenient(true);
        } else {
            return QueryBuilders
                    .multiMatchQuery(
                            ctx.getText(),
                            ctx.getSearchFields().toArray(
                                    new String[ctx.getSearchFields().size()]))
                    .maxExpansions(SearchUtils.DEFAULT_MAX_EXPANSIONS_VALUE)
                    .slop(SearchUtils.DEFAULT_SLOP_VALUE)
                    .operator(Operator.AND)
                    .analyzer(SearchUtils.STANDARD_STRING_ANALYZER_NAME)
                    .zeroTermsQuery(MatchQuery.ZeroTermsQuery.ALL)
                    .lenient(true);
        }
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
        SearchRequestBuilder srb = client.prepareSearch(indexName)
                .setTypes(ctx.getType().getName())
                //.setFetchSource(ctx.isSource())
                .setQuery(query)
                .setMinScore(ctx.getScore() != null ? ctx.getScore() : defaultMinScore)
                .setFrom(ctx.getPage() * ctx.getCount())
                .setSize(ctx.getCount());

        if(ctx.getShardNumber() != null){
            srb.setPreference(Preference.SHARDS.type() + ":" + ctx.getShardNumber().toString());
        }

        if (!CollectionUtils.isEmpty(ctx.getRoutings())) {
            srb.setRouting(ctx.getRoutings().toArray(new String[ctx.getRoutings().size()]));
        }

        if (!ctx.isOnlyQuery()) {
            addFilters(srb, ctx);
        }

        addSorts(srb, ctx);
        addAggregations(srb, ctx);

        srb.setFetchSource(SearchUtils.extractReturnFields(ctx), null);
        return srb;
    }

    /**
     * Prepares a count request.
     *
     * @param ctx   the context
     * @param query the query
     * @return request builder
     */
    private SearchRequestBuilder prepareCountRequest(SearchRequestContext ctx,
                                                     QueryBuilder query) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);
        SearchRequestBuilder srb = client.prepareSearch(indexName)
                .setTypes(ctx.getType().getName())
                .setFetchSource(false)
                .setQuery(query)
                .setFrom(0)
                .setSize(0);

        if (!ctx.isOnlyQuery()) {
            addFilters(srb, ctx);
        }

        addAggregations(srb, ctx);
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

        Date point = ctx.getAsOf() == null ? new Date() : ctx.getAsOf();

        BoolQueryBuilder asOfDate = QueryBuilders
                .boolQuery()
                .must(QueryBuilders.rangeQuery(RecordHeaderField.FIELD_FROM.getField()).lte(point.getTime()))
                .must(QueryBuilders.rangeQuery(RecordHeaderField.FIELD_TO.getField()).gte(point.getTime()));

        filters.add(asOfDate);
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
            filters.add(QueryBuilders
                    .boolQuery()
                    .should(QueryBuilders.termQuery(RecordHeaderField.FIELD_PUBLISHED.getField(), Boolean.TRUE))
                    .should(QueryBuilders.termQuery(RecordHeaderField.FIELD_ORIGINATOR.getField(), user)));

            for (int i = 0; ctx.getFacets() != null && i < ctx.getFacets().size(); i++) {

                FacetName f = ctx.getFacets().get(i);
                if (FacetName.FACET_NAME_ERRORS_ONLY == f) {
                    filters.add(QueryBuilders.nestedQuery(RecordHeaderField.FIELD_DQ_ERRORS.getField(),
                            QueryBuilders.existsQuery(RecordHeaderField.FIELD_DQ_ERRORS.getField()), ScoreMode.None));
                }
                if (FacetName.FACET_NAME_INACTIVE_ONLY == f) {
                    filters.remove(activeRecords);
                    filters.add(QueryBuilders.termQuery(RecordHeaderField.FIELD_DELETED.getField(), Boolean.TRUE));
                }
                if (FacetName.FACET_NAME_PENDING_ONLY == f) {
                    filters.add(QueryBuilders.termQuery(RecordHeaderField.FIELD_PENDING.getField(), Boolean.TRUE));
                }
                if (FacetName.FACET_NAME_ACTIVE_ONLY == f) {
                    filters.add(QueryBuilders.termQuery(RecordHeaderField.FIELD_PENDING.getField(), Boolean.FALSE));
                }
                if (FacetName.FACET_NAME_PUBLISHED_ONLY == f) {
                    filters.add(QueryBuilders.termQuery(RecordHeaderField.FIELD_PUBLISHED.getField(), Boolean.TRUE));
                }
                if (FacetName.FACET_UN_RANGED == f) {
                    filters.remove(asOfDate);
                }
            }

            addSecurityFilter(filters, ctx);
        }

        if (!filters.isEmpty()) {
            BoolQueryBuilder result = QueryBuilders.boolQuery();
            filters.stream().forEach(result::must);
            srb.setPostFilter(result);
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
            String sortFieldName = sortField.isAnalyzedAttribute() ? sortField.getFieldName() + SearchUtils.DOT + SearchUtils.NAN_FIELD : sortField.getFieldName();
            srb.addSort(sortFieldName, sortOrder);
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
                    if (!StringUtils.isEmpty(sla.getValue())) {

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

    /**
     * Count errors by severity.
     *
     * @param severity severity.
     * @return severity.
     */
    public long countErrorsBySeverity(String severity, SearchRequestContext ctx) {
        // todo refactoring this method
        BoolQueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(RecordHeaderField.FIELD_DELETED.getField(), false))
                .must(QueryBuilders.rangeQuery(RecordHeaderField.FIELD_FROM.getField()).lte(ctx.getAsOf().getTime()))
                .must(QueryBuilders.rangeQuery(RecordHeaderField.FIELD_TO.getField()).gte(ctx.getAsOf().getTime()))
                .must(QueryBuilders.nestedQuery(RecordHeaderField.FIELD_DQ_ERRORS.getField(), QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery(RecordHeaderField.FIELD_DQ_ERRORS.getField() + ".severity", severity)), ScoreMode.None));
        return prepareCountRequest(ctx, qb).get().getHits().getTotalHits();

    }

    /**
     * Count records with errors.
     *
     * @param ctx search context.
     * @return number of records with errors.
     */
    public long countErrorRecords(SearchRequestContext ctx) {
        BoolQueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(RecordHeaderField.FIELD_DELETED.getField(), false))
                .must(QueryBuilders.rangeQuery(RecordHeaderField.FIELD_FROM.getField()).lte(ctx.getAsOf().getTime()))
                .must(QueryBuilders.rangeQuery(RecordHeaderField.FIELD_TO.getField()).gte(ctx.getAsOf().getTime()))
                .must(QueryBuilders.nestedQuery(RecordHeaderField.FIELD_DQ_ERRORS.getField(),
                        QueryBuilders.existsQuery(RecordHeaderField.FIELD_DQ_ERRORS.getField()), ScoreMode.None));
        return prepareCountRequest(ctx, qb).get().getHits().getTotalHits();
    }
}
