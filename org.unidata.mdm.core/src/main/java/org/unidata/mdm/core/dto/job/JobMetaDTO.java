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

package org.unidata.mdm.core.dto.job;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Kostovarov
 */
abstract class JobMetaDTO<T extends JobTemplateParameterDTO> {
    private String name;
    private String jobNameReference;
    private List<T> parameters;

    JobMetaDTO(){
        //no-op.
    }

    JobMetaDTO(String name, String jobNameReference, List<T> parameters) {
        this.name = name;
        this.jobNameReference = jobNameReference;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobNameReference() {
        return jobNameReference;
    }

    public void setJobNameReference(String jobNameReference) {
        this.jobNameReference = jobNameReference;
    }

    public List<T> getParameters() {
        return parameters;
    }

    public void setParameters(List<T> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(T param) {
        if (parameters == null) {
            parameters = new ArrayList<>();
        }
        parameters.add(param);
    }
}
