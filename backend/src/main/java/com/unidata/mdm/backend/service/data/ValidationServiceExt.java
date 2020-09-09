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

package com.unidata.mdm.backend.service.data;

import com.google.common.collect.Multimap;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.service.ValidationService;

import java.util.Date;

/**
 * @author Dmitry Kopin on 01.12.2017.
 */
public interface ValidationServiceExt extends ValidationService {


    /**
     * Calculate count of incomming links to the etalon.
     * @param etalonId - etalon id
     * @param asOf - as of date
     * @return count of incomming links to the etalon
     */
    Multimap<AttributeInfoHolder, Object> getMissedLinkedLookupEntities(String etalonId, Date asOf);

    /**
     * Validate relations
     * @param ctx upsert request context
     */
    void checkRelations(UpsertRequestContext ctx);
}
