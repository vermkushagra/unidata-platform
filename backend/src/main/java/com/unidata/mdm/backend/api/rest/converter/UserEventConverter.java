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
package com.unidata.mdm.backend.api.rest.converter;

import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.security.UserEventRO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;

/**
 * @author Mikhail Mikhailov
 *
 */
public class UserEventConverter {

    /**
     * Constructor.
     */
    private UserEventConverter() {
        super();
    }

    /**
     * To REST from system.
     * @param source system
     * @return REST
     */
    public static UserEventRO to(UserEventDTO source) {
        if (source == null) {
            return null;
        }

        UserEventRO target = new UserEventRO();

        target.setId(source.getId());
        target.setBinaryDataId(source.getBinaryDataId());
        target.setCharacterDataId(source.getCharacterDataId());
        target.setContent(source.getContent());
        target.setCreateDate(source.getCreateDate());
        target.setCreatedBy(source.getCreatedBy());
        target.setType(source.getType());

        return target;
    }

    /**
     * To REST from system.
     * @param source system
     * @param target REST
     */
    public static void to (List<UserEventDTO> source, List<UserEventRO> target) {
        if (source == null || source.isEmpty()) {
            return;
        }

        for (UserEventDTO d : source) {
            target.add(to(d));
        }
    }
}
