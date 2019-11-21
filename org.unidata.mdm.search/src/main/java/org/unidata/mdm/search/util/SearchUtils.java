/**
 *
 */
package org.unidata.mdm.search.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.search.context.SearchRequestContext;
import org.unidata.mdm.search.dto.SearchResultHitDTO;
import org.unidata.mdm.search.dto.SearchResultHitFieldDTO;
import org.unidata.mdm.search.exception.SearchExceptionIds;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.util.ConvertUtils;

/**
 * @author Mikhail Mikhailov
 *         Various constants and utilities.
 */
public class SearchUtils {
    /**
     * TODO: Hide details! Visibility problem.
     */
    private static final String ETALON_ID_FIELD = "$etalon_id";
    /**
     * Array contain only etalon field
     * TODO: Hide details! Visibility problem.
     */
    private static final String[] SINGLE_ETALON_FIELD_ARRAY = new String[] { ETALON_ID_FIELD };
    /**
     * Min from.
     */
    public static final String ES_MIN_FROM = "-292275054-01-01T00:00:00.000Z";
    /**
     * Max to.
     */
    public static final String ES_MAX_TO = "292278993-12-31T23:59:59.999Z";
    /**
     * Elastic min date
     */
    private static final Date ES_MIN_DATE = new DateTime(ES_MIN_FROM).toDate();
    /**
     * Elastic max date
     */
    private static final Date ES_MAX_DATE = new DateTime(ES_MAX_TO).toDate();

    private static final FastDateFormat DEFAULT_TIMESTAMP_WITH_OFFSET
            = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS", TimeZone.getTimeZone("UTC"));

