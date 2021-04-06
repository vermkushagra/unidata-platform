package com.unidata.mdm.backend.service.search.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.elasticsearch.action.ActionRequestBuilder;
import org.elasticsearch.action.ActionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.context.SearchContext;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;

public class ElasticBaseComponent {

    /**
     * Default prefix
     */
    private static final String DEFAULT_PREFIX = "default";

    /**
     * under score
     */
    private static final String UNDER_SCORE = "_";
    /**
     * The logger.
     */
    private static Logger LOGGER = LoggerFactory.getLogger(ElasticBaseComponent.class);
    /**
     * Search nodes.
     */
    @Value("${" + ConfigurationConstants.SEARCH_INDEX_PREFIX_PROPERTY + ":" + DEFAULT_PREFIX + "}")
    private String indexPrefix = DEFAULT_PREFIX;


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
    protected final String constructIndexName(@Nonnull SearchContext ctx) {
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
            b.append(indexPrefix).append(UNDER_SCORE);
        }

        if (storage != null) {
            b.append(storage).append(UNDER_SCORE);
        } else if (SecurityUtils.getCurrentUserStorageId() != null) {
            b.append(SecurityUtils.getCurrentUserStorageId()).append(UNDER_SCORE);
        }

        return b.append(entity).toString().toLowerCase();
    }
}
