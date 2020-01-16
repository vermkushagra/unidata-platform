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

package org.unidata.mdm.data.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.unidata.mdm.search.context.IndexRequestContext;
import org.unidata.mdm.search.service.SearchService;

/**
 * @author Mikhail Mikhailov
 * Common operations
 */
public abstract class AbstractChangeSetProcessor {
    /**
     * Search service.
     */
    @Autowired
    protected SearchService searchService;
    /**
     * Preclustering service.
     */
// @Modules Moved to commercial part.
//    @Autowired
//    protected PreclusteringService preclusteringService;
    /**
     * Constructor.
     */
    protected AbstractChangeSetProcessor() {
        super();
    }
    /**
     * Applies index changes.
     * @param ctxts the contexts
     * @param refresh refresh index immediately or not
     */
    protected void applyIndexUpdates(List<IndexRequestContext> ctxts, boolean refresh) {
// @Modules Moved to commercial part.
//        preclusteringService.putClusters(ctxts);
        searchService.process(ctxts, refresh);
    }
    /**
     * Applies index changes.
     * @param ctxts the contexts
     */
    protected void applyIndexUpdate(IndexRequestContext ctx) {
// @Modules Moved to commercial part.
//        preclusteringService.putClusters(Collections.singletonList(ctx));
        searchService.process(ctx);
    }
}
