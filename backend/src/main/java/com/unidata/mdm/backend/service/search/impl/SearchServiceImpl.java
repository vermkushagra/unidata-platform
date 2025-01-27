package com.unidata.mdm.backend.service.search.impl;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forAuditEvents;
import static com.unidata.mdm.backend.common.context.SearchRequestContext.forClassifierElements;
import static com.unidata.mdm.backend.common.context.SearchRequestContext.forEtalonData;
import static com.unidata.mdm.backend.common.context.SearchRequestContext.forModelElements;
import static com.unidata.mdm.backend.service.search.impl.IndexComponent.AUDIT_SEARCH_INDEX;
import static com.unidata.mdm.backend.service.search.impl.IndexComponent.CLASSIFIER_NODE_SEARCH_INDEX;
import static com.unidata.mdm.backend.service.search.impl.IndexComponent.MODEL_SEARCH_INDEX;
import static com.unidata.mdm.backend.service.search.util.RecordHeaderField.FIELD_ETALON_ID;
import static java.util.Collections.singletonList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
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
import org.springframework.stereotype.Service;

import com.unidata.mdm.backend.api.rest.constants.SearchConstants;
import com.unidata.mdm.backend.common.context.AggregationRequestContext;
import com.unidata.mdm.backend.common.context.CardinalityAggregationRequestContext;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.ValueCountAggregationRequestContext;
import com.unidata.mdm.backend.common.dto.AggregationResultDTO;
import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SearchApplicationException;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.common.integration.exits.SearchListener;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.SearchRequestType;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;
import com.unidata.mdm.backend.conf.impl.SearchImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.model.ModelSearchObject;
import com.unidata.mdm.backend.service.search.Event;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov Search service aggregate, containing all the
 */
@Service
public class SearchServiceImpl implements SearchServiceExt {

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
    private AdminAgentComponent adminComponent;

    /**
     * Index component
     */
    @Autowired
    private IndexComponent indexComponent;

    /**
     * Mapping component
     */
    @Autowired
    private MappingComponent mappingComponent;

