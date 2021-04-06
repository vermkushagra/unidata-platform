package com.unidata.mdm.backend.service.job.remove;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.service.DataRecordsService;

public class RemoveItemWriter implements ItemWriter<DeleteRequestContext> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveItemWriter.class);

    /**
     * Data record service
     */
    @Autowired
    private DataRecordsService dataRecordsService;

    @Override
    public void write(List<? extends DeleteRequestContext> items) throws Exception {
        for (DeleteRequestContext ctx : items) {
            try {
                dataRecordsService.deleteRecord(ctx);
            } catch (Exception e) {
                LOGGER.error("Error during removing :{}", e);
            }
        }
    }
}
