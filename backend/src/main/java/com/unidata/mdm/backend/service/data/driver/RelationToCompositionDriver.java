/**
 *
 */
package com.unidata.mdm.backend.service.data.driver;

import java.util.List;

import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * BVT composition driver.
 * @author Mikhail Mikhailov
 */
public class RelationToCompositionDriver extends EtalonCompositionDriverBase<DataRecord> {
    /**
     * Constructor.
     */
    public RelationToCompositionDriver() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasActiveBVR(List<CalculableHolder<DataRecord>> calculables) {
        return super.composeDefaultBVR(calculables, false) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataRecord composeBVR(List<CalculableHolder<DataRecord>> versions, boolean includeInactive, boolean includeWinners) {
    	return super.composeDefaultBVR(versions, includeInactive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasActiveBVT(List<CalculableHolder<DataRecord>> calculables) {
        // Not applicable yet
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataRecord composeBVT(List<CalculableHolder<DataRecord>> calculables, boolean includeInactive, boolean includeWinners) {
        // Not applicable yet
        return null;
    }
}
