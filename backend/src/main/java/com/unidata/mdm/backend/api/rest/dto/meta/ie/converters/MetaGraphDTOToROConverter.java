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

package com.unidata.mdm.backend.api.rest.dto.meta.ie.converters;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaGraphRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;

/**
 * The Class MetaGraphDTOToROConverter.
 * @author ilya.bykov
 */
public class MetaGraphDTOToROConverter {

    /**
     * Convert.
     *
     * @param source the source
     * @return the meta graph RO
     */
    public static MetaGraphRO convert(MetaGraph source) {
        if (source == null) {
            return null;
        }
        MetaGraphRO target = new MetaGraphRO(
                source.getId(), source.getFileName(),
                MetaVertexDTOToROConverter.convert(source.vertexSet()),
                MetaEdgeDTOToROConverter.convert(source.edgeSet()),
                source.isImportRoles(),
                source.isImportUsers()
        );
        target.setOverride(source.isOverride());
        return target;
    }
}
