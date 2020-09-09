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

package com.unidata.mdm.backend.service.job.exchange.in.sql;

import com.unidata.mdm.backend.dao.util.DatabaseVendor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Alexey Tsarapkin
 *
 * FOR DB2 ONLY =>
 *
 *  __Query with [LimitPredicat] and [Orderby] for example:
 *
 *    select null as VALID_FROM,
 *           null as VALID_TO,
 *           t.id  as KEY,
 *           t.id as id,
 *           '__LAST_FIELD__' as a_2b93720662f35a14b69844a008a5
 *    from UD_TEST t where [LimitPredicat >] [offset] and [Orderby]<=[limit]
 *
 *    <= DB2 END
 *
 * __Query for example:
 *
 *  select null as VALID_FROM,
 *         null as VALID_TO,
 *         t.id  as KEY,
 *         t.id as id,
 *         '__LAST_FIELD__' as a_2b93720662f35a14b69844a008a5
 *  from UD_TEST t
 *  order by t.id asc
 *  offset 4 rows fetch next 2 rows only
 *
 * __Predicate for example:
 *
 *  select null as VALID_FROM,
 *         null as VALID_TO,
 *         t.id  as KEY,
 *         t.id as id,
 *         '__LAST_FIELD__' as a_3a5faabe7aad2cf016e5fd4b002c
 *  from UD_TEST t
 *  where t.id in (select distinct t.id from UD_TEST t order by t.id asc  offset 8 rows fetch next 2 rows only )
 */
public class DB2ImportQuery extends DatabaseImportQueryImpl {

    @Override
    protected void addOrderByAndOffset(StringBuilder sqlb, boolean hasJoins, String orderByColumn,
                                       String limitPredicate, long offset, long limit, DatabaseVendor vendor) {

        //FOR DB2 ONLY
        if (StringUtils.isNotBlank(limitPredicate) && StringUtils.isNotBlank(orderByColumn)) {
            if ( offset > 0) {
                sqlb.append(hasJoins ? "and " : "where ")
                        .append(limitPredicate)
                        .append(" ")
                        .append(offset)
                        .append(" ")
                        .append(" and ")
                        .append(orderByColumn)
                        .append(" <= ")
                        .append(offset + limit);

                return;
            }
        }//END DB2

        if (limitPredicate != null && offset > 0) {
            sqlb.append(hasJoins ? "and " : "where ")
                    .append(limitPredicate)
                    .append(" ")
                    .append(offset)
                    .append(" ");
        }

        addOrderBy(sqlb, orderByColumn);

        sqlb.append(" offset ")
                .append(limitPredicate == null ? offset : 0)
                .append(" rows ");

        if (limit > 0) {
            sqlb.append("fetch next ")
                    .append(limit)
                    .append(" rows only ");
        }
    }

}
