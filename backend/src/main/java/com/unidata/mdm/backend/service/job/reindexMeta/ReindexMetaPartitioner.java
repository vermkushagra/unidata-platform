package com.unidata.mdm.backend.service.job.reindexMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.job.JobUtil;

/**
 * Made partitions for meta data!
 */
public class ReindexMetaPartitioner implements Partitioner {
    /**
     * Detect reindexing model meta data
     */
    private Boolean reindexModelMeta;
    /**
     * Detect reindexing classifier meta data
     */
    private Boolean reindexClassifiersMeta;

    /**
     * Meta model service
     */
    @Autowired
    private JobUtil jobUtil;

    /**
     * Classifier service!.
     */
    @Autowired
    private ClsfService classifierService;

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        if (!reindexClassifiersMeta && !reindexModelMeta) {
            return Collections.emptyMap();
        }
        Map<String, ExecutionContext> result = new HashMap<>();
        if (reindexModelMeta) {
            for (String entity : jobUtil.getEntityList(JobUtil.ALL)) {
                ExecutionContext context = new ExecutionContext();
                context.put("entityName", entity);
                context.put("isClassifier", false);

                result.put(JobUtil.partitionName(result.size()), context);
            }
        }

        if (reindexClassifiersMeta) {
            for (ClsfDTO clsfDTO : classifierService.getAllClassifiersWithoutDescendants()) {
                ExecutionContext context = new ExecutionContext();
                context.put("entityName", clsfDTO.getName());
                context.put("isClassifier", true);

                result.put(JobUtil.partitionName(result.size()), context);
            }
        }

        return result;
    }

    public void setReindexModelMeta(Boolean reindexModelMeta) {
        this.reindexModelMeta = reindexModelMeta;
    }

    public void setReindexClassifiersMeta(Boolean reindexClassifiersMeta) {
        this.reindexClassifiersMeta = reindexClassifiersMeta;
    }
}
