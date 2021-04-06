package com.unidata.mdm.backend.exchange.chain.worker;

import java.util.Collection;
import java.util.Date;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.common.context.CommonSendableContext;
import com.unidata.mdm.backend.common.runtime.MeasurementContextName;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.CodeAttributeAlias;
import com.unidata.mdm.backend.common.types.impl.RecordKeysCache;
import com.unidata.mdm.backend.exchange.ExchangeContext;
import com.unidata.mdm.backend.exchange.chain.ChainMember;
import com.unidata.mdm.backend.exchange.chain.Result;
import com.unidata.mdm.backend.service.job.importJob.record.ImportRecordProcessor;
import com.unidata.mdm.backend.service.job.importJob.record.ImportRecordReader;
import com.unidata.mdm.backend.service.job.importJob.record.ImportRecordWriter;
import com.unidata.mdm.backend.service.job.importJob.types.ImportRecordSet;

public class UpsertRecordsEmulationJob extends JobEmulationRunnable<CommonSendableContext, ImportRecordSet> {

    @SuppressWarnings("unchecked")
    public UpsertRecordsEmulationJob(ExecutionContext step, ExchangeContext ctx, RecordKeysCache KEYS_CACHE) {
        super(step);
        ImportRecordReader reader = new ImportRecordReader();
        reader.setDataSource(ctx.getLandingDataSource());
        reader.setSql(step.getString("sql"));
        reader.setRowMapper((RowMapper<ImportRecordSet>) step.get("rowMapper"));
        reader.setMaxItemCount(step.getInt("bulkSize"));
        reader.setBatchSize(step.getInt("bulkSize"));
        reader.setStep(step.getInt("partition"));
        reader.setOperationId(step.getString("operationId"));
        reader.setAuditEventsWriter(ChainMember.createAuditWriter(ctx));
        this.reader = reader;

        ImportRecordProcessor processor = new ImportRecordProcessor();
        processor.setAliasCodeAttributePointers((Collection<CodeAttributeAlias>) step.get("aliasCodeAttributePointers"));
        processor.setEntityName(step.getString("entityName"));
        processor.setFrom((Date) step.get("from"));
        processor.setTo((Date) step.get("to"));
        processor.setBatchSize(step.getInt("bulkSize"));
        processor.setStep(step.getInt("partition"));
        processor.setSkipCleanse((Boolean) step.get("skipCleanse"));
        processor.setSourceSystem(step.getString("sourceSystem"));
        processor.setOperationId(step.getString("operationId"));
        this.itemProcessor = processor;

        ImportRecordWriter writer = new ImportRecordWriter();
        writer.setRecordsService(ChainMember.createDataRecordsService(ctx));
        writer.setKeyCache(KEYS_CACHE);
        writer.setMessageSource(ChainMember.createMessageSource(ctx));
        this.itemWriter = writer;

        LOGGER.info("OverAll: entityName = {} , bulkSize = {} , sourceSystem = {} , skipCleanse = {}", step.getString("entityName"), step.getInt("bulkSize"), step.getString("sourceSystem"), step.get("skipCleanse"));
    }

    @Override
    public Result call() throws Exception {
        ImportRecordReader importRecordReader = (ImportRecordReader) reader;
        ImportRecordWriter importRecordWriter = (ImportRecordWriter) itemWriter;
        MeasurementPoint.init(MeasurementContextName.MEASURE_STEP_IMPORT_RECORDS);
        try {
            importRecordReader.open(executionContext);
            LOGGER.info("Start partition processing , SQL: {}", importRecordReader.getSql());
            Result result = super.call();
            int totalProcessedRecords = importRecordReader.getCurrentItemCount() == executionContext.getInt("bulkSize") ? importRecordReader.getCurrentItemCount() : importRecordReader.getCurrentItemCount() - 1;
            result.setEntityName(executionContext.getString("entityName")).
                    setTotal(totalProcessedRecords)
                    .setFailed(importRecordWriter.getFailed())
                    .setReject(totalProcessedRecords - importRecordWriter.getTotal())
                    .setProcessed(importRecordWriter.getTotal() - importRecordWriter.getFailed());
            LOGGER.info("Processed {} records for {}", importRecordReader.getCurrentItemCount(), executionContext.getString("entityName"));
            return result;
        } finally {
            importRecordReader.close();
        }
    }

}
