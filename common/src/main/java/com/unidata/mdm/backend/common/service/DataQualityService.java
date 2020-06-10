package com.unidata.mdm.backend.common.service;

import com.unidata.mdm.backend.common.context.DataQualityContext;

/**
 * Data quality service public interface.
 * @author ilya.bykov
 *
 */
public interface DataQualityService {
    /**
     * Apply rules.
     *
     * @param ctx the context
     */
    void apply(DataQualityContext ctx);
}
