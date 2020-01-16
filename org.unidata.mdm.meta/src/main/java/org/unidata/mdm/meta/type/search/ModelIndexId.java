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

package org.unidata.mdm.meta.type.search;

import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.id.AbstractManagedIndexId;

/**
 * @author Mikhail Mikhailov
 * Classifier data index id.
 */
public class ModelIndexId extends AbstractManagedIndexId {
    /**
     * The name of the classifier.
     */
    private String classifierName;
    /**
     * Constructor.
     */
    private ModelIndexId() {
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
    public IndexType getSearchType() {
        return ModelIndexType.MODEL;
    }
    /**
     * Creates a new 'to' relation index id.
     * @param entityName the entity name
     * @param classifierName the name of the classifier
     * @param etalonId the record etalon id
     * @param nodeId the classifier naode id
     * @return index id
     */
    public static ModelIndexId of(String entityName, String classifierName, String etalonId, String nodeId) {

        ModelIndexId id = new ModelIndexId();

        id.entityName = entityName;
        id.classifierName = classifierName;
        id.indexId = PeriodIdUtils.childPeriodId(etalonId, classifierName, nodeId);
        id.routing = etalonId;

        return id;
    }
}
