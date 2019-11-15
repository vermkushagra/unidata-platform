package org.unidata.mdm.search.service.impl;

import static org.unidata.mdm.search.util.SearchUtils.DEFAULT_NUMBER_OF_REPLICAS;
import static org.unidata.mdm.search.util.SearchUtils.DEFAULT_NUMBER_OF_SHARDS;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.cluster.settings.ClusterUpdateSettingsRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.unidata.mdm.search.configuration.SearchConfigurationConstants;
import org.unidata.mdm.search.context.MappingRequestContext;
import org.unidata.mdm.search.context.TypedSearchContext;
import org.unidata.mdm.search.exception.SearchExceptionIds;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.HierarchicalIndexType;
import org.unidata.mdm.search.type.mapping.Mapping;
import org.unidata.mdm.search.type.mapping.MappingField;
import org.unidata.mdm.search.type.mapping.impl.AbstractTemporalMappingField;
import org.unidata.mdm.search.type.mapping.impl.AbstractValueMappingField;
import org.unidata.mdm.search.type.mapping.impl.CompositeMappingField;
import org.unidata.mdm.search.type.mapping.impl.StringMappingField;
import org.unidata.mdm.search.util.SearchUtils;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Mikhail Mikhailov on Oct 8, 2019
 */
@Component
public class MappingComponentImpl extends BaseAgentComponent {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingComponentImpl.class);
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

    private static final Settings LOWERCASE_NORMALIZER_INDEX_SETTINGS = Settings.builder()
            .put("analysis.normalizer." + SearchUtils.LOWERCASE_STRING_NORMALIZER_NAME  +  ".type", "custom")
            .putArray("analysis.normalizer." + SearchUtils.LOWERCASE_STRING_NORMALIZER_NAME + ".filter",
                      "lowercase")
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
    @Value("${" + SearchConfigurationConstants.SEARCH_SHARDS_NUMBER_PROPERTY + ":" + SearchUtils.DEFAULT_NUMBER_OF_SHARDS + "}")
    private String numberOfShards = DEFAULT_NUMBER_OF_SHARDS;
    /**
     * Number of replicas
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_REPLICAS_NUMBER_PROPERTY + ":" + SearchUtils.DEFAULT_NUMBER_OF_REPLICAS + "}")
    private String numberOfReplicas = DEFAULT_NUMBER_OF_REPLICAS;
    /**
     * Number of fields per index.
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_FIELDS_NUMBER_PROPERTY + ":" + SearchUtils.DEFAULT_NUMBER_OF_FIELDS + "}")
    private String numberOfFields = SearchUtils.DEFAULT_NUMBER_OF_FIELDS;
    /**
     * Number of shards for entity
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_ENTITY_SHARDS_NUMBER_PROPERTY + ":}")
    private String numberOfShardsForEntity = "";
    /**
     * Number of replicas for entity
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_ENTITY_REPLICAS_NUMBER_PROPERTY + ":}")
    private String numberOfReplicasForEntity = "";
    /**
     * Number of shards for lookup
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_LOOKUP_ENTITY_SHARDS_NUMBER_PROPERTY + ":}")
    private String numberOfShardsForLookup = "";
    /**
     * Number of replicas
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_LOOKUP_ENTITY_REPLICAS_NUMBER_PROPERTY + ":}")
    private String numberOfReplicasForLookup = "";
    /**
     * Admin action timeout.
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_ADMIN_ACTION_TIMEOUT + ":5000}")
    private long adminActionTimeout;
    /**
     * Transport client to use.
     */
    @Autowired
    private Client client;
    /**
     * Constructor.
     */
    public MappingComponentImpl() {
        super();
    }
    /**
     * Does processing of the mapping process, which may be:
     * <ul>
     * <li>- create index</li>
     * <li>- drop index</li>
     * <li>- put / merge mapping</li>
     * </ul>
     * @param ctx the context
     * @return true, if successful, false otherwise
     */
    public boolean process(@Nonnull MappingRequestContext ctx) {

        // Drop and exit
        if (ctx.drop()) {
            return dropIndex(ctx);
        }

        boolean exists = indexExists(ctx);
        if (exists && ctx.forceCreate()) {
            closeIndex(ctx);
            dropIndex(ctx);
            exists = false;
        }

        if (!exists) {
            createIndex(ctx, initIndexProperties(ctx));
        }

        for (Mapping m : ctx.getMappings()) {
            createMapping(ctx, m);
        }

        return true;
    }

    private Properties initIndexProperties(MappingRequestContext ctx) {

        Properties indexProperties = new Properties();
        indexProperties.setProperty(
                SearchUtils.ES_NUMBER_OF_SHARDS_SETTING,
                ctx.getShards() > 0 ? Integer.toString(ctx.getShards()) : numberOfShardsForEntity);
        indexProperties.setProperty(
                SearchUtils.ES_NUMBER_OF_REPLICAS_SETTING,
                ctx.getReplicas() > 0 ? Integer.toString(ctx.getReplicas()) : numberOfReplicasForEntity);

        if (ctx.getFields() > 0) {
            indexProperties.setProperty(SearchUtils.ES_LIMIT_OF_TOTAL_FIELDS, Integer.toString(ctx.getFields()));
        }

        if (ctx.whitespace()) {
            indexProperties.put("analysis.analyzer." + SearchUtils.DEFAULT_STRING_ANALYZER_NAME + ".tokenizer", "whitespace");
            indexProperties.put("analysis.analyzer." + SearchUtils.MORPH_STRING_ANALYZER_NAME + ".tokenizer", "whitespace");
        }

        return indexProperties;
    }

    private Properties verifyIndexProperties(Properties specialProperties) {

       Properties result = new Properties();

       // This is block with default properties.
       String shards = StringUtils.isBlank(numberOfShards) ? SearchUtils.DEFAULT_NUMBER_OF_SHARDS : numberOfShards;
       String replicas = StringUtils.isBlank(numberOfReplicas) ? SearchUtils.DEFAULT_NUMBER_OF_REPLICAS : numberOfReplicas;
       String fields = StringUtils.isBlank(numberOfFields) ? SearchUtils.DEFAULT_NUMBER_OF_FIELDS : numberOfFields;

       result.setProperty(SearchUtils.ES_NUMBER_OF_SHARDS_SETTING, shards);
       result.setProperty(SearchUtils.ES_NUMBER_OF_REPLICAS_SETTING, replicas);
       result.setProperty(SearchUtils.ES_LIMIT_OF_TOTAL_FIELDS, fields);
       result.setProperty("max_result_window", getMaxWindowSize().toString());

       if (specialProperties != null) {
           result.putAll(specialProperties);
       }

       return result;
   }

    private boolean createIndex(@Nonnull final TypedSearchContext ctx, @Nullable Properties properties) {

        // 1. Compose the name of the type
        final String indexName = constructIndexName(ctx);

        // 2. Delete if requested
        dropIndex(ctx);

        // 3. Put a new one
        Settings settings = null;
        try {

            settings = Settings.builder()
                    .put(DEFAULT_INDEX_SETTINGS)
                    .put(MORPHOLOGICAL_INDEX_SETTINGS)
                    .put(LOWERCASE_NORMALIZER_INDEX_SETTINGS)
                    .put(verifyIndexProperties(properties))
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

    private boolean createMapping(MappingRequestContext ctx, Mapping mapping) {

        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {

            if (!mapping.getIndexType().isHierarchical()
              || mapping.getIndexType().toHierarchical().isTopType()) {
                builder
                    .startObject()
                        .startObject(mapping.getIndexType().getName())
                            .field("dynamic", false)
                            .startObject("properties");
            } else {

                HierarchicalIndexType hit = mapping.getIndexType().toHierarchical();
                builder
                    .startObject()
                        .startObject(mapping.getIndexType().getName())
                            .startObject(SearchUtils.PARENT_FIELD)
                                .field("type", hit.getTopType().getName())
                            .endObject()
                            .field("dynamic", false)
                            .startObject("properties");
            }

            for (MappingField field : mapping.getFields()) {

                if (field.getFieldType() == FieldType.COMPOSITE) {
                    createCompositeFieldMapping((CompositeMappingField) field, builder);
                } else {
                    createValueFieldMapping((AbstractValueMappingField<?>) field, builder);
                }
            }

            builder
                            .endObject()
                        .endObject()
                    .endObject();

            final String indexName = constructIndexName(ctx);
            PutMappingRequestBuilder request = client.admin()
                    .indices()
                    .preparePutMapping(indexName)
                    .setSource(builder)
                    .setType(mapping.getIndexType().getName());

            PutMappingResponse response = executeRequest(request);
            return response.isAcknowledged();

        } catch (Exception e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new PlatformFailureException(message, e, SearchExceptionIds.EX_SEARCH_MAPPING_IO_FAILURE);
        }
    }

    private void createCompositeFieldMapping(CompositeMappingField field, XContentBuilder builder) throws IOException {

        builder
            .startObject(field.getName());

        if (field.isNested()) {
            builder.field("type", "nested");
        }

        builder.startObject("properties");

        for (MappingField f : field.getFields()) {

            if (f.getFieldType() == FieldType.COMPOSITE) {
                createCompositeFieldMapping((CompositeMappingField) f, builder);
            } else {
                createValueFieldMapping((AbstractValueMappingField<?>) f, builder);
            }
        }

        builder
                .endObject()
            .endObject();
    }

    private void createValueFieldMapping(AbstractValueMappingField<?> field, XContentBuilder builder) throws IOException {

        builder
            .startObject(field.getName());

        switch (field.getFieldType()) {
        case BOOLEAN:
            builder.field("type", "boolean");
            break;
        case DATE:
        case TIME:
        case TIMESTAMP:
            builder.field("type", "date");

            AbstractTemporalMappingField<?> temporalField = (AbstractTemporalMappingField<?>) field;
            if (StringUtils.isNotBlank(temporalField.getFormat())) {
                builder.field("format", temporalField.getFormat());
            } else if (temporalField.getFieldType() == FieldType.TIME) {
                builder.field("format", "'T'HH:mm:ss||'T'HH:mm:ss.SSS||HH:mm:ss||HH:mm:ss.SSS||'T'HH:mm:ssZZ||'T'HH:mm:ss.SSSZZ||HH:mm:ssZZ||HH:mm:ss.SSSZZ");
            }

            break;
        case NUMBER:
            builder.field("type", "double");
            break;
        case INTEGER:
            builder.field("type", "long");
            break;
        case STRING:

            StringMappingField stringField = (StringMappingField) field;
            if (stringField.isNonAnalyzable()) {
                builder.field("type", "keyword");
            } else {

                builder.field("type", "text");
                builder.field("analyzer", SearchUtils.DEFAULT_STRING_ANALYZER_NAME);

                // Add not analyzed counter part for term matches
                builder
                    .startObject("fields")
                        .startObject(SearchUtils.NAN_FIELD)
                            .field("type", "keyword");

                if (stringField.isCaseInsensitive()) {
                    builder.field("normalizer", SearchUtils.LOWERCASE_STRING_NORMALIZER_NAME);
                }

                builder.endObject(); // $nan

                if (stringField.isMorphologicalAnalysis()) {
                    builder
                        .startObject(SearchUtils.MORPH_FIELD)
                            .field("type", "text")
                            .field("analyzer", SearchUtils.MORPH_STRING_ANALYZER_NAME)
                        .endObject();
                }

                builder.endObject(); // fields
            }

            break;
        default:
            final String message = "Cannot map attribute '{}'. Type '{}' is not recognized.";
            LOGGER.warn(message, field.getName(), field.getFieldType());
            throw new PlatformFailureException(message, SearchExceptionIds.EX_SEARCH_MAPPING_TYPE_UNKNOWN,
                    field.getName(), field.getFieldType().name());
        }

        if (Objects.nonNull(field.getDefaultValue())) {
            builder.field("null_value", field.getDefaultValue());
        }

        if (field.isDocValue()) {
            builder.field("doc_values", true);
        }

        builder
            .endObject();
    }
    /**
     * Drops an index.
     *
     * @param ctx the context to use
     * @return true, if successful, false otherwise
     */
    public boolean dropIndex(@Nonnull final TypedSearchContext ctx) {

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
        } else {
            return true;
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Drop index exception", e);
        }

        return deleteSuccess;
    }

    /**
     * Tells if an index exists.
     *
     * @param ctx the context
     * @return true, if exists, false otherwise
     */
    public boolean indexExists(@Nonnull final TypedSearchContext ctx) {

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
    public boolean refreshIndex(TypedSearchContext ctx, boolean wait) {

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
     * {@inheritDoc}
     */
    public boolean closeIndex(TypedSearchContext ctx) {

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
    public boolean openIndex(TypedSearchContext ctx) {

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
    public boolean setIndexSettings(TypedSearchContext ctx, Map<String, Object> settings) {

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
}
