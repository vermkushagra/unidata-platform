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

package com.unidata.mdm.backend.service.bulk;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.BulkOperationRequestContext;
import com.unidata.mdm.backend.common.context.ComplexSearchRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.service.SearchService;

/**
 * General bulk methods
 */
public abstract class AbstractBulkOperation implements BulkOperation {

    /**
     * Search service
     */
    @Autowired(required = false)
    private SearchService searchService;

    /**
     * @param ctx bulk context
     * @return list of etalon ids, which should be processed
     */
    protected final List<String> getEtalonIds(BulkOperationRequestContext ctx) {
        if (!CollectionUtils.isEmpty(ctx.getApplyBySelectedIds())) {
            return ctx.getApplyBySelectedIds();
        } else if (ctx.getApplyBySearchContext() == null) {
            return Collections.emptyList();
        } else {
            ComplexSearchRequestContext requestContext = ctx.getApplyBySearchContext();
            return searchService.search(requestContext)
                                .get(requestContext.getMainRequest())
                                .getHits()
                                .stream()
                                .map(SearchResultHitDTO::getId)
                                .distinct()
                                .collect(Collectors.toList());
        }
    }
}
