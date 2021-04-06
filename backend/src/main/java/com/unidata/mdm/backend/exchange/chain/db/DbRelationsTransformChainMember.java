/**
 *
 */
package com.unidata.mdm.backend.exchange.chain.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;
import com.unidata.mdm.backend.exchange.chain.BaseTransformImportChainMember;
import com.unidata.mdm.backend.exchange.chain.ChainMember;
import com.unidata.mdm.backend.exchange.chain.Result;
import com.unidata.mdm.backend.exchange.chain.worker.UpsertReferenceRelationEmulationJob;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.RelatesToRelation;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbRelatesToRelation;
import com.unidata.mdm.backend.service.job.importJob.relation.ImportRelationPartitioner;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;


/**
 * @author Mikhail Mikhailov
 *         Process relations import definitions.
 */
public final class DbRelationsTransformChainMember extends BaseTransformImportChainMember implements ChainMember {
    /**
     * Pool executor.
     */
    private ExecutorService executor;

    /**
     * Constructor.
     */
    public DbRelationsTransformChainMember() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(ExchangeContext ctx, Action currentAction) {

        executor = Executors.newFixedThreadPool(ctx.getPoolSize());

        // Definition
        ExchangeDefinition def = ctx.getExchangeDefinition();
        Collection<Future<Result>> results = new ArrayList<>();

        // Process relations
        if (!CollectionUtils.isEmpty(def.getEntities())) {

            // 1.1 Sort to enable right order.
            Collections.sort(def.getEntities(), ENTITY_IMPORT_ORDER_COMPARATOR);
            for (ExchangeEntity e : def.getEntities()) {

                // 1.1.1 Skip other import types
                if (!DbExchangeEntity.class.isAssignableFrom(e.getClass())) {
                    continue;
                }

                if (!importRelatesToRelations(ctx, e, results)) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.warn("Failed to import 'RelationTo' relations for object {}.", e.getName());
                }

                if (!importContainmentRelations(ctx, e, results)) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.warn("Failed to import 'Contains' relations for object {}.", e.getName());
                }
            }
        }

        executor.shutdown();
        processResult(results);

        return true;
    }

    /**
     * Imports realtes-to relations.
     *
     * @param ctx            the context
     * @param entityExchange the entity
     * @return result
     */
    private boolean importRelatesToRelations(ExchangeContext ctx, ExchangeEntity entityExchange, Collection<Future<Result>> results) {

        MetaModelServiceExt svc = getMetaModelService(ctx);
        List<RelatesToRelation> references = entityExchange.getRelates();

        // 1. Process relations
        for (int i = 0; references != null && i < references.size(); i++) {
            // 2. Find relation definition.
            DbRelatesToRelation rel = (DbRelatesToRelation) references.get(i);
            RelationDef relDef = svc.getRelationById(rel.getRelation());
            try {

                if (relDef == null || relDef.getName() == null || relDef.getRelType() == RelType.CONTAINS) {
                    throw new IllegalArgumentException("Unknown or invalid relation given [" + rel.getRelation() + "].");
                }

                ImportRelationPartitioner partitioner = new ImportRelationPartitioner();
                partitioner.setBatchSize(ctx.getBlockSize());
                partitioner.setDataSource(ctx.getLandingDataSource());
                partitioner.setBaseDefinition(rel);
                partitioner.setQuantityOfProcessedRecords(rel.getMaxRecordCount());
                partitioner.setOffset(ctx.getStartOffset());
                partitioner.setModelService(svc);
                partitioner.setFromSourceSystem(entityExchange.getSourceSystem());
                partitioner.setOperationId(ctx.getOperationId());

                Map<String, ExecutionContext> executionContextMap = partitioner.partition(DEFAULT_BULK_SIZE);
                TRANSFORM_CHAIN_MEMBER_LOGGER.info("Number of partitions = {} for relation: {}", executionContextMap.size(), rel.getRelation());

                for (ExecutionContext step : executionContextMap.values()) {
                    Future<Result> resultFuture = executor.submit(new UpsertReferenceRelationEmulationJob(step, ctx, KEYS_CACHE));
                    results.add(resultFuture);
                }
            } catch (Exception exc) {
                TRANSFORM_CHAIN_MEMBER_LOGGER.error("Something wrong happen during relation upsert , {}", exc);
                return false;
            }
        }

        return true;
    }

    /**
     * Imports containment relations.
     *
     * @param ctx the context
     * @param e   the entity
     * @return result
     */
    private boolean importContainmentRelations(ExchangeContext ctx, ExchangeEntity e, Collection<Future<Result>> results) {

        MetaModelServiceExt svc = getMetaModelService(ctx);
        List<ContainmentRelation> containments = e.getContains();

        for (int i = 0; containments != null && i < containments.size(); i++) {
            ContainmentRelation rel = containments.get(i);
            RelationDef relDef = svc.getRelationById(rel.getRelation());

            if (relDef == null || relDef.getName() == null || relDef.getRelType() != RelType.CONTAINS) {
                throw new IllegalArgumentException("Unknown or invalid relation given [" + rel.getRelation() + "].");
            }

            try {
                ImportRelationPartitioner partitioner = new ImportRelationPartitioner();
                partitioner.setBatchSize(ctx.getBlockSize());
                partitioner.setDataSource(ctx.getLandingDataSource());
                partitioner.setBaseDefinition(rel);
                partitioner.setQuantityOfProcessedRecords(rel.getMaxRecordCount());
                partitioner.setOffset(ctx.getStartOffset());
                partitioner.setModelService(svc);
                partitioner.setFromSourceSystem(e.getSourceSystem());
                partitioner.setOperationId(ctx.getOperationId());

                Map<String, ExecutionContext> executionContextMap = partitioner.partition(DEFAULT_BULK_SIZE);
                TRANSFORM_CHAIN_MEMBER_LOGGER.info("Number of partitions = {} for relation: {}", executionContextMap.size(), rel.getRelation());

                for (ExecutionContext step : executionContextMap.values()) {
                    Future<Result> resultFuture = executor.submit(new UpsertReferenceRelationEmulationJob(step, ctx, KEYS_CACHE));
                    results.add(resultFuture);
                }
            } catch (Exception exc) {
                TRANSFORM_CHAIN_MEMBER_LOGGER.error("Something wrong happen during relation upsert , {}", exc);
                return false;
            }
        }

        return true;
    }

}