    /**
     * Date format without milliseconds.
     * Frontend specific.
     * usage DateTimeFormatter DEFAULT_TIMESTAMP_SYSTEM_DEFAULT_NEW
     */
    @Deprecated
    public static final FastDateFormat DEFAULT_TIMESTAMP_SYSTEM_DEFAULT
            = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * Date format without milliseconds.
     * Frontend specific.
     */
    public static final DateTimeFormatter DEFAULT_TIMESTAMP_SYSTEM_DEFAULT_NEW
            = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    /**
     * File name capable format.
     */
    private static final FastDateFormat DEFAULT_INDEX_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("UTC"));
    /**
     * Default ES port.
     */
    public static final String DEFAULT_PORT_VALUE = "9200";
    /**
     * Default number of shards.
     */
    public static final String DEFAULT_NUMBER_OF_SHARDS = "1";
    /**
     * Default number of replicas.
     */
    public static final String DEFAULT_NUMBER_OF_REPLICAS = "0";
    /**
     * Default number of fields per index.
     */
    public static final String DEFAULT_NUMBER_OF_FIELDS = "1000";
    /**
     * Cluster name setting.
     */
    public static final String ES_CLUSTER_NAME_SETTING = "cluster.name";
    /**
     * Number of shards configuration property.
     */
    public static final String ES_NUMBER_OF_SHARDS_SETTING = "number_of_shards";
    /**
     * Number of replicas configuration property.
     */
    public static final String ES_NUMBER_OF_REPLICAS_SETTING = "number_of_replicas";
    /**
     * Number of fields per index.
     */
    public static final String ES_LIMIT_OF_TOTAL_FIELDS = "index.mapping.total_fields.limit";
    /**
     * Comma separator for various fields.
     */
    public static final String COMMA_SEPARATOR = ",";
    /**
     * Colon separator for various fields.
     */
    public static final String COLON_SEPARATOR = ":";
    /**
     * Pipe separator for various fields.
     */
    public static final String PIPE_SEPARATOR = "|";
    /**
     * Fields delimiter.
     */
    public static final String FIELDS_DELIMITER = "\\|";
    /**
     * Fields delimiter.
     */
    public static final String ALL_FIELD = "_all";
    /**
     * Raw, not analyzed, field value.
     */
    public static final String NAN_FIELD = "$nan";
    /**
     * Morphologically analyzed, field value (string).
     */
    public static final String MORPH_FIELD = "$morph";
    /**
     * Separator for complex type (example: field.$nan)
     */
    public static final String DOT = ".";
    /**
     * Prefix for all system fields
     */
    public static final String DOLLAR = "$";
    /**
     * Default unidata analyzer name.
     */
    public static final String DEFAULT_STRING_ANALYZER_NAME = "unidata_default_analyzer";
    /**
     * Default morphological analyzer name.
     */
    public static final String MORPH_STRING_ANALYZER_NAME = "unidata_morph_analyzer";

    public static final String LOWERCASE_STRING_NORMALIZER_NAME = "unidata_lowercase_normalizer";
    /**
     * Standard ES analyzer name.
     */
    public static final String STANDARD_STRING_ANALYZER_NAME = "standard";
    /**
     * No analyzer value.
     */
    public static final String NONE_STRING_ANALYZER_NAME = "not_analyzed";
    /**
     * No indexed name.
     */
    public static final String NO_INDEXED_NAME = "no";
    /**
     * Default max expansions value for phrase_prefix queries.
     */
    public static final int DEFAULT_MAX_EXPANSIONS_VALUE = 50;
    /**
     * Default slop value for phrase_prefix queries.
     */
    public static final int DEFAULT_SLOP_VALUE = 10;
    /**
     * parent field
     */
    public static final String PARENT_FIELD = "_parent";
    /**
     * parent field
     */
    public static final String ID_FIELD = "_id";

    /**
     * Default fields param, if nothing is set ('_all').
     */
    private static final List<String> DEFAULT_FIELDS_VALUE = Arrays.asList(ALL_FIELD);
    /**
     * Default empty facets list.
     */
    private static final List<String> DEFAULT_FACETS_VALUE = new ArrayList<>();
    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchUtils.class);

    /**
     * No instance of this class allowed.
     */
    private SearchUtils() {
        super();
    }

    /**
     * Colaesce from date.
     * @param ts the time stamp
     * @return timestamp
     */
    public static Date coalesceFrom(Date ts) {
        return ts != null ? ts : ES_MIN_DATE;
    }
    /**
     * Colaesce to date.
     * @param ts the time stamp
     * @return timestamp
     */
    public static Date coalesceTo(Date ts) {
        return ts != null ? ts : ES_MAX_DATE;
    }

    /**
     * FIXME Looks like unused! Check and remove.
     * Returns {@link Long#MIN_VALUE} if the input is null.
     *
     * @param from the from date
     * @return value
     */
    public static Long ensureMinDate(Date from) {
        return from == null ? Long.MIN_VALUE : from.getTime();
    }

    /**
     * FIXME Looks like unused! Check and remove.
     * Returns {@link Long#MAX_VALUE} if the input is null.
     *
     * @param to the to date
     * @return value
     */
    public static Long ensureMaxDate(Date to) {
        return to == null ? Long.MAX_VALUE : to.getTime();
    }

    public static String parseForIndex(Object date) {
        if (date == null) {
            return null;
        }
        return DEFAULT_INDEX_FORMAT.format(date);
    }

    public static Date parseFromIndex(Object date) {
        if (date == null) {
            return null;
        }

        try {
            return DEFAULT_INDEX_FORMAT.parse(date.toString());
        } catch (ParseException e) {
            LOGGER.error("Can't parse date", e);
            return null;
        }
    }

    public static boolean isSystemField(String fieldName) {
        return fieldName == null || fieldName.startsWith(DOLLAR);
    }
    /**
     * Extract return fields.
     *
     * @param ctx the context
     * @return fields
     */
    public static String[] extractReturnFields(final SearchRequestContext ctx) {

        List<String> fields = ctx.getReturnFields() != null ? ctx.getReturnFields() : ctx.getSearchFields();

        if (ctx.isSource() || fields == null || fields.isEmpty()) {
            return ctx.isSkipEtalonId() ? ArrayUtils.EMPTY_STRING_ARRAY : SINGLE_ETALON_FIELD_ARRAY;
        }

        Set<String> result = new TreeSet<>(fields);
        if (!ctx.isSkipEtalonId()) {
            result.add(ETALON_ID_FIELD);
        }
        return result.toArray(new String[result.size()]);
    }

    /**
     * Creates a search client instance.
     *
     * @return a new instance or null
     */
    public static Client initializeSearchClient(String searchCluster, String searchNodes) {

        if (StringUtils.isBlank(searchCluster)) {
            LOGGER.error("Cannot create search client instance. Search cluster name is blank.");
            return null;
        }

        List<InetSocketTransportAddress> addresses = new ArrayList<>();
        if (!StringUtils.isBlank(searchNodes)) {
            String[] tokens = searchNodes.trim().split(COMMA_SEPARATOR);
            for (String token : tokens) {
                String[] pair = token.split(COLON_SEPARATOR);
                String host = pair.length > 0 ? pair[0] : null;
                String port = pair.length > 1 ? pair[1] : DEFAULT_PORT_VALUE;
                try {
                    addresses.add(new InetSocketTransportAddress(InetAddress.getByName(host), Integer.valueOf(port)));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }

        if (addresses.isEmpty()) {
            LOGGER.error("Cannot create search client instance. Nodes are invalid or absent.");
            return null;
        }

        Settings.Builder b = Settings.builder()
                .put(SearchUtils.ES_CLUSTER_NAME_SETTING, searchCluster.trim());

        TransportClient searchClient = new PreBuiltTransportClient(b.build());
        for (InetSocketTransportAddress address : addresses) {
            searchClient.addTransportAddress(address);
        }

        return searchClient;
    }

    /**
     * Gets the fields as list, setting _all, if nothing is specified.
     *
     * @param fields fields, delimited by '|' character
     * @return list of fields
     */
    public static List<String> getFields(String fields) {
        if (StringUtils.isBlank(fields)) {
            return SearchUtils.DEFAULT_FIELDS_VALUE;
        }

        return Arrays.asList(fields.split(SearchUtils.FIELDS_DELIMITER));
    }

    /**
     * Gets requested facets as list.
     *
     * @param facets the facets,  delimited by '|' character
     * @return list of facets or null
     */
    public static List<String> getFacets(String facets) {
        if (StringUtils.isBlank(facets)) {
            return SearchUtils.DEFAULT_FACETS_VALUE;
        }

        return Arrays.asList(facets.split(SearchUtils.FIELDS_DELIMITER));
    }

    /**
     * Parses string representation of date according to date format from
     * {@see DEFAULT_TIMESTAMP_NO_OFFSET}.
     *
     * @param dateAsString string representation of date.
     * @return parsed date.
     */
    public static Date parse(String dateAsString) {

        Date result;
        if (ES_MIN_FROM.equals(dateAsString) || ES_MAX_TO.equals(dateAsString)) {
            result = null;
        } else {
            try {
                result = dateAsString != null ? DEFAULT_TIMESTAMP_WITH_OFFSET.parse(dateAsString) : null;
            } catch (ParseException e) {
                throwCannotParseDate(dateAsString);
                // Trick the sonar. Won't ever happen.
                result = null;
            }
        }

        return result;
    }

    /**
     * Can be used after hit modifier to extract correct system date
     * TODO refactoring remove this ulgy method
     * @param dateAsString
     * @return
     */
    public static Date parseWithoutOffset(String dateAsString) {

        Date result;
        if (ES_MIN_FROM.equals(dateAsString) || ES_MAX_TO.equals(dateAsString)) {
            result = null;
        } else {
            try {
                result = dateAsString != null ? DEFAULT_TIMESTAMP_SYSTEM_DEFAULT.parse(dateAsString) : null;
            } catch (ParseException e) {
                throwCannotParseDate(dateAsString);
                // Trick the sonar. Won't ever happen.
                result = null;
            }
        }

        return result;
    }

    /**
     * Parses string representation of date according to date format from
     * {@see DEFAULT_TIMESTAMP_NO_OFFSET}.
     *
     * @param dateAsString string representation of date.
     * @return parsed date.
     */
    public static String formatForUI(String dateAsString) {
        Date result;
        if (ES_MIN_FROM.equals(dateAsString) || ES_MAX_TO.equals(dateAsString)) {
            result = null;
        } else {
            try {
                result = dateAsString != null ? DEFAULT_TIMESTAMP_WITH_OFFSET.parse(dateAsString) : null;
            } catch (ParseException e) {
                throwCannotParseDate(dateAsString);
                // Trick the sonar. Won't ever happen.
                result = null;
            }
        }
        return result == null ? null : DEFAULT_TIMESTAMP_SYSTEM_DEFAULT_NEW.format(ConvertUtils.date2LocalDateTime(result));
    }

    @Nullable
    public static Range<Date> getDateRange(SearchResultHitDTO hit, String fieldFrom, String fieldTo) {

        SearchResultHitFieldDTO to = hit.getFieldValue(fieldFrom);
        SearchResultHitFieldDTO from = hit.getFieldValue(fieldTo);

        try {

            Date validTo = to == null || to.isNullField() ? SearchUtils.ES_MAX_DATE : new DateTime(to.getFirstValue().toString()).toDate();
            Date validFrom = from == null || from.isNullField() ? SearchUtils.ES_MIN_DATE : new DateTime(from.getFirstValue().toString()).toDate();
            return Range.between(validFrom, validTo);

        } catch (Exception e) {
            return null;
        }
    }


    public static Date getDateForDisplayAttributes(Date now, Date validFrom, Date validTo) {
        Date result = validFrom;
        if (validFrom != null) {
            if (validTo != null) {
                if (validFrom.getTime() <= now.getTime() && validTo.getTime() >= now.getTime()) {
                    result = now;
                }
            } else if (validFrom.getTime() <= now.getTime()) {
                result = now;
            }
        } else {
            if (validTo != null) {
                if (validTo.getTime() >= now.getTime()) {
                    result = now;
                }
            } else {
                result = now;
            }
        }
        if (result == null) {
            // UN-8625
            result = DateUtils.addDays(validTo, -1);
        }
        return result;
    }

    public static boolean dateInPeriod(Date dateForCheck, Date from, Date to) {
        if (dateForCheck == null) {
            return from == null;
        }
        if (from == null) {
            if (to == null) {
                return true;
            } else {
                return dateForCheck.getTime() <= to.getTime();
            }
        } else {
            if (to == null) {
                return dateForCheck.getTime() >= from.getTime();
            } else {
                return dateForCheck.getTime() <= to.getTime() && dateForCheck.getTime() >= from.getTime();
            }
        }
    }

    private static void throwCannotParseDate(String dateAsString) {
        throw new PlatformFailureException(
                "Incorrect date format found, unable to parse date string!",
                SearchExceptionIds.EX_SEARCH_CANNOT_PARSE_DATE,
                dateAsString);
    }
}
