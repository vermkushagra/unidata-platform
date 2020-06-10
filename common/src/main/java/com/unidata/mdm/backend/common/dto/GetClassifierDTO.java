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

package com.unidata.mdm.backend.common.dto;

import java.util.List;

import com.unidata.mdm.backend.common.dto.security.ResourceSpecificRightDTO;
import com.unidata.mdm.backend.common.dto.wf.WorkflowTaskDTO;
import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.OriginClassifier;

/**
 * @author Mikhail Mikhailov
 * Get type DTO for classifier data.
 */
public class GetClassifierDTO implements ClassifierDTO, EtalonClassifierDTO, OriginClassifiersDTO {
    /**
     * The keys.
     */
    private ClassifierKeys classifierKeys;
    /**
     * Etalon classifier record.
     */
    private EtalonClassifier etalon;
    /**
     * Origin classifier records.
     */
    private List<OriginClassifier> origins;
    /**
     * Tasks set
     */
    private List<WorkflowTaskDTO> tasks;
    /**
     * Rights.
     */
    private ResourceSpecificRightDTO rights;
    /**
     * Constructor.
     */
    public GetClassifierDTO() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ClassifierKeys getClassifierKeys() {
        return classifierKeys;
    }
    /**
     * @param classifierKeys the classifierKeys to set
     */
    public void setClassifierKeys(ClassifierKeys classifierKeys) {
        this.classifierKeys = classifierKeys;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public EtalonClassifier getEtalon() {
        return etalon;
    }
    /**
     * @param classifier the classifier to set
     */
    public void setEtalon(EtalonClassifier classifier) {
        this.etalon = classifier;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List<OriginClassifier> getOrigins() {
        return origins;
    }
    /**
     * @param origins the origins to set
     */
    public void setOrigins(List<OriginClassifier> origins) {
        this.origins = origins;
    }
    /**
     * @return the tasks
     */
    public List<WorkflowTaskDTO> getTasks() {
        return tasks;
    }
    /**
     * @param tasks the tasks to set
     */
    public void setTasks(List<WorkflowTaskDTO> tasks) {
        this.tasks = tasks;
    }
    /**
     * @return the rights
     */
    public ResourceSpecificRightDTO getRights() {
        return rights;
    }
    /**
     * @param rights the rights to set
     */
    public void setRights(ResourceSpecificRightDTO rights) {
        this.rights = rights;
    }
}
