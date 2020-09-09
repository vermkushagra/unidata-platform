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

import java.util.List;

import org.jgrapht.EdgeFactory;

import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaEdgeRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaGraphRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ie.MetaVertexRO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdgeFactory;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;

/**
 * The Class MetaGraphROToDTOConverter.
 *
 * @author ilya.bykov
 */
public class MetaGraphROToDTOConverter {

    /** The ef. */
    private static EdgeFactory<MetaVertex, MetaEdge<MetaVertex>> EF = new MetaEdgeFactory();

    /**
     * Convert.
     *
     * @param source
     *            the source
     * @return the meta graph
     */
    public static MetaGraph convert(MetaGraphRO source) {
        if (source == null) {
            return null;
        }
        MetaGraph target = new MetaGraph(EF);
        List<MetaVertexRO> vertexes = source.getVertexes();
        for (MetaVertexRO vertex : vertexes) {
            target.addVertex(MetaVertexROToDTOConverter.convert(vertex));
        }
        List<MetaEdgeRO> edges = source.getEdges();
        for (MetaEdgeRO edge : edges) {
            target.addEdge(MetaVertexROToDTOConverter.convert(edge.getFrom()),
                    MetaVertexROToDTOConverter.convert(edge.getTo()));
        }
        target.setOverride(source.isOverride());
        target.setId(source.getId());
        target.setFileName(source.getFileName());
        target.setImportRoles(source.isRoles());
        target.setImportUsers(source.isUsers());
        return target;
    }

}
