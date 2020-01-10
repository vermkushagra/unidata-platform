package org.unidata.mdm.data.service.segments.records;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.unidata.mdm.meta.type.search.RecordHeaderField.FIELD_DELETED;
import static org.unidata.mdm.meta.type.search.RecordHeaderField.FIELD_ETALON_ID;
import static org.unidata.mdm.meta.type.search.RecordHeaderField.FIELD_PUBLISHED;
import static org.unidata.mdm.search.type.form.FormField.strictValue;
import static org.unidata.mdm.search.type.form.FormField.FilteringType.POSITIVE;
import static org.unidata.mdm.search.type.form.FormFieldsGroup.createAndGroup;
import static org.unidata.mdm.search.type.form.FormFieldsGroup.createOrGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.DeleteRequestContext;
import org.unidata.mdm.data.context.GetRelationsTimelineRequestContext;
import org.unidata.mdm.data.context.RecordIdentityContextSupport;
import org.unidata.mdm.data.exception.DataConsistencyException;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.service.impl.CommonRelationsComponent;
import org.unidata.mdm.data.service.segments.TimelineSelectionSupport;
import org.unidata.mdm.data.type.data.EtalonRecord;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.meta.AbstractEntityDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.LookupEntityDef;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.meta.type.search.RecordHeaderField;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.context.TermsAggregationRequestContext;
import org.unidata.mdm.search.dto.SearchResultDTO;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.form.FormField;
import org.unidata.mdm.search.type.form.FormFieldsGroup;
import org.unidata.mdm.search.type.search.SearchRequestOperator;
import org.unidata.mdm.search.util.SearchUtils;
import org.unidata.mdm.system.exception.ValidationResult;
import org.unidata.mdm.system.type.pipeline.Point;
import org.unidata.mdm.system.type.pipeline.Start;

