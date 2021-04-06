package com.unidata.mdm.backend.service.search.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.unidata.mdm.backend.service.search.util.SearchUtils.DEFAULT_NUMBER_OF_REPLICAS;
import static com.unidata.mdm.backend.service.search.util.SearchUtils.DEFAULT_NUMBER_OF_SHARDS;

@Component
public class ElasticIndexComponent extends ElasticBaseComponent implements IndexComponent {

    @Value("${unidata.elastic.admin.action.timeout:5000}")
    private volatile long adminActionTimeout = 5000L;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAgentComponent.class);
    /**
     * Default type settings.
     */
    private static final Settings DEFAULT_INDEX_SETTINGS = Settings.builder()
            .put("analysis.analyzer." + SearchUtils.DEFAULT_STRING_ANALYZER_NAME + ".type", "custom")
            .put("analysis.analyzer." + SearchUtils.DEFAULT_STRING_ANALYZER_NAME + ".tokenizer", "standard")
            .putArray("analysis.analyzer." + SearchUtils.DEFAULT_STRING_ANALYZER_NAME + ".filter",
                    "standard",
                    "lowercase",
                    "autocomplete_filter")
            .put("analysis.filter.autocomplete_filter.type", "edge_ngram")
            .put("analysis.filter.autocomplete_filter.min_gram", "1")
            .put("analysis.filter.autocomplete_filter.max_gram", "55")
            .build();
    /**
     * Morpho russian + english.
     */
    private static final Settings MORPHOLOGICAL_INDEX_SETTINGS = Settings.builder()
            .put("analysis.analyzer." + SearchUtils.MORPH_STRING_ANALYZER_NAME + ".type", "custom")
            .put("analysis.analyzer." + SearchUtils.MORPH_STRING_ANALYZER_NAME + ".tokenizer", "standard")
            .putArray("analysis.analyzer." + SearchUtils.MORPH_STRING_ANALYZER_NAME + ".filter",
                    "lowercase",
                    "russian_morphology",
                    "english_morphology",
                    "morph_stopwords")
            .put("analysis.filter.morph_stopwords.type", "stop")
            .put("analysis.filter.morph_stopwords.stopwords",
                    "а,без,более,бы,был,была,были,было,быть,в,вам,вас,весь,во,вот,все,всего,всех,вы,где,да,даже,для," +
                    "до,его,ее,если,есть,еще,же,за,здесь,и,из,или,им,их,к,как,ко,когда,кто,ли,либо,мне,может,мы,на,надо," +
                    "наш,не,него,нее,нет,ни,них,но,ну,о,об,однако,он,она,они,оно,от,очень,по,под,при,с,со,так,также,такой," +
                    "там,те,тем,то,того,тоже,той,только,том,ты,у,уже,хотя,чего,чей,чем,что,чтобы,чье,чья,эта,эти,это,я,a," +
                    "an,and,are,as,at,be,but,by,for,if,in,into,is,it,no,not,of,on,or,such,that,the,their,then,there,these,they,this,to,was,will,with")
            .build();

    /**
     * Number of shards
     */
    @Value("${" + ConfigurationConstants.SEARCH_SHARDS_NUMBER_PROPERTY + ":" + SearchUtils.DEFAULT_NUMBER_OF_SHARDS + "}")
    private String numberOfShards = DEFAULT_NUMBER_OF_SHARDS;
    /**
     * Number of replicas
     */
    @Value("${" + ConfigurationConstants.SEARCH_REPLICAS_NUMBER_PROPERTY + ":" + SearchUtils.DEFAULT_NUMBER_OF_REPLICAS + "}")
    private String numberOfReplicas = DEFAULT_NUMBER_OF_REPLICAS;
    /**
     * Number of fields per index.
     */
    @Value("${" + ConfigurationConstants.SEARCH_FIELDS_NUMBER_PROPERTY + ":" + SearchUtils.DEFAULT_NUMBER_OF_FIELDS + "}")
    private String numberOfFields = SearchUtils.DEFAULT_NUMBER_OF_FIELDS;
    /**
     * Transport client to use.
     */
    @Autowired
    private Client client;

    public ElasticIndexComponent() {
    }

    public ElasticIndexComponent(Client client) {
        this.client = client;
    }

    @Override
    public boolean safeCreateIndex(@Nonnull final SearchRequestContext ctx, @Nullable Properties properties) {
        boolean exists = indexExists(ctx);
        if (exists) {
            return true;
        } else {
            return createIndex(ctx, properties);
        }
    }

    @Override
    public boolean forceCreateIndex(@Nonnull final SearchRequestContext ctx, @Nullable Properties properties) {
        return createIndex(ctx, properties);
    }

    private boolean createIndex(@Nonnull final SearchRequestContext ctx, @Nullable Properties properties) {
        // 1. Compose the name of the type
         final String indexName = constructIndexName(ctx);

        // 2. Delete if requested
        dropIndex(ctx);

        // 3. Put a new one
        Settings settings = null;
        try {

            settings = Settings.builder()
                    .put(properties == null ? getDefaultProperties() : properties)
                    .put(DEFAULT_INDEX_SETTINGS)
                    .put(MORPHOLOGICAL_INDEX_SETTINGS)
                    .build();

        } catch (Exception t) {
            LOGGER.error("Settings builder failed. {}", t);
            return false;
        }

        boolean result = client.admin()
                .indices()
                .create(new CreateIndexRequest(indexName, settings))
                .actionGet(adminActionTimeout)
                .isAcknowledged();

        if (!result) {
            LOGGER.error("Failed to create index '{}'.", indexName);
        }

        return result;
    }

    /**
     * Drops an index.
     *
     * @param ctx the context to use
     * @return true, if successful, false otherwise
     */
    @Override
    public boolean dropIndex(@Nonnull final SearchRequestContext ctx) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);

        // 2. Check exists
        boolean exists = indexExists(ctx);

        // 3. Delete
        boolean deleteSuccess = false;
        if (exists) {
            deleteSuccess = client.admin()
                    .indices()
                    .delete(new DeleteIndexRequest(indexName))
                    .actionGet(adminActionTimeout)
                    .isAcknowledged();
            if (!deleteSuccess) {
                LOGGER.error("Failed to delete index '{}'. Returning.", indexName);
                return false;
            }
        }

        return deleteSuccess;
    }

    /**
     * Tells if an index exists.
     *
     * @param ctx the context
     * @return true, if exists, false otherwise
     */
    @Override
    public boolean indexExists(@Nonnull final SearchRequestContext ctx) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);

        // 2. Check index exists
        return client.admin()
                .indices()
                .exists(new IndicesExistsRequest(indexName))
                .actionGet(adminActionTimeout)
                .isExists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean refreshIndex(SearchRequestContext ctx, boolean wait) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);

        // 2. Check index exists
        if (wait) {
            return client.admin()
                    .indices()
                    .refresh(new RefreshRequest(indexName))
                    .actionGet()
                    .getFailedShards() == 0;
        }

        return client.admin()
                .indices()
                .refresh(new RefreshRequest(indexName))
                .actionGet(adminActionTimeout)
                .getFailedShards() == 0;
    }

    /**
     * TODO remove!
     * {@inheritDoc}
     */
    @Override
    public boolean setIndexRefreshInterval(SearchRequestContext ctx, String value) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);

        // 2. Set refresh interval
        return client.admin()
                .indices()
                .prepareUpdateSettings(indexName)
                .setSettings(Collections.singletonMap("index.refresh_interval", value))
                .execute()
                .actionGet(adminActionTimeout)
                .isAcknowledged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean closeIndex(SearchRequestContext ctx) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);

        // 2. Close the index
        return client.admin()
                .indices()
                .prepareClose(indexName)
                .execute()
                .actionGet(adminActionTimeout)
                .isAcknowledged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean openIndex(SearchRequestContext ctx) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);

        // 2. Open the index
        return client.admin()
                .indices()
                .prepareOpen(indexName)
                .execute()
                .actionGet(adminActionTimeout)
                .isAcknowledged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setIndexSettings(SearchRequestContext ctx, Map<String, Object> settings) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);

        // 2. Set refresh interval
        return client.admin()
                .indices()
                .prepareUpdateSettings(indexName)
                .setSettings(settings)
                .execute()
                .actionGet(adminActionTimeout)
                .isAcknowledged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     *
     * @return ES properties
     */
    private Properties getDefaultProperties() {

        Properties indexProperties = new Properties();
        String shards = StringUtils.isBlank(numberOfShards) ? SearchUtils.DEFAULT_NUMBER_OF_SHARDS : numberOfShards;
        String replicas = StringUtils.isBlank(numberOfReplicas) ? SearchUtils.DEFAULT_NUMBER_OF_REPLICAS : numberOfReplicas;
        String fields = StringUtils.isBlank(numberOfFields) ? SearchUtils.DEFAULT_NUMBER_OF_FIELDS : numberOfFields;

        indexProperties.setProperty(SearchUtils.ES_NUMBER_OF_SHARDS_SETTING, shards);
        indexProperties.setProperty(SearchUtils.ES_NUMBER_OF_REPLICAS_SETTING, replicas);
        indexProperties.setProperty(SearchUtils.ES_LIMIT_OF_TOTAL_FIELDS, fields);
        indexProperties.setProperty("max_result_window", "200000");

        return indexProperties;
    }
}
