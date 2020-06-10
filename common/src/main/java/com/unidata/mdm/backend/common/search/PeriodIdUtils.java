package com.unidata.mdm.backend.common.search;

import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Mikhail Mikhailov
 * Utility methods related to period id generation.
 */
public class PeriodIdUtils {
    /**
     * Same as in ValidityPeriodUtils.
     */
    private static final long TIMELINE_MAX_PERIOD_ID = 9223372036825200000L;
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
     * Returns period id as string.
     *
     * @param date date
     * @return
     */
    public static String periodIdFromDate(Date date) {
        return periodIdValToString(Objects.isNull(date) ? TIMELINE_MAX_PERIOD_ID : date.getTime());
    }

}
