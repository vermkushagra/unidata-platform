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

package org.unidata.mdm.data.service.segments;

import java.util.Collection;
import java.util.Objects;

import org.unidata.mdm.core.type.calculables.Calculable;
import org.unidata.mdm.core.type.calculables.CalculableHolder;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.BinaryLargeValue;
import org.unidata.mdm.core.type.data.CharacterLargeValue;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.SimpleAttribute.DataType;

/**
 * @author Mikhail Mikhailov
 * Common routines for LOB stuff.
 */
public interface LobSubmitSupport {
    /**
     * Tells whether
     * @param state
     * @param lobId
     * @param path
     * @return
     */
    default <T extends Calculable> boolean isALreadyActivated(Collection<CalculableHolder<T>> state, String lobId, String path) {

        for (CalculableHolder<T> prev : state) {

            if (Objects.isNull(prev) || Objects.isNull(prev.getValue())) {
                continue;
            }

            Collection<Attribute> others = ((DataRecord) prev.getValue()).getAttributeRecursive(path);
            return others.stream().anyMatch(a -> {

                if (((SimpleAttribute<?>) a).getDataType() == DataType.BLOB) {
                    SimpleAttribute<BinaryLargeValue> blob = a.narrow();
                    return blob.getValue() != null && lobId.equals(blob.getValue().getId());
                }

                SimpleAttribute<CharacterLargeValue> clob = a.narrow();
                return clob.getValue() != null && lobId.equals(clob.getValue().getId());
            });
        }

        return false;
    }
    /**
     * @param simpleAttribute
     * @return Object id if exist
     */
    default String getBlobObjectId(SimpleAttribute<?> simpleAttribute) {
        BinaryLargeValue blobValue = simpleAttribute.castValue();
        return blobValue == null ? null : blobValue.getId();
    }

    /**
     * @param simpleAttribute
     * @return Object id if exist
     */
    default String getClobObjectId(SimpleAttribute<?> simpleAttribute) {
        CharacterLargeValue clobValue = simpleAttribute.castValue();
        return clobValue == null ? null : clobValue.getId();
    }
}
