package com.unidata.mdm.backend.service.job.modify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.job.JobUtil;

/**
 * The Class ModifyItemPartitioner.
 */
@Component("modifyItemPartitioner")
@JobScope
public class ModifyItemPartitioner implements Partitioner {

    /** The batch size. */
    private int batchSize;

    /** The record key. */
    private String recordKey;

    /** The classifiers key. */
    private String classifiersKey;

    /** The relations key. */
    private String relationsKey;

    /** The user key. */
    private String userKey;

    /** The ids key. */
    private String idsKey;

    /** The job parameter holder. */
    @Autowired
    private ComplexJobParameterHolder jobParameterHolder;

    /* (non-Javadoc)
     * @see org.springframework.batch.core.partition.support.Partitioner#partition(int)
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {

        if (idsKey == null || recordKey == null || userKey == null || classifiersKey == null) {
            return Collections.emptyMap();
        }

        List<String> etalonIds = jobParameterHolder.getComplexParameterAndRemove(idsKey);
        EtalonRecord etalonRecord = jobParameterHolder.getComplexParameterAndRemove(recordKey);
        List<EtalonClassifier> classifiers = jobParameterHolder.getComplexParameterAndRemove(classifiersKey);
        List<EtalonRelation> relations = jobParameterHolder.getComplexParameter(relationsKey);
        Authentication authentication = jobParameterHolder.getComplexParameterAndRemove(userKey);

        if (etalonIds == null || etalonRecord == null || authentication == null) {
            return Collections.emptyMap();
        }

        if (CollectionUtils.isEmpty(etalonIds)) {
            return Collections.emptyMap();
        }
        Iterator<String> etalonIdIterator = etalonIds.iterator();
        Map<String, ExecutionContext> result = new HashMap<>();
        Date now = new Date();
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
            context.put("record", etalonRecord);
            context.put("classifiers", classifiers);
            context.put("relations", relations);
            context.put("asOf", now);

            result.put(JobUtil.partitionName(batchCounter), context);
            batchCounter++;
        }
        return result;
    }

    /**
     * Sets the batch size.
     *
     * @param batchSize the new batch size
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    /**
     * Sets the record key.
     *
     * @param recordKey the new record key
     */
    public void setRecordKey(String recordKey) {
        this.recordKey = recordKey;
    }

    /**
     * Sets the classifiers key.
     *
     * @param classifiersKey the classifiersKey to set
     */
    public void setClassifiersKey(String classifiersKey) {
        this.classifiersKey = classifiersKey;
    }

    /**
     * Sets the relationss key.
     *
     * @param relationsKey the new relationss key
     */
    public void setRelationsKey(String relationsKey) {
        this.relationsKey = relationsKey;
    }

    /**
     * Sets the user key.
     *
     * @param userKey the new user key
     */
    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    /**
     * Sets the ids key.
     *
     * @param idsKey the new ids key
     */
    public void setIdsKey(String idsKey) {
        this.idsKey = idsKey;
    }
}
