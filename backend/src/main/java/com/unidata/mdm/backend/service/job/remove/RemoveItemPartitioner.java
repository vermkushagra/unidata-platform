package com.unidata.mdm.backend.service.job.remove;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.job.JobUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("removeItemPartitioner")
@JobScope
public class RemoveItemPartitioner implements Partitioner {
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
     * complex params holder
     */
    @Autowired
    private ComplexJobParameterHolder jobParameterHolder;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        if (StringUtils.isBlank(idsKey) && StringUtils.isBlank(userKey)) {
            return Collections.emptyMap();
        }

        final Authentication authentication = jobParameterHolder.getComplexParameterAndRemove(userKey);
        final List<String> etalonIds = jobParameterHolder.getComplexParameterAndRemove(idsKey);

        if (CollectionUtils.isEmpty(etalonIds)) {
            return Collections.emptyMap();
        }
        Iterator<String> etalonIdIterator = etalonIds.iterator();
        Map<String, ExecutionContext> result = new HashMap<>();
        int batchCounter = 0;
        while (true) {
            if (!etalonIdIterator.hasNext()) {
                break;
            }
            List<String> batch = new ArrayList<>(batchSize);
            int idCounter = 0;
            while (etalonIdIterator.hasNext()) {
                idCounter++;
                if (idCounter >= batchSize) {
                    break;
                }
                String etalonId = etalonIdIterator.next();
                batch.add(etalonId);
            }
            final ExecutionContext context = new ExecutionContext();
            context.put("ids", batch);
            context.put("user", authentication);

            result.put(JobUtil.partitionName(batchCounter), context);
            batchCounter++;
        }
        return result;
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
}
