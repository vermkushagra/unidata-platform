/**
 *
 */
package com.unidata.mdm.backend.exchange.chain.csv;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext.UpsertRelationRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext.UpsertRelationsRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.ReferenceAliasKey;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.types.OriginRelationInfoSection;
import com.unidata.mdm.backend.common.types.impl.OriginRelationImpl;
import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;
import com.unidata.mdm.backend.exchange.chain.ChainMember;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeField;
import com.unidata.mdm.backend.exchange.def.ExchangeFieldTransformer;
import com.unidata.mdm.backend.exchange.def.RelatesToRelation;
import com.unidata.mdm.backend.exchange.def.csv.CsvExchangeEntity;
import com.unidata.mdm.backend.exchange.def.csv.CsvExchangeField;
import com.unidata.mdm.backend.exchange.def.csv.CsvNaturalKey;
import com.unidata.mdm.backend.exchange.def.csv.CsvRelatesToRelation;
import com.unidata.mdm.backend.service.job.importJob.record.ImportRecordWriter;
import com.unidata.mdm.backend.service.job.importJob.relation.ImportRelationWriter;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.RelationWrapper;
import com.unidata.mdm.backend.util.CollectionUtils;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 *         Transform CSV input if needed.
 */
public class CsvTransformImportChainMember
        extends CsvBaseTransformChainMember
        implements ChainMember {

    /**
     * Constructor.
     */
    public CsvTransformImportChainMember() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(ExchangeContext ctx, Action currentAction) {

        ExchangeDefinition def = ctx.getExchangeDefinition();
        MetaModelServiceExt svc = getMetaModelService(ctx);

        Map<String, List<UpsertRequestContext>> records
                = new HashMap<String, List<UpsertRequestContext>>();
        Map<String, List<UpsertRelationsRequestContext>> relations
                = new HashMap<>();

        // 1. Lookup entities
        if (def.getLookupEntities() != null && def.getLookupEntities().size() > 0) {

            // 1.1 Sort to enable right order.
            for (ExchangeEntity e : def.getLookupEntities()) {

                // 1.1 Skip other import types
                if (!CsvExchangeEntity.class.isAssignableFrom(e.getClass())) {
                    continue;
                }

                if (e.isProcessRelations()) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.info("Skip entity import, only relations requested");
                    continue;
                }


                if (!records.containsKey(e.getName())) {
                    records.put(e.getName(), new ArrayList<UpsertRequestContext>());
                }

                // 1.2 Read input
                List<List<String>> input = loadResultSet(ctx, (CsvExchangeEntity) e);
                if (input == null || input.isEmpty()) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.warn("Entity definition for entity '{}' deliverd an empty result set. Skipping.", e.getName());
                    continue;
                }

                // 1.3 Get attributes cache.
                Map<String, AttributeInfoHolder> attrs
                        = svc.getValueById(e.getName(), LookupEntityWrapper.class).getAttributes();

                // 1.4 Add imported
                records.get(e.getName()).addAll(importLookupEntities(input, e, attrs, ctx.getImportSystem()));
            }
        }

        // 2. Top level entities
        if (def.getEntities() != null && def.getEntities().size() > 0) {

            // 2.2 Go the export definitions through and import records
            for (ExchangeEntity e : def.getEntities()) {

                // 1.1 Skip other import types
                if (!CsvExchangeEntity.class.isAssignableFrom(e.getClass())) {
                    continue;
                }

                if (e.isProcessRelations()) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.info("Skip entity import, only relations requested");
                    continue;
                }


                // 1.2 Read input
                List<List<String>> input = loadResultSet(ctx, (CsvExchangeEntity) e);
                if (input == null || input.isEmpty()) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.warn("Entity definition for entity '{}' deliverd an empty result set. Skipping.", e.getName());
                    continue;
                }

                if (!records.containsKey(e.getName())) {
                    records.put(e.getName(), new ArrayList<UpsertRequestContext>());
                }
                if (!relations.containsKey(e.getName())) {
                    relations.put(e.getName(), new ArrayList<UpsertRelationsRequestContext>());
                }

                // 1.3 Create top level attributes cache.
                // Populated later with deeper level attributes
                Map<String, AttributeInfoHolder> attrs
                        = svc.getValueById(e.getName(), EntityWrapper.class)
                        .getAttributes();

                // 1.4 Filter input, if required
                List<List<String>> lines;
                if (e.isUnique()) {
                    lines = CollectionUtils.filter(new CsvUniqueSetFilter(e, false), input);
                } else {
                    lines = input;
                }

                List<UpsertRequestContext> currentSet = new ArrayList<>();
                for (List<String> line : lines) {
                    UpsertRequestContext uCtx = importEntity(line, e, attrs);
                    if (uCtx != null) {
                        currentSet.add(uCtx);
                    }
                }
                records.get(e.getName()).addAll(currentSet);

                relations.get(e.getName()).addAll(importContainmentRelations(ctx, e));
                relations.get(e.getName()).addAll(importRelatesToRelations(ctx, e));
            }
        }
        final ImportRecordWriter importRecordWriter = new ImportRecordWriter();
        importRecordWriter.setRecordsService(ChainMember.createDataRecordsService(ctx));
        importRecordWriter.setMessageSource(ChainMember.createMessageSource(ctx));

        final ImportRelationWriter importRelationWriter = new ImportRelationWriter();
        importRelationWriter.setDataRecordsService(ChainMember.createDataRecordsService(ctx));
        importRelationWriter.setMessageSource(ChainMember.createMessageSource(ctx));

        for (String entityName : records.keySet()) {
            List<UpsertRequestContext> upserts = records.get(entityName);
            try {
                importRecordWriter.write(upserts);
            } catch (Exception e) {
                TRANSFORM_CHAIN_MEMBER_LOGGER.error("Error during records upsert{}",e);
            }
        }

        for (String relName : relations.keySet()) {
            List<UpsertRelationsRequestContext> rels = relations.get(relName);
            try {
                importRelationWriter.write(rels);
            } catch (Exception e) {
                TRANSFORM_CHAIN_MEMBER_LOGGER.error("Error during relations upsert{}",e);
            }
        }

        return true;
    }

    /**
     * Lookup entities.
     *
     * @param fields         the fields
     * @param exchangeEntity import description
     * @param attrs          attributes cache
     * @param sourceSystem   the source system
     * @return list of imported entity records
     */
    private List<UpsertRequestContext> importLookupEntities(List<List<String>> fields, ExchangeEntity exchangeEntity,
                                                            Map<String, AttributeInfoHolder> attrs, String sourceSystem) {
        List<UpsertRequestContext> records = new ArrayList<>();

        // 1. Reduce set.
        List<List<String>> reduced
                = CollectionUtils.filter(new CsvUniqueSetFilter(exchangeEntity, true), fields);

        // 2. Proceed like with any other entities.
        for (List<String> line : reduced) {
            UpsertRequestContext uCtx = importEntity(line, exchangeEntity, attrs);
            if (uCtx != null) {
                records.add(uCtx);
            }
        }

        return records;
    }

    /**
     * Imports realtes-to relations.
     *
     * @param ctx the context
     * @param e   the entity
     * @return result
     */
    private List<UpsertRelationsRequestContext> importRelatesToRelations(ExchangeContext ctx, ExchangeEntity e) {

        MetaModelServiceExt svc = getMetaModelService(ctx);
        List<UpsertRelationsRequestContext> result = new ArrayList<>();
        List<RelatesToRelation> references = e.getRelates();
        Map<String, List<UpsertRelationRequestContext>> target = new HashMap<>();

        for (int i = 0; references != null && i < references.size(); i++) {

            CsvRelatesToRelation rel = (CsvRelatesToRelation) references.get(i);
            RelationDef relDef = svc.getRelationById(rel.getRelation());

            // 1. basic relation check
            if (relDef == null || relDef.getName() == null || relDef.getRelType() == RelType.CONTAINS) {
                throw new IllegalArgumentException("Unknown or invalid relation given [" + rel.getRelation() + "].");
            }

            // 2 Get attributes cache.
            Map<String, AttributeInfoHolder> attrs
                    = svc.getValueById(relDef.getName(), RelationWrapper.class).getAttributes();
            List<UpsertRelationRequestContext> relations = new ArrayList<>();
            List<List<String>> input = loadResultSet(ctx, rel);

            CsvNaturalKey toNaturalKey = (CsvNaturalKey) rel.getToNaturalKey();
            CsvNaturalKey fromNaturalKey = (CsvNaturalKey) rel.getFromNaturalKey();

            int count = 0;
            for (List<String> fields : input) {

                TRANSFORM_CHAIN_MEMBER_LOGGER.info("Start processing rel TO relation of type {}, #{}.", relDef.getName(), ++count);
                long start = System.currentTimeMillis();

                // 3. Key import and check
                final OriginKey toKey = importOriginKey(fields, toNaturalKey, relDef.getToEntity(), rel.getToSourceSystem());
                if (toKey == null) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.warn("TO key is unusable. Relation cannot be imported [{}].", fields);
                    return null;
                }

                final OriginKey fromKey = importOriginKey(fields, fromNaturalKey, e.getName(), e.getSourceSystem());
                if (fromKey == null) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.warn("FROM key is unusable. Relation cannot be imported [{}].", fields);
                    return null;
                }

                OriginRelationImpl record = new OriginRelationImpl();
                record.withInfoSection(new OriginRelationInfoSection());
                record.getInfoSection().withRelationName(relDef.getName());

                for (ExchangeField f : rel.getFields()) {
                    // 3.1. Get value
                    Integer index = ((CsvExchangeField) f).getIndex();
                    String value = index != null
                            ? fields.size() > index ? fields.get(index) : null
                            : f.getValue() != null ? f.getValue().toString() : null;
                    if (value == null || value.isEmpty()) {
                        continue;
                    }

                    // 3.2. Possibly transform
                    List<ExchangeFieldTransformer> tf = f.getTransformations();
                    if (tf != null) {
                        value = applyTransformation(value, tf);
                        if (value == null) {
                            continue;
                        }
                    }

                    // 3.3. Resolve and set
                    setAttribute(record, f, attrs, f.getName(), value, 0);
                }


                // 4. Import possibly defined ranges
                final Date from = importRangeFrom(fields, rel.getVersionRange());
                final Date to = importRangeTo(fields, rel.getVersionRange());

                ReferenceAliasKey referenceAliasKey = null;
                if (StringUtils.isBlank(rel.getToEntityAttributeName())) {
                    record.getInfoSection().withToOriginKey(toKey);
                } else {
                    referenceAliasKey = ReferenceAliasKey.builder().value(toKey.getId()).entityAttributeName(rel.getToEntityAttributeName()).build();
                }

                UpsertRelationRequestContext urCtx
                        = new UpsertRelationRequestContextBuilder()
                        .relation(record)
                        .validFrom(from)
                        .validTo(to)
                        .sourceSystem(rel.getToSourceSystem())
                        .referenceAliasKey(referenceAliasKey)
                        .build();

                relations.add(urCtx);
                TRANSFORM_CHAIN_MEMBER_LOGGER.info("Finished processing rel TO relation of type {}, #{} in {} millis.",
                        relDef.getName(), count, System.currentTimeMillis() - start);
            }

            // Put collected
            target.put(relDef.getName(), relations);
        }

        if (!target.isEmpty()) {
            UpsertRelationsRequestContext rCtx
                    = new UpsertRelationsRequestContextBuilder()
                    //.relations(target)
                    .build();

            result.add(rCtx);
        }

        return result;
    }

    /**
     * Imports containment relations.
     *
     * @param ctx the context
     * @param e   the entity
     * @return result
     */
    private List<UpsertRelationsRequestContext> importContainmentRelations(ExchangeContext ctx, ExchangeEntity e) {

        MetaModelServiceExt svc = getMetaModelService(ctx);
        List<UpsertRelationsRequestContext> result = new ArrayList<>();
        List<ContainmentRelation> containments = e.getContains();
        Map<String, List<UpsertRelationRequestContext>> target = new HashMap<>();

        for (int i = 0; containments != null && i < containments.size(); i++) {
            ContainmentRelation rel = containments.get(i);
            RelationDef relDef = svc.getRelationById(rel.getRelation());

            if (relDef == null || relDef.getName() == null || relDef.getRelType() != RelType.CONTAINS) {
                throw new IllegalArgumentException("Unknown or invalid relation given [" + rel.getRelation() + "].");
            }

            // 2.3 Get attributes cache.
            Map<String, AttributeInfoHolder> attrs
                    = svc.getValueById(rel.getEntity().getName(), EntityWrapper.class).getAttributes();
            List<UpsertRelationRequestContext> relations = new ArrayList<>();
            List<List<String>> input = loadResultSet(ctx, (CsvExchangeEntity) rel.getEntity());

            int count = 0;
            for (List<String> line : input) {

                TRANSFORM_CHAIN_MEMBER_LOGGER.info("Start processing containment relation of type {}, #{}.", rel.getEntity().getName(), ++count);
                long start = System.currentTimeMillis();

                UpsertRequestContext uCtx = importEntity(line, e, attrs);

                String originFromKey = null;
                if (uCtx != null) {
                    CsvNaturalKey keyDef = (CsvNaturalKey) rel.getFromNaturalKey();
                    OriginKey keyObject = importOriginKey(line, keyDef, null, null);
                    if (keyObject != null) {
                        originFromKey = keyObject.getExternalId();
                    }
                }

                if (originFromKey == null) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.warn("Cannot determine FROM origin key. Aborting.");
                    continue;
                }

                OriginRelationImpl ori = new OriginRelationImpl()
                        .withDataRecord(uCtx.getRecord())
                        .withInfoSection(new OriginRelationInfoSection()
                                .withRelationName(relDef.getName()));

                UpsertRelationRequestContext rCtx
                        = new UpsertRelationRequestContextBuilder()
                        .validFrom(uCtx.getValidFrom())
                        .validTo(uCtx.getValidTo())
                        .relation(ori)
                        .build();

                relations.add(rCtx);

                TRANSFORM_CHAIN_MEMBER_LOGGER.info("Finished processing containment relation of type {}, #{} in {} millis.",
                        rel.getEntity().getName(), count, System.currentTimeMillis() - start);
            }

            // Put collected
            target.put(relDef.getName(), relations);
        }

        if (!target.isEmpty()) {
            UpsertRelationsRequestContext rCtx
                    = new UpsertRelationsRequestContextBuilder()
                    // .relations(target)
                    .build();

            result.add(rCtx);
        }

        return result;
    }
}
