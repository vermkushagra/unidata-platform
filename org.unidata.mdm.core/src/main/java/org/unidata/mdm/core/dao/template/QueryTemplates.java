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

package org.unidata.mdm.core.dao.template;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Mikhail Mikhailov
 * The templates holder.
 */
public class QueryTemplates {
    /**
     * The queries.
     */
    private Map<? extends QueryTemplateDescriptor, QueryTemplate> queries;
    /**
     * Constructor.
     */
    public QueryTemplates(Map<? extends QueryTemplateDescriptor, QueryTemplate> queries) {
        super();
        this.queries = queries;
    }

    public String getQuery(QueryTemplateDescriptor d) {
        // NPE or wrong, i. e. distributed query may fail the request here
        QueryTemplate qt = queries.get(d);
        return qt.toSourceQuery();
    }

    public String getQuery(QueryTemplateDescriptor d, int shard) {
        // NPE or wrong, not distributed query may fail the request here
        QueryTemplate qt = queries.get(d);
        return qt.toQuery(shard);
    }

    public void init(int shards) {
        for (Entry<? extends QueryTemplateDescriptor, QueryTemplate> entry : queries.entrySet()) {
            if (entry.getKey().isDistributed()) {
                entry.getValue().init(shards);
            }
        }
    }
}
