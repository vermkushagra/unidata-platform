package org.unidata.mdm.meta.context;

import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov on Nov 28, 2019
 */
public class MetaContextFlags {
    /**
     * Notification flag.
     */
    public static final int FLAG_DRAFT = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Gather shallow, reduced set of information.
     */
    public static final int FLAG_REDUCED = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All groups requested.
     */
    public static final int FLAG_ALL_ENTITY_GROUPS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All entities requested.
     */
    public static final int FLAG_ALL_ENTITIES = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All lookups requested.
     */
    public static final int FLAG_ALL_LOOKUPS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All enumerations requested.
     */
    public static final int FLAG_ALL_ENUMERATIONS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All relations requested.
     */
    public static final int FLAG_ALL_RELATIONS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All source systems requested.
     */
    public static final int FLAG_ALL_SOURCE_SYSTEMS = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Admin SS requested.
     */
    public static final int FLAG_ADMIN_SOURCE_SYSTEM = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * All measured values requested.
     */
    public static final int FLAG_ALL_MEASURED_VALUES = CommonRequestContext.FLAG_ID_PROVIDER.getAndIncrement();
    /**
     * Constructor.
     */
    private MetaContextFlags() {
        super();
    }
}
