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

package com.unidata.mdm.backend.common.search.id;

import com.unidata.mdm.backend.common.search.PeriodIdUtils;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.search.types.SearchType;

/**
 * @author Mikhail Mikhailov
 * Classifier data index id.
 */
public class ClassifierIndexId extends AbstractManagedIndexId {
    /**
     * The name of the classifier.
     */
    private String classifierName;
    /**
     * Constructor.
     */
    private ClassifierIndexId() {
        super();
    }
    /**
     * @return the classifierName
     */
    public String getClassifierName() {
        return classifierName;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public SearchType getSearchType() {
        return EntitySearchType.CLASSIFIER;
    }
    /**
     * Creates a new 'to' relation index id.
     * @param entityName the entity name
     * @param classifierName the name of the classifier
     * @param etalonId the record etalon id
     * @param nodeId the classifier naode id
     * @return index id
     */
    public static ClassifierIndexId of(String entityName, String classifierName, String etalonId, String nodeId) {

        ClassifierIndexId id = new ClassifierIndexId();

        id.entityName = entityName;
        id.classifierName = classifierName;
        id.indexId = PeriodIdUtils.childPeriodId(etalonId, classifierName, nodeId);
        id.routing = etalonId;

        return id;
    }
}
