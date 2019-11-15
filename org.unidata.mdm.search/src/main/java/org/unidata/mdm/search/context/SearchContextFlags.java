package org.unidata.mdm.search.context;

import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Context flags.
 */
public final class SearchContextFlags {
    /**
     * Notification flag.
     */
    public static final int FLAG_INDEX_FORCE_CREATE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Notification flag.
     */
    public static final int FLAG_INDEX_DROP = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Notification flag.
     */
    public static final int FLAG_INDEX_WHITESPACE_TOKENIZE = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Constructor.
     */
    private SearchContextFlags() {
        super();
    }
}
