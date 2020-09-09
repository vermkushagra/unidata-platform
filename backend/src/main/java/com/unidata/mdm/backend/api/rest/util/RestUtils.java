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

package com.unidata.mdm.backend.api.rest.util;

import java.util.Date;
import java.util.List;

import javax.ws.rs.core.PathSegment;

import com.unidata.mdm.backend.util.ValidityPeriodUtils;

/**
 * @author Michael Yashin. Created on 02.04.2015.
 */
public class RestUtils {

    /**
     * Constructor.
     */
    private RestUtils() {
        super();
    }

    /**
     * Extract starting time stamp from list of path segments.
     * @param timestamps list of path segments
     * @return date
     */
    public static Date extractStart(List<PathSegment> timestamps){

        if (timestamps == null || timestamps.size() != 2) {
            // TODO remove this!
            throw new RuntimeException("Timestamps aren't even!");
        }

        return "null".equals(timestamps.get(0).getPath()) ? null : ValidityPeriodUtils.parse(timestamps.get(0).getPath());
    }

    /**
     * Extract ending time stamp from list of path segments.
     * @param timestamps list of path segments
     * @return date
     */
    public static Date extractEnd(List<PathSegment> timestamps){

        if (timestamps == null || timestamps.size() != 2) {
            // TODO remove this!
            throw new RuntimeException("Timestamps aren't even!");
        }

        return "null".equals(timestamps.get(1).getPath()) ? null : ValidityPeriodUtils.parse(timestamps.get(1).getPath());
    }
}
