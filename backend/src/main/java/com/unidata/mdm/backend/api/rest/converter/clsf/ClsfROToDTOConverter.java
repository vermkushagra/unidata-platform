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

package com.unidata.mdm.backend.api.rest.converter.clsf;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;


/**
 * The Class ClsfROToDTOConverter.
 */
public class ClsfROToDTOConverter {

    /**
     * Convert.
     *
     * @param source the source
     * @return the clsf DTO
     */
    public static ClsfDTO convert(ClsfRO source) {
        if (source == null) {
            return null;
        }
        ClsfDTO target = new ClsfDTO();
        target.setRootNode(ClsfNodeROToDTOConverter.convert(source.getChildren().stream().findFirst().orElse(null)));
        target.setCodePattern(source.getCodePattern());
        target.setDescription(source.getDescription());
        target.setDisplayName(source.getDisplayName());
        target.setName(source.getName());
        target.setValidateCodeByLevel(source.isValidateCodeByLevel());
        return target;
    }

    /**
     * Convert.
     *
     * @param source the source
     * @return the list
     */
    public static List<ClsfDTO> convert(List<ClsfRO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfDTO> target = new ArrayList<>();
        for (ClsfRO element : source) {
            target.add(convert(element));
        }
        return target;
    }
}
