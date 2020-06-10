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

/**
 *
 */
package com.unidata.mdm.backend.common.dto;

import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.types.EtalonRecord;

/**
 * @author Mikhail Mikhailov
 * Get recordS result DTO.
 */
public class GetRecordsDTO {

    /**
     * 0 or more origin records.
     */
    private List<EtalonRecord> etalons;
    /**
     * Relations.
     */
    private Map<EtalonRecord, Map<RelationStateDTO, List<GetRelationDTO>>> relations;
    /**
     * Classifiers.
     */
    private Map<EtalonRecord, Map<String, List<GetClassifierDTO>>> classifiers;
    /**
     * Constructor.
     */
    public GetRecordsDTO() {
        super();
    }
    /**
     * @return the list of {@link EtalonRecord}
     */
    public List<EtalonRecord> getEtalons() {
        return etalons;
    }
    /**
     * @param etalons the etalons to set
     */
    public void setEtalons(List<EtalonRecord> etalons) {
        this.etalons = etalons;
    }
    /**
     * @return the relations
     */
    public Map<EtalonRecord, Map<RelationStateDTO, List<GetRelationDTO>>> getRelations() {
        return relations;
    }
    /**
     * @param relations the relations to set
     */
    public void setRelations(Map<EtalonRecord, Map<RelationStateDTO, List<GetRelationDTO>>> relations) {
        this.relations = relations;
    }
    /**
     * @return the classifiers
     */
    public Map<EtalonRecord, Map<String, List<GetClassifierDTO>>> getClassifiers() {
        return classifiers;
    }
    /**
     * @param classifiers the classifiers to set
     */
    public void setClassifiers(Map<EtalonRecord, Map<String, List<GetClassifierDTO>>> classifiers) {
        this.classifiers = classifiers;
    }

}
