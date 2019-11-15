package org.unidata.mdm.search.service.impl;

import static org.unidata.mdm.search.util.SearchUtils.PARENT_FIELD;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequestBuilder;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.unidata.mdm.search.configuration.SearchConfigurationConstants;
import org.unidata.mdm.search.context.ComplexSearchRequestContext;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.exception.SearchExceptionIds;
import org.unidata.mdm.search.type.IndexField;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Mikhail Mikhailov
 * FIXME: Move dlete methods to search component. Leave admin actions only!
 */
@Component
public class AdminComponentImpl extends BaseAgentComponent /*, ConfigurationUpdatesConsumer */ {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminComponentImpl.class);

    private static final boolean DEFAULT_INDEX_RELATIONS_STRAIGHT = true;

    private static final Integer DELETE_QUERY_SIZE = 10000;

    /**
     * Transport client to use.
     */
    @Autowired
    private Client client;

    @Value("${" + SearchConfigurationConstants.SEARCH_INDEX_RELATIONS_STRAIGHT + ":" + DEFAULT_INDEX_RELATIONS_STRAIGHT + "}")
    private Boolean indexRelationsStraight;
    @Value("${" + SearchConfigurationConstants.SEARCH_ADMIN_ACTION_TIMEOUT + ":5000}")
    private long adminActionTimeout;
            // (Integer) SearchConfigurationConstants.UNIDATA_ELASTIC_ADMIN_ACTION_TIMEOUT.getDefaultValue().get();

    /**
     * Empty args constructor, used by container.
     */
    public AdminComponentImpl() {
        super();
    }

    /**
     * One arg constructor, used by utility.
     */
    public AdminComponentImpl(Client client) {
        super();
        this.client = client;
    }

    /**
     * Be sure to set commit_interval to 1000 and not higher for jobs,
     * able to generate long term requests, which can cause Lucene ToManyClauses error.
     *
     * @param ctx - index context
     * @return drop query
     */
    @Nonnull
    public QueryBuilder createDropQuery(@Nonnull final IndexRequestContext ctx) {
        /*
        BoolQueryBuilder resultQuery = QueryBuilders.boolQuery();
        //data
        for (EtalonRecord etalonRecord : ctx.getRecords().keySet()) {
            EtalonRecordInfoSection infoSection = etalonRecord.getInfoSection();
            QueryBuilder forEtalon = createCrossQuery(EntitySearchType.ETALON_DATA, infoSection.getEtalonKey().getId(),
                    infoSection.getValidFrom(), infoSection.getValidTo());
            resultQuery.should(forEtalon);
        }
        //rels
        for (EtalonRelation etalonRelation : ctx.getRelations()) {
            EtalonRelationInfoSection infoSection = etalonRelation.getInfoSection();
            QueryBuilder forEtalon;
            if (infoSection.getRelationType() == RelationType.MANY_TO_MANY) {
                forEtalon = QueryBuilders.termQuery(RelationHeaderField.FIELD_ETALON_ID.getName(),
                        infoSection.getRelationEtalonKey());
            } else {
                forEtalon = createCrossQuery(EntitySearchType.ETALON_RELATION, infoSection.getRelationEtalonKey(),
                        infoSection.getValidFrom(), infoSection.getValidTo());
            }
            resultQuery.should(forEtalon);
        }

        return resultQuery;
        */
        return QueryBuilders.matchAllQuery();
    }

    /**
     * Sets several system fields at once.
     *
     * @param ctx the context
     * @param fields fields to set
     * @return true, if successful, false otherwise
     */
    public boolean mark(final SearchRequestContext ctx, Map<? extends IndexField, Object> fields, Boolean refreshImmediate) {
        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);
        final QueryBuilder qb = createGeneralQueryFromContext(ctx);
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            // 2. Set marks
            builder.startObject();
            for (Entry<? extends IndexField, Object> entry : fields.entrySet()) {
                builder.field(entry.getKey().getName(), entry.getValue());
            }
            builder.endObject();
            return updateInternal(qb, builder, ctx.getType(), refreshImmediate, indexName);
        } catch (IOException e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new PlatformFailureException(message, e, SearchExceptionIds.EX_SEARCH_MARK_DOCUMENT_FAILED);
        }
    }

    /**
     * Sets several system fields at once.
     *
     * @param ctx the context
     * @param fields fields to set
     * @return true, if successful, false otherwise
     */
    public boolean mark(final ComplexSearchRequestContext ctx, Map<? extends IndexField, Object> fields, Boolean refreshImmediate) {
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
            org.unidata.mdm.search.type.IndexType type = null;
            // 1. Set marks
            builder.startObject();
            for (Entry<? extends IndexField, Object> entry : fields.entrySet()) {
                type = entry.getKey().getType();
                builder.field(entry.getKey().getName(), entry.getValue());
            }
            builder.endObject();
            return updateInternal(boolQuery, builder, type, refreshImmediate, entityNames);
        } catch (IOException e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new PlatformFailureException(message, e, SearchExceptionIds.EX_SEARCH_MARK_DOCUMENT_FAILED);
        }
    }

    /**
     * @param query - query
     * @param update - updates
     * @param searchType - can be null, if defined search will be more strict
     * @param indexNames - index names
     * @return true if successful, false otherwise
     */
    private boolean updateInternal(@Nonnull QueryBuilder query, @Nonnull XContentBuilder update,
                                   @Nullable org.unidata.mdm.search.type.IndexType searchType, Boolean refreshImmediate, final String... indexNames) {
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
                    .setRefreshPolicy(refreshImmediate
                            ? WriteRequest.RefreshPolicy.IMMEDIATE
                            : WriteRequest.RefreshPolicy.NONE);

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
     * @param qb the query builder
     * @param searchType type of search type
     * @param indexNames - collection of index names.
     * @return true if successful, false otherwise
     */
    private boolean deleteInternal(@Nonnull QueryBuilder qb,
                                   @Nullable org.unidata.mdm.search.type.IndexType searchType, boolean refreshImmediate, final String... indexNames) {
        Collection<DeleteRequestBuilder> deletes = processDelete(qb, searchType, indexNames);
        if (CollectionUtils.isEmpty(deletes)) {
            return true;
        }

        final BulkRequestBuilder bulkRequest = client.prepareBulk().setRefreshPolicy(refreshImmediate
                ? WriteRequest.RefreshPolicy.IMMEDIATE
                : WriteRequest.RefreshPolicy.NONE);

        for (DeleteRequestBuilder drb : deletes) {
            bulkRequest.add(drb);
        }
        if (refreshImmediate) {
            final BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            boolean hasFailures = bulkResponse.hasFailures();
            if (hasFailures) {
                LOGGER.error("Error during delete {}.", bulkResponse.buildFailureMessage());
            }

            return !hasFailures;
        } else {
            bulkRequest.execute();
            return true;
        }
    }

    /**
     * @param ctx the context
     * @return true if successful, false otherwise
     */
    public boolean deleteAll(final SearchRequestContext ctx, boolean refresh) {

        final String indexName = constructIndexName(ctx);
        QueryBuilder queryBuilder = createGeneralQueryFromContext(ctx);
        return DeleteByQueryAction.INSTANCE
                .newRequestBuilder(client)
                .filter(queryBuilder)
                .source(indexName)
                .refresh(refresh)
                .get().getBulkFailures().isEmpty();

    }

    /**
     * Deletes documents for request.
     *
     * @param ctx - context
     * @return true if successful, false otherwise
     */
    public boolean delete(final SearchRequestContext ctx, boolean refreshImmediate) {
        final String indexName = constructIndexName(ctx);
        QueryBuilder queryBuilder = createGeneralQueryFromContext(ctx);
        //type from context should be pass to internal method!
        return deleteInternal(queryBuilder, ctx.getType(), refreshImmediate, indexName);
    }

    /**
     * Deletes documents for request.
     *
     * @param context - context
     * @return true if successful, false otherwise
     */
    public boolean delete(final ComplexSearchRequestContext context, boolean refreshImmediate) {
        Map<SearchRequestContext, QueryBuilder> queries = createQueryForComplex(context);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().minimumShouldMatch(1);
        queries.values().forEach(boolQuery::should);
        String[] entityNames = context.getAllInnerContexts()
                .stream()
                .map(this::constructIndexName)
                .distinct()
                .toArray(String[]::new);
        return deleteInternal(boolQuery, null, refreshImmediate, entityNames);
    }

    /**
     * Collects delete objects.
     *
     * @param qb query
     * @param searchType search type
     * @param indexNames index names
     * @return collection
     */
    private Collection<DeleteRequestBuilder> processDelete(@Nonnull QueryBuilder qb,
                                                           @Nullable org.unidata.mdm.search.type.IndexType searchType, final String... indexNames) {

        SearchRequestBuilder srb = client.prepareSearch(indexNames)
                //.setScroll(new TimeValue(60000))
                .setFetchSource(false)
                .setQuery(qb)
                .addSort("_uid", SortOrder.ASC)
                .setSize(DELETE_QUERY_SIZE);
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
                    if (routing != null) {
                        result.add(client.prepareDelete(hit.getIndex(), hit.getType(), hit.getId())
                                .setRouting(routing.getValue()));
                    } else {
                        result.add(client.prepareDelete(hit.getIndex(), hit.getType(), hit.getId()));
                    }

                }
            }

            // After deleting, we should check for more records
            if (hits.length >= DELETE_QUERY_SIZE) {
                srb = client.prepareSearch(indexNames)
                        //.setScroll(new TimeValue(60000))
                        .setFetchSource(false)
                        .setQuery(qb)
                        .addSort("_uid", SortOrder.ASC)
                        .searchAfter(hits[hits.length - 1].getSortValues())
                        .setSize(DELETE_QUERY_SIZE);

                idsResponse = executeRequest(srb);

                hits = idsResponse.getHits().getHits();
                if (hits.length == 0) {
                    break;
                }
            } else {
                break;
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean setClusterSettings(Map<String, Object> settings, boolean persistent) {

        // 1. Set settings
        ClusterUpdateSettingsRequestBuilder b = client.admin()
                .cluster()
                .prepareUpdateSettings();

        if (persistent) {
            b.setPersistentSettings(settings);
        } else {
            b.setTransientSettings(settings);
        }

        // 2. Execute
        return b.execute()
                .actionGet(adminActionTimeout)
                .isAcknowledged();
    }

    public Client getClient() {
        return client;
    }
}
