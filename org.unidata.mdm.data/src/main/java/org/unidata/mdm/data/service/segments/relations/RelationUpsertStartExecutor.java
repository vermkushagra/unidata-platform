package org.unidata.mdm.data.service.segments.relations;

import static org.unidata.mdm.search.type.form.FormField.strictValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.type.keys.ReferenceAliasKey;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.data.context.UpsertRelationRequestContext;
import org.unidata.mdm.data.exception.DataExceptionIds;
import org.unidata.mdm.data.exception.DataProcessingException;
import org.unidata.mdm.data.module.DataModule;
import org.unidata.mdm.data.po.data.RelationEtalonPO;
import org.unidata.mdm.data.po.data.RelationOriginPO;
import org.unidata.mdm.data.po.keys.RelationExternalKeyPO;
import org.unidata.mdm.data.service.impl.CommonRecordsComponent;
import org.unidata.mdm.data.service.impl.CommonRelationsComponent;
import org.unidata.mdm.data.type.apply.RelationUpsertChangeSet;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.data.UpsertAction;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;
import org.unidata.mdm.data.type.keys.RelationEtalonKey;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.data.type.keys.RelationOriginKey;
import org.unidata.mdm.data.type.timeline.RelationTimeline;
import org.unidata.mdm.data.util.RecordFactoryUtils;
import org.unidata.mdm.data.util.StorageUtils;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.meta.type.search.RecordHeaderField;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.dto.SearchResultDTO;
import org.unidata.mdm.search.dto.SearchResultHitFieldDTO;
import org.unidata.mdm.search.service.SearchService;
import org.unidata.mdm.search.type.FieldType;
import org.unidata.mdm.search.type.form.FormField;
import org.unidata.mdm.search.type.form.FormFieldsGroup;
import org.unidata.mdm.search.type.search.FacetName;
import org.unidata.mdm.system.exception.PlatformBusinessException;
import org.unidata.mdm.system.type.pipeline.Start;

/**
 * @author Mikhail Mikhailov on Nov 24, 2019
 */
