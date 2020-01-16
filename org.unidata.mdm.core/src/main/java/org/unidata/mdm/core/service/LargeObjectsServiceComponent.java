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

package org.unidata.mdm.core.service;

import org.unidata.mdm.core.context.DeleteLargeObjectRequestContext;
import org.unidata.mdm.core.context.FetchLargeObjectRequestContext;
import org.unidata.mdm.core.context.SaveLargeObjectRequestContext;
import org.unidata.mdm.core.dto.LargeObjectDTO;

/**
 * @author Dmitry Kopin on 26.12.2017.
 */
public interface LargeObjectsServiceComponent {

    /**
     * Gets large object input stream according to context specification.
     * @param ctx the context
     * @return {@link LargeObjectDTO} instance
     */
    LargeObjectDTO fetchLargeObject(FetchLargeObjectRequestContext ctx);

    byte[] fetchLargeObjectByteArray(FetchLargeObjectRequestContext ctx);

    /**
     * Saves large object data.
     * @param ctx the context
     * @return true if successful, false otherwise
     */
    LargeObjectDTO saveLargeObject(SaveLargeObjectRequestContext ctx);

    /**
     * Deletes large object data.
     * @param ctx the context
     * @return true if successful, false otherwise
     */
    boolean deleteLargeObject(DeleteLargeObjectRequestContext ctx);

    /**
     * Check large object exist
     * @param ctx context
     * @return return true if exist, else false
     */
    boolean checkExistLargeObject(FetchLargeObjectRequestContext ctx);
}
