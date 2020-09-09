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

/**
 *
 */
package com.unidata.mdm.backend.service.data.driver;

import java.util.List;

import com.unidata.mdm.backend.common.data.CalculableHolder;
import com.unidata.mdm.backend.common.types.TimeIntervalContributorInfo;

/**
 * @author mikhail
 * Time line support.
 */
public class TimeIntervalCompositionDriver extends EtalonCompositionDriverBase<TimeIntervalContributorInfo> {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasActiveBVR(List<CalculableHolder<TimeIntervalContributorInfo>> calculables) {
        return super.composeDefaultBVR(calculables, false) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasActiveBVT(List<CalculableHolder<TimeIntervalContributorInfo>> calculables) {
        // Nothing to do, not applicable
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalContributorInfo composeBVR(
            List<CalculableHolder<TimeIntervalContributorInfo>> calculables,
            boolean includeInactive,
            boolean includeWinners) {
        return super.composeDefaultBVR(calculables, includeInactive);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeIntervalContributorInfo composeBVT(
            List<CalculableHolder<TimeIntervalContributorInfo>> calculables,
            boolean includeInactive,
            boolean includeWinners) {
        // Nothing to do, not applicable
        return null;
    }
}
