package com.unidata.mdm.backend.service.job.duplicates;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.service.DataRecordsService;

public class DuplicateItemWriter implements ItemWriter<List<MergeRequestContext>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateItemWriter.class);


    @Autowired
    private DataRecordsService dataService;

    @Override
    public void write(List<? extends List<MergeRequestContext>> items) throws Exception {
        for (List<MergeRequestContext> ctxs : items) {
            try {
                dataService.batchMerge(ctxs);
            } catch (Exception e) {
                //there we can add adition information on audit event!
                LOGGER.error("Something happened duping recognizing winner{}", e);
            }
        }

    }


}
