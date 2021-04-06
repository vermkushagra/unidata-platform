package com.unidata.mdm.backend.service.job.duplicates;

import com.unidata.mdm.backend.common.matching.Cluster;
import com.unidata.mdm.backend.common.matching.ClusterMetaData;
import com.unidata.mdm.backend.common.matching.ClusterRecord;
import com.unidata.mdm.backend.common.service.ClusterService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component("duplicateReader")
@Scope("step")
public class DuplicateItemReader extends AbstractItemCountingItemStreamItemReader<Collection<Collection<String>>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DuplicateItemReader.class);

    private ClusterMetaData clusterMetaData;

    private Integer shardNumber;

    private Long blockSize;

    @Autowired
    private ClusterService clusterService;

    public DuplicateItemReader(){
        super();
        setName(ClassUtils.getShortName(DuplicateItemReader.class));
    }


    @Override
    protected void doOpen() {

    }

    @Override
    protected void doClose() {

    }

    @Override
    protected Collection<Collection<String>> doRead() {
        List<Collection<String>> result = null;
        Collection<Cluster> clusters = clusterService.getClusters(clusterMetaData, blockSize.intValue(), 0, shardNumber);
        if (CollectionUtils.isNotEmpty(clusters)) {
            result = clusters.stream()
                    .map(cluster -> cluster.getClusterRecords().stream()
                            .map(ClusterRecord::getEtalonId)
                            .collect(Collectors.toList()))
                    .collect(Collectors.toList()) ;
        }
        return result;
    }

    @Required
    public void setClusterMetaData(ClusterMetaData clusterMetaData) {
        this.clusterMetaData = clusterMetaData;
    }


    @Required
    public void setShardNumber(Integer shardNumber) {
        this.shardNumber = shardNumber;
    }


    @Required
    public void setBlockSize(Long blockSize) {
        this.blockSize = blockSize;
    }
}