@Component(RecordDeleteDataConsistencyExecutor.SEGMENT_ID)
public class RecordDeleteDataConsistencyExecutor extends Point<DeleteRequestContext>
        implements RecordIdentityContextSupport, TimelineSelectionSupport<DeleteRequestContext, EtalonRecord> {
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RECORD_DELETE_DATA_CONSISTENCY]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".record.delete.consistency.check.description";
    /**
     * Aggregation name
     */
    private static final String AGGR_NAME = "AGGR_NAME";
    /**
     * Search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * MetaModel service.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * Relations common component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Constructor.
     */
    public RecordDeleteDataConsistencyExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION);
    }

    @Override
    public void point(DeleteRequestContext ctx) {

        RecordKeys keys = ctx.keys();

        // workflow action we skip
        if (keys.isPending() || ctx.isWorkflowAction()) {
            return;
        }

        //we process only etalon removing and etalon period removing!
        if (!(ctx.isInactivateEtalon() || ctx.isInactivatePeriod() || ctx.isWipe())) {
            return;
        }

        String entityName = keys.getEntityName();
        if (!ctx.isInactivatePeriod() && metaModelService.isEntity(entityName)) {

            final List<RelationDef> relationsToEntity = metaModelService.getRelationsByToEntityName(entityName);
            List<String> relationNames = (relationsToEntity.stream())
                    .map(RelationDef::getName)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(relationNames)) {
                // @Modules
                final String relationIdDeleteStarted = null; // ctx.getFromStorage(StorageId.DELETE_BY_RELATION);
                Map<String, List<Timeline<OriginRelation>>> result
                    = commonRelationsComponent.loadTimelines(GetRelationsTimelineRequestContext.builder()
                        .etalonKey(keys.getEtalonKey().getId())
                        .relationNames(relationNames)
                        .fetchData(false)
                        .fetchByToSide(true)
                        .build());

                Map<String, Long> collected = result.values().stream()
                        .flatMap(Collection::stream)
                        .map(Timeline::<RelationKeys>getKeys)
                        .filter(k -> k.getEtalonKey().getStatus() == RecordStatus.ACTIVE
                             && k.getEtalonKey().getFrom().getStatus() == RecordStatus.ACTIVE
                             && !k.getEtalonKey().getId().equals(relationIdDeleteStarted))
                        .collect(Collectors.groupingBy(RelationKeys::getRelationName, Collectors.counting()));

                if (!collected.isEmpty()) {

                    ValidationResult v
                        = new ValidationResult("The etalon record has incoming relations and cannot be deleted. Connections from ({}).",
                            (String) null,
                            collected.entrySet().stream()
                                .map(entry -> StringUtils.join(entry.getKey(), " : ", entry.getValue().toString()))
                                .collect(Collectors.joining(", ")));

                    throw new DataConsistencyException(
                            "The etalon record has incoming relations and cannot be deleted.",
                            DataExceptionIds.EX_DATA_ETALON_HAS_INCOMING_RELATIONS,
                            Collections.singletonList(v)
                    );
                }
            }
        }

        if (metaModelService.isLookupEntity(entityName)) {

            LookupEntityDef lookupEntityDef = metaModelService.getLookupEntityById(entityName);
            String codeAttrName = lookupEntityDef.getCodeAttribute().getName();
            DataRecord record = getCurrentEtalonRecord(ctx);

            // try get etalon by validity period
            if(record == null){
                record = getFirstNonNullCalculationResult(ctx);
            }

            // can't get etalon by keys
            if (record == null) {
                return;
            }

            FormFieldsGroup timeLineFormFields = timeLineFormField(ctx);

            //always one code attr!
            CodeAttribute<?> codeAttribute = record.getCodeAttribute(codeAttrName);
            Map<String, Long> linkedRecords = new HashMap<>();

            // 1. Check Entity -> Lookup
            Map<EntityDef, Set<AttributeModelElement>> entities = metaModelService.getEntitiesReferencingThisLookup(entityName);
            entities.entrySet()
                    .stream()
                    .map(e -> findLinks(e, codeAttribute, timeLineFormFields))
                    .filter(Objects::nonNull)
                    .forEach(pair -> linkedRecords.put(pair.getKey(), pair.getValue()));

            // 2. Check Lookup -> Lookup
            Map<LookupEntityDef, Set<AttributeModelElement>> lookupEntities = metaModelService.getLookupsReferencingThisLookup(entityName);
            lookupEntities.entrySet()
                    .stream()
                    .map(e -> findLinks(e, codeAttribute, timeLineFormFields))
                    .filter(Objects::nonNull)
                    .forEach(pair -> linkedRecords.put(pair.getKey(), pair.getValue()));

            if (!linkedRecords.isEmpty()) {

                ValidationResult v
                    = new ValidationResult("The etalon record has incoming relations and cannot be deleted. Connections from ({}).",
                        (String) null,
                        linkedRecords.entrySet().stream()
                            .map(entry -> StringUtils.join(entry.getKey(), " : ", entry.getValue().toString()))
                            .collect(Collectors.joining(", ")));

                throw new DataConsistencyException(
                        "The etalon record has incoming links and cannot be deleted.",
                        DataExceptionIds.EX_DATA_ETALON_HAS_INCOMING_LINKS,
                        Collections.singletonList(v)
                );
            }

        }
    }

    @Nullable
    private Pair<String, Long> findLinks(Map.Entry<? extends AbstractEntityDef, Set<AttributeModelElement>> e,
                                         CodeAttribute<?> codeAttribute, FormFieldsGroup timeLineFormFields) {

        String linkedEntityName = e.getKey().getName();
        Set<AttributeModelElement> linkedAttrs = e.getValue();

        if (linkedAttrs.isEmpty()) {
            return null;
        }

        List<Object> values = new ArrayList<>();
        values.add(codeAttribute.getValue());
        if (codeAttribute.hasSupplementary()) {
            values.addAll(codeAttribute.getSupplementary());
        }

        FormFieldsGroup baseForm = createAndGroup(
                strictValue(FieldType.BOOLEAN, FIELD_DELETED.getName(), FALSE),
                strictValue(FieldType.BOOLEAN, FIELD_PUBLISHED.getName(), TRUE),
                strictValue(FieldType.BOOLEAN, RecordHeaderField.FIELD_INACTIVE.getName(), FALSE)
        );

        FormFieldsGroup linkedValuesForm = createOrGroup();
        linkedAttrs.forEach(linkedAttr -> linkedValuesForm.addFormField(FormField.strictValues(
                linkedAttr.getValueType().toSearchType(),
                linkedAttr.getPath(),
                values)));

        baseForm.addChildGroup(linkedValuesForm);

        TermsAggregationRequestContext taCtx = TermsAggregationRequestContext.builder()
                .name(AGGR_NAME)
                .path(FIELD_ETALON_ID.getName())
                .size(1)
                .build();

        SearchRequestContext ctx = SearchRequestContext.builder(EntityIndexType.RECORD, linkedEntityName, SecurityUtils.getCurrentUserStorageId())
                .operator(SearchRequestOperator.OP_AND)
                .onlyQuery(true)
                .totalCount(true)
                .skipEtalonId(true)
                .count(0)
                .page(0)
                .form(baseForm, timeLineFormFields)
                .aggregations(Collections.singletonList(taCtx))
                .build();

        SearchResultDTO result = searchService.search(ctx);
        final Long count = result.getTotalCount() - (
                CollectionUtils.isNotEmpty(result.getAggregates()) ?
                        result.getAggregates().stream()
                                .flatMap(a -> a.getCountMap().values().stream().map(v -> v - 1))
                                .mapToLong(v -> v)
                                .sum() :
                        0
        );

        if (count > 0) {
            return Pair.of(e.getKey().getDisplayName(), count);
        } else {
            return null;
        }
    }


    private FormFieldsGroup timeLineFormField(DeleteRequestContext ctx) {

        if (!ctx.isInactivatePeriod()) {
            return null;
        }

        return FormFieldsGroup.createAndGroup(
                FormField.range(FieldType.TIMESTAMP, RecordHeaderField.FIELD_FROM.getName(), POSITIVE, null, SearchUtils.coalesceTo(ctx.getValidTo())),
                FormField.range(FieldType.TIMESTAMP, RecordHeaderField.FIELD_TO.getName(), POSITIVE, SearchUtils.coalesceFrom(ctx.getValidFrom()), null));
    }

    @Override
    public boolean supports(Start<?> start) {
        return DeleteRequestContext.class.isAssignableFrom(start.getInputTypeClass());
    }
}