    @Autowired
    private ConfigurationService configurationService;

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
        this.adminComponent = new AdminAgentComponent(client);
        this.indexComponent = new ElasticIndexComponent(client);
        this.mappingComponent = new ElasticMappingComponent(client, indexComponent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean createIndex(final String name, final String storageId,
                               final Properties properties, boolean forceCreate) throws IOException {
        try {
            SearchRequestContext searchContext = SearchRequestContext.forIndex(name).storageId(storageId).build();
            return forceCreate ? indexComponent.forceCreateIndex(searchContext, properties) : indexComponent.safeCreateIndex(searchContext, properties);
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean dropIndex(final String name, String storageId) {
        try {
            SearchRequestContext searchContext = SearchRequestContext.forIndex(name).storageId(storageId).build();
            return indexComponent.dropIndex(searchContext);
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean refreshIndex(String name, String storageId, boolean wait) {
        try {
            SearchRequestContext searchContext = SearchRequestContext.forIndex(name).storageId(storageId).build();
            return indexComponent.refreshIndex(searchContext, wait);
        } catch (ElasticsearchTimeoutException te) {
            LOGGER.warn("Timeout exception caught! It may indicate network problems, but may also indicate a lengthly operation");
            return false;
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean indexExists(final String name, final String storageId) {
        try {
            SearchRequestContext searchContext = SearchRequestContext.forIndex(name).storageId(storageId).build();
            return indexComponent.indexExists(searchContext);
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setIndexRefreshInterval(String name, String storageId, String value) {
        try {
            SearchRequestContext searchContext = SearchRequestContext.forIndex(name).storageId(storageId).build();
            return indexComponent.setIndexRefreshInterval(searchContext, value);
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setIndexSettings(String name, String storageId, Map<String, Object> settings) {
        try {

            SearchRequestContext ctx = SearchRequestContext.forIndex(name)
                    .storageId(storageId)
                    .build();

            return indexComponent.setIndexSettings(ctx, settings);
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setClusterSettings(Map<String, Object> settings, boolean persistent) {
        try {
            return indexComponent.setClusterSettings(settings, persistent);
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean closeIndex(String name, String storageId) {
        try {

            SearchRequestContext ctx = SearchRequestContext.forIndex(name)
                    .storageId(storageId)
                    .build();

            return indexComponent.closeIndex(ctx);
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openIndex(String name, String storageId) {
        try {

            SearchRequestContext ctx = SearchRequestContext.forIndex(name)
                    .storageId(storageId)
                    .build();

            return indexComponent.openIndex(ctx);
        } catch (ElasticsearchException exc) {
            LOGGER.warn("Search exception caught.", exc);
            throw exc;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean modelIndexExists(final String storageId) {
        return indexExists(MODEL_SEARCH_INDEX, storageId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean classifierIndexExist(@Nullable String storageId) {
        storageId = storageId == null ? SecurityUtils.getCurrentUserStorageId() : storageId;
        return indexExists(CLASSIFIER_NODE_SEARCH_INDEX, storageId);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.search.impl.MappingComponent#dropMapping(java.lang.String,java.lang.String)
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropEtalonDataMapping(@Nonnull String entityName, @Nullable String storageId) {
        mappingComponent.dropMapping(entityName, EntitySearchType.ETALON_DATA, storageId);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.search.impl.MappingComponent#dropMappings(java.util.Collection,java.lang.String)
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropEtalonDataMappings(@Nonnull Collection<String> entitiesNames, @Nullable String storageId) {
        mappingComponent.dropMappings(entitiesNames, EntitySearchType.ETALON_DATA, storageId);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.search.impl.MappingComponent#updateLookupEntityMapping(com.unidata.mdm.meta.LookupEntityDef,java.lang.String)
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateLookupEntityMapping(@Nonnull LookupEntityDef lookupEntityDef, @Nullable String storageId) {
        String lookupEntityName = lookupEntityDef.getName();
        boolean hasData = countAllIndexedRecords(lookupEntityName) > 0;
        if (!hasData) {
            //we do it because without data we allow change mapping dramatically
            dropIndex(lookupEntityName, storageId);
        }
        return mappingComponent.updateLookupEntityMapping(lookupEntityDef, storageId);
    }

    /* (non-Javadoc)
     * @see com.unidata.mdm.backend.service.search.impl.MappingComponent#updateEntityMapping(com.unidata.mdm.meta.EntityDef,java.util.Collection,java.lang.String)
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateEntityMapping(@Nonnull EntityDef entity, @Nonnull Collection<NestedEntityDef> nestedEntities,
                                       @Nullable String storageId) {
        String entityName = entity.getName();
        boolean hasData = countAllIndexedRecords(entityName) > 0;
        if (!hasData) {
            //we do it because without data we allow change mapping dramatically
            dropIndex(entityName, storageId);
        }
        return mappingComponent.updateEntityMapping(entity, nestedEntities, storageId);
    }

    @Override
    public boolean updateRelationMapping(RelationDef entity, String storageId) {
        return mappingComponent.updateRelationDefMapping(entity, storageId);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void createModelIndex(@Nullable String storageId) {

        String selectedStorageId = storageId == null ? SecurityUtils.getCurrentUserStorageId() : storageId;
        try {
            boolean result = createIndex(MODEL_SEARCH_INDEX, selectedStorageId, null, false);
            if (result) {
                mappingComponent.updateModelSearchElementsMapping(selectedStorageId);
            }
        } catch (Exception ex) {
            throw new SystemRuntimeException("During index creation ", ex, ExceptionId.EX_META_ROOT_GROUP_IS_ABSENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createAuditIndex(@Nullable String storageId, boolean forceCreate) {
        storageId = storageId == null ? SecurityUtils.getCurrentUserStorageId() : storageId;
        try {
            boolean result = createIndex(AUDIT_SEARCH_INDEX, storageId, null, forceCreate);
            if (result) {
                mappingComponent.updateAuditSearchNodesMapping(storageId);
            }
        } catch (Exception ex) {
            throw new SystemRuntimeException("During index creation ", ex, ExceptionId.EX_META_ROOT_GROUP_IS_ABSENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createClassifierIndex(@Nullable String storageId) {
        storageId = storageId == null ? SecurityUtils.getCurrentUserStorageId() : storageId;
        try {
            boolean result = createIndex(CLASSIFIER_NODE_SEARCH_INDEX, storageId, null, false);
            if (result) {
                mappingComponent.updateClassifierSearchNodesMapping(storageId);
            }
        } catch (Exception ex) {
            throw new SystemRuntimeException("During index creation ", ex, ExceptionId.EX_META_ROOT_GROUP_IS_ABSENT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean indexModelSearchElements(@Nullable String storageId, @Nonnull Collection<ModelSearchObject> modelSearchObjects) {
        SearchRequestContext searchContext = forModelElements().storageId(storageId).build();
        return adminComponent.indexModelSearchElements(searchContext, modelSearchObjects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean indexAuditEvent(@Nullable String storageId, @Nonnull Event event) {
        storageId = storageId == null ? SecurityUtils.getCurrentUserStorageId() : storageId;
        SearchRequestContext searchContext = forAuditEvents().storageId(storageId).build();
        return adminComponent.indexAuditEvent(searchContext, event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean indexClassifierNode(@Nullable String storageId, @Nonnull ClsfNodeDTO node) {
        storageId = storageId == null ? SecurityUtils.getCurrentUserStorageId() : storageId;
        SearchRequestContext searchContext = forClassifierElements().storageId(storageId).build();
        return adminComponent.indexNodeSearchElements(searchContext, node);
    }

    @Override
    public boolean indexClassifierNodes(@Nullable String storageId, @Nonnull List<ClsfNodeDTO> nodes) {
        storageId = storageId == null ? SecurityUtils.getCurrentUserStorageId() : storageId;
        SearchRequestContext searchContext = forClassifierElements().storageId(storageId).build();
        return adminComponent.indexNodesSearchElements(searchContext, nodes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean index(IndexRequestContext ctx) {

        if (MapUtils.isEmpty(ctx.getRecords())
        && CollectionUtils.isEmpty(ctx.getRecordsToDelete())
        && CollectionUtils.isEmpty(ctx.getRelations())
        && MapUtils.isEmpty(ctx.getRelationsToDelete())
        && CollectionUtils.isEmpty(ctx.getClassifiers())
        && CollectionUtils.isEmpty(ctx.getClassifiersToDelete())
        && MapUtils.isEmpty(ctx.getClusters())) {
            return true;
        }

        return adminComponent.index(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean index(final List<IndexRequestContext> ctxts) {

        if (CollectionUtils.isEmpty(ctxts)) {
            return true;
        }

        return adminComponent.index(ctxts);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mark(String entityName, String etalonId, @Nonnull Map<RecordHeaderField, Object> fields) {
        SearchRequestContext ctx = forEtalonData(entityName).values(singletonList(etalonId))
                .search(SearchRequestType.TERM)
                .searchFields(singletonList(FIELD_ETALON_ID.getField()))
                .build();
        return mark(ctx, fields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mark(@Nonnull SearchRequestContext context, @Nonnull Map<? extends SearchField, Object> fields) {

        if (fields.isEmpty()) {
            return true;
        }

        boolean isCorrect = fields.keySet()
                .stream()
                .map(SearchField::linkedSearchType)
                .allMatch(type -> type.equals(context.getType()));

        if (!isCorrect) {
            throw new BusinessException("Try to update fields in another search type",
                    ExceptionId.EX_SEARCH_NOT_RELATED_SEARCH_TYPES_IN_MARK_OPERATION);
        }

        return adminComponent.mark(context, fields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mark(@Nonnull ComplexSearchRequestContext context, @Nonnull Map<? extends SearchField, Object> fields) {
        if (fields.isEmpty() || context.isEmpty()) {
            return true;
        }
        SearchType searchType = getSearchType(context);
        boolean isIncorrect = fields.keySet()
                .stream()
                .map(SearchField::linkedSearchType)
                .anyMatch(type -> !type.equals(searchType));
        if (isIncorrect) {
            throw new BusinessException("Try to update fields in another search type",
                    ExceptionId.EX_SEARCH_NOT_RELATED_SEARCH_TYPES_IN_MARK_OPERATION);
        }
        return adminComponent.mark(context, fields);
    }

    private SearchType getSearchType(@Nonnull ComplexSearchRequestContext context) {
        if (context.getType() == ComplexSearchRequestContext.Type.HIERARCHICAL) {
            return context.getMainRequest().getType();
        } else {
            SearchType first = context.getSupplementary().iterator().next().getType();
            boolean notAllTheSame = context.getSupplementary()
                    .stream()
                    .map(SearchRequestContext::getType)
                    .anyMatch(type -> !Objects.equals(type, first));
            if (notAllTheSame) {
                throw new BusinessException("Try to update fields in another search type",
                        ExceptionId.EX_SEARCH_NOT_RELATED_SEARCH_TYPES_IN_MARK_OPERATION);
            }
            return first;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteAll(SearchRequestContext ctx) {
        return adminComponent.deleteAll(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFoundResult(SearchRequestContext requestForDelete) {
        return adminComponent.delete(requestForDelete);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFoundResult(ComplexSearchRequestContext requestForDelete) {
        return adminComponent.delete(requestForDelete);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countAllIndexedRecords(final String type) {
        try {
            SearchRequestContext searchContext = forEtalonData(type).build();
            return adminComponent.countAll(searchContext);
        } catch (ElasticsearchException e) {
            LOGGER.warn("Search exception caught.", e);
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<SearchRequestContext, SearchResultDTO> search(ComplexSearchRequestContext searchRequest) {
        return extractSearchResults(searchComponent.parameterizedSearch(searchRequest));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SearchResultDTO search(final SearchRequestContext ctx) {
        MeasurementPoint.start();
        try {
            ExitResult beforeExitResult = null;
            SearchImpl searchExits = configurationService.getSearch();
            if (ctx.isRunExits() && searchExits != null) {
                SearchListener beforeSearchListener =
                        configurationService.getSearch().getBeforeSearchInstances().get(ctx.getEntity());

                if (beforeSearchListener != null) {
                    beforeExitResult = beforeSearchListener.beforeSearch(ctx);
                    if (beforeExitResult != null && ExitResult.Status.ERROR.equals(beforeExitResult.getStatus())) {
                        LOGGER.error("Error occurred during run before search user exit: {}", beforeExitResult.getWarningMessage());
                        throw new SearchApplicationException("Error occurred during run before search user exit",
                                ExceptionId.EX_SEARCH_BEFORE_USER_EXIT_EXCEPTION, beforeExitResult.getWarningMessage());
                    }
                    if (beforeExitResult != null && ExitResult.Status.WARNING.equals(beforeExitResult.getStatus())) {
                        LOGGER.warn("Warning occurred during run before search user exit: {}", beforeExitResult.getWarningMessage());
                    }
                }
            }

            SearchResultDTO result;
            // Run total hits query and return, if requested
            if (ctx.isTotalCount() && ctx.isCountOnly()) {
                SearchResponse countResponse = searchComponent
                        .parameterizedCount(ctx);
                result = new SearchResultDTO();
                result.setTotalCount(countResponse.getHits().getTotalHits());
                return result;
            }

            // Run search and extract result
            if (ctx.isSayt()) {
                result = extractSearchResult(ctx, searchComponent.parameterizedSayt(ctx));
            } else if (ctx.isScrollScan()) {
                result = extractSearchResult(ctx, searchComponent.parameterizedScrollScanSearch(ctx));
            } else {
                result = extractSearchResult(ctx, searchComponent.parameterizedSearch(ctx));
            }

            if (beforeExitResult != null && ExitResult.Status.WARNING.equals(beforeExitResult.getStatus())) {
                if (result.getErrors() == null) {
                    result.setErrors(new ArrayList<>());
                }

                ErrorInfoDTO errorInfo = new ErrorInfoDTO();
                errorInfo.setSeverity(ErrorInfoDTO.Severity.LOW);
                errorInfo.setUserMessage(MessageUtils.getMessage(SearchConstants.SEARCH_BEFORE_USER_EXIT_EXCEPTION,
                        beforeExitResult.getWarningMessage()));
                result.getErrors().add(0, errorInfo);
            }


            return result;
        } catch (ElasticsearchException esx) {
            final String message = "Search exception caught.";
            LOGGER.warn(message, esx);
            throw new SearchApplicationException(message, esx,
                    ExceptionId.EX_SEARCH_ES_ESC_CAUGHT, ctx);
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
     * @param ctx       search context
     * @param responses responses
     * @return result
     */
    private SearchResultDTO extractSearchResult(SearchRequestContext ctx, List<SearchResponse> responses) {

        SearchResultDTO result = new SearchResultDTO();
        long totalCount = responses.iterator().next().getHits().getTotalHits();
        float maxScore = 0;
        List<SearchResultHitDTO> srhs = new ArrayList<>();
        List<AggregationResultDTO> aggs = CollectionUtils.isNotEmpty(ctx.getAggregations())
                ? new ArrayList<>(ctx.getAggregations().size())
                : null;

        for (SearchResponse response : responses) {// Set total count if required
            maxScore = Math.max(maxScore, response.getHits().getMaxScore());
            SearchHit[] hits = response.getHits().getHits();

            for (int i = 0; hits != null && i < hits.length; i++) {

                // This will throw, if id contains garbage
                Object source = null;
                Map<String, SearchResultHitFieldDTO> preview = null;
                if (!ctx.isSource()) {
                    Map<String, Object> sourceAsMap = hits[i].getSourceAsMap();
                    if (sourceAsMap == null) {
                        preview = new HashMap<>(0);
                    } else {
                        preview = new HashMap<>(ctx.getReturnFields() == null
                                ? 1
                                : ctx.getReturnFields().size() + 1);

                        if (CollectionUtils.isNotEmpty(ctx.getReturnFields())) {
                            for (String returnField : ctx.getReturnFields()) {
                                Object value = XContentMapValues.extractValue(returnField, sourceAsMap);
                                if (value != null) {
                                    preview.put(returnField, new SearchResultHitFieldDTO(returnField,
                                            value instanceof List
                                                    ? (List) value
                                                    : Collections.singletonList(value)));
                                }
                            }
                        }

                        //
                        preview.put(FIELD_ETALON_ID.getField(), new SearchResultHitFieldDTO(FIELD_ETALON_ID.getField(),
                                Collections.singletonList(sourceAsMap.get(FIELD_ETALON_ID.getField()))));
                    }
                } else {
                    source = hits[i].getSourceAsString();
                }

                Object id = hits[i].getSourceAsMap().get(FIELD_ETALON_ID.getField());
                SearchResultHitDTO hitToAdd = new SearchResultHitDTO(
                        id != null ? id.toString() : hits[i].getId(),
                        hits[i].getId(),
                        hits[i].getScore(),
                        preview,
                        source);
                if(hits[i].getInnerHits() != null && ctx.getInnerHits() != null){
                    SearchHits innerSearchHits  = hits[i].getInnerHits().get(ctx.getInnerHits().getKey());
                    if(innerSearchHits.getHits() != null){
                        List<SearchResultHitDTO> innerResultHits = new ArrayList<>();
                        for(SearchHit innerHit : innerSearchHits.getHits()){
                            Map<String, SearchResultHitFieldDTO> innerPreview = new HashMap<>();
                            for(String field : ctx.getInnerHits().getRight()){
                                Object value = XContentMapValues.extractValue(field, innerHit.getSourceAsMap());
                                if(value != null){
                                    innerPreview.put(field, new SearchResultHitFieldDTO(field,
                                            value instanceof List
                                                    ? (List) value
                                                    : Collections.singletonList(value)));
                                }
                            }

                            innerResultHits.add(new SearchResultHitDTO(
                                    innerHit.getId(),
                                    innerHit.getId(),
                                    innerHit.getScore(),
                                    innerPreview,
                                    null));
                        }
                        hitToAdd.setInnerHits(innerResultHits);
                    }

                }
                srhs.add(hitToAdd);
            }

            Aggregations aggregates = response.getAggregations();
            if (!CollectionUtils.isEmpty(ctx.getAggregations()) && Objects.nonNull(aggregates)) {

                for (AggregationRequestContext aCtx : ctx.getAggregations()) {
                    AggregationResultDTO aggregationResult = extractAggregationResult(aCtx, aggregates.get(aCtx.getName()));
                    if (Objects.nonNull(aggregationResult)) {
                        aggs.add(aggregationResult);
                    }
                }
            }
        }

        result.setTotalCount(totalCount);
        result.setHits(srhs);
        result.setMaxScore(maxScore);
        result.setAggregates(aggs);

        // Set resulting cached field names (fields or form)
        result.setFields(ctx.getReturnFields() != null ? ctx.getReturnFields() : ctx.getSearchFields());

        runAfterUserExitIfNeed(ctx, result);
        return result;
    }

    private void runAfterUserExitIfNeed(SearchRequestContext ctx, SearchResultDTO result) {
        SearchImpl searchExits = configurationService.getSearch();
        if (!ctx.isRunExits() || searchExits == null) {
            return;
        }

        SearchListener afterSearchListener =
                configurationService.getSearch().getAfterSearchInstances().get(ctx.getEntity());
        if (afterSearchListener != null) {
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

    /**
     * Extracts aggregation tree.
     *
     * @param request  the request
     * @param response the response
     * @return tresult
     */
    private AggregationResultDTO extractAggregationResult(AggregationRequestContext request, Aggregation response) {

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
     * @param request  request
     * @param response response
     * @return aggregation result
     */
    @Nonnull
    private AggregationResultDTO extractSingleBucketAggregation(AggregationRequestContext request, SingleBucketAggregation response) {

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

        for (AggregationRequestContext ctx : request.aggregations()) {

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
     * @param request  request
     * @param response response
     * @return aggregation result
     */
    @Nonnull
    private AggregationResultDTO extractMultiBucketAggregation(AggregationRequestContext request, MultiBucketsAggregation response) {


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

            for (AggregationRequestContext ctx : request.aggregations()) {

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
    @Override
    public long countErrorsBySeverity(String severity, SearchRequestContext ctx) {
        return searchComponent.countErrorsBySeverity(severity, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countErrorRecords(SearchRequestContext ctx) {
        return searchComponent.countErrorRecords(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public Map<SearchRequestContext, SearchResultDTO> search(Collection<SearchRequestContext> ctxts) {
        ComplexSearchRequestContext context = ComplexSearchRequestContext.multi(ctxts);
        return search(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public Map<SearchRequestContext, SearchResultDTO> search(Collection<SearchRequestContext> ctxts, boolean sayt) {
        if (!sayt) {
            return search(ctxts);
        } else {
            Map<SearchRequestContext, SearchRequestContext> contextMap = new HashMap<>(ctxts.size(), 1);
            for (SearchRequestContext ctx : ctxts) {
                if (ctx == null) {
                    continue;
                }
                contextMap.put(ctx, convertToSayt(ctx));
            }
            Map<SearchRequestContext, SearchResultDTO> result = search(contextMap.values());
            Map<SearchRequestContext, SearchResultDTO> convertedResult = new HashMap<>(ctxts.size(), 1);
            for (Entry<SearchRequestContext, SearchRequestContext> entry : contextMap.entrySet()) {
                convertedResult.put(entry.getKey(), result.get(entry.getValue()));
            }
            return convertedResult;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public boolean exists(String type, String id) {
        SearchRequestContext context = SearchRequestContext.forEtalonData(type)
                .values(singletonList(id))
                .searchFields(singletonList(FIELD_ETALON_ID.getField()))
                .search(SearchRequestType.TERM)
                .countOnly(true)
                .totalCount(true)
                .onlyQuery(true)
                .build();
        return search(context).getTotalCount() > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public SearchResultDTO search(SearchRequestContext ctx, boolean sayt) {
        if (!sayt || StringUtils.isBlank(ctx.getText())) {
            return search(ctx);
        } else {
            return search(convertToSayt(ctx));
        }
    }

    private SearchRequestContext convertToSayt(SearchRequestContext ctx) {
        if (StringUtils.isBlank(ctx.getText())) {
            return ctx;
        } else {
            return SearchRequestContext.forEtalonData(ctx.getEntity())
                    .form(ctx.getForm())
                    .asOf(ctx.getAsOf())
                    .search(ctx.getSearch())
                    .values(ctx.getValues())
                    .totalCount(ctx.isTotalCount())
                    .saytText(ctx.getText())
                    .source(ctx.isSource())
                    .skipEtalonId(ctx.isSkipEtalonId())
                    .count(ctx.getCount())
                    .searchFields(ctx.getSearchFields())
                    .returnFields(ctx.getReturnFields())
                    .page(ctx.getPage())
                    .storageId(ctx.getStorageId())
                    .operator(ctx.getOperator())
                    .fetchAll(ctx.isFetchAll())
                    .countOnly(ctx.isCountOnly())
                    .facets(ctx.getFacets())
                    .addSorting(ctx.getSortFields())
                    .onlyQuery(ctx.isOnlyQuery())
                    .score(ctx.getScore())
                    .build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public long countAll(String type) {
        return countAllIndexedRecords(type);
    }
}
