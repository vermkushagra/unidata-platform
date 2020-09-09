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

package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.bulk.GetBulkOperationRO;
import com.unidata.mdm.backend.common.dto.GetBulkOperationDTO;

/**
 * @author Mikhail Mikhailov
 * Get bulk operation converter.
 */
public class GetBulkOperationConverter {

    /**
     * Constructor.
     */
    private GetBulkOperationConverter() {
        super();
    }

    /**
     * From internal 'to'.
     * @param source the source
     * @return RO
     */
    public static GetBulkOperationRO to(GetBulkOperationDTO source) {

        if (source == null) {
            return null;
        }

        GetBulkOperationRO target = new GetBulkOperationRO();
        target.setType(source.getType().name());
        target.setDescription(source.getDescription());

        return target;
    }

    /**
     * From internal 'to' list.
     * @param source the source list
     * @return RO list
     */
    public static List<GetBulkOperationRO> to(List<GetBulkOperationDTO> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<GetBulkOperationRO> target = new ArrayList<>(source.size());
        for (GetBulkOperationDTO dto : source) {
            target.add(to(dto));
        }

        return target;
    }
}
