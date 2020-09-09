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

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forClassifierElements;
import static com.unidata.mdm.backend.common.context.SearchRequestContext.forModelElements;
import static com.unidata.mdm.backend.common.search.FormField.strictString;
import static com.unidata.mdm.backend.common.search.FormField.strictValue;
import static com.unidata.mdm.backend.common.search.fields.ClassifierHeaderField.CLASSIFIER_NAME;
import static com.unidata.mdm.backend.common.search.fields.ModelHeaderField.ENTITY_NAME;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.meta.SimpleDataType;

public class ReindexMetaReader implements ItemReader<Boolean> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReindexMetaReader.class);

    /**
     * Is classifier
     */
    private Boolean isClassifier;

    /**
     * Entity name
     */
    private String entity;

    private boolean pass = false;

    /**
     * Search service
     */
    @Autowired
    private SearchServiceExt searchServiceExt;

    @Override
    public Boolean read() throws Exception {
        SearchRequestContext searchContext = null;
        if (isClassifier) {
            FormField entityNameField = strictString(CLASSIFIER_NAME.getField(), entity);
            searchContext = forClassifierElements().form(FormFieldsGroup.createAndGroup(entityNameField))
                                                   .storageId(SecurityUtils.getCurrentUserStorageId())
                                                   .totalCount(true)
                                                   .countOnly(true)
                                                   .build();
        } else {
            //it is a crunch, we should use string value, but for backward compatibility for UI , we should use name instand of $name,
            FormField entityNameField = strictValue(SimpleDataType.INTEGER, ENTITY_NAME.getField(), entity);
            searchContext = forModelElements().form(FormFieldsGroup.createAndGroup(entityNameField))
                                              .storageId(SecurityUtils.getCurrentUserStorageId())
                                              .totalCount(true)
                                              .countOnly(true)
                                              .build();
        }
        LOGGER.info("Try to get info about {}", entity);
        if (!pass) {
            pass = true;
            return searchServiceExt.search(searchContext).getTotalCount() != 0;
        } else {
            return null;
        }
    }

    public void setClassifier(Boolean classifier) {
        isClassifier = classifier;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }
}
