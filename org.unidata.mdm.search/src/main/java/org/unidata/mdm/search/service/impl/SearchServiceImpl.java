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

package org.unidata.mdm.search.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.elasticsearch.search.aggregations.bucket.SingleBucketAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidata.mdm.search.configuration.SearchConfigurationConstants;
import org.unidata.mdm.search.context.AggregationSearchContext;
import org.unidata.mdm.search.context.CardinalityAggregationRequestContext;
import org.unidata.mdm.search.context.ComplexSearchRequestContext;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.context.MappingRequestContext;
import org.unidata.mdm.search.context.NestedSearchRequestContext;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.context.TypedSearchContext;
import org.unidata.mdm.search.context.ValueCountAggregationRequestContext;
import org.unidata.mdm.search.dto.AggregationResultDTO;
import org.unidata.mdm.search.dto.SearchResultDTO;
import org.unidata.mdm.search.dto.SearchResultHitDTO;
import org.unidata.mdm.search.dto.SearchResultHitFieldDTO;
import org.unidata.mdm.search.exception.SearchApplicationException;
import org.unidata.mdm.search.exception.SearchExceptionIds;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov Search service aggregate, containing all the
 */
@Service
public class SearchServiceImpl implements SearchService {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServiceImpl.class);
    /**
     * Search component.
     */
    @Autowired
    private SearchAgentComponent searchComponent;
    /**
     * Admin component.
     */
    @Autowired
    private AdminComponentImpl adminComponent;

    // FIXME: Commented out in scope of UN-11834. Invisible here.
    /*
    @Autowired
    private ConfigurationServiceExt configurationService;
    */

    @Autowired
    private MappingComponentImpl mappingImpl;

    @Autowired
    private IndexComponentImpl indexingImpl;

    /**
     * Number of shards for system indexes
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_SYSTEM_SHARDS_NUMBER_PROPERTY + ":}")
    private String numberOfShardsForSystem = "";
    /**
     * Number of replicas for system indexes
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_SYSTEM_REPLICAS_NUMBER_PROPERTY + ":}")
    private String numberOfReplicasForSystem = "";
    /**
     * Delay for async audit operations.
     */
    @Value("${unidata.data.refresh.immediate:true}")
    private boolean refreshImmediate;

    // FIXME Kill this!
    private static final String FIELD_ETALON_ID = "$etalon_id";

    /**
     * Empty arguments constructor used by spring.
     */
    public SearchServiceImpl() {
        super();
    }

    /**
     * Client arg constructor used by command line utilities.
     *
     * @param client search client
     */
    public SearchServiceImpl(Client client) {
        super();
        this.searchComponent = new SearchAgentComponent(client);
        this.adminComponent = new AdminComponentImpl(client);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setClusterSettings(Map<String, Object> settings, boolean persistent) {
        try {
            return adminComponent.setClusterSettings(settings, persistent);
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    private IndexType getSearchType(@Nonnull ComplexSearchRequestContext context) {
        if (context.getType() == ComplexSearchRequestContext.Type.HIERARCHICAL) {
            return context.getMainRequest().getType();
        } else {
            IndexType first = context.getSupplementary().iterator().next().getType();
            boolean notAllTheSame = context.getSupplementary()
                    .stream()
                    .map(SearchRequestContext::getType)
                    .anyMatch(type -> !Objects.equals(type, first));
            if (notAllTheSame) {
                throw new PlatformFailureException("Try to update fields in another search type",
                        SearchExceptionIds.EX_SEARCH_NOT_RELATED_SEARCH_TYPES_IN_MARK_OPERATION);
            }
            return first;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteAll(SearchRequestContext ctx) {
        return adminComponent.deleteAll(ctx, true);
    }

    @Override
    public boolean deleteAll(SearchRequestContext ctx, boolean refresh) {
        return adminComponent.deleteAll(ctx, refresh);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFoundResult(SearchRequestContext requestForDelete) {
        return adminComponent.delete(requestForDelete, true);
    }

    @Override
    public boolean deleteFoundResult(SearchRequestContext requestForDelete, boolean refreshImmediate) {
        return adminComponent.delete(requestForDelete, refreshImmediate);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFoundResult(ComplexSearchRequestContext requestForDelete) {
        return adminComponent.delete(requestForDelete, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countAll(SearchRequestContext ctx) {
        return searchComponent.countAll(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<SearchRequestContext, SearchResultDTO> search(ComplexSearchRequestContext searchRequest) {
        try {
            return extractSearchResults(searchComponent.parameterizedSearch(searchRequest));
        } catch (ElasticsearchException esx) {
            throw processElasticsearchException(esx, searchRequest.getMainRequest() != null
                            ? searchRequest.getMainRequest().getEntity()
                            : searchRequest.getSupplementary().iterator().next().getEntity(),
                    searchRequest);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultDTO search(final SearchRequestContext ctx) {
        MeasurementPoint.start();
        try {
            List<String> beforeExitWarningMessages = null;
            /*
            SearchImpl searchExits = configurationService.getSearch();
            if (ctx.isRunExits() && searchExits != null) {
                Collection<SearchListener> beforeSearchListeners = configurationService.getListeners(
                        ctx.getEntity(),
                        configurationService.getSearch().getBeforeSearchInstances());
                if (CollectionUtils.isNotEmpty(beforeSearchListeners)) {
                    for (SearchListener beforeSearchListener : beforeSearchListeners) {
                        ExitResult beforeExitResult = beforeSearchListener.beforeSearch(ctx);
                        if (beforeExitResult != null && ExitResult.Status.ERROR.equals(beforeExitResult.getStatus())) {
                            LOGGER.error("Error occurred during run before search user exit: {}", beforeExitResult.getWarningMessage());
                            throw new SearchApplicationException("Error occurred during run before search user exit",
                                    ExceptionId.EX_SEARCH_BEFORE_USER_EXIT_EXCEPTION, beforeExitResult.getWarningMessage());
                        }
                        if (beforeExitResult != null && ExitResult.Status.WARNING.equals(beforeExitResult.getStatus())) {
                            LOGGER.warn("Warning occurred during run before search user exit: {}", beforeExitResult.getWarningMessage());
                            if (beforeExitWarningMessages == null) {
                                beforeExitWarningMessages = new ArrayList<>();
                            }
                            beforeExitWarningMessages.add(beforeExitResult.getWarningMessage());
                        }
                    }
                }
            }
            */
            SearchResultDTO result;
            if (ctx.isScrollScan()) {
                result = extractSearchResult(ctx, searchComponent.parameterizedScrollScanSearch(ctx));
            } else {
                result = extractSearchResult(ctx, searchComponent.parameterizedSearch(ctx));
            }
            /*
            if (CollectionUtils.isNotEmpty(beforeExitWarningMessages)) {
                Collections.reverse(beforeExitWarningMessages);
                if (result.getErrors() == null) {
                    result.setErrors(new ArrayList<>());
                }
                for (String warningMessage : beforeExitWarningMessages) {
                    ErrorInfoDTO errorInfo = new ErrorInfoDTO();
                    errorInfo.setSeverity(ErrorInfoDTO.Severity.LOW);
                    errorInfo.setUserMessage(MessageUtils.getMessage(SearchConstants.SEARCH_BEFORE_USER_EXIT_EXCEPTION,
                            warningMessage));
                    result.getErrors().add(0, errorInfo);
                }
            }
            */

            return result;
        } catch (ElasticsearchException esx) {
            throw processElasticsearchException(esx, ctx.getEntity(), ctx);
        } finally {
            MeasurementPoint.stop();
        }
    }


    /**
     * Extracts multiple results.
     *
     * @param input the input
     * @return map
     */
    private Map<SearchRequestContext, SearchResultDTO> extractSearchResults(Map<SearchRequestContext, SearchResponse> input) {

        if (MapUtils.isEmpty(input)) {
            return Collections.emptyMap();
        }

        Map<SearchRequestContext, SearchResultDTO> result = new HashMap<>();
        for (Entry<SearchRequestContext, SearchResponse> ent : input.entrySet()) {
            result.put(ent.getKey(), extractSearchResult(ent.getKey(), ent.getValue()));
        }

        return result;
    }

    private SearchResultDTO extractSearchResult(SearchRequestContext ctx, SearchResponse response) {
        return extractSearchResult(ctx, Collections.singletonList(response));
    }

    /**
     * Extract result.
     *
     * @param ctx search context
     * @param responses responses
     * @return result
     */
    private SearchResultDTO extractSearchResult(SearchRequestContext ctx, List<SearchResponse> responses) {

        SearchResultDTO result = new SearchResultDTO();
        result.setTotalCountLimit(searchComponent.getMaxWindowSize());
        long totalCount = responses.iterator().next().getHits().getTotalHits();
        result.setTotalCount(totalCount);

        if (ctx.isCountOnly()) {
            return result;
        }

        float maxScore = 0;
        List<SearchResultHitDTO> srhs = new ArrayList<>();
        List<AggregationResultDTO> aggs = CollectionUtils.isNotEmpty(ctx.getAggregations())
                ? new ArrayList<>(ctx.getAggregations().size())
                : null;

        for (SearchResponse response : responses) {// Set total count if required
            maxScore = Math.max(maxScore, response.getHits().getMaxScore());
            extractHits(ctx, response.getHits().getHits(), srhs);

            Aggregations aggregates = response.getAggregations();
            if (!CollectionUtils.isEmpty(ctx.getAggregations()) && Objects.nonNull(aggregates)) {

                for (AggregationSearchContext aCtx : ctx.getAggregations()) {
                    AggregationResultDTO aggregationResult = extractAggregationResult(aCtx, aggregates.get(aCtx.getName()));
                    if (Objects.nonNull(aggregationResult)) {
                        aggs.add(aggregationResult);
                    }
                }
            }
        }
        SearchResponse lastResponse = responses.get(responses.size() - 1);
        if (lastResponse.getHits().getHits().length > 0) {
            result.setSortValues(Arrays.asList(lastResponse.getHits().getHits()[lastResponse.getHits().getHits().length - 1].getSortValues()));
        }

        result.setHits(srhs);
        result.setMaxScore(maxScore);
        result.setAggregates(aggs);

        // Set resulting cached field names (fields or form)
        result.setFields(ctx.getReturnFields() != null ? ctx.getReturnFields() : ctx.getSearchFields());

        runAfterUserExitIfNeed(ctx, result);
        return result;
    }

    private void extractHits(SearchRequestContext ctx, SearchHit[] hits, List<SearchResultHitDTO> srhs) {
        if (hits == null) {
            return;
        }

        if (ctx.isSource()) {
            Arrays.stream(hits).forEach(hit -> {
                Object id = hit.getSourceAsMap().get(FIELD_ETALON_ID);
                srhs.add(new SearchResultHitDTO(
                        id != null ? id.toString() : hit.getId(),
                        hit.getId(),
                        hit.getScore(),
                        null,
                        hit.getSourceAsString()));
            });
        } else {
            List<String> returnFields = ctx.getReturnFields() == null
                    ? Collections.singletonList(FIELD_ETALON_ID)
                    : new ArrayList<>(ctx.getReturnFields());

            if (!returnFields.contains(FIELD_ETALON_ID)) {
                returnFields.add(FIELD_ETALON_ID);
            }

            for (SearchHit hit : hits) {

                SearchResultHitDTO hitToAdd = extractHit(returnFields, hit);
                srhs.add(hitToAdd);

                if (MapUtils.isNotEmpty(hit.getInnerHits()) && CollectionUtils.isNotEmpty(ctx.getNestedSearch())) {
                    for (NestedSearchRequestContext innerHitMapping : ctx.getNestedSearch()) {
                        SearchHits searchInnerHits = hit.getInnerHits().get(innerHitMapping.getNestedQueryName());
                        if (searchInnerHits != null) {
                            List<SearchResultHitDTO> innerHits = new ArrayList<>();
                            Arrays.stream(searchInnerHits.getHits()).forEach(innerHit ->
                                    innerHits.add(extractHit(innerHitMapping.getNestedSearch().getReturnFields(), innerHit)));
                            hitToAdd.addInnerHit(innerHitMapping.getNestedQueryName(), innerHits);
                        }
                    }
                }

            }
        }
    }

    private SearchResultHitDTO extractHit(List<String> returnFields, SearchHit hit) {

        Map<String, SearchResultHitFieldDTO> preview = null;
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        if (sourceAsMap == null) {
            preview = new HashMap<>(0);
        } else {
            preview = new HashMap<>(returnFields.size());

            for (String returnField : returnFields) {
                Object value = XContentMapValues.extractRawValues(returnField, sourceAsMap);
                if (value != null) {
                    preview.put(returnField, new SearchResultHitFieldDTO(returnField,
                            value instanceof List
                                    ? (List) value
                                    : Collections.singletonList(value)));
                }
            }
        }

        Object id = hit.getSourceAsMap().get(FIELD_ETALON_ID);
        return new SearchResultHitDTO(
                id != null ? id.toString() : hit.getId(),
                hit.getId(),
                hit.getScore(),
                preview,
                null);

    }

    private void runAfterUserExitIfNeed(SearchRequestContext ctx, SearchResultDTO result) {
        // FIXME: @Modules User exits won't work!
        /*
        SearchImpl searchExits = configurationService.getSearch();
        if (!ctx.isRunExits() || searchExits == null) {
            return;
        }

        Collection<SearchListener> afterSearchListeners = configurationService.getListeners(
                ctx.getEntity(),
                configurationService.getSearch().getAfterSearchInstances());
        if (CollectionUtils.isNotEmpty(afterSearchListeners)) {
            for (SearchListener afterSearchListener : afterSearchListeners) {
                ExitResult exitResult = afterSearchListener.afterSearch(ctx, result);
                if (exitResult != null && ExitResult.Status.ERROR.equals(exitResult.getStatus())) {
                    LOGGER.error("Error occurred during run after search user exit: {}", exitResult.getWarningMessage());
                    throw new SearchApplicationException("Error occurred during run after search user exit",
                            ExceptionId.EX_SEARCH_AFTER_USER_EXIT_EXCEPTION, exitResult.getWarningMessage());
                }
                if (exitResult != null && ExitResult.Status.WARNING.equals(exitResult.getStatus())) {
                    LOGGER.warn("Warning occurred during run after search user exit: {}", exitResult.getWarningMessage());
                    if (result.getErrors() == null) {
                        result.setErrors(new ArrayList<>());
                    }
                    ErrorInfoDTO errorInfo = new ErrorInfoDTO();
                    errorInfo.setSeverity(ErrorInfoDTO.Severity.LOW);
                    errorInfo.setUserMessage(MessageUtils.getMessage(SearchConstants.SEARCH_AFTER_USER_EXIT_EXCEPTION,
                            exitResult.getWarningMessage()));
                    result.getErrors().add(errorInfo);
                }
            }
        }
        */
    }

    /**
     * Extracts aggregation tree.
     *
     * @param request the request
     * @param response the response
     * @return tresult
     */
    private AggregationResultDTO extractAggregationResult(AggregationSearchContext request, Aggregation response) {

        if (Objects.isNull(request) || Objects.isNull(response)) {
            return null;
        }

        switch (request.getAggregationType()) {
            case CARDINALITY:
                CardinalityAggregationRequestContext caCtx = request.narrow();
                AggregationResultDTO cardinalityResult = new AggregationResultDTO(request.getName(), request.getAggregationType(), 1, false);
                cardinalityResult.add(caCtx.getPath(), ((Cardinality) response).getValue());
                cardinalityResult.setDocumentsCount(1);
                return cardinalityResult;
            case VALUE_COUNT:
                ValueCountAggregationRequestContext vcCtx = request.narrow();
                AggregationResultDTO valueCountResult = new AggregationResultDTO(request.getName(), request.getAggregationType(), 1, false);
                valueCountResult.add(vcCtx.getPath(), ((ValueCount) response).getValue());
                valueCountResult.setDocumentsCount(1);
                return valueCountResult;
            case FILTER:
                Filter filterResponse = (Filter) response;
                return extractSingleBucketAggregation(request, filterResponse);
            case NESTED:
                Nested nestedResponse = (Nested) response;
                return extractSingleBucketAggregation(request, nestedResponse);
            case REVERSE_NESTED:
                ReverseNested reverseNestedResponse = (ReverseNested) response;
                return extractSingleBucketAggregation(request, reverseNestedResponse);
            case TERM:
                Terms termsResponse = (Terms) response;
                return extractMultiBucketAggregation(request, termsResponse);
            default:
                break;
        }

        return null;
    }

    /**
     * Extracts single bucket aggregation.
     *
     * @param request request
     * @param response response
     * @return aggregation result
     */
    @Nonnull
    private AggregationResultDTO extractSingleBucketAggregation(AggregationSearchContext request, SingleBucketAggregation response) {

        AggregationResultDTO singleBucketResult
                = new AggregationResultDTO(
                response.getName(),
                request.getAggregationType(),
                1,
                response.getDocCount() > 0);

        singleBucketResult.setDocumentsCount(response.getDocCount());
        if (singleBucketResult.getDocumentsCount() == 0) {
            return singleBucketResult;
        }

        for (AggregationSearchContext ctx : request.aggregations()) {

            AggregationResultDTO subAggregate = extractAggregationResult(ctx, response.getAggregations().get(ctx.getName()));
            if (Objects.isNull(subAggregate)) {
                continue;
            }

            // Single bucket, total count is the sub aggregation count.
            singleBucketResult.add(ctx.getName(), response.getDocCount());
            singleBucketResult.add(ctx.getName(), subAggregate);
        }

        return singleBucketResult;
    }

    /**
     * Extracts multi bucket aggregation.
     *
     * @param request request
     * @param response response
     * @return aggregation result
     */
    @Nonnull
    private AggregationResultDTO extractMultiBucketAggregation(AggregationSearchContext request, MultiBucketsAggregation response) {


        Collection<? extends Bucket> buckets = response.getBuckets();
        if (CollectionUtils.isEmpty(buckets)) {
            return new AggregationResultDTO(request.getName(), request.getAggregationType(), 0, false);
        }

        AggregationResultDTO multiBucketResult
                = new AggregationResultDTO(request.getName(), request.getAggregationType(), buckets.size(), !CollectionUtils.isEmpty(request.aggregations()));

        long totalCount = 0;
        for (Bucket bucket : buckets) {

            totalCount += bucket.getDocCount();
            multiBucketResult.add(bucket.getKey().toString(), bucket.getDocCount());

            for (AggregationSearchContext ctx : request.aggregations()) {

                AggregationResultDTO subAggregate = extractAggregationResult(ctx, bucket.getAggregations().get(ctx.getName()));
                if (Objects.isNull(subAggregate)) {
                    continue;
                }

                multiBucketResult.add(bucket.getKey().toString(), subAggregate);
            }
        }

        multiBucketResult.setDocumentsCount(totalCount);
        return multiBucketResult;
    }

    /**
     * {@inheritDoc}
     */


    private SearchApplicationException processElasticsearchException(ElasticsearchException ex, String entityName, Object... args) {

        StringBuilder elasticMessage = new StringBuilder();
        if (ex.guessRootCauses() != null) {
            for (ElasticsearchException rootEsx : ex.guessRootCauses()) {
                if (rootEsx instanceof IndexNotFoundException
                        || StringUtils.containsIgnoreCase(rootEsx.getDetailedMessage(), "No mapping found")) {
                    throw new SearchApplicationException(elasticMessage.toString(), ex,
                            SearchExceptionIds.EX_SEARCH_ES_NO_MAPPING_FOUND, entityName);
                } else {
                    elasticMessage.append(rootEsx.getMessage()).append(System.lineSeparator());
                }
            }
        }

        if (elasticMessage.length() == 0) {
            elasticMessage.append("Search exception caught.");
        }

        final String message = elasticMessage.toString();
        LOGGER.warn(message, ex);
        throw new SearchApplicationException(elasticMessage.toString(), ex, SearchExceptionIds.EX_SEARCH_ES_ESC_CAUGHT, args);
    }

    // Fixme: Commented out in scope of UN-11834. New stuff. Add it.
    @Override
    public void process(IndexRequestContext ctx) {
        indexingImpl.process(ctx);
    }

    @Override
    public void process(Collection<IndexRequestContext> ctxs) {
        indexingImpl.process(ctxs, false);
    }

    @Override
    public void process(Collection<IndexRequestContext> ctxs, boolean refresh) {
        indexingImpl.process(ctxs, refresh);
    }

    @Override
    public boolean process(MappingRequestContext ctx) {
        return mappingImpl.process(ctx);
    }

    @Override
    public boolean dropIndex(TypedSearchContext ctx) {
        return mappingImpl.dropIndex(ctx);
    }

    @Override
    public boolean indexExists(TypedSearchContext ctx) {
        return mappingImpl.indexExists(ctx);
    }

    @Override
    public boolean refreshIndex(TypedSearchContext ctx, boolean wait) {
        return mappingImpl.refreshIndex(ctx, wait);
    }

    @Override
    public boolean closeIndex(TypedSearchContext ctx) {
        return mappingImpl.closeIndex(ctx);
    }

    @Override
    public boolean openIndex(TypedSearchContext ctx) {
        return mappingImpl.openIndex(ctx);
    }

    @Override
    public boolean setIndexSettings(TypedSearchContext ctx, Map<String, Object> settings) {
        return mappingImpl.setIndexSettings(ctx, settings);
    }
    /**
     * @return the numberOfShardsForSystem
     */
    @Override
    public String getNumberOfShardsForSystem() {
        return numberOfShardsForSystem;
    }
    /**
     * @return the numberOfReplicasForSystem
     */
    @Override
    public String getNumberOfReplicasForSystem() {
        return numberOfReplicasForSystem;
    }

    @Override
    public boolean isRefreshImmediate() {
        return refreshImmediate;
    }
}
