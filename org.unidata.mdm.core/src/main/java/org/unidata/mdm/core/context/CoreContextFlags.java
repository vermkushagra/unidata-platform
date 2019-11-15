package org.unidata.mdm.core.context;

import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Context flags.
 */
public final class CoreContextFlags {
    /**
     * Notification flag.
     */
    public static final int FLAG_SEND_NOTIFICATION = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Constructor.
     */
    private CoreContextFlags() {
        super();
    }
}
