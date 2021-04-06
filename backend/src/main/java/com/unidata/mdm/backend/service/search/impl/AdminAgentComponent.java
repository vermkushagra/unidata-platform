package com.unidata.mdm.backend.service.search.impl;

import static com.unidata.mdm.backend.service.search.util.SearchUtils.ES_MAX_TO;
import static com.unidata.mdm.backend.service.search.util.SearchUtils.ES_MIN_FROM;
import static com.unidata.mdm.backend.service.search.util.SearchUtils.PARENT_FIELD;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchRequestBuilder;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.MultiSearchResponse.Item;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.indices.TypeMissingException;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SearchApplicationException;
import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterSet;
import com.unidata.mdm.backend.common.search.SearchField;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.BinaryLargeValue;
import com.unidata.mdm.backend.common.types.CharacterLargeValue;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonClassifierInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;
import com.unidata.mdm.backend.common.types.InfoSection;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.RecordType;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.DateArrayValue;
import com.unidata.mdm.backend.common.types.impl.IntegerArrayValue;
import com.unidata.mdm.backend.common.types.impl.NumberArrayValue;
import com.unidata.mdm.backend.common.types.impl.StringArrayValue;
import com.unidata.mdm.backend.common.types.impl.TimeArrayValue;
import com.unidata.mdm.backend.common.types.impl.TimestampArrayValue;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.data.util.DataUtils;
import com.unidata.mdm.backend.service.model.ModelSearchObject;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.search.Event;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.service.search.util.ClassifierDataHeaderField;
import com.unidata.mdm.backend.service.search.util.ClassifierHeaderField;
import com.unidata.mdm.backend.service.search.util.DqHeaderField;
import com.unidata.mdm.backend.service.search.util.MatchingHeaderField;
import com.unidata.mdm.backend.service.search.util.ModelHeaderField;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

/**
 * @author Mikhail Mikhailov
 */
