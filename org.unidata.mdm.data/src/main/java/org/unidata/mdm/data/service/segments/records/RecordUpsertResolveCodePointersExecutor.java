package org.unidata.mdm.data.service.segments.records;

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
import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.AttributeModelElement.AttributeValueType;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.core.util.AttributeUtils;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.CodeAttributeAlias;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.context.UpsertRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.dto.SearchResultDTO;
import org.unidata.mdm.search.dto.SearchResultHitFieldDTO;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.form.FormField;
import org.unidata.mdm.search.type.form.FormFieldsGroup;
import org.unidata.mdm.search.type.search.FacetName;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

/**
 * Executor responsible for modifying records have a links to lookup entities but used for this alias code attributes.
 */
@Component(RecordUpsertResolveCodePointersExecutor.SEGMENT_ID)
public class RecordUpsertResolveCodePointersExecutor
    extends Point<UpsertRequestContext>
    implements RecordIdentityContextSupport {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RecordUpsertResolveCodePointersExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_UPSERT_RESOLVE_CODE_POINTERS]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.upsert.resolve.code.pointers.description";
    /**
     * Search service
     */
    @Autowired
    private SearchService searchService;
    /**
     * Meta model service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Constructor.
     */
    public RecordUpsertResolveCodePointersExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    @Override
    public void point(UpsertRequestContext ctx) {

        MeasurementPoint.start();
        try {

            Collection<CodeAttributeAlias> aliasCodeAttributePointers = ctx.getCodeAttributeAliases();
            if (CollectionUtils.isEmpty(aliasCodeAttributePointers) || ctx.getRecord() == null) {
                return;
            }

            String entityName = selectEntityName(ctx);

            Collection<ModifyInstruction> modifyInstructions = createModifyInstructions(entityName, aliasCodeAttributePointers);
            Date asOf = ctx.getValidFrom() == null ? ctx.getValidTo() : ctx.getValidFrom();

            modifyAliasCodeAttributeInOrigin(ctx.getRecord(), modifyInstructions, asOf);

        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * Method change value of alias code attribute to real code attribute.
     *
     * @param originRecord mo
     * @return true if all available attributes was modified otherwise false.
     */
    private void modifyAliasCodeAttributeInOrigin(@Nonnull DataRecord originRecord,
            @Nonnull Collection<ModifyInstruction> modifyInstructions, @Nullable Date asOf) {

        if (modifyInstructions.isEmpty()) {
            return;
        }

        //todo merge optimization which reduce number of requests to ES
        MeasurementPoint.start();
        try {
            for (ModifyInstruction modifyInstruction : modifyInstructions) {

                Collection<SimpleAttribute<?>> modifiedAttrs
                    = originRecord.getSimpleAttributeRecursive(modifyInstruction.getRecordAttrName());

                List<Object> aliasCodeAttrs = modifiedAttrs.stream()
                        .filter(modifiedAttr -> modifiedAttr.getValue() != null)
                        .map(attr -> (Object) attr.getValue())
                        .collect(Collectors.toList());

                if (aliasCodeAttrs.isEmpty()) {
                    continue;
                }

                //for include all time intervals.
                int multiplier = 3;
                SearchRequestContext searchContext = SearchRequestContext.builder(EntityIndexType.RECORD, modifyInstruction.getLookupEntityName(), SecurityUtils.getCurrentUserStorageId())
                        .asOf(asOf)
                        .form(FormFieldsGroup
                                .createAndGroup()
                                .addFormField(FormField.strictValues(modifyInstruction.getDataType().toSearchType(),
                                    modifyInstruction.getAliasCodeAttrName(),
                                    aliasCodeAttrs)))
                        .returnFields(Arrays.asList(modifyInstruction.getCodeAttrName(), modifyInstruction.getAliasCodeAttrName()))
                        .facets(Collections.singletonList(FacetName.FACET_NAME_ACTIVE_ONLY))
                        .count(aliasCodeAttrs.size() * multiplier)
                        .page(0)
                        .build();

                SearchResultDTO searchResultDTO = searchService.search(searchContext);

                Map<String, SearchResultHitFieldDTO> result = searchResultDTO.getHits().stream()
                                                                             .map(hit -> Pair.of(hit.getFieldValue(modifyInstruction.getAliasCodeAttrName()), hit.getFieldValue(modifyInstruction.getCodeAttrName())))
                                                                             .filter(pair -> pair.getLeft() != null || pair.getLeft().isNonNullField())
                                                                             .collect(Collectors.toMap(pair -> pair.getLeft().getFirstValue().toString(), Pair::getRight));

                modifiedAttrs.stream()
                        .filter(modifiedAttr -> modifiedAttr.getValue() != null)
                        .forEach(modifiedAttr -> this.setRealCodeAttr(modifiedAttr, result));
            }
        } finally {
            MeasurementPoint.stop();
        }
    }

    private void setRealCodeAttr(@Nonnull SimpleAttribute<?> simpleAttribute,@Nonnull Map<String, SearchResultHitFieldDTO> result) {
        String aliasCodeAttr = simpleAttribute.castValue().toString();
        SearchResultHitFieldDTO realCodeAttr = result.get(aliasCodeAttr);
        if (realCodeAttr != null && realCodeAttr.isNonNullField()) {
            AttributeUtils.processSimpleAttributeValue(simpleAttribute, realCodeAttr.getFirstValue());
        } else {
            LOGGER.warn("SKIP: Failed to upsert record.");
            throw new DataProcessingException("Alias Code Attribute not found", DataExceptionIds.EX_DATA_UPSERT_INVALID_ALIAS_CODE_ATTRIBUTE);
        }
    }

    /**
     * Transform alias code attribute pointers to modify instructions , which collect all necessary information about modification.
     *
     * @param entityName                 - from this entity or lookup entity refers to lookup entity
     * @param aliasCodeAttributePointers - collection of pointer which show
     * @return collection of modify instructions.
     */
    private Collection<ModifyInstruction> createModifyInstructions(
            @Nonnull String entityName,
            @Nonnull Collection<CodeAttributeAlias> aliasCodeAttributePointers) {

        if (aliasCodeAttributePointers.isEmpty()) {
            return Collections.emptyList();
        }

        EntityModelElement thisElement = metaModelService.getEntityModelElementById(entityName);
        if (Objects.isNull(thisElement)) {
            return Collections.emptyList();
        }

        Collection<ModifyInstruction> modifyInstructions = new ArrayList<>(aliasCodeAttributePointers.size());
        for (CodeAttributeAlias pointer : aliasCodeAttributePointers) {

            String attributeName = pointer.getRecordAttributeName();

            AttributeModelElement attributeElement = thisElement.getAttributes().get(attributeName);
            if (Objects.isNull(attributeElement) || !attributeElement.isLookupLink()) {
                continue;
            }

            EntityModelElement lookupElement = metaModelService.getEntityModelElementById(attributeElement.getLookupLinkName());
            if (Objects.isNull(lookupElement) || !lookupElement.isLookup()) {
                continue;
            }

            AttributeModelElement hit = lookupElement.getCodeAttributed().getCodeAliases().stream()
                .filter(attr -> attr.getName().equals(pointer.getAliasAttributeName()))
                .findFirst()
                .orElse(null);

            if (Objects.isNull(hit)) {
                continue;
            }

            modifyInstructions.add(
                new ModifyInstruction(
                        attributeName,
                        lookupElement.getName(),
                        pointer.getAliasAttributeName(),
                        lookupElement.getCodeAttributed().getCodeAttribute().getName(),
                        hit.getValueType()));
        }

        return modifyInstructions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supports(Start<?> start) {
        return UpsertRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }

    private class ModifyInstruction {
        @Nonnull
        private final String recordAttrName;
        @Nonnull
        private final String lookupEntityName;
        @Nonnull
        private final String aliasCodeAttrName;
        @Nonnull
        private final String codeAttrName;
        @Nonnull
        private final AttributeValueType dataType;

        public ModifyInstruction(@Nonnull String recordAttrName,
                                 @Nonnull String lookupEntityName,
                                 @Nonnull String aliasCodeAttrName,
                                 @Nonnull String codeAttrName,
                                 @Nonnull AttributeValueType dataType) {
            this.recordAttrName = recordAttrName;
            this.lookupEntityName = lookupEntityName;
            this.aliasCodeAttrName = aliasCodeAttrName;
            this.codeAttrName = codeAttrName;
            this.dataType = dataType;
        }

        /**
         * @return name of attribute which will be modified in record
         */
        @Nonnull
        public String getRecordAttrName() {
            return recordAttrName;
        }

        /**
         * @return name of alias code attribute in lookup entity
         */
        @Nonnull
        public String getAliasCodeAttrName() {
            return aliasCodeAttrName;
        }

        /**
         * @return name of code attribute in lookup entity
         */
        @Nonnull
        public String getCodeAttrName() {
            return codeAttrName;
        }

        /**
         * @return name of lookup entity
         */
        @Nonnull
        public String getLookupEntityName() {
            return lookupEntityName;
        }

        /**
         * @return data type
         */
        @Nonnull
        public AttributeValueType getDataType() {
            return dataType;
        }
    }

}
