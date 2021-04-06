package com.unidata.mdm.backend.service.search.util;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.search.types.SearchType;
import com.unidata.mdm.backend.common.search.types.ServiceSearchType;

/**
 * @author Mikhail Mikhailov
 *         Header mark fields for indexed classifiers data.
 */
public enum ClassifierHeaderField implements SearchObjectConvert<ClsfNodeDTO> {
    /**
     * Classifier name
     */
    CLASSIFIER_NAME("$name") {
        @Override
        public Object getIndexedElement(ClsfNodeDTO indexedObject) {
            return indexedObject.getClsfName();
        }
    },
    /**
     * Unique id for node
     */
    NODE_UNIQUE_ID("$unique_id") {
        @Override
        public Object getIndexedElement(ClsfNodeDTO indexedObject) {
            return indexedObject.getNodeId();
        }
    },
    /**
     * Node name
     */
    NODE_SEARCH_ELEMENT("node_search_elements") {
        @Override
        public Object getIndexedElement(ClsfNodeDTO indexedObject) {
            return StringUtils.isBlank(indexedObject.getCode()) ?
                    indexedObject.getName() :
                    Arrays.asList(indexedObject.getName(), indexedObject.getCode());
        }
    };

    private final String field;

    ClassifierHeaderField(String field) {
        this.field = field;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public SearchType linkedSearchType() {
        return ServiceSearchType.CLASSIFIER;
    }
}
