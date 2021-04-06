package com.unidata.mdm.backend.service.job.duplicates;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.service.job.JobUtil;
import com.unidata.mdm.backend.common.service.ClusterService;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

import static com.unidata.mdm.backend.service.search.util.SearchUtils.DEFAULT_NUMBER_OF_SHARDS;

public class DuplicateItemPartitioner implements Partitioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateItemPartitioner.class);

    private String entityName;


    private String matchingName;

    /**
     * Number of shards
     */
    @Value("${" + ConfigurationConstants.SEARCH_SHARDS_NUMBER_PROPERTY + ":" + SearchUtils.DEFAULT_NUMBER_OF_SHARDS + "}")
    public String numberOfShards = DEFAULT_NUMBER_OF_SHARDS;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private JobUtil jobUtil;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Integer shardNumber = Integer.parseInt(numberOfShards);
        Map<String, ExecutionContext> map = new HashMap<>(shardNumber);
        for(Integer shard = 0; shard < shardNumber; shard++){
            ClusterMetaData clusterMetaData = jobUtil.getMatchingSettings(entityName, matchingName);
            LOGGER.info("Start partitioning of clusters between workers. Entity name {} , {}:{}", entityName, matchingName);

            final ExecutionContext value = new ExecutionContext();
            value.put("clusterMetaData", clusterMetaData);
            value.put("shardNumber", shard);
            map.put("partition:" + shard, value);
        }

        return map;
    }

    @Required
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }


    @Required
    public void setMatchingName(String matchingName) {
        this.matchingName = matchingName;
    }
}
