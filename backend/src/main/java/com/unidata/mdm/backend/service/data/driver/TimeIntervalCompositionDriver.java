/**
 *
 */
package com.unidata.mdm.backend.service.data.driver;

import java.util.List;

import com.unidata.mdm.backend.common.dto.ContributorDTO;

/**
 * @author mikhail
 * Time line support.
 */
public class TimeIntervalCompositionDriver extends EtalonCompositionDriverBase<ContributorDTO> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasActiveBVR(List<CalculableHolder<ContributorDTO>> calculables) {
        return super.composeDefaultBVR(calculables, false) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasActiveBVT(List<CalculableHolder<ContributorDTO>> calculables) {
        // Nothing to do, not applicable
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContributorDTO composeBVR(List<CalculableHolder<ContributorDTO>> calculables, boolean includeInactive, boolean includeWinners) {
        // Nothing to do, not applicable
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContributorDTO composeBVT(List<CalculableHolder<ContributorDTO>> calculables, boolean includeInactive, boolean includeWinners) {
        // Nothing to do, not applicable
        return null;
    }

}
