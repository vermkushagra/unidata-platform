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

package com.unidata.mdm.backend.service.matching.algorithms;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;

/**
 * Algorithm define methods and rules, which can say is record matched or not.
 */
public interface Algorithm {
    /**
     * @return id
     */
    Integer getAlgorithmId();
    /**
     * @return algorithm
     */
    @Nonnull
    String getAlgorithmName();
    /**
     * @return algorithm description
     */
    @Nonnull
    String getAlgorithmDescription();
    /**
     * @return true if exact otherwise false
     */
    boolean isExact();
    /**
     * Gets exact algorithm type.
     * @return type
     */
    AlgorithmType getType();
    /**
     * Constraucts value, suitable for clustering/blocking off the attribute.
     * @param attr the attribute to use
     * @return value, may be null
     */
    @Nullable
    Object construct(Attribute attr);

    /**
     * Constraucts value, suitable for clustering/blocking off the attribute.
     * @param attr the attribute to use
     * @param additional information to use
     * @return value, may be null
     */
    @Nullable
    default Object construct(Attribute attr, Object additional){
        return null;
    }
    /**
     * @param attributeMap - attribute map
     * @return - null in case when it mean find all records
     */
    @Nullable
    FormFieldsGroup getFormFieldGroup(Map<Integer, Pair<AttributeInfoHolder, SimpleAttribute<?>>> attributeMap);

    @Nullable
    default FormFieldsGroup getFormFieldGroup(Map<Integer, Pair<AttributeInfoHolder, SimpleAttribute<?>>> attributeMap,
            Map<Integer, Object> additionalMap) {
        return null;
    }
    /**
     * @return template for filling
     */
    @Nonnull
    MatchingAlgorithm getTemplate();
    /**
     * @return collection of fields ids, which can be used for calculation cluster id.
     */
    Collection<Integer> getFieldsForIdentify();

    default Collection<Integer> getSupplementaryFields(Integer id){
        return Collections.emptyList();
    }
}
