/**
 *
 */
package com.unidata.mdm.backend.exchange.chain.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.item.ExecutionContext;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.ExchangeContext.Action;
import com.unidata.mdm.backend.exchange.chain.BaseTransformImportChainMember;
import com.unidata.mdm.backend.exchange.chain.ChainMember;
import com.unidata.mdm.backend.exchange.chain.Result;
import com.unidata.mdm.backend.exchange.chain.worker.UpsertRecordsEmulationJob;
import com.unidata.mdm.backend.exchange.def.ExchangeDefinition;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;
import com.unidata.mdm.backend.po.EtalonRecordPO;
import com.unidata.mdm.backend.po.OriginRecordPO;
import com.unidata.mdm.backend.service.job.importJob.record.ImportRecordPartitioner;


/**
 * @author Mikhail Mikhailov
 *         DB transform import chain member.
 */
public final class DbEntitiesTransformChainMember extends BaseTransformImportChainMember implements ChainMember {
    /**
     * Thread pool executor.
     */
    private ExecutorService executor;

    /**
     * Prefetch keys if requested.
     * TODO: move keys cache to entity instance.
     *
     * @param ctx the context
     */
    private void prefetchKeys(ExchangeContext ctx, ExchangeEntity entity) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entity.getPrefetchKeys().size(); i++) {
            String entityName = entity.getPrefetchKeys().get(i);
            sb
                    .append(StringUtils.wrap(entityName, "'"))
                    .append(i < (entity.getPrefetchKeys().size() - 1) ? ", " : "");
        }

        final String sql = "select e." + EtalonRecordPO.FIELD_ID + " as etalon_id, "
                + "e." + EtalonRecordPO.FIELD_STATUS + " as etalon_status, "
                + "coalesce((select ov.approval from origins_vistory ov, origins oo where oo.etalon_id = e.id and oo.status = 'ACTIVE' "
                + "and ov.origin_id = oo.id and ov.approval = 'PENDING' fetch first 1 rows only), 'APPROVED') as etalon_state,"
                + "o." + OriginRecordPO.FIELD_ID + " as origin_id, "
                + "o." + OriginRecordPO.FIELD_EXTERNAL_ID + " as external_id, "
                + "o." + OriginRecordPO.FIELD_SOURCE_SYSTEM + " as source_system, "
                + "o." + OriginRecordPO.FIELD_STATUS + " as origin_status, "
                + "o." + OriginRecordPO.FIELD_NAME + " as name from "
                + EtalonRecordPO.TABLE_NAME + " e, "
                + OriginRecordPO.TABLE_NAME + " o "
                + "where e." + EtalonRecordPO.FIELD_NAME + " in (" + sb.toString() + ") and "
                + "e." + EtalonRecordPO.FIELD_ID + " = o." + OriginRecordPO.FIELD_ETALON_ID;

        ResultSet keys = null;
        TRANSFORM_CHAIN_MEMBER_LOGGER.info("Start prefill cache");
        long start = System.currentTimeMillis();
        int count = 0;
        try {
            keys = ctx.getUnidataDataSource()
                    .getConnection()
                    .createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
                    .executeQuery(sql);

            while (keys != null && keys.next()) {
                String etalonId = keys.getString("etalon_id");
                String originId = keys.getString("origin_id");
                String externalId = keys.getString("external_id");
                String sourceSystem = keys.getString("source_system");
                String name = keys.getString("name");
                RecordStatus etalonStatus = RecordStatus.fromValue(keys.getString("etalon_status"));
                RecordStatus originStatus = RecordStatus.fromValue(keys.getString("origin_status"));
                ApprovalState etalonState = ApprovalState.valueOf(keys.getString("etalon_state"));
                EtalonKey etalonKey = EtalonKey.builder().id(etalonId).build();
                OriginKey originKey = OriginKey.builder()
                        .id(originId)
                        .entityName(name)
                        .externalId(externalId)
                        .sourceSystem(sourceSystem)
                        .build();

                KEYS_CACHE.add(RecordKeys.builder()
                        .entityName(name)
                        .etalonKey(etalonKey)
                        .originKey(originKey)
                        .etalonStatus(etalonStatus)
                        .originStatus(originStatus)
                        .etalonState(etalonState)
                        .build());
                count++;
            }
        } catch (Exception exc) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.error("Exception caught.", exc);
            return;
        } finally {
            try {
                if (keys != null && !keys.getStatement().getConnection().isClosed()) {
                    keys.getStatement().getConnection().close();
                }
            } catch (SQLException sqle) {
            }
        }

        TRANSFORM_CHAIN_MEMBER_LOGGER.info("End prefill cache [{} objects, in {} millis]", count, System.currentTimeMillis() - start);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(ExchangeContext ctx, Action currentAction) {

        // Definition
        ExchangeDefinition def = ctx.getExchangeDefinition();

        executor = Executors.newFixedThreadPool(ctx.getPoolSize());
        Collection<Future<Result>> results = new ArrayList<>();

        // 1. Lookup entities
        if (def.getLookupEntities() != null && def.getLookupEntities().size() > 0) {

            // 1.1 Sort to enable right order.
            Collections.sort(def.getLookupEntities(), ENTITY_IMPORT_ORDER_COMPARATOR);
            for (ExchangeEntity e : def.getLookupEntities()) {

                // 1.1 Skip other import types
                if (!DbExchangeEntity.class.isAssignableFrom(e.getClass()) || e.isProcessRelations()) {
                    continue;
                }

                // 1.1.2
                if (e.getPrefetchKeys() != null && !e.getPrefetchKeys().isEmpty()) {
                    prefetchKeys(ctx, e);
                }

                if (e.isProcessRelations()) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.info("Skip entity import, only relations requested");
                    continue;
                }

                // 2.4 Import
                if (!importEntities(ctx, (DbExchangeEntity) e, results)) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.warn("Failed to import object {}.", e.getName());
                }
            }
        }

        // 2. Entities
        if (def.getEntities() != null && def.getEntities().size() > 0) {

            // 1.1 Sort to enable right order.
            Collections.sort(def.getEntities(), ENTITY_IMPORT_ORDER_COMPARATOR);
            for (ExchangeEntity e : def.getEntities()) {

                // 1.1.1 Skip other import types
                if (!DbExchangeEntity.class.isAssignableFrom(e.getClass())) {
                    continue;
                }

                // 1.1.2
                if (e.getPrefetchKeys() != null && !e.getPrefetchKeys().isEmpty()) {
                    prefetchKeys(ctx, e);
                }

                if (e.isProcessRelations()) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.info("Skip entity import, only relations requested");
                    continue;
                }

                // 1.1.4 Import
                if (!importEntities(ctx, (DbExchangeEntity) e, results)) {
                    TRANSFORM_CHAIN_MEMBER_LOGGER.warn("Failed to import object {}.", e.getName());
                }
            }
        }

        executor.shutdown();
        processResult(results);

        return true;
    }

    /**
     * Imports entities set.
     *
     * @param ctx            -  ExchangeContext
     * @param exchangeEntity - definition
     * @return
     */
    private boolean importEntities(ExchangeContext ctx, DbExchangeEntity exchangeEntity, Collection<Future<Result>> results) {
        TRANSFORM_CHAIN_MEMBER_LOGGER.info("Process entity: {}", exchangeEntity.getName());
        try {
            ImportRecordPartitioner importRecordPartitioner = new ImportRecordPartitioner();
            importRecordPartitioner.setBatchSize(ctx.getBlockSize());
            importRecordPartitioner.setOffset(ctx.getStartOffset());
            importRecordPartitioner.setQuantityOfProcessedRecords(exchangeEntity.getMaxRecordCount());
            importRecordPartitioner.setModelService(getMetaModelService(ctx));
            importRecordPartitioner.setDataSource(ctx.getLandingDataSource());
            importRecordPartitioner.setBaseDefinition(exchangeEntity);
            importRecordPartitioner.setOperationId(ctx.getOperationId());

            Map<String, ExecutionContext> executionContextMap = importRecordPartitioner.partition(DEFAULT_BULK_SIZE);
            TRANSFORM_CHAIN_MEMBER_LOGGER.info("Number of partitions = {} for entity: {}", executionContextMap.size(), exchangeEntity.getName());

            for (ExecutionContext step : executionContextMap.values()) {
                Future<Result> resultFuture = executor.submit(new UpsertRecordsEmulationJob(step, ctx, KEYS_CACHE));
                results.add(resultFuture);
            }
            return true;
        } catch (Exception e) {
            TRANSFORM_CHAIN_MEMBER_LOGGER.error("Something wrong during records upsert ,{}", e);
            return false;
        }
    }


}
