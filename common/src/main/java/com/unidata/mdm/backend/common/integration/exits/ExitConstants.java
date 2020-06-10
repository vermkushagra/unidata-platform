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

package com.unidata.mdm.backend.common.integration.exits;

/**
 * @author Mikhail Mikhailov
 * Some useful constants.
 */
public enum ExitConstants {
    /**
     * IN context parameter Current etalon ID.
     */
    IN_UPSERT_CURRENT_RECORD_ETALON_ID,
    /**
     * IN context parameter Current origin ID.
     */
    IN_UPSERT_CURRENT_RECORD_ORIGIN_ID,
    /**
     * IN context parameter for valid from field, modified by user exit.
     */
    IN_UPSERT_CURRENT_RECORD_VALID_FROM,
    /**
     * IN context parameter for valid to field, modified by user exit.
     */
    IN_UPSERT_CURRENT_RECORD_VALID_TO,
    /**
     * OUT context parameter for valid from field, modified by user exit.
     */
    OUT_UPSERT_CURRENT_RECORD_VALID_FROM,
    /**
     * OUT context parameter for valid to field, modified by user exit.
     */
    OUT_UPSERT_CURRENT_RECORD_VALID_TO,
    /**
     * OUT context parameter for created by field, modified by user exit.
     */
    OUT_UPSERT_CURRENT_RECORD_CREATED_BY,
    /**
     * OUT context parameter for status field, modified by user exit.
     */
    OUT_UPSERT_CURRENT_RECORD_STATUS,
    /**
     * OUT record was modified mark (will be saved as PRISTINE version).
     */
    OUT_UPSERT_CURRENT_RECORD_IS_MODIFIED;
}
