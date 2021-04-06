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
public class ReindexDataJobMappingPartitioner extends ReindexDataJobAbstractMappingPartitioner {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobMappingPartitioner.class);
    /**
     * Comma separated reindex types.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_UPDATE_MAPPINGS + "] ?: false}")
    private Boolean updateMappings;
    /**
     * Comma separated reindex types.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_CLEAN_INDEXES + "] ?: false}")
    private Boolean cleanIndexes;
    /**
     * Constructor.
     */
    public ReindexDataJobMappingPartitioner() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        if (!updateMappings && !cleanIndexes) {
            LOGGER.info("No mapping update nor index clean required. Mapping step is skipped.");
            return Collections.emptyMap();
        }

        return super.partition(gridSize);
    }
}
