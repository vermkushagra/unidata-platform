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
