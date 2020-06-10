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

package com.unidata.mdm.backend.common.search.fields;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.search.SearchObjectConvert;
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
