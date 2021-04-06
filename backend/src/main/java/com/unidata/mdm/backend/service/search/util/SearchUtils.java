/**
 *
 */
package com.unidata.mdm.backend.service.search.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.function.BiFunction;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.dto.AggregationResultDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.search.types.Aggregatable;

/**
 * @author Mikhail Mikhailov
 *         Various constants and utilities.
 */
public class SearchUtils {

    /**
     * Empty returned field array
     */
    private static final String[] EMPTY_FIELD_ARRAY = new String[0];

    /**
     * Array contain only etalon field
     */
    private static final String[] SINGLE_ETALON_FIELD_ARRAY = new String[]{RecordHeaderField.FIELD_ETALON_ID.getField()};
    /**
     * Max period id.
     */
    public static final long ES_TIMELINE_PERIOD_ID_UPPER_BOUND = 9223372036825200000L;
    /**
     * Min from.
     */
    public static final String ES_MIN_FROM = "1900-01-01T00:00:00.000Z";
    /**
     * Max to.
     */
    public static final String ES_MAX_TO = "9999-12-31T23:59:59.999Z";
    /**
     * Elastic min date
     */
    public static final Date ES_MIN_DATE = new DateTime(ES_MIN_FROM).toDate();

    /**
     * Elastic max date
     */
    public static final Date ES_MAX_DATE = new DateTime(ES_MAX_TO).toDate();

    private static final FastDateFormat DEFAULT_TIMESTAMP_WITH_OFFSET
            = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS", TimeZone.getTimeZone("UTC"));

    /**
     * Date format without milliseconds.
     * Frontend specific.
     */
    public static final FastDateFormat DEFAULT_TIMESTAMP_SYSTEM_DEFAULT
            = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS");

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
     * Separator for period id fields among other things
     */
    public static final String HYPHEN = "-";
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
     * Default fields param, if nothing is set ('_all').
     */
    @SuppressWarnings("serial")
    public static final List<String> DEFAULT_FIELDS_VALUE = new ArrayList<String>() {{
        add(ALL_FIELD);
    }};
    /**
     * Default empty facets list.
     */
    public static final List<String> DEFAULT_FACETS_VALUE = new ArrayList<>();
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
     * Returns {@link Long#MIN_VALUE} if the input is null.
     *
     * @param from the from date
     * @return value
     */
    public static Long ensureMinDate(Date from) {
        return from == null ? Long.MIN_VALUE : from.getTime();
    }

    /**
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

    /**
     * Extract return fields.
     *
     * @param ctx the context
     * @return fields
     */
    public static String[] extractReturnFields(final SearchRequestContext ctx) {

        List<String> fields = ctx.getReturnFields() != null ? ctx.getReturnFields() : ctx.getSearchFields();

        if (ctx.isSource() || fields == null || fields.isEmpty()) {
            return ctx.isSkipEtalonId() ? EMPTY_FIELD_ARRAY : SINGLE_ETALON_FIELD_ARRAY;
        }

        Set<String> result = new TreeSet<>(fields);
        if (!ctx.isSkipEtalonId()) {
            result.add(RecordHeaderField.FIELD_ETALON_ID.getField());
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
                throw new SystemRuntimeException("Incorrect date format found, unable to parse date string!",
                        ExceptionId.EX_DATA_CANNOT_PARSE_DATE, dateAsString);
            }
        }
        return result;
    }

    /**
     * Processes aggregations.
     *
     * @param aggs      aggregations
     * @param path      path to process
     * @param processor the processor
     * @return collection of aggregated types
     */
    public static <V extends Aggregatable> List<V> processAggregations(
            Collection<AggregationResultDTO> aggs, String[] path, BiFunction<AggregationResultDTO, V, V> processor) {

        if (CollectionUtils.isEmpty(aggs) || ArrayUtils.isEmpty(path)) {
            return Collections.emptyList();
        }

        List<V> result = new ArrayList<>();
        int depth = 0;
        for (AggregationResultDTO aggregate : aggs) {
            if (path[depth].equals(aggregate.getAggregationName())) {
                processAggregation(result, aggregate, path, depth, processor, null);
                // One path segment processed on one level.
                break;
            }
        }

        return result;
    }

    /**
     * Processes aggregation recursively, applying function.
     *
     * @param result    storage to collect values
     * @param aggregate the aggregate
     * @param path      the path
     * @param depth     current depth
     * @param processor processor
     * @param current   current result
     * @return aggregated type
     */
    private static <V extends Aggregatable> V processAggregation(
            List<V> result, AggregationResultDTO aggregate, String[] path, int depth, BiFunction<AggregationResultDTO, V, V> processor, V current) {

        // 1. Check this depth level for being the one, creating objects
        boolean wasNull = Objects.isNull(current);

        // 2. Apply current
        V update = processor.apply(aggregate, current);

        // 3. Check state and go into recursion for sub aggregations
        boolean unwind = aggregate.hasSubAggregations() && (Objects.isNull(update) || !update.stop());
        if (unwind) {

            int pos = (depth + 1);
            if (aggregate.getAggregationType().isMultiBucket()) {
                Map<String, Map<String, AggregationResultDTO>> buckets = aggregate.getSubAggregations();
                if (MapUtils.isNotEmpty(buckets)) {
                    for (Entry<String, Map<String, AggregationResultDTO>> bucket : buckets.entrySet()) {
                        AggregationResultDTO subAggregate = bucket.getValue().get(path[pos]);
                        if (Objects.nonNull(subAggregate)) {
                            update = processAggregation(result, subAggregate, path, pos, processor, update);
                        }
                    }
                }
            } else {
                Map<String, AggregationResultDTO> bucket = aggregate.getSubAggregations().get(path[pos]);
                if (MapUtils.isNotEmpty(bucket)) {
                    AggregationResultDTO subAggregate = bucket.get(path[pos]);
                    if (Objects.nonNull(subAggregate)) {
                        update = processAggregation(result, subAggregate, path, pos, processor, update);
                    }
                }
            }
        }

        // 4. Collect, if this is the level, creating objects
        if (wasNull && Objects.nonNull(update)) {
            if (!update.discard()) {
                result.add(update);
            }
            return null;
        }

        return update;
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
                throw new SystemRuntimeException("Incorrect date format found, unable to parse date string!",
                        ExceptionId.EX_DATA_CANNOT_PARSE_DATE, dateAsString);
            }
        }
        return result == null ? null : DEFAULT_TIMESTAMP_SYSTEM_DEFAULT.format(result);
    }

    /**
     * Ids for children objects.
     *
     * @param parts prefix parts (etalon id, classifier/relation name etc.)
     * @return id string
     */
    public static String childPeriodId(String... parts) {
        return StringUtils.join(parts, HYPHEN);
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
                .append(StringUtils.join(parts, HYPHEN))
                .append(HYPHEN)
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
     * @param val period id value
     * @return
     */
    public static String periodIdFromDate(Date d) {
        return periodIdValToString(Objects.isNull(d) ? ES_TIMELINE_PERIOD_ID_UPPER_BOUND : d.getTime());
    }
}
