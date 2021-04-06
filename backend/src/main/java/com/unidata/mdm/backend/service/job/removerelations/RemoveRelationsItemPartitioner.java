package com.unidata.mdm.backend.service.job.removerelations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.job.JobUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("removeRelationsItemPartitioner")
@JobScope
public class RemoveRelationsItemPartitioner implements Partitioner {
    /**
     * Batch Size
     */
    private int batchSize;

    /**
     * user
     */
    private String userKey;

    /**
     * key for ids
     */
    private String idsKey;

    /**
     * Key for relations names
     */
    private String relationsNamesKey;

    /**
     * complex params holder
     */
    private ComplexJobParameterHolder jobParameterHolder;

    @Autowired
    public RemoveRelationsItemPartitioner setJobParameterHolder(final ComplexJobParameterHolder jobParameterHolder) {
        this.jobParameterHolder = jobParameterHolder;
        return this;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        if (StringUtils.isBlank(idsKey)
                || StringUtils.isBlank(userKey)) {
            return Collections.emptyMap();
        }

        List<String> etalonIds = jobParameterHolder.getComplexParameterAndRemove(idsKey);
        if (CollectionUtils.isEmpty(etalonIds)) {
            return Collections.emptyMap();
        }

        final Authentication authentication = jobParameterHolder.getComplexParameterAndRemove(userKey);
        final List<String> relationsNames = jobParameterHolder.getComplexParameterAndRemove(relationsNamesKey);

        return partitionIds(etalonIds, relationsNames, authentication);
    }

    private Map<String, ExecutionContext> partitionIds(
            final List<String> etalonIds,
            final List<String> relationsNames,
            final Authentication authentication
    ) {
        return IntStream.range(0, etalonIds.size() / batchSize + 1).mapToObj(batchNumber -> {
            final int lowerBound = batchNumber * batchSize;
            final int upperBound = lowerBound + batchSize;
            return Pair.of(
                    batchNumber,
                        new ArrayList<>(
                            etalonIds.subList(lowerBound, etalonIds.size() >= upperBound ? upperBound : etalonIds.size())
                        )
            );
        }).map(batch -> {
            final ExecutionContext context = new ExecutionContext();
            context.putString("batchName", JobUtil.partitionName((batch.getLeft())));
            context.put("ids", Collections.unmodifiableList(batch.getRight()));
            context.put("user", authentication);
            context.put("relationsNames", relationsNames);
            return context;
        }).collect(Collectors.toMap(ex -> ex.getString("batchName"), ex -> ex));
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public void setIdsKey(String idsKey) {
        this.idsKey = idsKey;
    }

    public void setRelationsNamesKey(final String relationsNamesKey) {
        this.relationsNamesKey = relationsNamesKey;
    }
}
