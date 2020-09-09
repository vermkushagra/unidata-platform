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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.service.classifier.ClsfServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ModelSearchObject;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

public class ReindexMetaWriter implements ItemWriter<Boolean> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexMetaWriter.class);

    /**
     * Is classifier
     */
    private Boolean isClassifier;

    /**
     * Entity name
     */
    private String entity;

    /**
     * Meta model service
     */
    @Autowired
    private MetaModelServiceExt metaModelServiceExt;
    /**
     * Classifier service!.
     */
    @Autowired
    private ClsfService classifierService;
    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchServiceExt;

    @Override
    public void write(List<? extends Boolean> items) throws Exception {
        for (Boolean hasData : items) {
            String entityType = isClassifier ? "Classifier" : "Entity";
            if (hasData) {
                LOGGER.info("Skip {}-{} because it has data", entityType, entity);
                continue;
            }
            LOGGER.info("Try to fill {}-{}", entityType, entity);

            if (isClassifier) {
                ClsfDTO clsfDTO = classifierService.getClassifierByNameWithAllNodes(entity);
                if (clsfDTO != null && clsfDTO.getRootNode() != null) {
                    indexNode(clsfDTO.getRootNode());
                }
            } else {
                boolean isEntity = metaModelServiceExt.isEntity(entity);
                ModelSearchObject modelSearchElement = null;
                if (isEntity) {
                    EntityWrapper wrapper = metaModelServiceExt.getValueById(entity, EntityWrapper.class);
                    modelSearchElement = wrapper.getModelSearchElement();
                } else {
                    LookupEntityWrapper wrapper = metaModelServiceExt.getValueById(entity, LookupEntityWrapper.class);
                    modelSearchElement = wrapper.getModelSearchElement();
                }
                if (modelSearchElement == null) {
                    continue;
                }
                searchServiceExt.indexModelSearchElements(SecurityUtils.getCurrentUserStorageId(),
                        Collections.singletonList(modelSearchElement));
            }
        }
    }

    /**
     * Index node.
     *
     * @param node the node
     */
    private void indexNode(ClsfNodeDTO node) {
        List<ClsfNodeDTO> nodes = new ArrayList<>();
        collectNode(node, nodes);
        List<List<ClsfNodeDTO>> nodesForIndex = nodes.stream()
                .collect(new ClsfServiceImpl.BatchCollector<>(classifierService.getClassifierBatchSize()));

        if(CollectionUtils.isNotEmpty(nodesForIndex)){
            nodesForIndex.forEach(clsfNodeDTOS ->
                    searchServiceExt.indexClassifierNodes(SecurityUtils.getCurrentUserStorageId(), clsfNodeDTOS));
        }
    }

    private void collectNode(ClsfNodeDTO node, List<ClsfNodeDTO> res) {
        node.setClsfName(entity);
        res.add(node);
        if (CollectionUtils.isNotEmpty(node.getChildren())) {
            node.getChildren().forEach(childNode -> collectNode(childNode, res));
        }
     }

    public void setClassifier(Boolean classifier) {
        isClassifier = classifier;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
