package com.unidata.mdm.backend.service.job.importJob.relation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.service.job.importJob.AbstractCountingWriter;

@Component("importRelationWriter")
@StepScope
public class ImportRelationWriter extends AbstractCountingWriter<UpsertRelationsRequestContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportRelationWriter.class);

    /**
     * data record service
     */
    @Autowired
    private DataRecordsService dataRecordsService;

    @Override
    public void write(List<? extends UpsertRelationsRequestContext> items) throws Exception {
        setTotal(items.size());
        for (UpsertRelationsRequestContext ctx : items) {
            try {
                dataRecordsService.upsertRelations(ctx);
            } catch (Exception e) {
                incrementFailed();
                String message = getErrorMessage(e, ctx);
                //write in db any cases!
                LOGGER.error("Error during upsert relation, {},{}", message, e);
            }
        }
    }

    public void setDataRecordsService(DataRecordsService dataRecordsService) {
        this.dataRecordsService = dataRecordsService;
    }
}
