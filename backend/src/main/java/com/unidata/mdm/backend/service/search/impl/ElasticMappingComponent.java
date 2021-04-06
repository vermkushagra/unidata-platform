package com.unidata.mdm.backend.service.search.impl;

import static com.unidata.mdm.backend.common.search.types.EntitySearchType.ETALON;
import static com.unidata.mdm.backend.common.search.types.EntitySearchType.ETALON_DATA;
import static com.unidata.mdm.backend.common.search.types.EntitySearchType.ETALON_RELATION;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SearchApplicationException;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.ServiceSearchType;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.search.util.AuditHeaderField;
import com.unidata.mdm.backend.service.search.util.ClassifierDataHeaderField;
import com.unidata.mdm.backend.service.search.util.ClassifierHeaderField;
import com.unidata.mdm.backend.service.search.util.DqHeaderField;
import com.unidata.mdm.backend.service.search.util.MatchingHeaderField;
import com.unidata.mdm.backend.service.search.util.ModelHeaderField;
import com.unidata.mdm.backend.service.search.util.RecordHeaderField;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.CodeAttributeDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;

@Component
public class ElasticMappingComponent extends ElasticBaseComponent implements MappingComponent {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MappingComponent.class);


    /**
     * index component
     */
    @Autowired
    private IndexComponent indexComponent;
    /**
     * classifier cache
     */
    @Autowired
    private ClsfService classifierService;

    @Autowired
    private ConversionService conversionService;
    /**
     * Transport client to use.
     */
    @Autowired
    private Client client;

    public ElasticMappingComponent() {
    }

    public ElasticMappingComponent(Client client,IndexComponent indexComponent) {
        this.client = client;
        this.indexComponent = indexComponent;
    }

    @Override
    public boolean updateModelSearchElementsMapping(@Nonnull String storageId) {
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            // 2. Put mappings
            builder
                    .startObject()
                    .startObject(ServiceSearchType.MODEL.getName())
                    .field("dynamic", false)
                    .startObject("properties");

            builder.startObject(ModelHeaderField.ENTITY_NAME.getField())
                    .field("type", "string")
                    .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                    .endObject();

            builder.startObject(ModelHeaderField.DISPLAY_ENTITY_NAME.getField())
                    .field("type", "string")
                    .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                    .endObject();

            builder.startObject(ModelHeaderField.SEARCH_OBJECT.getField())
                    .field("type", "string")
                    .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                    .endObject();

            builder.startObject(ModelHeaderField.VALUE.getField())
                    .field("type", "string")
                    .field("analyzer", SearchUtils.DEFAULT_STRING_ANALYZER_NAME)
                    .startObject("fields")
                    .startObject(SearchUtils.NAN_FIELD)
                    .field("type", "string")
                    .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                    .endObject()
                    .endObject()
                    .endObject();

            builder
                    .endObject()
                    .endObject()
                    .endObject();
            SearchRequestContext context = SearchRequestContext.forModelElements().storageId(storageId).build();
            return putMapping(context, builder);
        } catch (Exception e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_MAPPING_IO_FAILURE);
        }
    }


    @Override
    public boolean updateAuditSearchNodesMapping(@Nonnull String storageId) {
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            // 2. Put mappings
            builder.startObject().startObject(ServiceSearchType.AUDIT.getName()).field("dynamic", false)
                    .startObject("properties");

            for (AuditHeaderField auditHeaderField : AuditHeaderField.values()) {
                boolean isString = auditHeaderField.getType().equals("string");
                if (isString) {
                    builder.startObject(auditHeaderField.getField());
                    builder.field("analyzer", SearchUtils.DEFAULT_STRING_ANALYZER_NAME);
                    builder.field("type", "string");
                    builder.startObject("fields");
                    builder.startObject(SearchUtils.NAN_FIELD);
                    builder.field("type", "string");
                    builder.field("index", SearchUtils.NONE_STRING_ANALYZER_NAME);
                    builder.endObject();
                    builder.endObject();
                    builder.endObject();
                } else {
                    builder.startObject(auditHeaderField.getField());
                    builder.field("type", auditHeaderField.getType());
                    builder.endObject();
                }
            }
            builder.endObject();


            builder.endObject().endObject();
            SearchRequestContext context = SearchRequestContext.forAuditEvents().storageId(storageId).build();
            return putMapping(context, builder);
        } catch (Exception e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_MAPPING_IO_FAILURE);
        }
    }

    @Override
    public boolean updateClassifierSearchNodesMapping(@Nonnull String storageId) {
        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            // 2. Put mappings
            builder
                    .startObject()
                    .startObject(ServiceSearchType.CLASSIFIER.getName())
                    .field("dynamic", false)
                    .startObject("properties");

            builder.startObject(ClassifierHeaderField.CLASSIFIER_NAME.getField())
                    .field("type", "string")
                    .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                    .endObject();

            builder.startObject(ClassifierHeaderField.NODE_UNIQUE_ID.getField())
                    .field("type", "string")
                    .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                    .endObject();

            builder.startObject(ClassifierHeaderField.NODE_SEARCH_ELEMENT.getField())
                    .field("type", "string")
                    .field("analyzer", SearchUtils.DEFAULT_STRING_ANALYZER_NAME)
                    .startObject("fields")
                    .startObject(SearchUtils.NAN_FIELD)
                    .field("type", "string")
                    .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                    .endObject()
                    .endObject()
                    .endObject();

            builder
                    .endObject()
                    .endObject()
                    .endObject();
            SearchRequestContext context = SearchRequestContext.forClassifierElements().storageId(storageId).build();
            return putMapping(context, builder);
        } catch (Exception e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_MAPPING_IO_FAILURE);
        }
    }

    @Override
    public boolean updateEntityMapping(@Nonnull EntityDef entity, @Nonnull Collection<NestedEntityDef> nestedEntities,
                                       @Nullable String storageId) {
        String entityName = entity.getName();
        SearchRequestContext etalonContext = SearchRequestContext.forEtalon(ETALON, entityName)
                                                                 .storageId(storageId)
                                                                 .build();
        // 1. Create index if not exist
        boolean result = indexComponent.safeCreateIndex(etalonContext, null);
        if (!result) {
            return false;
        }

        boolean relationMapping = relationMapping(entity, storageId);
        if (!relationMapping) {
            return false;
        }

        boolean classifierMapping = classifiersMapping(entity, storageId);
        if (!classifierMapping) {
            return false;
        }

        boolean matchingMapping = matchingMapping(entityName, storageId);
        if (!matchingMapping) {
            return false;
        }

        boolean etalonMapping = etalonMapping(etalonContext);
        if (!etalonMapping) {
            return false;
        }

        SearchRequestContext dataContext = SearchRequestContext.forEtalon(ETALON_DATA, entityName)
                                                               .storageId(storageId)
                                                               .build();
        return entityMapping(entity, nestedEntities, dataContext);
    }

    @Override
    public boolean updateLookupEntityMapping(@Nonnull LookupEntityDef lookupEntityDef, @Nullable String storageId) {
        String lookupEntityName = lookupEntityDef.getName();
        SearchRequestContext etalonContext = SearchRequestContext.forEtalon(ETALON, lookupEntityName)
                                                                 .storageId(storageId)
                                                                 .build();
        // 1. Create index if not exist
        boolean result = indexComponent.safeCreateIndex(etalonContext, null);
        if (!result) {
            return false;
        }

        boolean classifierMapping = classifiersMapping(lookupEntityDef, storageId);
        if (!classifierMapping) {
            return false;
        }

        boolean matchingMapping = matchingMapping(lookupEntityName, storageId);
        if (!matchingMapping) {
            return false;
        }

        boolean etalonMapping = etalonMapping(etalonContext);
        if (!etalonMapping) {
            return false;
        }

        SearchRequestContext dataContext = SearchRequestContext.forEtalon(ETALON_DATA, lookupEntityName)
                                                               .storageId(storageId)
                                                               .build();
        return lookupMapping(lookupEntityDef, dataContext);
    }

    private boolean entityMapping(
            @Nonnull EntityDef entity,
            @Nonnull Collection<NestedEntityDef> nestedEntities,
            @Nonnull SearchRequestContext searchContext) {

        try (XContentBuilder builder = XContentFactory.jsonBuilder()) {
            // 2. Put mappings
            builder
                    .startObject()
                    .startObject(searchContext.getType().getName())
                    .startObject(SearchUtils.PARENT_FIELD)
                    .field("type", ETALON.getName())
                    .endObject()
                    .field("dynamic", false)
                    .startObject("properties");
            processHeader(builder);
            processEntityDef(builder, entity, nestedEntities);
            createErrorsMapping(builder);

            builder
                    .endObject()
                    .endObject()
                    .endObject();

            return putMapping(searchContext, builder);
        } catch (Exception e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_MAPPING_IO_FAILURE);
        }
    }

    /**
     * @param lookupEntityDef - lookup entity definition
     * @param searchContext - data context
     * @return true if mapping was applied
     */
    private boolean lookupMapping(@Nonnull LookupEntityDef lookupEntityDef, @Nonnull SearchRequestContext searchContext) {
        try (XContentBuilder dataBuilder = XContentFactory.jsonBuilder()) {
            // 2. Put mappings
            dataBuilder
                    .startObject()
                    .startObject(searchContext.getType().getName())
                    .startObject(SearchUtils.PARENT_FIELD)
                    .field("type", ETALON.getName())
                    .endObject()
                    .field("dynamic", false)
                    .startObject("properties");
            processHeader(dataBuilder);
            processLookupEntityDef(dataBuilder, lookupEntityDef);
            createErrorsMapping(dataBuilder);
            dataBuilder
                    .endObject()
                    .endObject()
                    .endObject();
            return putMapping(searchContext, dataBuilder);
        } catch (Exception e) {
            final String message = "XContentBuilder threw an exception. {}.";
            LOGGER.warn(message, e);
            throw new SearchApplicationException(message, e, ExceptionId.EX_SEARCH_MAPPING_IO_FAILURE);
        }
    }

    /**
     * @param searchContext - search context
     * @return true if mapping was applied.
     */
    private boolean etalonMapping(@Nonnull SearchRequestContext searchContext) {
        try (XContentBuilder etalonBuilder = XContentFactory.jsonBuilder()) {

            etalonBuilder
                .startObject()
                    .startObject(searchContext.getType().getName())
                        .field("dynamic", false)
                    .endObject()
                .endObject();

            return putMapping(searchContext, etalonBuilder);
        } catch (Exception e) {
            LOGGER.warn("Error during putting etalon mapping, {}", e);
            return false;
        }
    }

    /**
     * Process classifier data, defined for this entity.
     * @param entity entity name
     * @param  storageId storage id
     * @return true, if successful, false otherwise
     */
    private boolean classifiersMapping(@Nonnull AbstractEntityDef entity, @Nullable String storageId) {

        SearchRequestContext classifierDataContext = SearchRequestContext
                .forEtalon(EntitySearchType.CLASSIFIER, entity.getName())
                .storageId(storageId)
                .build();

        try (XContentBuilder classifierBuilder = XContentFactory.jsonBuilder()) {
            classifierBuilder
                .startObject()
                    .startObject(classifierDataContext.getType().getName())
                        .startObject(SearchUtils.PARENT_FIELD)
                             .field("type", EntitySearchType.ETALON.getName())
                        .endObject()
                        .field("dynamic", false)
                        .startObject("properties")
                            .startObject(ClassifierDataHeaderField.FIELD_ETALON_ID.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(ClassifierDataHeaderField.FIELD_ETALON_ID_RECORD.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(ClassifierDataHeaderField.FIELD_FROM.getField())
                                .field("type", "date")
                                .field("null_value", SearchUtils.ES_MIN_FROM)
                            .endObject()
                            .startObject(ClassifierDataHeaderField.FIELD_TO.getField())
                                .field("type", "date")
                                .field("null_value", SearchUtils.ES_MAX_TO)
                            .endObject()
                            .startObject(ClassifierDataHeaderField.FIELD_CREATED_AT.getField())
                                .field("type", "date")
                            .endObject()
                            .startObject(ClassifierDataHeaderField.FIELD_UPDATED_AT.getField())
                                .field("type", "date")
                            .endObject()
                            .startObject(ClassifierDataHeaderField.FIELD_NAME.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(ClassifierDataHeaderField.FIELD_PENDING.getField())
                                .field("type", "boolean")
                                .field("null_value", false)
                            .endObject()
                            .startObject(ClassifierDataHeaderField.FIELD_PUBLISHED.getField())
                                .field("type", "boolean")
                                .field("null_value", true)
                            .endObject()
                            .startObject(ClassifierDataHeaderField.FIELD_DELETED.getField())
                                .field("type", "boolean")
                                .field("null_value", false)
                            .endObject();

            for (String classifierName : entity.getClassifiers()) {

                ClsfDTO classifier = classifierService.getClassifierByName(classifierName);
                if (classifier == null) {
                    continue;
                }

                classifierBuilder
                            .startObject(classifierName)
                                .startObject("properties")
                                    .startObject(ClassifierDataHeaderField.FIELD_ROOT_NODE_ID.getField())
                                        .field("type", "string")
                                        .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                                    .endObject()
                                    .startObject(ClassifierDataHeaderField.FIELD_NODES.getField())
                                        .startObject("properties")
                                            .startObject(ClassifierDataHeaderField.FIELD_NODE_ID.getField())
                                                .field("type", "string")
                                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                                            .endObject()
                                        .endObject()
                                    .endObject();

                List<ClsfNodeAttrDTO> allAttrs = classifierService.getAllClsfAttr(classifierName);
                for (ClsfNodeAttrDTO classifierAttr : allAttrs) {

                    SimpleAttributeWithOptionalValueDef attrDef
                        = conversionService.convert(classifierAttr, SimpleAttributeWithOptionalValueDef.class);

                    String originalType = attrDef.getValueType() != null ? attrDef.getValueType().name() : null;
                    String type = getTypeFromDataType(originalType);

                    createField(classifierBuilder, classifierAttr.getAttrName(), type, originalType, false);
                }

                classifierBuilder
                                .endObject()
                            .endObject();
            }

            classifierBuilder
                        .endObject()
                    .endObject()
                .endObject();

            return putMapping(classifierDataContext, classifierBuilder);
        } catch (Exception e) {
            LOGGER.warn("Error during putting classifier data mapping, {}", e);
            return false;
        }
    }
    /**
     * @param entity - relation entity definition
     * @param  storageId storage id
     * @return true if mapping was applied.
     */
    private boolean relationMapping(@Nonnull AbstractEntityDef entity, @Nullable String storageId) {

        SearchRequestContext relContext = SearchRequestContext.forEtalon(ETALON_RELATION, entity.getName())
                .storageId(storageId)
                .build();

        try (XContentBuilder relationBuilder = XContentFactory.jsonBuilder()) {
            relationBuilder
                .startObject()
                    .startObject(relContext.getType().getName())
                        .startObject(SearchUtils.PARENT_FIELD)
                             .field("type", ETALON.getName())
                        .endObject()
                        .field("dynamic", false)
                        .startObject("properties")
                            .startObject(RelationHeaderField.FIELD_ETALON_ID.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_FROM_ETALON_ID.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_TO_ETALON_ID.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_PERIOD_ID.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(RelationHeaderField.REL_TYPE.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(RelationHeaderField.REL_NAME.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_FROM.getField())
                                .field("type", "date")
                                .field("null_value", SearchUtils.ES_MIN_FROM)
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_TO.getField())
                                .field("type", "date")
                                .field("null_value", SearchUtils.ES_MAX_DATE)
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_CREATED_AT.getField())
                                .field("type", "date")
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_UPDATED_AT.getField())
                                .field("type", "date")
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_PENDING.getField())
                                .field("type", "boolean")
                                .field("null_value", false)
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_PUBLISHED.getField())
                                .field("type", "boolean")
                                .field("null_value", true)
                            .endObject()
                            .startObject(RelationHeaderField.FIELD_DELETED.getField())
                                .field("type", "boolean")
                                .field("null_value", false)
                            .endObject()
                        .endObject()
                    .endObject()
               .endObject();

            return putMapping(relContext, relationBuilder);
        } catch (Exception e) {
            LOGGER.warn("Error during putting etalon mapping, {}", e);
            return false;
        }
    }

    /**
     * Adds matching mapping to main type.
     * @param entityName entity name
     * @param storageId storage id
     * @return true, if successful, false otherwise
     */
    private boolean matchingMapping(@Nonnull String entityName, String storageId) {
        boolean result = true;
        SearchRequestContext relContext = SearchRequestContext.forEtalon(EntitySearchType.MATCHING, entityName)
                .storageId(storageId)
                .build();

        try (XContentBuilder matchingBuilder = XContentFactory.jsonBuilder()) {
            matchingBuilder
                .startObject()
                    .startObject(relContext.getType().getName())
                        .startObject(SearchUtils.PARENT_FIELD)
                             .field("type", EntitySearchType.MATCHING.getTopType().getName())
                        .endObject()
                        .field("dynamic", false)
                        .startObject("properties")
                            .startObject(MatchingHeaderField.FIELD_ETALON_ID.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(MatchingHeaderField.FIELD_FROM.getField())
                                .field("type", "date")
                                .field("null_value", SearchUtils.ES_MIN_FROM)
                            .endObject()
                            .startObject(MatchingHeaderField.FIELD_TO.getField())
                                .field("type", "date")
                                .field("null_value", SearchUtils.ES_MAX_TO)
                            .endObject()
                            .startObject(MatchingHeaderField.FIELD_GROUP_ID.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                                .field("doc_values", Boolean.TRUE)
                            .endObject()
                            .startObject(MatchingHeaderField.FIELD_RULE_ID.getField())
                                .field("type", "string")
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                                .field("doc_values", Boolean.TRUE)
                            .endObject()
                            // More types follow. One field per supported type of analysis.
                            // Only exact is supported so far.
                            .startObject(MatchingHeaderField.FIELD_EXACT_CLUSTER_DATA.getField())
                                .field("type", "string")
                                .field("doc_values", Boolean.TRUE)
                                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                            .endObject()
                            .startObject(MatchingHeaderField.FIELD_CREATED_AT.getField())
                                .field("type", "date")
                                .field("doc_values", Boolean.TRUE)
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject();

            result =  putMapping(relContext, matchingBuilder);
        } catch (Exception e) {
            LOGGER.warn("Error during putting matching mapping, {}", e);
            return false;
        }

        SearchRequestContext matchingHeaderContext = SearchRequestContext.forEtalon(EntitySearchType.MATCHING_HEAD, entityName)
                .storageId(storageId)
                .build();
        try (XContentBuilder matchingHeadBuilder = XContentFactory.jsonBuilder()) {
            matchingHeadBuilder
                .startObject()
                    .startObject(matchingHeaderContext.getType().getName())
                        .field("dynamic", false)
                    .endObject()
                .endObject();
            result &= putMapping(matchingHeaderContext, matchingHeadBuilder);
        } catch (Exception e) {
            LOGGER.warn("Error during putting matching mapping, {}", e);
            return false;
        }
        return result;
    }

    @Override
    public boolean updateRelationDefMapping (RelationDef relation, @Nullable String storageId) {
        SearchRequestContext forEntity = SearchRequestContext.forEtalon(ETALON_RELATION, relation.getFromEntity())
                .storageId(storageId)
                .build();
        SearchRequestContext toEntity = SearchRequestContext.forEtalon(ETALON_RELATION, relation.getToEntity())
                .storageId(storageId)
                .build();
        try (XContentBuilder relationBuilder = XContentFactory.jsonBuilder()) {
            relationBuilder.startObject()
                    .startObject(forEntity.getType().getName())
                        .startObject(SearchUtils.PARENT_FIELD)
                            .field("type", ETALON.getName())
                        .endObject()
                        .field("dynamic", false)
                        .startObject("properties")
                            .startObject(relation.getName())
                                .startObject("properties");
                                    processEntityDef(relationBuilder, relation, Collections.emptyList());
            relationBuilder.endObject()
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();

            boolean result =  putMapping(forEntity, relationBuilder);
            result &= putMapping(toEntity, relationBuilder);
            return result;
        } catch (Exception e) {
            LOGGER.warn("Error during putting etalon mapping, {}", e);
            return false;
        }
    }

    /**
     * Writes special header fields to the mapping.
     *
     * @param builder the builder
     * @return builder
     * @throws IOException
     */
    private XContentBuilder processHeader(XContentBuilder builder)
            throws IOException {

        builder
            .startObject(RecordHeaderField.FIELD_ETALON_ID.getField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                .field("doc_values", true)
            .endObject()
            .startObject(RecordHeaderField.FIELD_PERIOD_ID.getField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                .field("doc_values", true)
            .endObject()
            .startObject(RecordHeaderField.FIELD_ORIGINATOR.getField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
            .endObject()
            .startObject(RecordHeaderField.FIELD_FROM.getField())
                .field("type", "date")
                .field("null_value", SearchUtils.ES_MIN_FROM)
            .endObject()
            .startObject(RecordHeaderField.FIELD_TO.getField())
                .field("type", "date")
                .field("null_value", SearchUtils.ES_MAX_TO)
            .endObject()
            .startObject(RecordHeaderField.FIELD_CREATED_AT.getField())
                .field("type", "date")
            .endObject()
            .startObject(RecordHeaderField.FIELD_UPDATED_AT.getField())
                .field("type", "date")
                .endObject()
            .startObject(RecordHeaderField.FIELD_PENDING.getField())
                .field("type", "boolean")
                .field("null_value", false)
            .endObject()
            .startObject(RecordHeaderField.FIELD_PUBLISHED.getField())
                .field("type", "boolean")
                .field("null_value", true)
            .endObject()
            .startObject(RecordHeaderField.FIELD_DELETED.getField())
                .field("type", "boolean")
                .field("null_value", false)
            .endObject();

        return builder;
    }

    /**
     * Processes NestedEntityDef recursively.
     *
     * @param builder the builder
     * @param def     the definition
     * @return updated builder instance
     * @throws IOException
     */
    private XContentBuilder processEntityDef(final XContentBuilder builder,
                                             final ComplexAttributesHolderEntityDef def, @Nonnull Collection<NestedEntityDef> nestedEntities) throws IOException {

        // 1. Simple attributes
        for (SimpleAttributeDef simpleAttr : def.getSimpleAttribute()) {
            processSimpleAttribute(builder, simpleAttr);
        }

        // 2. Array attributes
        for (ArrayAttributeDef arrayAttr : def.getArrayAttribute()) {
            processArrayAttribute(builder, arrayAttr);
        }

        // 3. Complex attributes
        for (ComplexAttributeDef complexAttr : def.getComplexAttribute()) {
            String nestedEntityName = complexAttr.getNestedEntityName();
            NestedEntityDef nested = nestedEntities.stream()
                    .filter(nestedEntityDef -> nestedEntityDef.getName().equals(nestedEntityName))
                    .findAny().orElse(null);

            if (nested == null) {
                String message = "Invalid model. Nested entity [{}] from complex attribute [{}] not found.";
                LOGGER.warn(message, nestedEntityName, complexAttr.getName());
                throw new SearchApplicationException(message,
                        ExceptionId.EX_SEARCH_MAPPING_NESTED_ENTITY_NOT_FOUND,
                        complexAttr.getNestedEntityName(), complexAttr.getName());
            }

            builder
                    .startObject(complexAttr.getName())
                    .startObject("properties");
            processEntityDef(builder, nested, nestedEntities);
            builder
                    .endObject()
                    .endObject();
        }

        return builder;
    }

    /**
     * Processes LookupEntityDef.
     *
     * @param builder the builder
     * @param def     the lookup entity
     * @return updated XContent builder
     * @throws IOException
     */
    private XContentBuilder processLookupEntityDef(
            final XContentBuilder builder, final LookupEntityDef def)
            throws IOException {

        // 1. Code attribute and alias code attributes
        Collection<CodeAttributeDef> codeAttributeCollection = new ArrayList<>();
        codeAttributeCollection.add(def.getCodeAttribute());
        codeAttributeCollection.addAll(def.getAliasCodeAttributes());
        for (CodeAttributeDef codeAttribute : codeAttributeCollection) {
            if (codeAttribute.getName() != null) {
                String originalType = codeAttribute.getSimpleDataType().name();
                createField(builder, codeAttribute.getName(), getTypeFromDataType(originalType), originalType, false);
            }
        }

        // 2. Simple attributes
        for (SimpleAttributeDef attr : def.getSimpleAttribute()) {
            processSimpleAttribute(builder, attr);
        }

        // 3. Array attributes
        for (ArrayAttributeDef arrayAttr : def.getArrayAttribute()) {
            processArrayAttribute(builder, arrayAttr);
        }
        return builder;
    }

    /**
     * Processes an array attribute from EntityDef.
     *
     * @param builder the builder
     * @param attr    the attribute
     * @return the builder
     * @throws IOException
     */
    private XContentBuilder processArrayAttribute(final XContentBuilder builder, ArrayAttributeDef attr) throws IOException {

        if (attr == null) {
            return builder;
        }

        String name = attr.getName();
        String type = null;
        String originalType = null;
        if (StringUtils.isNotBlank(attr.getLookupEntityType())) {
            originalType = attr.getLookupEntityCodeAttributeType().name();
            type = getTypeFromDataType(originalType);
        } else if (attr.getArrayValueType() != null) {
            originalType = attr.getArrayValueType().name();
            type = getTypeFromDataType(originalType);
        }

        createField(builder, name, type, originalType, attr.isSearchMorphologically());
        return builder;
    }

    /**
     * Processes a simple attribute from EntityDef.
     *
     * @param builder the builder
     * @param attr    the attribute
     * @return the builder
     * @throws IOException
     */
    private XContentBuilder processSimpleAttribute(final XContentBuilder builder, SimpleAttributeDef attr) throws IOException {

        if (attr == null) {
            return builder;
        }

        String name = attr.getName();
        String type = null;
        String originalType = null;
        if (attr.getSimpleDataType() != null) {
            originalType = attr.getSimpleDataType().name();
            type = getTypeFromDataType(originalType);
        } else if (StringUtils.isNotBlank(attr.getEnumDataType())
                || StringUtils.isNotBlank(attr.getLinkDataType())) {
            type = "string";
        } else if (StringUtils.isNotBlank(attr.getLookupEntityType())) {
            originalType = attr.getLookupEntityCodeAttributeType().name();
            type = getTypeFromDataType(originalType);
        } else {
            type = "string";
        }

        createField(builder, name, type, originalType, attr.isSearchMorphologically());
        return builder;
    }

    private String getTypeFromDataType(String attrType) {
        String type = null;

        if (attrType != null) {
            switch (attrType) {
                case "BOOLEAN":
                    type = "boolean";
                    break;
                case "DATE":
                case "TIME":
                case "TIMESTAMP":
                    type = "date";
                    break;
                case "INTEGER":
                    type = "long";
                    break;
                case "NUMBER":
                case "MEASURED":
                    type = "double";
                    break;
                case "STRING":
                case "BLOB":
                case "CLOB":
                    type = "string";
                    break;
                default:
                    break;
            }
        }
        return type;
    }

    /**
     * Creates a mapping field.
     * @param builder content builder
     * @param name the name
     * @param type the type
     * @param originalType the original type
     * @param morphologySupport support morphology for string type or not
     * @throws IOException
     */
    private void createField(final XContentBuilder builder, String name, String type, String originalType, boolean morphologySupport)
            throws IOException {

        if (type == null) {
            final String message = "Cannot map attribute = {}. Type {} is not recognized.";
            LOGGER.warn(message, name, type, originalType);
            throw new SearchApplicationException(message, ExceptionId.EX_SEARCH_MAPPING_TYPE_UNKNOWN, name, originalType);
        }

        builder.startObject(name).field("type", type);

        if (StringUtils.equals(type, "date") && "TIME".equals(originalType)) {
            builder.field("format", "'T'HH:mm:ss||'T'HH:mm:ss.SSS||HH:mm:ss||HH:mm:ss.SSS||'T'HH:mm:ssZZ||'T'HH:mm:ss.SSSZZ||HH:mm:ssZZ||HH:mm:ss.SSSZZ");
        }

        if (StringUtils.equals(type, "string")) {
            builder.field("analyzer", SearchUtils.DEFAULT_STRING_ANALYZER_NAME);

            // Add not analyzed counter part for term matches
            builder
                    .startObject("fields")
                    .startObject(SearchUtils.NAN_FIELD)
                    .field("type", type)
                    .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                    .endObject();

            if (morphologySupport) {
                builder.startObject(SearchUtils.MORPH_FIELD)
                    .field("type", type)
                    .field("analyzer", SearchUtils.MORPH_STRING_ANALYZER_NAME)
                    .endObject();
            }

            builder.endObject(); // fields
        }

        builder.endObject();
    }

    /**
     * Create mapping for the DQ errors.
     *
     * @param builder builder.
     * @throws IOException in case of exception.
     */
    private void createErrorsMapping(XContentBuilder builder) throws IOException {
        builder
                .startObject(DqHeaderField.getParentField())
                .field("type", "nested")
                .startObject("properties")
                .startObject(DqHeaderField.ERROR_ID.getDirectField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                .endObject()
                .startObject(DqHeaderField.CREATE_DATE.getDirectField())
                .field("type", "date")
                .endObject()
                .startObject(DqHeaderField.UPDATE_DATE.getDirectField())
                .field("type", "date")
                .endObject()
                .startObject(DqHeaderField.STATUS.getDirectField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                .endObject()
                .startObject(DqHeaderField.RULE_NAME.getDirectField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                .endObject()
                .startObject(DqHeaderField.MESSAGE.getDirectField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                .endObject()
                .startObject(DqHeaderField.SEVERITY.getDirectField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                .endObject()
                .startObject(DqHeaderField.CATEGORY.getDirectField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                .endObject()
                .startObject(DqHeaderField.FIELD.getDirectField())
                .field("type", "string")
                .field("index", SearchUtils.NONE_STRING_ANALYZER_NAME)
                .endObject()
                .endObject()
                .endObject()
                .startObject(RecordHeaderField.FIELD_DQ_ERRORS_AS_BINARY.getField())
                .field("type", "string")
                .field("index", SearchUtils.NO_INDEXED_NAME)
                .field("doc_values", false)
                .endObject();
    }

    /* (non-Javadoc)
    * @see com.unidata.mdm.backend.service.search.impl.ElasticManagerComponent#dropMappings(java.util.Collection)
    */
    @Override
    public void dropMappings(@Nonnull Collection<String> entitiesNames,@Nonnull EntitySearchType searchType, @Nullable String storageId) {
        for (String entityName : entitiesNames) {
            SearchRequestContext context = SearchRequestContext.forEtalon(searchType, entityName)
                                                               .storageId(storageId)
                                                               .build();
            boolean result = dropMapping(context);
            if (!result) {
                LOGGER.warn("Failed to delete entity mappings for index/type [{}]", entityName);
            }
        }
    }

    /* (non-Javadoc)
    * @see com.unidata.mdm.backend.service.search.impl.ElasticManagerComponent#dropMapping(java.lang.String)
    */
    @Override
    public void dropMapping(@Nonnull String entityName, @Nonnull EntitySearchType searchType, @Nullable String storageId) {
        SearchRequestContext context = SearchRequestContext.forEtalon(searchType, entityName)
                                                           .storageId(storageId)
                                                           .build();
        boolean result = dropMapping(context);
        if (!result) {
            LOGGER.warn("Failed to delete entity mappings for index/type [{}]", entityName);
        }
    }

    private boolean dropMapping(final SearchRequestContext ctx) {

//        // 1. Compose the name of the type
//        final String indexName = constructIndexName(ctx);
//        try {
//            DeleteMappingRequestBuilder builder = client.admin()
//                                                        .indices()
//                    .delete
//                                                        .prepareDeleteMapping(indexName)
//
//                                                        .setType(ctx.getType().getName());
//
//            DeleteMappingResponse response = executeRequest(builder);
//            return response.isAcknowledged();
//        } catch (TypeMissingException | IndexMissingException tme) {
//            LOGGER.warn(
//                    "Drop mapping failed. Type [{}] not found in index [{}]. Skipping.",
//                    ctx.getType().getName(), indexName);
//        }
//
//        return false;
        //todo remove this
        return true;
    }

    private boolean putMapping(SearchRequestContext context, XContentBuilder builder) {
        final String indexName = constructIndexName(context);
        PutMappingRequestBuilder request = client.admin()
                                                 .indices()
                                                 .preparePutMapping(indexName)
                                                 .setSource(builder)
                                                 .setType(context.getType().getName());


        PutMappingResponse response = executeRequest(request);
        return response.isAcknowledged();
    }
}
