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
