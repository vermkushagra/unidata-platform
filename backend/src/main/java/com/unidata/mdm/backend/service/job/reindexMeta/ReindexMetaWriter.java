package com.unidata.mdm.backend.service.job.reindexMeta;

import java.util.Collections;
import java.util.List;

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
        node.setClsfName(entity);
        searchServiceExt.indexClassifierNode(SecurityUtils.getCurrentUserStorageId(), node);
        if (node.getChildren() != null && node.getChildren().size() != 0) {
            List<ClsfNodeDTO> nodes = node.getChildren();
            nodes.forEach(this::indexNode);
        }
    }

    public void setClassifier(Boolean classifier) {
        isClassifier = classifier;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
