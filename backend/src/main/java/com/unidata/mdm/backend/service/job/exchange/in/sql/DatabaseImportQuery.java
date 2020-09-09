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

import java.util.Date;
import java.util.List;
import javax.annotation.Nonnull;

import com.unidata.mdm.backend.dao.util.DatabaseVendor;
import com.unidata.mdm.backend.exchange.def.ExchangeRelation;
import com.unidata.mdm.backend.exchange.def.db.DbExchangeEntity;

/**
 * @author Alexey Tsarapkin
 */
public interface DatabaseImportQuery {
    /**
     * Gets SQL for a entity.
     *
     * @param entity the definition
     * @param offset the offset
     * @param limit the limit
     * @param databaseVendor database vendor
     * @return SQL string
     */
    String getSql(@Nonnull DbExchangeEntity entity, long offset, long limit, DatabaseVendor databaseVendor, Date previousSuccessStartDate);
    
    /**
     * Gets SQL for a relation.
     *
     * @param relation the definition
     * @param offset the offset
     * @param limit the limit
     * @param databaseVendor database vendor
     * @return SQL string
     */
    String getSql(@Nonnull ExchangeRelation relation, long offset, long limit, DatabaseVendor databaseVendor, Date previousSuccessStartDate);

    /**
     * @param tables - affected tables
     * @param joins  - applied joins
     * @return SQL request which contains from path of SQL query
     */
    String getFromSql(@Nonnull List<String> tables, @Nonnull List<String> joins, DatabaseVendor databaseVendor, Date previousSuccessStartDate);
}
