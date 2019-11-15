package org.unidata.mdm.search.service.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.unidata.mdm.search.configuration.SearchConfigurationConstants;
import org.unidata.mdm.search.context.TypedSearchContext;
import org.unidata.mdm.system.type.runtime.MeasurementPoint;

public class ElasticBaseComponent {
    /**
     * Default prefix.
     */
    protected static final String DEFAULT_PREFIX = "default";
    /**
     * Underscore.
     */
    protected static final String UNDERSCORE = "_";
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticBaseComponent.class);
    /**
     * Search nodes.
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_INDEX_PREFIX_PROPERTY + ":" + DEFAULT_PREFIX + "}")
    private String indexPrefix = DEFAULT_PREFIX;
    /**
     * Maximum search window size, used in paginated requests.
     */
    @Value("${" + SearchConfigurationConstants.SEARCH_TOTAL_COUNT_LIMIT + ":200000}")
    private Integer maxWindowSize;
    /**
     * Execute request method, common to all agents.
     *
     * @param b an action request builder
     * @return action response
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected final <R extends ActionResponse, B extends ActionRequestBuilder> R executeRequest(B b) {

        MeasurementPoint.start();
        try {

            long startTime = 0L;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Executing request: {}", b);
                startTime = System.currentTimeMillis();
            }

            R response = (R) b.execute().actionGet();

            if (LOGGER.isDebugEnabled()) {
                long endTime = System.currentTimeMillis();
                LOGGER.debug("Request executed in {} ms, with response {}.", endTime - startTime, response);
            }

            return response;
        } finally {
            MeasurementPoint.stop();
        }
    }

    /**
     * @param ctx context which contain all necessary info about entityName and storageId
     * @return index name
     */
    protected final String constructIndexName(@Nonnull TypedSearchContext ctx) {
        return constructIndexName(ctx.getEntity(), ctx.getStorageId());
    }

    /**
     * @param entity  - entity name
     * @param storage - storage
     * @return index name
     */
    protected String constructIndexName(@Nonnull String entity, @Nullable String storage) {

        StringBuilder b = new StringBuilder();
        if (indexPrefix != null) {
            b.append(indexPrefix).append(UNDERSCORE);
        }

        if (storage != null) {
            b.append(storage).append(UNDERSCORE);
        } else {
            // TODO: Temporary. Storage id is not visible here, since SU are unavailable.
            b.append(DEFAULT_PREFIX).append(UNDERSCORE);
        }

        return b.append(entity).toString().toLowerCase();
    }

    /**
     * Gets the current max. window size.
     * @return the window size
     */
    public Integer getMaxWindowSize() {
        return maxWindowSize;
    }
}
