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

package org.unidata.mdm.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @author Mikhail Mikhailov
 * Utility methods related to period id generation.
 */
public class PeriodIdUtils {
    /**
     * Positive infinity and max period id.
     */
    public static final long TIMELINE_POSITIVE_INFINITY = 9223372036825200000L;
    /**
     * Negative infinity.
     */
    public static final long TIMELINE_NEGATIVE_INFINITY = -9223372036832400000L;
    /**
     * Practical lower bound, used instead of null in some places.
     */
    public static final Date TIMELINE_LOWER_BOUND = new Date(TIMELINE_NEGATIVE_INFINITY);
    /**
     * Practical upper bound, used instead of null in some places.
     */
    public static final Date TIMELINE_UPPER_BOUND = new Date(TIMELINE_POSITIVE_INFINITY);
    /**
     * Separator for period id fields (hyphen).
     */
    private static final String PERIOD_ID_SEPARATOR = "-";
    /**
     * Constructor.
     */
    private PeriodIdUtils() {
        super();
    }

    /**
     * Ids for children objects.
     *
     * @param parts prefix parts (etalon id, classifier/relation name etc.)
     * @return id string
     */
    public static String childPeriodId(String... parts) {
        return StringUtils.join(parts, PERIOD_ID_SEPARATOR);
    }

    /**
     * Ids for children objects.
     *
     * @param val   period id value
     * @param parts prefix parts (etalon id, classifier/relation name etc.)
     * @return id string
     */
    public static String childPeriodId(long val, String... parts) {

        StringBuilder buf = new StringBuilder()
                .append(StringUtils.join(parts, PERIOD_ID_SEPARATOR))
                .append(PERIOD_ID_SEPARATOR)
                .append(periodIdValToString(val));

        return buf.toString();
    }

    /**
     * Returns period id as string.
     *
     * @param val period id value
     * @return
     */
    public static String periodIdValToString(long val) {

        String valString = Long.toUnsignedString(val);
        StringBuilder sb = new StringBuilder().append(Long.signum(val) == -1 ? "0" : "1");
        for (int i = valString.length(); i < 19; i++) {
            sb.append("0");
        }

        return sb
                .append(valString)
                .toString();
    }
    /**
     * Ensures valid max period id for null dates and returns ts millis.
     *
     * @param d the ts
     * @return millis
     */
    public static long ensureDateValue(Date d) {
        return Objects.isNull(d) ? TIMELINE_POSITIVE_INFINITY : d.getTime();
    }
    /**
     * Returns period id as string.
     *
     * @param d the date to turn into period id
     * @return period id as string
     */
    public static String periodIdFromDate(Date d) {
        return periodIdValToString(ensureDateValue(d));
    }
}
