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
