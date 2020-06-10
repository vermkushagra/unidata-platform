package com.unidata.mdm.backend.common.dq;

import com.unidata.mdm.backend.common.data.ModificationBox;

/**
 * @author Mikhail Mikhailov
 * DQ Execution mode.
 */
public enum DataQualityExecutionMode {
    /**
     * Use origin record.
     * Perform all modifications on it.
     * Cache to {@link ModificationBox#originState(com.unidata.mdm.backend.common.types.DataRecord)}.
     */
    MODE_ORIGIN,
    /**
     * Use calculated etalon record.
     * Perform all modifications on it.
     * Cache to {@link ModificationBox#etalonState(com.unidata.mdm.backend.common.types.DataRecord)}.
     */
    MODE_ETALON,
    /**
     * Use origin record regradless of the settings of the rule, being executed.
     * Perform all modifications on it.
     * Cache to {@link ModificationBox#originState(com.unidata.mdm.backend.common.types.DataRecord)}.
     */
    MODE_ONLINE,
    /**
     * Use combo of {@link DataQualityExecutionMode#MODE_ORIGIN} and {@link DataQualityExecutionMode#MODE_ETALON}.
     * Perform all modifications on origin record.
     * Cache to {@link ModificationBox#originState(com.unidata.mdm.backend.common.types.DataRecord)}.
     */
    MODE_PREVIEW
}
