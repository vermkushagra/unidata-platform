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

import com.unidata.mdm.backend.common.keys.ClassifierKeys;
import com.unidata.mdm.backend.common.types.EtalonClassifier;

/**
 * @author Mikhail Mikhailov
 * Upsert classifiers DTO.
 */
public class UpsertClassifierDTO implements ClassifierDTO, EtalonClassifierDTO {

    /**
     * The keys.
     */
    private ClassifierKeys classifierKeys;
    /**
     * Etalon classifier record.
     */
    private EtalonClassifier etalon;
    /**
     * Constructor.
     */
    public UpsertClassifierDTO() {
        super();
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
    public ClassifierKeys getClassifierKeys() {
        return classifierKeys;
    }
    /**
     * @param classifierKeys the classifierKeys to set
     */
    public void setClassifierKeys(ClassifierKeys classifierKeys) {
        this.classifierKeys = classifierKeys;
    }
}
