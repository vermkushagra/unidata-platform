package com.unidata.mdm.backend.service.job.reindex;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Mikhail Mikhailov
 * Update data mapping partitioner.
 */
@JobScope
public class ReindexDataJobResetPartitioner extends ReindexDataJobAbstractMappingPartitioner {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobResetPartitioner.class);
    /**
     * If true, record's data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RECORDS + "] ?: false}")
    private Boolean reindexRecords;
    /**
     * If true, rels will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_RELATIONS + "] ?: false}")
    private Boolean reindexRelations;
    /**
     * If true, maching data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_MATCHING + "] ?: false}")
    private Boolean reindexMatching;
    /**
     * If true, classifiers data will be reindexed
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS + "] ?: false}")
    private Boolean reindexClassifiers;
    /**
     * Constructor.
     */
    public ReindexDataJobResetPartitioner() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        if (!reindexRecords && !reindexRelations && !reindexMatching && !reindexClassifiers) {
            LOGGER.info("No data kind specified for reindexing [reindexRecords, reindexRelations, reindexMatching, reindexClassifiers are all false]. No reset executed.");
            return Collections.emptyMap();
        }

        return super.partition(gridSize);
    }
}
