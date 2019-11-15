package org.unidata.mdm.search.service;

import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.search.context.SearchRequestContext;

/**
 * Component responsible for working with indexes over abstract search engine.
 */
public interface IndexComponent {
    /**
     * Creates a new index but if the index already exist operation do nothing.
     *
     * @param ctx        the context to use
     * @param properties properties, if properties is not defined will be used properties from backed.properties
     * @return true if successful, false otherwise
     */
    boolean safeCreateIndex(@Nonnull final SearchRequestContext ctx, @Nullable Properties properties);

    /**
     * Creates a new index even if the index is existed.
     *
     * @param ctx        the context to use
     * @param properties properties, if properties is not defined will be used properties from backed.properties
     * @return true if successful, false otherwise
     */
    boolean forceCreateIndex(@Nonnull final SearchRequestContext ctx, @Nullable Properties properties);


    /**
     * Tells if an index exists.
     *
     * @param ctx the context
     * @return true, if exists, false otherwise
     */
    boolean indexExists(@Nonnull final SearchRequestContext ctx);

    /**
     * Drops an index.
     *
     * @param ctx the context to use
     * @return true, if successful, false otherwise
     */
    boolean dropIndex(@Nonnull final SearchRequestContext ctx);

    /**
     * Triggers manual refresh on an index.
     *
     * @param ctx the context to use
     * @param wait will wait for foreground thread to complete forever or until interrupted
     * @return true, if successful, false otherwise
     */
    boolean refreshIndex(@Nonnull final SearchRequestContext ctx, boolean wait);

    /**
     * Sets index refresh interval.
     *
     * @param ctx the context to use
     * @param value the value
     * @return true, if successful, false otherwise
     */
    boolean setIndexRefreshInterval(@Nonnull final SearchRequestContext ctx, String value);
    /**
     * Sets index settings.
     *
     * @param ctx the context
     * @param settings the settings
     * @return true, if successful, false otherwise
     */
    boolean setIndexSettings(SearchRequestContext ctx, Map<String, Object> settings);
    /**
     * Sets cluster settings.
     *
     * @param settings the settings to set
     * @param persistent type of settings
     * @return true, if successful, false otherwise
     */
    boolean setClusterSettings(Map<String, Object> settings, boolean persistent);
    /**
     * Closes an index.
     * @param ctx the context
     * @return true, if successful, false otherwise
     */
    boolean closeIndex(SearchRequestContext ctx);
    /**
     * Opens an index.
     * @param ctx the context
     * @return true, if successful, false otherwise
     */
    boolean openIndex(SearchRequestContext ctx);
}
