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

package com.unidata.mdm.backend.common.context;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.common.keys.ClassifierKeys;

/**
 * @author Mikhail Mikhailov
 * Classifier data record identity context.
 */
public interface ClassifierIdentityContext extends RecordIdentityContext {
    /**
     * Gets the classifier etalon id.
     * @return the classifierEtalonKey
     */
    String getClassifierEtalonKey();
    /**
     * Gets the classifier origin id.
     * @return the classifierOriginKey
     */
    String getClassifierOriginKey();
    /**
     * Gets the classifier name.
     * @return the classifier name
     */
    String getClassifierName();
    /**
     * Gets the classifier node id.
     * @return the classifier node id
     */
    String getClassifierNodeId();
    /**
     * Gets the classifier node name.
     * @return the classifier node name
     */
    String getClassifierNodeName();
    /**
     * Gets the classifier node code.
     * @return the classifier node code
     */
    String getClassifierNodeCode();
    /**
     * Gets the classfier keys.
     * @return keys or null, if not set
     */
    ClassifierKeys classifierKeys();
    /**
     * Gets the keys id.
     * @return keys id
     */
    default StorageId classifierKeysId() {
        return StorageId.CLASSIFIERS_CLASSIFIER_KEYS;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    default StorageId keysId() {
        return StorageId.RECORDS_RECORD_KEYS;
    }
    /**
     * Tells whether this context can be used for identification.
     * @return true if so, false otherwise
     */
    default boolean isValidClassifierKey() {
        return isClassifierEtalonKey()
            || isClassifierOriginKey()
            || isRecordKeyAndNodeId()
            || isRecordKeyAndNodeCode()
            || isRecordKeyAndNodeName();
    }
    /**
     * Tells, if this is classifier etalon key.
     * @return true, if so, false otherwise
     */
    default boolean isClassifierEtalonKey() {
        return StringUtils.isNoneBlank(getClassifierEtalonKey());
    }
    /**
     * Tells, if this is classifier origin key.
     * @return true, if so, false otherwise
     */
    default boolean isClassifierOriginKey() {
        return StringUtils.isNoneBlank(getClassifierOriginKey());
    }
    /**
     * Tells, if this is a valid record key, classifier name and node id combo.
     * @return true, if so, false otherwise
     */
    default boolean isRecordKeyAndNodeId() {
        return StringUtils.isNoneBlank(getClassifierName())
            && StringUtils.isNoneBlank(getClassifierNodeId())
            && (keys() != null || isValidRecordKey());
    }
    /**
     * Tells, if this is a valid record key, classifier name and node code combo.
     * @return true, if so, false otherwise
     */
    default boolean isRecordKeyAndNodeCode() {
        return StringUtils.isNoneBlank(getClassifierName())
                && StringUtils.isNoneBlank(getClassifierNodeCode())
                && (keys() != null || isValidRecordKey());
    }
    /**
     * Tells, if this is a valid record key, classifier name and node name combo.
     * @return true, if so, false otherwise
     */
    default boolean isRecordKeyAndNodeName() {
        return StringUtils.isNoneBlank(getClassifierName())
                && StringUtils.isNoneBlank(getClassifierNodeName())
                && (keys() != null || isValidRecordKey());
    }
}