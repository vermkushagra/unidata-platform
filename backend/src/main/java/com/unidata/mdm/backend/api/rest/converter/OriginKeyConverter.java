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

package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.data.OriginKeyRO;
import com.unidata.mdm.backend.common.keys.OriginKey;

/**
 * @author Mikhail Mikhailov
 *
 */
public class OriginKeyConverter {

    /**
     * Constructor.
     */
    private OriginKeyConverter() {
        super();
    }

    public static OriginKeyRO to(OriginKey key) {

        if (Objects.isNull(key)) {
            return null;
        }

        OriginKeyRO result = new OriginKeyRO();
        result.setEnrichment(key.isEnriched());
        result.setEntityName(key.getEntityName());
        result.setExternalId(key.getExternalId());
        result.setSourceSystem(key.getSourceSystem());
        result.setId(key.getId());

        return result;
    }

    public static List<OriginKeyRO> to(List<OriginKey> keys) {

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptyList();
        }

        List<OriginKeyRO> result = new ArrayList<>(keys.size());
        for (OriginKey key : keys) {
            OriginKeyRO ro = to(key);
            if (Objects.isNull(ro)) {
                continue;
            }

            result.add(ro);
        }

        return result;
    }
}
