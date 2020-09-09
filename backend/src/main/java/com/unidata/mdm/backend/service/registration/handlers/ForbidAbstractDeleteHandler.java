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

package com.unidata.mdm.backend.service.registration.handlers;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.service.registration.keys.UniqueRegistryKey;

public abstract class ForbidAbstractDeleteHandler<D extends UniqueRegistryKey, L extends UniqueRegistryKey> implements DeleteHandler<D, L> {
    @Override
    public void onDelete(D removingKey, L linkingKey) {
        //temporary disabled
//        throw new BusinessException("Element can't be removed", ExceptionId.EX_SYSTEM_REMOVING_FORBID_HAS_LINKS,
//                (getRemovedEntityType().getDescription() + ":" + removingKey.toString()),
//                (getLinkedEntityType().getDescription()) + ":" + linkingKey.toString());
    }
}
