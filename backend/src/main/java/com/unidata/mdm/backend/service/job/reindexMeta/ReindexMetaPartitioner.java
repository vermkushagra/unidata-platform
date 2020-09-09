/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
