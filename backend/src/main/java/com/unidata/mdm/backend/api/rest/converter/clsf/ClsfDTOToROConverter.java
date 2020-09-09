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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;

/**
 * The Class ClsfDTOToROConverter.
 */
public class ClsfDTOToROConverter {
    /** The Constant CLSF_NODE_COMPARATOR. */
    private static final Comparator<ClsfRO> CLSF_COMPARATOR = new Comparator<ClsfRO>() {

        @Override
        public int compare(ClsfRO o1, ClsfRO o2) {
            if (o1 == null || o2 == null) {
                return 0;
            }
            if (o1.getDisplayName() == null || o2.getDisplayName() == null) {
                return 0;
            }
            return o1.getDisplayName().toLowerCase().compareTo(o2.getDisplayName().toLowerCase());
        }

    };

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the clsf RO
     */
    public static ClsfRO convert(ClsfDTO source) {
        if (source == null) {
            return null;
        }
        ClsfRO target = new ClsfRO();
        target.setChildren(
                Collections.singletonList(ClsfNodeDTOToROConverter.convert(source.getRootNode(), source.getName())));
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
     * @param source
     *            the source
     * @return the list
     */
    public static List<ClsfRO> convert(List<ClsfDTO> source) {
        if (source == null) {
            return null;
        }
        List<ClsfRO> target = new ArrayList<>();
        for (ClsfDTO element : source) {
            target.add(convert(element));
        }
        target.sort(CLSF_COMPARATOR);
        return target;
    }
}
