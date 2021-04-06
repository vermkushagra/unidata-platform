package com.unidata.mdm.backend.common.service;

import com.unidata.mdm.backend.common.context.DQContext;
import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * Data quality service public interface.
 * @author ilya.bykov
 *
 */
public interface DataQualityService {
	 /**
     * Apply rules.
     *
     * @param context
     *            the context
     */
    void applyRules(DQContext<DataRecord> context);

}
