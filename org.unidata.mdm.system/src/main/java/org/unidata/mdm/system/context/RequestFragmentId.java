package org.unidata.mdm.system.context;

import java.util.function.Supplier;

/**
 * @author Mikhail Mikhailov
 * Request context fragment ID.
 */
public final class RequestFragmentId<F extends RequestFragmentContext<F>> {
    /**
     * The name of the ID.
     */
    private final String name;
    /**
     * The factory instance.
     */
    private final Supplier<F> factory;
    /**
     * Constructor.
     * @param name the name of the ID
     * @param s the supplier of default empty instances
     */
    public RequestFragmentId(String name, Supplier<F> s) {
        super();
        this.name = name;
        this.factory = s;
    }
    /**
     * Gets the name if this fragment.
     * @return fragment name
     */
    public String getName() {
        return name;
    }
    /**
     * Creates default instance, if needed.
     * @return default instance
     */
    public F getDefaultInstance() {
        return factory.get();
    }
}
