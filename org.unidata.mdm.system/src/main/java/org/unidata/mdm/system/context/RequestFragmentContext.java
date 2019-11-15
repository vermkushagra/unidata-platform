package org.unidata.mdm.system.context;

/**
 * @author Mikhail Mikhailov
 * A request fragment container.
 */
public interface RequestFragmentContext<F extends RequestFragmentContext<F>> {
    /**
     * Gets fragment id of this context.
     * @return fragment id
     */
    RequestFragmentId<F> getFragmentId();
}
