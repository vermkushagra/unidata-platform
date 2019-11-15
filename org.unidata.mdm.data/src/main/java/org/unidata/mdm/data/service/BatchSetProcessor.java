package org.unidata.mdm.data.service;

import org.unidata.mdm.data.type.apply.batch.BatchSetAccumulator;

/**
 * @author Mikhail Mikhailov
 * Basic batch set processor.
 */
public interface BatchSetProcessor {
    /**
     * Apply updates, collected by this accumulator.
     * @param bsa accumulator to use
     */
    void apply(BatchSetAccumulator<?> bsa);
}
