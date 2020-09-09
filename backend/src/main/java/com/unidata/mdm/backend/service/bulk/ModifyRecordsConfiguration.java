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
package com.unidata.mdm.backend.service.bulk;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.bulk.BulkOperationConfiguration;
import com.unidata.mdm.backend.common.types.BulkOperationType;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;

/**
 * The Class ModifyRecordsConfiguration.
 *
 * @author Mikhail Mikhailov Modify records configuration.
 */
public class ModifyRecordsConfiguration extends BulkOperationConfiguration {

    /**
     * Partially filled record, which will be used for partially updating real
     * etalon record.
     */
    private EtalonRecord partiallyFilledRecord;

    /**
     * Classifier records.
     */
    private List<EtalonClassifier> classifierRecords;

    /** The etalon relations. */
    private List<EtalonRelation> etalonRelations;

    /**
     * Constructor.
     */
    public ModifyRecordsConfiguration() {
        super(BulkOperationType.MODIFY_RECORDS);
    }

    /**
     * Gets the partially filled record.
     *
     * @return the partially filled record
     */
    public EtalonRecord getPartiallyFilledRecord() {
        return partiallyFilledRecord;
    }

    /**
     * Sets the partially filled record.
     *
     * @param partiallyFilledRecord the new partially filled record
     */
    public void setPartiallyFilledRecord(EtalonRecord partiallyFilledRecord) {
        this.partiallyFilledRecord = partiallyFilledRecord;
    }

    /**
     * Gets the classifier records.
     *
     * @return the classifierRecords
     */
    public List<EtalonClassifier> getClassifierRecords() {
        return classifierRecords;
    }

    /**
     * Sets the classifier records.
     *
     * @param classifierRecords the classifierRecords to set
     */
    public void setClassifierRecords(List<EtalonClassifier> classifierRecords) {
        this.classifierRecords = classifierRecords;
    }

    /**
     * Gets the etalon relations.
     *
     * @return the etalon relations
     */
    public List<EtalonRelation> getEtalonRelations() {
        if(this.etalonRelations==null){
            this.etalonRelations = new ArrayList<>();
        }
        return etalonRelations;
    }

    /**
     * Sets the etalon relations.
     *
     * @param etalonRelations the new etalon relations
     */
    public void setEtalonRelations(List<EtalonRelation> etalonRelations) {
 
        this.etalonRelations = etalonRelations;
    }
}