@Component
public class AdminAgentComponent extends BaseAgentComponent {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAgentComponent.class);

    private static final boolean DEFAULT_INDEX_RELATIONS_STRAIGHT = true;
    /**
     * Transport client to use.
     */
    @Autowired
    private Client client;
    /**
     * Classifiers service.
     */
    @Autowired
    private ClsfService classifierService;

    @Value("${" + ConfigurationConstants.SEARCH_INDEX_RELATIONS_STRAIGHT + ":" + DEFAULT_INDEX_RELATIONS_STRAIGHT + "}")
    private Boolean indexRelationsStraight;

    /**
     * Empty args constructor, used by container.
     */
    public AdminAgentComponent() {
        super();
    }

    /**
     * One arg constructor, used by utility.
     */
    public AdminAgentComponent(Client client) {
        super();
        this.client = client;
    }

    public boolean indexModelSearchElements(final SearchRequestContext ctx, Collection<ModelSearchObject> modelSearchObjects) {
        if (modelSearchObjects.isEmpty()) {
            return true;
        }
        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);
        final String type = ctx.getType().getName();
        BulkRequestBuilder bulkBuilder = client.prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        for (ModelSearchObject modelSearchObject : modelSearchObjects) {
            for (Entry<String,String> searchElement : modelSearchObject.getSearchElements().entries()) {
                try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
                    builder.startObject();
                    builder.field(ModelHeaderField.SEARCH_OBJECT.getField(), searchElement.getKey());
                    builder.field(ModelHeaderField.VALUE.getField(), searchElement.getValue());
                    builder.field(ModelHeaderField.ENTITY_NAME.getField(), modelSearchObject.getEntityName());
                    builder.field(ModelHeaderField.DISPLAY_ENTITY_NAME.getField(), modelSearchObject.getDisplayName());
                    builder.endObject();

                    IndexRequestBuilder request = client.prepareIndex(indexName, type, null).setSource(builder);
                    bulkBuilder.add(request);
                } catch (IOException e) {
                    handleIOException(e);
                }
            }
        }
        BulkResponse response = executeRequest(bulkBuilder);
        return !response.hasFailures();
    }

    public boolean indexAuditEvent(final SearchRequestContext ctx, @Nonnull Event event) {
        final String indexName = constructIndexName(ctx);
        final String type =  ctx.getType().getName();
        IndexRequestBuilder request = null;
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.startObject();
            for (AuditHeaderField field : AuditHeaderField.values()) {
                builder.field(field.getField(), field.getIndexedElement(event));
            }
            builder.endObject();

            request = client.prepareIndex(indexName, type, null).setSource(builder);
        } catch (IOException e) {
            handleIOException(e);
        }

        IndexResponse response = executeRequest(request);
        return RestStatus.CREATED.equals(response.status());
    }

    public boolean indexNodeSearchElements(@Nonnull SearchRequestContext ctx, @Nonnull ClsfNodeDTO node) {
        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);
        final String type = ctx.getType().getName();
        IndexRequestBuilder request = null;
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.startObject();
            for(ClassifierHeaderField classifierHeaderField: ClassifierHeaderField.values()){
                builder.field(classifierHeaderField.getField(),classifierHeaderField.getIndexedElement(node));
            }
            builder.endObject();

            request = client.prepareIndex(indexName, type, null).setSource(builder);
        } catch (IOException e) {
            handleIOException(e);
        }
        IndexResponse response = executeRequest(request);
        return RestStatus.CREATED.equals(response.status());
    }

    public boolean indexNodesSearchElements(@Nonnull SearchRequestContext ctx, @Nonnull List<ClsfNodeDTO> nodes) {
        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);
        final String type = ctx.getType().getName();
        final BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (final ClsfNodeDTO node : nodes) {
            try (final XContentBuilder builder = XContentFactory.jsonBuilder()) {

                builder.startObject();
                for (ClassifierHeaderField classifierHeaderField : ClassifierHeaderField.values()) {
                    builder.field(classifierHeaderField.getField(), classifierHeaderField.getIndexedElement(node));
                }
                builder.endObject();

                bulkRequestBuilder.add(client.prepareIndex(indexName, type, null).setSource(builder));
            } catch(IOException e){
                return handleIOException(e);
            }
        }
        final BulkResponse response = executeRequest(bulkRequestBuilder);
        return response.hasFailures();
    }

    private boolean handleIOException(IOException e) throws SearchApplicationException {
        final String message = "XContentBuilder threw an exception. {}.";
        LOGGER.warn(message, e);
        throw new SearchApplicationException(message, e,
                ExceptionId.EX_SEARCH_UPDATE_DOCUMENT_FAILED);
    }

    /**
     * Index a number of documents.
     *
     * @param ctxts contexts to process
     * @return true if successful, false otherwise
     */
    public boolean index(final List<IndexRequestContext> ctxts) {

        BulkRequestBuilder builder = client.prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);
        Collection<InfoSection> unspecifiedDeletes = new ArrayList<>();
        Collection<Pair<InfoSection, XContentBuilder>> unspecifiedUpdates = new ArrayList<>();
        for (IndexRequestContext ctx : ctxts) {

            if (ctx.isDrop()) {
                Pair<Collection<InfoSection>, Collection<DeleteRequestBuilder>> drbs = processDelete(ctx);
                for (DeleteRequestBuilder drb : drbs.getRight()) {
                    builder.add(drb);
                }

                unspecifiedDeletes.addAll(drbs.getLeft());
            }

            Collection<IndexRequestBuilder> irbs = processIndex(ctx);
            for (IndexRequestBuilder irb : irbs) {
                builder.add(irb);
            }

            Pair<Collection<Pair<InfoSection, XContentBuilder>>, Collection<UpdateRequest>> urs = processUpdate(ctx);
            for (UpdateRequest ur : urs.getRight()) {
               builder.add(ur);
            }

            unspecifiedUpdates.addAll(urs.getLeft());
        }

        // Special handling for unspecified updates
        specifyDeletes(builder, unspecifiedDeletes);
        specifyUpdates(builder, unspecifiedUpdates);

        BulkResponse response = executeRequest(builder);
        boolean failed = response.hasFailures();
        if (failed) {
            LOGGER.error("Error during indexing {}", response.buildFailureMessage());
        }

        return !failed;
    }

    /**
     * Index a document.
     *
     * @param ctx context
     * @return true if successful, false otherwise
     */
    public boolean index(final IndexRequestContext ctx) {

        WriteRequest.RefreshPolicy refreshPolicy = ctx.isRefresh()
                ? WriteRequest.RefreshPolicy.IMMEDIATE
                : WriteRequest.RefreshPolicy.NONE;
        // 1. Compose the name of the type
        BulkRequestBuilder builder = client.prepareBulk().setRefreshPolicy(refreshPolicy);
        Collection<InfoSection> unspecifiedDeletes = new ArrayList<>();
        Collection<Pair<InfoSection, XContentBuilder>> unspecifiedUpdates = new ArrayList<>();

        // 2. Call delete modified stuff
        if (ctx.isDrop()) {
            Pair<Collection<InfoSection>, Collection<DeleteRequestBuilder>> drbs = processDelete(ctx);
            for (DeleteRequestBuilder drb : drbs.getRight()) {
                builder.add(drb);
            }

            unspecifiedDeletes.addAll(drbs.getLeft());
        }

        // 3. Index stuff
        Collection<IndexRequestBuilder> irbs = processIndex(ctx);
        for (IndexRequestBuilder irb : irbs) {
            builder.add(irb);
        }

        Pair<Collection<Pair<InfoSection, XContentBuilder>>, Collection<UpdateRequest>> urs = processUpdate(ctx);
        for (UpdateRequest ur : urs.getRight()) {
           builder.add(ur);
        }

        unspecifiedUpdates.addAll(urs.getLeft());
        unspecifiedUpdates.addAll(urs.getLeft());

        // Special handling for unspecified updates
        specifyDeletes(builder, unspecifiedDeletes);
        specifyUpdates(builder, unspecifiedUpdates);

        if (CollectionUtils.isEmpty(builder.request().requests())) {
            return true;
        }

        BulkResponse response = executeRequest(builder);
        boolean failed = response.hasFailures();
        if (failed){
            LOGGER.error("Error during indexing {}.", response.buildFailureMessage());
        }

        return !failed;
    }

    /**
     * Be sure to set commit_interval to 1000 and not higher for jobs,
     * able to generate long term requests, which can cause Lucene ToManyClauses error.
     * @param ctx - index context
     * @return drop query
     */
    @Nonnull
    public QueryBuilder createDropQuery(@Nonnull final IndexRequestContext ctx) {
        BoolQueryBuilder resultQuery = QueryBuilders.boolQuery();
        //data
        for (EtalonRecord etalonRecord : ctx.getRecords().keySet()) {
            EtalonRecordInfoSection infoSection = etalonRecord.getInfoSection();
            QueryBuilder forEtalon = createCrossQuery(EntitySearchType.ETALON_DATA, infoSection.getEtalonKey().getId(),
                    infoSection.getValidFrom(), infoSection.getValidTo());
            resultQuery.should(forEtalon);
        }
        // Matching. Do it in a separate loop because matching data can theoretically be collected separatly from record data.
        for (EtalonRecord etalonRecord : ctx.getClusters().keySet()) {
            EtalonRecordInfoSection infoSection = etalonRecord.getInfoSection();
            QueryBuilder forEtalon = createCrossQuery(EntitySearchType.MATCHING, infoSection.getEtalonKey().getId(),
                    infoSection.getValidFrom(), infoSection.getValidTo());
            resultQuery.should(forEtalon);
        }
        //rels
        for (EtalonRelation etalonRelation : ctx.getRelations()) {
            EtalonRelationInfoSection infoSection = etalonRelation.getInfoSection();
            QueryBuilder forEtalon;
            if (infoSection.getType() == RelationType.MANY_TO_MANY) {
                forEtalon = QueryBuilders.termQuery(RelationHeaderField.FIELD_ETALON_ID.getField(),
                        infoSection.getRelationEtalonKey());
            } else {
                forEtalon = createCrossQuery(EntitySearchType.ETALON_RELATION, infoSection.getRelationEtalonKey(),
                        infoSection.getValidFrom(), infoSection.getValidTo());
            }
            resultQuery.should(forEtalon);
        }

        return resultQuery;
    }

    /**
     * @param searchType - type of record
     * @param id         - unique record id
     * @param from       - from boundary of record
     * @param to         - to boundary of record
     * @return query for cross records periods
     */
    @Nonnull
    private QueryBuilder createCrossQuery(@Nonnull EntitySearchType searchType, String id, Date from, Date to) {

        // todo think about generalization of types! (after classifiers!)
        String etalonIdField;
        String fromField;
        String toField;
        switch (searchType) {
        case MATCHING:
            etalonIdField = MatchingHeaderField.FIELD_ETALON_ID.getField();
            fromField = MatchingHeaderField.FIELD_FROM.getField();
            toField = MatchingHeaderField.FIELD_TO.getField();
            break;
        case ETALON_RELATION:
            etalonIdField = RelationHeaderField.FIELD_ETALON_ID.getField();
            fromField = RelationHeaderField.FIELD_FROM.getField();
            toField = RelationHeaderField.FIELD_TO.getField();
            break;
        default:
            etalonIdField = RecordHeaderField.FIELD_ETALON_ID.getField();
            fromField = RecordHeaderField.FIELD_FROM.getField();
            toField = RecordHeaderField.FIELD_TO.getField();
            break;
        }

        QueryBuilder forEtalonId = QueryBuilders.termQuery(etalonIdField, id);
        QueryBuilder rangeFrom = QueryBuilders.rangeQuery(fromField).lte(SearchUtils.ensureMaxDate(to));
        QueryBuilder rangeTo = QueryBuilders.rangeQuery(toField).gte(SearchUtils.ensureMinDate(from));
        return QueryBuilders.boolQuery()
                .must(forEtalonId)
                .filter(QueryBuilders.boolQuery().must(rangeFrom).must(rangeTo));

    }

    /**
     * Sets several system fields at once.
     *
     * @param ctx    the context
     * @param fields fields to set
     * @return true, if successful, false otherwise
     */
    public boolean mark(final SearchRequestContext ctx, Map<? extends SearchField, Object> fields) {
        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);
        final QueryBuilder qb = createGeneralQueryFromContext(ctx);
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            // 2. Set marks
            builder.startObject();
            for (Entry<? extends SearchField, Object> entry : fields.entrySet()) {
                builder.field(entry.getKey().getField(), entry.getValue());
            }
            builder.endObject();
            return updateInternal(qb, builder, ctx.getType(), indexName);
        } catch (IOException e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_MARK_DOCUMENT_FAILED);
        }
    }

    /**
     * Sets several system fields at once.
     *
     * @param ctx    the context
     * @param fields fields to set
     * @return true, if successful, false otherwise
     */
    public boolean mark(final ComplexSearchRequestContext ctx, Map<? extends SearchField, Object> fields) {
        // 1. Compose the name of the type
        Map<SearchRequestContext, QueryBuilder> queries = createQueryForComplex(ctx);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().minimumShouldMatch(1);
        queries.values().forEach(boolQuery::should);
        String[] entityNames = ctx.getAllInnerContexts()
                                      .stream()
                                      .map(this::constructIndexName)
                                      .distinct()
                                      .toArray(String[]::new);
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            com.unidata.mdm.backend.common.search.types.SearchType type = null;
            // 1. Set marks
            builder.startObject();
            for (Entry<? extends SearchField, Object> entry : fields.entrySet()) {
                type = entry.getKey().linkedSearchType();
                builder.field(entry.getKey().getField(), entry.getValue());
            }
            builder.endObject();
            return updateInternal(boolQuery, builder, type, entityNames);
        } catch (IOException e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_MARK_DOCUMENT_FAILED);
        }
    }

    /**
     * @param query      - query
     * @param update     - updates
     * @param searchType - can be null, if defined search will be more strict
     * @param indexNames - index names
     * @return true if successful, false otherwise
     */
    private boolean updateInternal(@Nonnull QueryBuilder query, @Nonnull XContentBuilder update,
            @Nullable com.unidata.mdm.backend.common.search.types.SearchType searchType, final String... indexNames) {
        SearchRequestBuilder srb = client.prepareSearch(indexNames)
                                         .setFetchSource(false)
                                         .setQuery(query)
                                         .setSize(5000)
                                         .setFetchSource(PARENT_FIELD, null);

        if (searchType != null) {
            srb.setTypes(searchType.getName());
        }

        SearchResponse idsResponse = executeRequest(srb);

        SearchHit[] hits = idsResponse.getHits().getHits();
        if (hits.length > 0) {

            final BulkRequestBuilder bulkRequest = client.prepareBulk()
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

            for (SearchHit hit : hits) {
                String parentId = hit.getFields().get(PARENT_FIELD).getValue();
                UpdateRequestBuilder requestBuilder = client.prepareUpdate(hit.getIndex(), hit.getType(), hit.getId())
                                                            .setDoc(update).setParent(parentId);
                bulkRequest.add(requestBuilder);
            }

            final BulkResponse bulkResponse = executeRequest(bulkRequest);
            if (bulkResponse.hasFailures()) {
                LOGGER.error(bulkResponse.buildFailureMessage());
            }

            return !bulkResponse.hasFailures();
        }

        return true;
    }

    /**
     * General method for removing something from ES
     *
     * @param qb         the query builder
     * @param searchType type of search type
     * @param indexNames - collection of index names.
     * @return true if successful, false otherwise
     */
    private boolean deleteInternal(@Nonnull QueryBuilder qb,
            @Nullable com.unidata.mdm.backend.common.search.types.SearchType searchType, final String... indexNames) {

        Collection<DeleteRequestBuilder> deletes = processDelete(qb, searchType, indexNames);
        if (CollectionUtils.isEmpty(deletes)) {
            return true;
        }

        final BulkRequestBuilder bulkRequest = client.prepareBulk().setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        for (DeleteRequestBuilder drb : deletes) {
            bulkRequest.add(drb);
        }

        final BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        boolean hasFailures = bulkResponse.hasFailures();
        if (hasFailures) {
            LOGGER.error("Error during delete {}.", bulkResponse.buildFailureMessage());
        }

        return !hasFailures;
    }

    /**
     * @param ctx the context
     * @return true if successful, false otherwise
     */
    public boolean deleteAll(final SearchRequestContext ctx) {

        final String indexName = constructIndexName(ctx);
        QueryBuilder queryBuilder = createGeneralQueryFromContext(ctx);
        return DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(queryBuilder)
                .source(indexName)
                .get().getBulkFailures().isEmpty();

    }

    /**
     * Deletes documents for request.
     * @param ctx - context
     * @return true if successful, false otherwise
     */
    public boolean delete(final SearchRequestContext ctx) {
        final String indexName = constructIndexName(ctx);
        QueryBuilder queryBuilder = createGeneralQueryFromContext(ctx);
        //type from context should be pass to internal method!
        return deleteInternal(queryBuilder, ctx.getType(), indexName);
    }

    /**
     * Deletes documents for request.
     * @param context - context
     * @return true if successful, false otherwise
     */
    public boolean delete(final ComplexSearchRequestContext context) {
        Map<SearchRequestContext, QueryBuilder> queries = createQueryForComplex(context);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().minimumShouldMatch(1);
        queries.values().forEach(boolQuery::should);
        String[] entityNames = context.getAllInnerContexts()
                                      .stream()
                                      .map(this::constructIndexName)
                                      .distinct()
                                      .toArray(String[]::new);
        return deleteInternal(boolQuery, null, entityNames);
    }



    /**
     * Gets a bytes reference made of an object.
     * @param object the object
     * @param header the header
     * @return bytes reference
     */
    private XContentBuilder buildObject(
            final EtalonRecord object,
            final Map<? extends SearchField, Object> header) {

        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.startObject();
            buildHeader(builder, header);
            buildRecord(builder, object, null);
            builder.endObject();
            return builder;

        } catch (IOException e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e,
                    ExceptionId.EX_SEARCH_UPDATE_DOCUMENT_FAILED);
        }
    }

    /**
     * Builds matching cluster.
     * @param record the record
     * @param clusterSet the cluster set
     * @return byte reference
     */
    private XContentBuilder buildObject(final EtalonRecord record, Date from, Date to, Cluster cluster, String matchingHead) {

        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder
                .startObject()
                    .field(MatchingHeaderField.FIELD_ETALON_ID.getField(), record.getInfoSection().getEtalonKey().getId())
                    .field(MatchingHeaderField.FIELD_FROM.getField(), from == null
                        ? SearchUtils.ES_MIN_FROM
                        : SearchUtils.parseForIndex(from))
                    .field(MatchingHeaderField.FIELD_TO.getField(), to == null
                        ? SearchUtils.ES_MAX_TO
                        : SearchUtils.parseForIndex(to))
                    .field(MatchingHeaderField.FIELD_GROUP_ID.getField(), cluster.getMetaData().getGroupId())
                    .field(MatchingHeaderField.FIELD_RULE_ID.getField(), cluster.getMetaData().getRuleId())
                    .field(MatchingHeaderField.FIELD_CREATED_AT.getField(), SearchUtils.parseForIndex(cluster.getMatchingDate()))
                    .field(MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField(), matchingHead);
            builder
                .endObject();

            return builder;

        } catch (IOException e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e,
                    ExceptionId.EX_SEARCH_UPDATE_DOCUMENT_FAILED);
        }
    }

    private XContentBuilder buildRelation(EtalonRelation etalonRelation, Boolean withData) {

        // Objects.isNull(ctx.getValidTo()) ? SearchUtils.ES_TIMELINE_PERIOD_ID_UPPER_BOUND : ctx.getValidTo().getTime()
        EtalonRelationInfoSection infoSection =  etalonRelation.getInfoSection();
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            builder.startObject();
            builder.field(RelationHeaderField.REL_NAME.getField(), infoSection.getRelationName());
            builder.field(RelationHeaderField.FIELD_ETALON_ID.getField(), infoSection.getRelationEtalonKey());
            builder.field(RelationHeaderField.FIELD_FROM_ETALON_ID.getField(), infoSection.getFromEtalonKey().getId());
            builder.field(RelationHeaderField.FIELD_TO_ETALON_ID.getField(), infoSection.getToEtalonKey().getId());
            builder.field(RelationHeaderField.FIELD_PERIOD_ID.getField(), SearchUtils.periodIdFromDate(infoSection.getValidTo()));
            builder.field(RelationHeaderField.REL_TYPE.getField(), infoSection.getType().name());
            builder.field(RelationHeaderField.FIELD_PENDING.getField(), infoSection.getApproval() == ApprovalState.PENDING);
            builder.field(RelationHeaderField.FIELD_DELETED.getField(), infoSection.getStatus() == RecordStatus.INACTIVE);

            builder.field(RelationHeaderField.FIELD_FROM.getField(), infoSection.getValidFrom() == null
                            ? ES_MIN_FROM
                            : SearchUtils.parseForIndex(infoSection.getValidFrom()));

            builder.field(RelationHeaderField.FIELD_TO.getField(), infoSection.getValidTo() == null
                            ? ES_MAX_TO
                            : SearchUtils.parseForIndex(infoSection.getValidTo()));

            builder.field(RelationHeaderField.FIELD_CREATED_AT.getField(),
                    SearchUtils.parseForIndex(infoSection.getCreateDate()));

            if(infoSection.getUpdateDate() != null){
                builder.field(RelationHeaderField.FIELD_UPDATED_AT.getField(),
                        SearchUtils.parseForIndex(infoSection.getUpdateDate()));
            }

            // save relation attributes data
            if(BooleanUtils.isTrue(withData) && !RelationType.CONTAINS.equals(etalonRelation.getInfoSection().getType())
            && etalonRelation.getSize()> 0){
                builder.startObject(etalonRelation.getInfoSection().getRelationName());
                   buildRecord(builder, etalonRelation, null);
                builder.endObject();
            }

            builder.endObject();
            return builder;
        } catch (IOException e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_UPDATE_DOCUMENT_FAILED);
        }
    }

    /**
     *
     * @param builder
     * @param header
     * @throws IOException
     */
    private void buildHeader(XContentBuilder builder, final Map<? extends SearchField, Object> header) throws IOException {
        if (header == null) {
            return;
        }
        for (Entry<? extends SearchField, Object> entry : header.entrySet()) {
            if (entry.getKey() == RecordHeaderField.FIELD_DQ_ERRORS) {
                fillErrors(builder, entry);
            } else if (entry.getKey() == RecordHeaderField.FIELD_FROM) {
                builder.field(entry.getKey().getField(), entry.getValue() == null ? ES_MIN_FROM : SearchUtils.parseForIndex(entry.getValue()));
            } else if (entry.getKey() == RecordHeaderField.FIELD_TO) {
                builder.field(entry.getKey().getField(), entry.getValue() == null ? ES_MAX_TO : SearchUtils.parseForIndex(entry.getValue()));
            } else if (entry.getKey() == RecordHeaderField.FIELD_CREATED_AT || entry.getKey() == RecordHeaderField.FIELD_UPDATED_AT){
                builder.field(entry.getKey().getField(), SearchUtils.parseForIndex(entry.getValue()));
            } else {
                builder.field(entry.getKey().getField(), entry.getValue());
            }
        }
    }

    /**
     * @param classifier - classifier
     * @throws IOException when something wrong with build classifers info
     */
    private XContentBuilder buildClassifier(@Nonnull EtalonClassifier classifier) {

        EtalonClassifierInfoSection infoSection = classifier.getInfoSection();
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {

            builder.startObject();

            // Fields
            builder.field(ClassifierDataHeaderField.FIELD_ETALON_ID.getField(), infoSection.getClassifierEtalonKey());
            builder.field(ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD.getField(), infoSection.getRecordEtalonKey().getId());
            builder.field(ClassifierDataHeaderField.FIELD_FROM.getField(), infoSection.getValidFrom() == null
                    ? ES_MIN_FROM
                    : SearchUtils.parseForIndex(infoSection.getValidFrom()));

            builder.field(ClassifierDataHeaderField.FIELD_TO.getField(), infoSection.getValidTo() == null
                    ? ES_MAX_TO
                    : SearchUtils.parseForIndex(infoSection.getValidTo()));

            builder.field(ClassifierDataHeaderField.FIELD_CREATED_AT.getField(), SearchUtils.parseForIndex(infoSection.getCreateDate()));
            builder.field(ClassifierDataHeaderField.FIELD_UPDATED_AT.getField(), SearchUtils.parseForIndex(infoSection.getUpdateDate()));
            builder.field(ClassifierDataHeaderField.FIELD_CREATED_BY.getField(), infoSection.getCreatedBy());
            builder.field(ClassifierDataHeaderField.FIELD_UPDATED_BY.getField(), infoSection.getUpdatedBy());
            builder.field(ClassifierDataHeaderField.FIELD_NAME.getField(), infoSection.getClassifierName());
            builder.field(ClassifierDataHeaderField.FIELD_PENDING.getField(), infoSection.getApproval() == ApprovalState.PENDING);
            builder.field(ClassifierDataHeaderField.FIELD_DELETED.getField(), infoSection.getStatus() == RecordStatus.INACTIVE);

            builder.startObject(infoSection.getClassifierName());

            builder.field(ClassifierDataHeaderField.FIELD_ROOT_NODE_ID.getField(), infoSection.getNodeId());
            buildRecord(builder, classifier, null);

            Collection<String> allNodeIds
                = classifierService.getIdsToRoot(infoSection.getNodeId(), infoSection.getClassifierName());

            if (!isEmpty(allNodeIds)) {
                builder.startArray(ClassifierDataHeaderField.FIELD_NODES.getField());
                for (String string : allNodeIds) {
                    builder.startObject();
                    builder.field(ClassifierDataHeaderField.FIELD_NODE_ID.getField(), string);
                    builder.endObject();
                }
                builder.endArray();
            }

            builder.endObject();
            builder.endObject();

            return builder;
        } catch (IOException e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_UPDATE_DOCUMENT_FAILED);
        }
    }

    /**
     * Builds JSON representation of an object for insert or update.
     *
     * @param builder    the builder
     * @param record     the record
     * @param recordPath current path
     * @throws IOException
     */
    private void buildRecord(XContentBuilder builder, DataRecord record, @Nullable String recordPath) throws IOException {
        for (Attribute attr : record.getAllAttributes()) {
            if (attr == null) {
                continue;
            }
            String attrPath = ModelUtils.getAttributePath(recordPath, attr.getName());
            buildAttr(builder, attr, attrPath);
        }
    }

    /**
     * Builds JSON representation of an attribute for insert or update.
     *
     * @param builder  the builder
     * @param attr     the attr
     * @param attrPath current path
     * @throws IOException
     */
    private void buildAttr(XContentBuilder builder, Attribute attr, @Nonnull String attrPath) throws IOException {
        final String name = attr.getName();
        switch (attr.getAttributeType()) {
        case SIMPLE:
            SimpleAttribute<?> sAttr = (SimpleAttribute<?>) attr;
            switch (sAttr.getDataType()) {
            case STRING:
            case LINK:
            case ENUM:
                builder.field(name, sAttr.getValue() == null || sAttr.getValue().toString().isEmpty() ?
                        null :
                        (String) sAttr.getValue());
                break;
            case NUMBER:
            case MEASURED:
                builder.field(name, sAttr.getValue() == null ? null : (Double) sAttr.getValue());
                break;
            case BOOLEAN:
                builder.field(name, sAttr.getValue() == null ? null : (Boolean) sAttr.getValue());
                break;
            case DATE:
                builder.field(name, sAttr.getValue() == null ?
                        null :
                        DateTimeFormatter.ISO_LOCAL_DATE.format((LocalDate) sAttr.getValue()));
                break;
            case TIME:
                builder.field(name, sAttr.getValue() == null ?
                        null :
                        DateTimeFormatter.ISO_LOCAL_TIME.format((LocalTime) sAttr.getValue()));
                break;
            case TIMESTAMP:
                builder.field(name, sAttr.getValue() == null ?
                        null :
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME.format((LocalDateTime) sAttr.getValue()));
                break;
            case INTEGER:
                builder.field(name, sAttr.getValue() == null ? null : (Long) sAttr.getValue());
                break;
            case BLOB:
                builder.field(name, sAttr.getValue() == null ? null : ((BinaryLargeValue) sAttr.getValue()).getFileName());
                break;
            case CLOB:
                builder.field(name, sAttr.getValue() == null ? null : ((CharacterLargeValue) sAttr.getValue()).getFileName());
                break;
            }
            break;
        case CODE:
            CodeAttribute<?> cAttr = (CodeAttribute<?>) attr;
            Collection<Object> codeResult = new ArrayList<>();
            codeResult.add(cAttr.getValue());

            if (cAttr.getSupplementary() != null && !cAttr.getSupplementary().isEmpty()) {
                codeResult.addAll(cAttr.getSupplementary());
            }

            builder.startArray(name);
            for (Object obj : codeResult) {
                builder.value(obj);
            }
            builder.endArray();

            break;
        case ARRAY:
            ArrayAttribute<?> aAttr = (ArrayAttribute<?>) attr;
            if (aAttr.isEmpty()) {
                return;
            }
            Collection<Object> arrayResult  = Collections.emptyList();
            switch (aAttr.getDataType()) {
            case STRING:
                arrayResult = aAttr.getValue().stream()
                               .map(value -> ((StringArrayValue) value).getValue())
                               .map(str -> StringUtils.isBlank(str) ? null : str)
                               .collect(Collectors.toSet());
                break;
            case NUMBER:
                arrayResult = aAttr.getValue().stream()
                               .map(value -> ((NumberArrayValue) value).getValue())
                               .filter(Objects::nonNull)
                               .collect(Collectors.toSet());
                break;
            case INTEGER:
                arrayResult = aAttr.getValue().stream()
                               .map(value -> ((IntegerArrayValue) value).getValue())
                               .filter(Objects::nonNull)
                               .collect(Collectors.toSet());
                break;
            case DATE:
                arrayResult = aAttr.getValue().stream()
                               .map(value -> ((DateArrayValue) value).getValue())
                               .filter(Objects::nonNull)
                               .map(DateTimeFormatter.ISO_LOCAL_DATE::format)
                               .collect(Collectors.toSet());
                break;
            case TIME:
                arrayResult = aAttr.getValue().stream()
                               .map(value -> ((TimeArrayValue) value).getValue())
                               .filter(Objects::nonNull)
                               .map(DateTimeFormatter.ISO_LOCAL_TIME::format)
                               .collect(Collectors.toSet());
                break;
            case TIMESTAMP:
                arrayResult = aAttr.getValue().stream()
                               .map(value -> ((TimestampArrayValue) value).getValue())
                               .filter(Objects::nonNull)
                               .map(DateTimeFormatter.ISO_LOCAL_DATE_TIME::format)
                               .collect(Collectors.toSet());
                break;
            }
            builder.field(name, arrayResult);

            break;
        case COMPLEX:
            ComplexAttribute complexAttribute = (ComplexAttribute) attr;
            if (complexAttribute.getRecords().isEmpty()) {
                break;
            }
            builder.startArray(attr.getName());
            for (DataRecord nested : complexAttribute.getRecords()) {
                String attrPath1 = ModelUtils.getAttributePath(attrPath, attr.getName());
                builder.startObject();
                buildRecord(builder, nested, attrPath1);
                builder.endObject();
            }
            builder.endArray();
            break;
        }
    }

    /**
     * Create DQ errors.
     *
     * @param builder builder
     * @param entry   an entry
     * @throws IOException
     */
    private void fillErrors(XContentBuilder builder, Entry<? extends SearchField, Object> entry) throws IOException {

        @SuppressWarnings("unchecked") List<DataQualityError> errors = (List<DataQualityError>) entry.getValue();
        if (!CollectionUtils.isEmpty(errors)) {

            builder.startArray(DqHeaderField.getParentField());
            for (DataQualityError error : errors) {
                builder.startObject()
                       .field(DqHeaderField.ERROR_ID.getDirectField(), error.getId())
                       .field(DqHeaderField.CREATE_DATE.getDirectField(), SearchUtils.parseForIndex(new Date()))
                       .field(DqHeaderField.UPDATE_DATE.getDirectField(), SearchUtils.parseForIndex(new Date()))
                       .field(DqHeaderField.STATUS.getDirectField(), error.getStatus().name())
                       .field(DqHeaderField.RULE_NAME.getDirectField(), error.getRuleName())
                       .field(DqHeaderField.MESSAGE.getDirectField(), error.getMessage())
                       .field(DqHeaderField.SEVERITY.getDirectField(), error.getSeverity().name())
                       .field(DqHeaderField.CATEGORY.getDirectField(), error.getCategory())
                       .endObject();
            }

            builder.endArray();
            builder.field(RecordHeaderField.FIELD_DQ_ERRORS_AS_BINARY.getField(),
                    DataUtils.toString((Serializable) errors));
        }
    }

    /**
     * Returns number of all existing records for an entity.
     * @param ctx the context
     * @return count
     */
    public long countAll(final SearchRequestContext ctx) {
        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);
        try {
            return client.prepareSearch(indexName)
                    .setTypes(ctx.getType().getName())
                    .setSize(0)
                    .execute()
                    .actionGet()
                    .getHits().getTotalHits();
        } catch (TypeMissingException |IndexNotFoundException tme) {
            LOGGER.warn(
                    "Count mapping failed. Type [{}] not found in index [{}]. Skipping.",
                    ctx.getType().getName(), indexName);
        }

        return 0;
    }

    /**
     * Collects delete objects.
     * @param qb query
     * @param searchType search type
     * @param indexNames index names
     * @return collection
     */
    private Collection<DeleteRequestBuilder> processDelete(@Nonnull QueryBuilder qb,
            @Nullable com.unidata.mdm.backend.common.search.types.SearchType searchType, final String... indexNames) {

        SearchRequestBuilder srb = client.prepareSearch(indexNames)
                                         .setScroll(new TimeValue(60000))
                                         .setFetchSource(false)
                                         .setQuery(qb)
                                         .setSize(1000);
        if (searchType != null) {
            srb.setTypes(searchType.getName());
        }

        SearchResponse idsResponse = executeRequest(srb);

        List<DeleteRequestBuilder> result = new ArrayList<>();
        SearchHit[] hits = idsResponse.getHits().getHits();
        while (true) {

            if (hits.length > 0) {
                for (SearchHit hit : hits) {
                    SearchHitField routing = hit.getField("_routing");
                    if(routing != null){
                        result.add(client.prepareDelete(hit.getIndex(), hit.getType(), hit.getId())
                                .setRouting(routing.getValue()));
                    } else {
                        result.add(client.prepareDelete(hit.getIndex(), hit.getType(), hit.getId()));
                    }

                }
            }

            // After deleting, we should check for more records
            SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(idsResponse.getScrollId())
                                                    .setScroll(new TimeValue(60000));

            idsResponse = executeRequest(ssrb);

            hits = idsResponse.getHits().getHits();
            if (hits.length == 0) {
                break;
            }
        }

        return result;
    }

    private Pair<Collection<InfoSection>, Collection<DeleteRequestBuilder>> processDelete(final IndexRequestContext ctx) {

        // 0. Simple check
        if (Objects.isNull(ctx.getEntity())
        && (CollectionUtils.isNotEmpty(ctx.getRecordsToDelete()) || CollectionUtils.isNotEmpty(ctx.getClassifiersToDelete()))) {
            final String message = "No entity name given for bulk delete.";
            LOGGER.warn(message);
            throw new SearchApplicationException(message, ExceptionId.EX_SEARCH_BULK_DELETE_NO_ENTITY_NAME);
        }

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);

        List<DeleteRequestBuilder> straightDesult = new ArrayList<>(
                  ctx.getRecordsToDelete().size()
                + ctx.getClassifiersToDelete().size()
                + ctx.getRelationsToDelete().size());

        // 2. Records and matching
        ctx.getRecordsToDelete().forEach(id -> {
            straightDesult.add(client.prepareDelete(indexName, EntitySearchType.ETALON_DATA.getName(), id).setRouting(ctx.getRouting()));
            straightDesult.add(client.prepareDelete(indexName, EntitySearchType.MATCHING.getName(), id).setRouting(ctx.getRouting()));
        });

        // 3. Relations
        ctx.getRelationsToDelete().forEach((name, ids) -> {
            if (CollectionUtils.isNotEmpty(ids)) {
                String targetName =  constructIndexName(name, null);
                ids.stream()
                    .map(id -> client.prepareDelete(targetName, EntitySearchType.ETALON_RELATION.getName(), id).setRouting(ctx.getRouting()))
                    .collect(Collectors.toCollection(() -> straightDesult));
            }
        });

        // 4. Classifiers
        ctx.getClassifiersToDelete().forEach(id ->
            straightDesult.add(client.prepareDelete(indexName, EntitySearchType.CLASSIFIER.getName(), id).setRouting(ctx.getRouting()))
        );

        List<InfoSection> queryDelete = new ArrayList<>(
                ctx.getClassifiersToQueryDelete().size()
              + ctx.getRecordsToQueryDelete().size()
              + ctx.getRelationsToQueryDelete().size());

        queryDelete.addAll(ctx.getClassifiersToQueryDelete());
        queryDelete.addAll(ctx.getRecordsToQueryDelete());
        queryDelete.addAll(ctx.getRelationsToQueryDelete());

        return new ImmutablePair<>(queryDelete, straightDesult);
    }

    /**
     * Does actual processing for index request.
     * @param ctx the context to process
     * @return collection of {@linkplain IndexRequestBuilder}
     */
    private Collection<IndexRequestBuilder> processIndex(final IndexRequestContext ctx) {

        if (CollectionUtils.isEmpty(ctx.getRelations())
         && CollectionUtils.isEmpty(ctx.getClassifiers())
         && MapUtils.isEmpty(ctx.getRecords())
         && MapUtils.isEmpty(ctx.getClusters())) {
            return Collections.emptyList();
        }

        List<IndexRequestBuilder> result = new ArrayList<>();
        boolean etalonSet = false;
        for (Entry<EtalonRecord, Map<? extends SearchField, Object>> e : ctx.getRecords().entrySet()) {

            EtalonRecord etalonRecord = e.getKey();
            EtalonRecordInfoSection infoSection = etalonRecord.getInfoSection();
            final String etalonId = infoSection.getEtalonKey().getId();
            final String indexName = constructIndexName(infoSection.getEntityName(), ctx.getStorageId());

            IndexRequestBuilder record = client.prepareIndex(indexName, EntitySearchType.ETALON_DATA.getName(),
                    SearchUtils.childPeriodId(infoSection.getPeriodId(), etalonId))
                                               .setSource(buildObject(etalonRecord, e.getValue()))
                                               .setParent(etalonId);
            result.add(record);

            // Update for each request (think about optimistic lock for control indexing)
            if (!etalonSet) {
                IndexRequestBuilder etalon = client.prepareIndex(indexName, EntitySearchType.ETALON.getName(), etalonId)
                                                   .setSource(Collections.emptyMap());
                result.add(etalon);
                etalonSet = true;
            }
        }

        for (Entry<EtalonRecord, ClusterSet> entry : ctx.getClusters().entrySet()) {

            EtalonRecord etalonRecord = entry.getKey();
            EtalonRecordInfoSection infoSection = etalonRecord.getInfoSection();
            final String etalonId = infoSection.getEtalonKey().getId();
            final String indexName = constructIndexName(infoSection.getEntityName(), ctx.getStorageId());
            for(Cluster cluster : entry.getValue().getClusters()){
                String matchingHead = String.join(SearchUtils.PIPE_SEPARATOR, cluster.getData().stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()));

                IndexRequestBuilder matchingHeadIndex = client.prepareIndex(indexName, EntitySearchType.MATCHING_HEAD.getName(), matchingHead)
                        .setSource(Collections.emptyMap());
                result.add(matchingHeadIndex);

                IndexRequestBuilder record = client.prepareIndex(indexName, EntitySearchType.MATCHING.getName(),
                        SearchUtils.childPeriodId(infoSection.getPeriodId(),
                                etalonId,
                                cluster.getMetaData().getGroupId().toString(),
                                cluster.getMetaData().getRuleId().toString()))
                        .setSource(buildObject(etalonRecord, entry.getValue().getFrom(), entry.getValue().getTo(), cluster, matchingHead))
                        .setParent(matchingHead);
                result.add(record);
            }
        }

        for (EtalonRelation relation : ctx.getRelations()) {

            EtalonRelationInfoSection infoSection = relation.getInfoSection();

            final String fromEtalonId = infoSection.getFromEtalonKey().getId();
            final String toEtalonId = infoSection.getToEtalonKey().getId();
            final String indexName =  constructIndexName(infoSection.getFromEntityName(), ctx.getStorageId());
            final String linkedIndex = constructIndexName(infoSection.getToEntityName(), ctx.getStorageId());

            IndexRequestBuilder straightRelation = client.prepareIndex(indexName, EntitySearchType.ETALON_RELATION.getName(),
                    SearchUtils.childPeriodId(infoSection.getPeriodId(), fromEtalonId, infoSection.getRelationName(), toEtalonId))
                    .setSource(buildRelation(relation, indexRelationsStraight))
                    .setParent(fromEtalonId);

            // always index revert side for relations
            IndexRequestBuilder revertRelation = client.prepareIndex(linkedIndex, EntitySearchType.ETALON_RELATION.getName(),
                    SearchUtils.childPeriodId(infoSection.getPeriodId(), toEtalonId,  infoSection.getRelationName(), fromEtalonId))
                    .setSource(buildRelation(relation, true))
                    .setParent(toEtalonId);

            result.add(revertRelation);
            result.add(straightRelation);
        }

        for (EtalonClassifier classifier : ctx.getClassifiers()) {

            EtalonClassifierInfoSection infoSection = classifier.getInfoSection();

            final String recordEtalonId = infoSection.getRecordEtalonKey().getId();
            final String indexName = constructIndexName(infoSection.getRecordEntityName(), ctx.getStorageId());

            IndexRequestBuilder classifierData = client.prepareIndex(indexName, EntitySearchType.CLASSIFIER.getName(),
                    SearchUtils.childPeriodId(recordEtalonId, infoSection.getClassifierName())) // Classifier data doesn't have periods
                    .setSource(buildClassifier(classifier))
                    .setParent(recordEtalonId);

            result.add(classifierData);
        }

        return result;
    }

    private void specifyDeletes(BulkRequestBuilder bulk, Collection<InfoSection> unspecifiedDeletes) {

        if (CollectionUtils.isEmpty(unspecifiedDeletes)) {
            return;
        }

        Map<String, Map<EntitySearchType, List<String>>> ids = new HashMap<>();
        Map<String, InfoSection> content = new HashMap<>();
        for (InfoSection unspecifiedDelete : unspecifiedDeletes) {

            if (unspecifiedDelete.getRecordType() == RecordType.DATA_RECORD) {
                EtalonRecordInfoSection eris = (EtalonRecordInfoSection) unspecifiedDelete;
                ids
                    .computeIfAbsent(eris.getEntityName(), k -> new EnumMap<>(EntitySearchType.class))
                    .computeIfAbsent(EntitySearchType.ETALON_DATA, k -> new ArrayList<>())
                    .add(eris.getEtalonKey().getId());
                content.put(eris.getEtalonKey().getId(), unspecifiedDelete);
            } else if (unspecifiedDelete.getRecordType() == RecordType.RELATION_RECORD) {
                EtalonRelationInfoSection eris = (EtalonRelationInfoSection) unspecifiedDelete;
                ids
                    .computeIfAbsent(eris.getFromEntityName(), k -> new EnumMap<>(EntitySearchType.class))
                    .computeIfAbsent(EntitySearchType.ETALON_RELATION, k -> new ArrayList<>())
                    .add(eris.getRelationEtalonKey());
                ids
                    .computeIfAbsent(eris.getToEntityName(), k -> new EnumMap<>(EntitySearchType.class))
                    .computeIfAbsent(EntitySearchType.ETALON_RELATION, k -> new ArrayList<>())
                    .add(eris.getRelationEtalonKey());
                content.put(eris.getRelationEtalonKey(), unspecifiedDelete);
            } else if (unspecifiedDelete.getRecordType() == RecordType.CLASSIFIER_RECORD) {
                EtalonClassifierInfoSection ecis = (EtalonClassifierInfoSection) unspecifiedDelete;
                ids
                    .computeIfAbsent(ecis.getRecordEntityName(), k -> new EnumMap<>(EntitySearchType.class))
                    .computeIfAbsent(EntitySearchType.CLASSIFIER, k -> new ArrayList<>())
                    .add(ecis.getClassifierEtalonKey());
                content.put(ecis.getClassifierEtalonKey(), unspecifiedDelete);
            }
        }

        List<SearchRequestBuilder> requests = ids.entrySet().stream()
            .map(entry -> entry.getValue().entrySet().stream()
                .map(child -> {
                    String indexName = constructIndexName(entry.getKey(), SecurityUtils.getCurrentUserStorageId());
                    String etalonIdField;
                    if (child.getKey() == EntitySearchType.ETALON_DATA) {
                        etalonIdField = RecordHeaderField.FIELD_ETALON_ID.getField();
                    } else if (child.getKey() == EntitySearchType.ETALON_RELATION) {
                        etalonIdField = RelationHeaderField.FIELD_ETALON_ID.getField();
                    } else {
                        etalonIdField = ClassifierDataHeaderField.FIELD_ETALON_ID.getField();
                    }

                    return client.prepareSearch(indexName)
                            .setTypes(child.getKey().getName())
                            .setSize(10000)
                            .setFetchSource(false)
                            .addDocValueField(etalonIdField)
                            .addDocValueField("_parent")
                            .setQuery(QueryBuilders.termsQuery(etalonIdField, child.getValue()));
                })
                .collect(Collectors.toList())
            )
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(requests)) {
            return;
        }

        MultiSearchRequestBuilder msrb = client.prepareMultiSearch()
                .setIndicesOptions(IndicesOptions.lenientExpandOpen());
        for (SearchRequestBuilder srb : requests) {
            msrb.add(srb);
        }

        MultiSearchResponse response = executeRequest(msrb);
        for (int i = 0; i < response.getResponses().length; i++) {
            Item it = response.getResponses()[i];
            it.getResponse().getHits().forEach(hit -> {
                SearchHitField hf = hit.getField(RecordHeaderField.FIELD_ETALON_ID.getField());
                if (Objects.isNull(hf)) {
                    // A failure, actually
                    return;
                }
                String etalonId = hf.getValue();
                InfoSection delete = content.get(etalonId);
                if (Objects.isNull(delete)) {
                    // Same here
                    return;
                }

                String type = hit.getType();
                String index = hit.getIndex();
                String id = hit.getId();
                String parent = hit.getField("_parent").getValue();

                DeleteRequestBuilder dr = client.prepareDelete(index, type, id)
                        .setParent(parent)
                        .setRouting(parent);

                bulk.add(dr);
            });
        }
    }

    // do something with horrible return type
    private Pair<Collection<Pair<InfoSection, XContentBuilder>>, Collection<UpdateRequest>>
        processUpdate(IndexRequestContext ctx) {

        if (CollectionUtils.isEmpty(ctx.getRecordsToSysUpdate())) {
            return new ImmutablePair<>(Collections.emptyList(), Collections.emptyList());
        }

        Pair<Collection<Pair<InfoSection, XContentBuilder>>, Collection<UpdateRequest>> result
            = new MutablePair<>(new ArrayList<>(), new ArrayList<>());

        for (EtalonRecordInfoSection is : ctx.getRecordsToSysUpdate()) {

            XContentBuilder builder;
            try {
                builder = XContentFactory.jsonBuilder();
                builder.startObject();
                if (Objects.nonNull(is.getApproval())) {
                    builder.field(RecordHeaderField.FIELD_PENDING.getField(), is.getApproval() == ApprovalState.PENDING);
                }
                if (Objects.nonNull(is.getStatus())) {
                    builder.field(RecordHeaderField.FIELD_DELETED.getField(), is.getStatus() == RecordStatus.INACTIVE);
                }
                builder.field(RecordHeaderField.FIELD_UPDATED_AT.getField(), SearchUtils.parseForIndex(is.getUpdateDate()));
                builder.endObject();
            } catch (IOException e) {
                final String message = "Update record document failed. XContentBuilder threw an exception. {}.";
                LOGGER.warn(message, e);
                throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_UPDATE_RECORD_DOCUMENT_FAILED);
            }

            if (Objects.nonNull(is.getPeriodId())) {
                UpdateRequest ur = new UpdateRequest();
                ur.doc(builder);
                ur.index(constructIndexName(is.getEntityName(), SecurityUtils.getCurrentUserStorageId()));
                ur.type(EntitySearchType.ETALON_DATA.getName());
                ur.parent(is.getEtalonKey().getId());
                ur.routing(is.getEtalonKey().getId());
                ur.id(SearchUtils.childPeriodId(is.getPeriodId(), is.getEtalonKey().getId()));
                result.getRight().add(ur);
            } else {
                result.getLeft().add(new ImmutablePair<>(is, builder));
            }
        }
        /*
        for (EtalonRelationInfoSection is : ctx.getRelationsToQueryDelete()) {

            XContentBuilder builder;
            try {
                builder = XContentFactory.jsonBuilder();
                builder.startObject();
                if (Objects.nonNull(is.getApproval())) {
                    builder.field(RelationHeaderField.FIELD_PENDING.getField(), is.getApproval() == ApprovalState.PENDING);
                }
                if (Objects.nonNull(is.getStatus())) {
                    builder.field(RelationHeaderField.FIELD_DELETED.getField(), is.getStatus() == RecordStatus.INACTIVE);
                }
                builder.field(RelationHeaderField.FIELD_UPDATED_AT.getField(), SearchUtils.parseForIndex(is.getUpdateDate()));
                builder.endObject();
            } catch (IOException e) {
                final String message = "Update relation document failed. XContentBuilder threw an exception. {}.";
                LOGGER.warn(message, e);
                throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_UPDATE_RELATION_DOCUMENT_FAILED);
            }

            if (Objects.nonNull(is.getPeriodId())) {
                UpdateRequest ur = new UpdateRequest();
                ur.doc(builder);
                ur.index(constructIndexName(is.getFromEntityName(), SecurityUtils.getCurrentUserStorageId()));
                ur.type(EntitySearchType.ETALON_RELATION.getName());
                ur.parent(is.getFromEtalonKey().getId());
                ur.routing(is.getFromEtalonKey().getId());
                ur.id(SearchUtils.childPeriodId(is.getPeriodId(), is.getFromEtalonKey().getId(), is.getRelationName(), is.getToEtalonKey().getId()));
                result.getRight().add(ur);

                ur = new UpdateRequest();
                ur.doc(builder);
                ur.index(constructIndexName(is.getToEntityName(), SecurityUtils.getCurrentUserStorageId()));
                ur.type(EntitySearchType.ETALON_RELATION.getName());
                ur.parent(is.getToEtalonKey().getId());
                ur.routing(is.getToEtalonKey().getId());
                ur.id(SearchUtils.childPeriodId(is.getPeriodId(), is.getToEtalonKey().getId(), is.getRelationName(), is.getFromEtalonKey().getId()));
            } else {
                result.getLeft().add(new ImmutablePair<>(is, builder));
            }
        }

        for (EtalonClassifierInfoSection is : ctx.getClassifiersToQueryDelete()) {

            XContentBuilder builder;
            try {
                builder = XContentFactory.jsonBuilder();
                builder.startObject();
                if (Objects.nonNull(is.getApproval())) {
                    builder.field(ClassifierDataHeaderField.FIELD_PENDING.getField(), is.getApproval() == ApprovalState.PENDING);
                }
                if (Objects.nonNull(is.getStatus())) {
                    builder.field(ClassifierDataHeaderField.FIELD_DELETED.getField(), is.getStatus() == RecordStatus.INACTIVE);
                }
                builder.field(ClassifierDataHeaderField.FIELD_UPDATED_AT.getField(), SearchUtils.parseForIndex(is.getUpdateDate()));
                builder.endObject();
            } catch (IOException e) {
                final String message = "Update classifier document failed. XContentBuilder threw an exception. {}.";
                LOGGER.warn(message, e);
                throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_UPDATE_CLASSIFIER_DOCUMENT_FAILED);
            }

            // Use after time periods are implemented
            // if (Objects.nonNull(is.getPeriodId())) {
            UpdateRequest ur = new UpdateRequest();
            ur.doc(builder);
            ur.index(constructIndexName(is.getRecordEntityName(), SecurityUtils.getCurrentUserStorageId()));
            ur.type(EntitySearchType.CLASSIFIER.getName());
            ur.parent(is.getRecordEtalonKey().getId());
            ur.routing(is.getRecordEtalonKey().getId());
            ur.id(SearchUtils.childPeriodId(is.getRecordEtalonKey().getId(), is.getClassifierName()));
        }
        */
        return result;
    }

    private void specifyUpdates(BulkRequestBuilder bulk, Collection<Pair<InfoSection, XContentBuilder>> unspecifiedUpdates) {

        if (CollectionUtils.isEmpty(unspecifiedUpdates)) {
            return;
        }

        Map<String, Map<EntitySearchType, List<String>>> ids = new HashMap<>();
        Map<String, Pair<InfoSection, XContentBuilder>> content = new HashMap<>();
        for (Pair<InfoSection, XContentBuilder> unspecifiedUpdate : unspecifiedUpdates) {

            if (unspecifiedUpdate.getKey().getRecordType() == RecordType.DATA_RECORD) {
                EtalonRecordInfoSection eris = (EtalonRecordInfoSection) unspecifiedUpdate.getKey();
                ids
                    .computeIfAbsent(eris.getEntityName(), k -> new EnumMap<>(EntitySearchType.class))
                    .computeIfAbsent(EntitySearchType.ETALON_DATA, k -> new ArrayList<>())
                    .add(eris.getEtalonKey().getId());
                content.put(eris.getEtalonKey().getId(), unspecifiedUpdate);
            } else if (unspecifiedUpdate.getKey().getRecordType() == RecordType.RELATION_RECORD) {
                EtalonRelationInfoSection eris = (EtalonRelationInfoSection) unspecifiedUpdate.getKey();
                ids
                    .computeIfAbsent(eris.getFromEntityName(), k -> new EnumMap<>(EntitySearchType.class))
                    .computeIfAbsent(EntitySearchType.ETALON_RELATION, k -> new ArrayList<>())
                    .add(eris.getRelationEtalonKey());
                ids
                    .computeIfAbsent(eris.getToEntityName(), k -> new EnumMap<>(EntitySearchType.class))
                    .computeIfAbsent(EntitySearchType.ETALON_RELATION, k -> new ArrayList<>())
                    .add(eris.getRelationEtalonKey());
                content.put(eris.getRelationEtalonKey(), unspecifiedUpdate);
            } else if (unspecifiedUpdate.getKey().getRecordType() == RecordType.CLASSIFIER_RECORD) {
                EtalonClassifierInfoSection ecis = (EtalonClassifierInfoSection) unspecifiedUpdate.getKey();
                ids
                    .computeIfAbsent(ecis.getRecordEntityName(), k -> new EnumMap<>(EntitySearchType.class))
                    .computeIfAbsent(EntitySearchType.CLASSIFIER, k -> new ArrayList<>())
                    .add(ecis.getClassifierEtalonKey());
                content.put(ecis.getClassifierEtalonKey(), unspecifiedUpdate);
            }
        }

        List<SearchRequestBuilder> requests = ids.entrySet().stream()
            .map(entry -> entry.getValue().entrySet().stream()
                .map(child -> {
                    String indexName = constructIndexName(entry.getKey(), SecurityUtils.getCurrentUserStorageId());
                    String etalonIdField;
                    if (child.getKey() == EntitySearchType.ETALON_DATA) {
                        etalonIdField = RecordHeaderField.FIELD_ETALON_ID.getField();
                    } else if (child.getKey() == EntitySearchType.ETALON_RELATION) {
                        etalonIdField = RelationHeaderField.FIELD_ETALON_ID.getField();
                    } else {
                        etalonIdField = ClassifierDataHeaderField.FIELD_ETALON_ID.getField();
                    }

                    return client.prepareSearch(indexName)
                            .setTypes(child.getKey().getName())
                            .setSize(10000)
                            .setFetchSource(false)
                            .addDocValueField(etalonIdField)
                            .addDocValueField("_parent")
                            .setQuery(QueryBuilders.termsQuery(etalonIdField, child.getValue()));
                })
                .collect(Collectors.toList())
            )
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(requests)) {
            return;
        }

        MultiSearchRequestBuilder msrb = client.prepareMultiSearch()
                .setIndicesOptions(IndicesOptions.lenientExpandOpen());
        for (SearchRequestBuilder srb : requests) {
            msrb.add(srb);
        }

        MultiSearchResponse response = executeRequest(msrb);
        for (int i = 0; i < response.getResponses().length; i++) {
            Item it = response.getResponses()[i];
            it.getResponse().getHits().forEach(hit -> {
                SearchHitField hf = hit.getField(RecordHeaderField.FIELD_ETALON_ID.getField());
                if (Objects.isNull(hf)) {
                    // A failure, actually
                    return;
                }
                String etalonId = hf.getValue();
                Pair<InfoSection, XContentBuilder> update = content.get(etalonId);
                if (Objects.isNull(update)) {
                    // Same here
                    return;
                }

                String type = hit.getType();
                String index = hit.getIndex();
                String id = hit.getId();
                String parent = hit.getField("_parent").getValue();

                UpdateRequest ur = new UpdateRequest();
                ur.doc(update.getValue());
                ur.index(index);
                ur.type(type);
                ur.parent(parent);
                ur.routing(parent);
                ur.id(id);

                bulk.add(ur);
            });
        }
    }
}
