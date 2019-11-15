package org.unidata.mdm.system.dto;

import java.util.function.Supplier;

/**
 * @author Mikhail Mikhailov on Oct 2, 2019
 * Fragment id.
 */
public final class ResultFragmentId<F extends ResultFragment<F>> {
    /**
     * The name.
     */
    private final String name;
    /**
     * The instance supplier.
     */
    private final Supplier<F> factory;
    /**
     * Constructor.
     * @param name the name
     * @param factory the instance supplier
     */
    public ResultFragmentId(String name, Supplier<F> factory) {
        super();
        this.name = name;
        this.factory = factory;
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
