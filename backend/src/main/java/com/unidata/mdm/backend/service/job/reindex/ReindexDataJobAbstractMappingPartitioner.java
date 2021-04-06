package com.unidata.mdm.backend.service.job.reindex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.service.job.JobUtil;

/**
 * @author Mikhail Mikhailov
 * Basic mapping partitioner.
 */
public abstract class ReindexDataJobAbstractMappingPartitioner implements Partitioner {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexDataJobAbstractMappingPartitioner.class);
    /**
     * Comma separated reindex types.
     */
    @Value("#{jobParameters[" + ReindexDataJobConstants.PARAM_REINDEX_TYPES + "]}")
    protected String reindexTypes;
    /**
     * Block size.
     */
    @Value("${" + ConfigurationConstants.REINDEX_JOB_MAPPING_BLOCK_SIZE + ":5}")
    protected Long blockSize;
    /**
     * job util.
     */
    @Autowired
    protected JobUtil jobUtil;
    /**
     * Constructor.
     */
    public ReindexDataJobAbstractMappingPartitioner() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        int number = 0;
        Map<String, ExecutionContext> result = new HashMap<>();
        List<String> entityNames = jobUtil.getEntityList(reindexTypes);
        ExecutionContext value = new ExecutionContext();
        List<String> current = new ArrayList<>();
        for (String entityName : entityNames) {

            current.add(entityName);
            if (current.size() == blockSize) {
                value.putString(ReindexDataJobConstants.PARAM_ENTITY_NAME, StringUtils.join(current, "|"));
                result.put(JobUtil.partitionName(number), value);
                value = new ExecutionContext();
                current.clear();
                number++;
            }
        }

        if (!current.isEmpty()) {
            value.putString(ReindexDataJobConstants.PARAM_ENTITY_NAME, StringUtils.join(current, "|"));
            result.put(JobUtil.partitionName(number), value);
        }

        LOGGER.info("Collected {} partitions [{} entities].", result.size(), (number * blockSize) + current.size());
        return result;
    }
}
