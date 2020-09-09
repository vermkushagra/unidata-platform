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

package com.unidata.mdm.backend.service.classifier.converters;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.service.classifier.cache.CachedClassifier;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;

/**
 * @author Mikhail Mikhailov
 * PO to CC converter.
 */
public class ClsfPOToCachedClassifierConverter {
    /**
     * Constructor.
     */
    private ClsfPOToCachedClassifierConverter() {
        super();
    }
    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf DTO
     */
    public static CachedClassifier convert(ClsfPO source) {

        if (source == null) {
            return null;
        }
        /*
        CachedClassifier target = new CachedClassifier();
        target.setCodePattern(source.getCodePattern());
        target.setCreatedAt(source.getCreatedAt());
        target.setCreatedBy(source.getCreatedBy());
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setRootNode(ClsfNodePOToDTOConverter.convert(source.getRootNode()));
        target.setUpdatedAt(source.getUpdatedAt());
        target.setUpdatedBy(source.getUpdatedBy());
        target.setValidateCodeByLevel(source.isValidateCodeByLevel());
        */
        return null; // target
    }
    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the list
     */
    public static List<ClsfDTO> convert(List<ClsfPO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfDTO> target = new ArrayList<>();
        for (ClsfPO element : source) {
            // target.add(convert(element));
        }
        return target;
    }
}
