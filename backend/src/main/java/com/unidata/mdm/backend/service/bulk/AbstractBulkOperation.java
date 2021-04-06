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
