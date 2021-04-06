package com.unidata.mdm.backend.service.job.reindex;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.IndexRequestContext;
import com.unidata.mdm.backend.service.search.SearchServiceExt;

/**
 * @author Denis Kostovarov
 */
@Component
@StepScope
public class ReindexDataJobDataItemWriter implements ItemWriter<IndexRequestContext> {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobDataItemWriter.class);
    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchServiceExt;
    /**
     * Clean types
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_CLEAN_INDEXES + "] ?: false }")
    private boolean indexesAreEmpty;
    /**
     * If true, record's data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RECORDS + "] ?: false}")
    private boolean jobReindexRecords;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RELATIONS + "] ?: false}")
    private boolean jobReindexRelations;
    /**
     * If true, record's data will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_RECORDS + "] ?: false}")
    private boolean stepReindexRecords;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{stepExecutionContext[" + ReindexDataJobConstants.PARAM_REINDEX_RELATIONS + "] ?: false}")
    private boolean stepReindexRelations;

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends IndexRequestContext> items) throws Exception {

        if (CollectionUtils.isEmpty(items)) {
            return;
        }

        try {
            searchServiceExt.index((List<IndexRequestContext>) items);
        } catch (Exception e) {
            LOGGER.error("Error during indexing records: {}", e);
        }
    }

    public void setIndexesAreEmpty(boolean cleanTypes) {
        this.indexesAreEmpty = cleanTypes;
    }

    public void setJobReindexRecords(Boolean reindexRecords) {
        this.jobReindexRecords = reindexRecords;
    }

    public void setJobReindexRelations(Boolean reindexRelations) {
        this.jobReindexRelations = reindexRelations;
    }
}
