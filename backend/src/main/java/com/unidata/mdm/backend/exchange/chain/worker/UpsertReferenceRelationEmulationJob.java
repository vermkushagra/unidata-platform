package com.unidata.mdm.backend.exchange.chain.worker;

import java.util.Date;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.impl.RecordKeysCache;
import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.chain.ChainMember;
import com.unidata.mdm.backend.exchange.chain.Result;
import com.unidata.mdm.backend.service.job.importJob.relation.ImportRelationProcessor;
import com.unidata.mdm.backend.service.job.importJob.relation.ImportRelationReader;
import com.unidata.mdm.backend.service.job.importJob.relation.ImportRelationWriter;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRelationSet;

public class UpsertReferenceRelationEmulationJob extends JobEmulationRunnable<UpsertRelationsRequestContext, ImportRelationSet> {


    @SuppressWarnings("unchecked")
    public UpsertReferenceRelationEmulationJob(ExecutionContext step, ExchangeContext ctx, RecordKeysCache keyCache) {
        super(step);

        ImportRelationReader reader = new ImportRelationReader();
        reader.setDataSource(ctx.getLandingDataSource());
        reader.setSql(step.getString("sql"));
        reader.setRowMapper((RowMapper<ImportRelationSet>) step.get("rowMapper"));
        reader.setMaxItemCount(step.getInt("bulkSize"));
        reader.setOperationId(step.getString("operationId"));
        reader.setImportErrorDao(ChainMember.createErrorDao(ctx));
        this.reader = reader;

        ImportRelationProcessor importRelationProcessor = new ImportRelationProcessor();
        importRelationProcessor.setKeysCache(keyCache);
        importRelationProcessor.setFromSourceSystem(step.getString("fromSourceSystem"));
        importRelationProcessor.setToSourceSystem(step.getString("toSourceSystem"));
        importRelationProcessor.setFrom((Date) step.get("from"));
        importRelationProcessor.setTo((Date) step.get("to"));
        importRelationProcessor.setEntityName(step.getString("entityName"));
        importRelationProcessor.setToEntityAttributeName((String) step.get("toEntityAttributeName"));
        importRelationProcessor.setOperationId(step.getString("operationId"));
        this.itemProcessor = importRelationProcessor;

        ImportRelationWriter importRelationWriter = new ImportRelationWriter();
        importRelationWriter.setDataRecordsService(ChainMember.createDataRecordsService(ctx));
        importRelationWriter.setMessageSource(ChainMember.createMessageSource(ctx));
        this.itemWriter = importRelationWriter;

        LOGGER.info("OverAll: relationName = {} , bulkSize = {} , fromSourceSystem = {} , toSourceSystem = {}", step.getString("relationName"), step.getInt("bulkSize"), step.getString("fromSourceSystem"), step.getString("toSourceSystem"));
    }

    @Override
    public Result call() throws Exception {
        ImportRelationReader importRelationReader = (ImportRelationReader) reader;
        ImportRelationWriter importRelationWriter = (ImportRelationWriter) itemWriter;
        MeasurementPoint.init(MeasurementContextName.MEASURE_STEP_IMPORT_RELATIONS);
        try {
            importRelationReader.open(executionContext);
            LOGGER.info("Start partition processing , SQL: {}", importRelationReader.getSql());
            Result result = super.call();
            int totalProcessedRecords = importRelationReader.getCurrentItemCount() == executionContext.getInt("bulkSize") ? importRelationReader.getCurrentItemCount() : importRelationReader.getCurrentItemCount() - 1;
            result.setEntityName(executionContext.getString("relationName")).
                    setTotal(totalProcessedRecords)
                    .setFailed(importRelationWriter.getFailed())
                    .setReject(totalProcessedRecords - importRelationWriter.getTotal())
                    .setProcessed(importRelationWriter.getTotal() - importRelationWriter.getFailed());
            LOGGER.info("Processed {} records for {}", importRelationReader.getCurrentItemCount(), executionContext.getString("relationName"));
            return result;
        } finally {
            importRelationReader.close();
        }
    }


}