@Component(RelationUpsertStartExecutor.SEGMENT_ID)
public class RelationUpsertStartExecutor extends Start<UpsertRelationRequestContext> {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationUpsertStartExecutor.class);
    /**
     * This segment ID.
     */
    public static final String SEGMENT_ID = DataModule.MODULE_ID + "[RELATION_UPSERT_START]";
    /**
     * Localized message code.
     */
    public static final String SEGMENT_DESCRIPTION = DataModule.MODULE_ID + ".relation.upsert.start.description";
    /**
     * Common records component.
     */
    @Autowired
    private CommonRecordsComponent commonRecordsComponent;
    /**
     * Common rel component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * MMS.
     */
    @Autowired
    private MetaModelService metaModelService;
    /**
     * The search service.
     */
    @Autowired
    private SearchService searchService;
    /**
     * Constructor.
     */
    public RelationUpsertStartExecutor() {
        super(SEGMENT_ID, SEGMENT_DESCRIPTION, UpsertRelationRequestContext.class);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void start(UpsertRelationRequestContext ctx) {
        setup(ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String subject(UpsertRelationRequestContext ctx) {
        setup(ctx);
        RelationKeys keys = ctx.relationKeys();
        return keys.getRelationName();
    }

    /**
     * {@inheritDoc}
     */
    private void setup(UpsertRelationRequestContext ctx) {

        if (ctx.setUp()) {
            return;
        }

        // Resolve pointer refs (Old API)
        setupPointers(ctx);

        // Possibly setup change set
        setupChangeSet(ctx);

        Timeline<OriginRelation> timeline = commonRelationsComponent.ensureAndGetRelationTimeline(ctx);
        RelationKeys relationKeys = timeline != null ? timeline.getKeys() : null;
        UpsertAction action = relationKeys == null ? UpsertAction.INSERT : UpsertAction.UPDATE;
        RelationUpsertChangeSet set = ctx.changeSet();

        // Both keys must be already resolved. Check for presence
        RecordKeys from = ctx.fromKeys();
        RecordKeys to = ctx.keys();

        String user = SecurityUtils.getCurrentUserName();
        Date ts = new Date(System.currentTimeMillis());

        ctx.upsertAction(action);
        ctx.timestamp(ts);

        // 1. Handle a possibly new object
        if (relationKeys == null) {

            // 1.1 Fail upsert. Etalon / LSN supplied for identity, but the rel couldn't be found
            if (ctx.isValidRelationKey()) {
                final String message = "Upsert relation received invalid input. Relation not found by etalon key [{}] | LSN [{}:{}].";
                LOGGER.warn(message, ctx.getRelationEtalonKey(), ctx.getShard(), ctx.getLsn());
                throw new DataProcessingException(message, DataExceptionIds.EX_DATA_RELATIONS_UPSERT_INVALID_INPUT);
            }

            // 1.1 Fail upsert. Incomplete identity.
            if (from == null) {
                final String message
                    = "Cannot identify relation's from side record by given origin id [{}], external id [{}, {}, {}], etalon id [{}]. Stopping.";
                LOGGER.warn(message,
                        ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
                throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_RELATIONS_UPSERT_FROM_NOT_FOUND,
                        ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
            }

            // 1.2 Fail upsert. Incomplete identity.
            if (to == null) {
                final String message
                    = "Cannot identify relation's to side record by given origin id [{}], external id [{}, {}, {}], etalon id [{}]. Stopping.";
                LOGGER.warn(message,
                        ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
                throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_RELATIONS_UPSERT_TO_NOT_FOUND,
                        ctx.getOriginKey(), ctx.getExternalId(), ctx.getSourceSystem(), ctx.getEntityName(), ctx.getEtalonKey());
            }

            // 1.3 Check sides status
            if (from.getEtalonKey().getStatus() != RecordStatus.ACTIVE
             || to.getEtalonKey().getStatus() != RecordStatus.ACTIVE) {
                final String message = "Left or right side of the relation is inactive. Stopping.";
                LOGGER.warn(message);
                throw new PlatformBusinessException(message, DataExceptionIds.EX_DATA_RELATIONS_UPSERT_SIDES_INACTIVE);
            }

            // 1.4 New relation etalon
            RelationEtalonPO etalon = RecordFactoryUtils.newRelationEtalonPO(ctx, RecordStatus.ACTIVE);
            set.setEtalonRelationInsertPO(etalon);

            relationKeys = RelationKeys.builder()
                    .relationName(ctx.relationName())
                    .relationType(ctx.relationType())
                    .fromEntityName(from.getEntityName())
                    .toEntityName(to.getEntityName())
                    .shard(etalon.getShard())
                    .node(StorageUtils.node(etalon.getShard()))
                    .etalonKey(RelationEtalonKey.builder()
                            .from(from.getEtalonKey())
                            .to(to.getEtalonKey())
                            .id(etalon.getId())
                            .status(etalon.getStatus())
                            .state(etalon.getApproval())
                            .build())
                    .createDate(ts)
                    .updateDate(ts)
                    .createdBy(user)
                    .updatedBy(user)
                    .build();
        // Have keys.
        // Otherwise fields set in connector.
        } else {
            ctx.relationName(relationKeys.getRelationName());
            ctx.relationType(relationKeys.getRelationType());
            ctx.accessRight(SecurityUtils.getRightsForResourceWithDefault(
                    relationKeys.getRelationType() == RelationType.CONTAINS
                        ? relationKeys.getToEntityName()
                        : relationKeys.getFromEntityName()));
        }

        // 2. Create new relation origin, if needed
        if (relationKeys.getOriginKey() == null) {

            RecordOriginKey fromSysKey = null;
            RecordOriginKey toSysKey = null;

            RelationOriginPO system = null;
            RelationOriginPO origin
                = RecordFactoryUtils.newRelationOriginPO(ctx, relationKeys,
                        from.getOriginKey(), to.getOriginKey(), RecordStatus.ACTIVE);

            String adminSourceSystem = metaModelService.getAdminSourceSystem().getName();
            if (action == UpsertAction.INSERT && !adminSourceSystem.equals(origin.getSourceSystem())) {

                fromSysKey = from.findBySourceSystemWithoutEnrichments(adminSourceSystem);
                toSysKey = to.findBySourceSystemWithoutEnrichments(adminSourceSystem);

                if (fromSysKey != null && toSysKey != null) {
                    system = RecordFactoryUtils.newRelationOriginPO(ctx,
                                relationKeys,
                                fromSysKey,
                                toSysKey,
                                RecordStatus.ACTIVE);
                } else {
                    LOGGER.warn("Cannot create system origin relation! Either 'from' or 'to' system key is missing.");
                }
            }

            RelationExternalKeyPO ext = new RelationExternalKeyPO();
            ext.setFromShard(from.getShard());
            ext.setToShard(to.getShard());
            ext.setFromRecordEtalonId(UUID.fromString(from.getEtalonKey().getId()));
            ext.setToRecordEtalonId(UUID.fromString(to.getEtalonKey().getId()));
            ext.setRelationName(ctx.relationName());
            ext.setRelationEtalonId(UUID.fromString(relationKeys.getEtalonKey().getId()));

            set.getOriginRelationInsertPOs().add(origin);
            set.getExternalKeyInsertPOs().add(ext);
            if (Objects.nonNull(system)) {
                set.getOriginRelationInsertPOs().add(system);
            }

            RelationOriginKey rok = RelationOriginKey.builder()
                    .from(from.getOriginKey())
                    .to(to.getOriginKey())
                    .id(origin.getId())
                    .initialOwner(origin.getInitialOwner())
                    .revision(0)
                    .status(origin.getStatus())
                    .sourceSystem(origin.getSourceSystem())
                    .build();

            RelationOriginKey sok = system == null
                    ? null
                    : RelationOriginKey.builder()
                        .from(fromSysKey)
                        .to(toSysKey)
                        .id(system.getId())
                        .initialOwner(system.getInitialOwner())
                        .revision(0)
                        .status(system.getStatus())
                        .sourceSystem(system.getSourceSystem())
                        .build();

            // New origin record, Batch will increment revisions using its own procedure.
            // For all the other puposes 1 should be used.
            relationKeys = RelationKeys.builder(relationKeys)
                    .originKey(rok)
                    .supplementaryKeys(sok == null ? Collections.singleton(rok) : Arrays.asList(rok, sok))
                    .build();
        }

        // 3. Check etalon status, re-enable, if inactive
        if (relationKeys.getEtalonKey().getStatus() == RecordStatus.INACTIVE) {

            RelationEtalonPO po = RecordFactoryUtils.newRelationEtalonPO(ctx, RecordStatus.ACTIVE);
            po.setId(relationKeys.getEtalonKey().getId());
            set.getEtalonRelationUpdatePOs().add(po);

            relationKeys = RelationKeys.builder(relationKeys)
                    .etalonKey(RelationEtalonKey.builder(relationKeys.getEtalonKey())
                            .status(RecordStatus.ACTIVE)
                            .build())
                    .build();
        }

        // 4. Check origin status, re-enable, if inactive
        if (relationKeys.getOriginKey().getStatus() == RecordStatus.INACTIVE) {

            RelationOriginPO origin = RecordFactoryUtils.newRelationOriginPO(ctx,
                    relationKeys,
                    relationKeys.getOriginKey().getFrom(),
                    relationKeys.getOriginKey().getTo(),
                    RecordStatus.ACTIVE);

            origin.setId(relationKeys.getOriginKey().getId());
            set.getOriginRelationUpdatePOs().add(origin);

            relationKeys = RelationKeys.builder(relationKeys)
                    .originKey(RelationOriginKey.builder(relationKeys.getOriginKey())
                            .status(RecordStatus.ACTIVE)
                            .build())
                    .build();
        }

        ctx.relationKeys(relationKeys);

        // 5. All calculations done on the record services side for containments.
        if (action == UpsertAction.INSERT || ctx.relationType() == RelationType.CONTAINS) {
            ctx.currentTimeline(new RelationTimeline(relationKeys));
        } else if (action == UpsertAction.UPDATE) {
            ctx.currentTimeline(timeline);
        }

        ctx.setUp(true);
    }

    private void setupChangeSet(UpsertRelationRequestContext ctx) {

        // May be already set by batch
        if (Objects.isNull(ctx.changeSet())) {
            RelationUpsertChangeSet set = new RelationUpsertChangeSet();
            set.setRelationType(ctx.relationType());
            ctx.changeSet(set);
        }
    }

    private void setupPointers(UpsertRelationRequestContext uCtx) {

        ReferenceAliasKey referenceResolver = uCtx.getReferenceAliasKey();
        if (referenceResolver == null || referenceResolver.getValue() == null || referenceResolver.getEntityAttributeName() == null) {
            //skip if we doesn't have all necessary information about alias key
            return;
        }

        if (uCtx.relationType() != RelationType.REFERENCES && uCtx.relationType() != RelationType.MANY_TO_MANY) {
            //skip if relation is not exist or if it is contains
            return;
        }

        String aliasAttrName = referenceResolver.getEntityAttributeName();
        RelationDef def = metaModelService.getRelationById(uCtx.relationName());

        AttributeModelElement attrInfo = metaModelService.getEntityAttributeInfoByPath(def.getToEntity(), aliasAttrName);

        // skip if alias key use complex attribute as a key
        // skip if alias attribute is not unique
        if (attrInfo.isComplex() || !attrInfo.isUnique()) {
            return;
        }

        FormField formField = strictValue(FieldType.fromValue(attrInfo.getValueType().name()), aliasAttrName, referenceResolver.getValue());
        Date asOf = uCtx.getValidFrom() == null ? uCtx.getValidTo() : uCtx.getValidFrom();

        SearchRequestContext searchContext = SearchRequestContext.builder(EntityIndexType.RECORD, def.getToEntity(), SecurityUtils.getCurrentUserStorageId())
                .asOf(asOf)
                .form(FormFieldsGroup.createAndGroup(formField))
                .returnFields(Collections.singletonList(RecordHeaderField.FIELD_ETALON_ID.getName()))
                .facets(Collections.singletonList(FacetName.FACET_NAME_ACTIVE_ONLY))
                .count(10)
                .page(0)
                .build();

        SearchResultDTO searchResultDTO = searchService.search(searchContext);

        String etalonId = searchResultDTO.getHits().stream()
                .map(hit -> hit.getFieldValue(RecordHeaderField.FIELD_ETALON_ID.getName()))
                .filter(Objects::nonNull)
                .filter(SearchResultHitFieldDTO::isNonNullField)
                .filter(SearchResultHitFieldDTO::isSingleValue)
                .map(field-> field.getFirstValue().toString())
                .findAny()
                .orElse(null);

        if (etalonId == null) {
            // Considered supplementary. Just warn and continue.
            LOGGER.warn("Relation reference didn't resolved by reference alias key {}.", referenceResolver);
            return;
        }

        RecordKeys keys = commonRecordsComponent.identify(RecordEtalonKey.builder().id(etalonId).build());
        if (keys == null) {
            // Considered supplementary. Just warn and continue.
            LOGGER.warn("Relation reference didn't resolved by reference alias key {}.", referenceResolver);
            return;
        }

        uCtx.keys(keys);
    }
}
