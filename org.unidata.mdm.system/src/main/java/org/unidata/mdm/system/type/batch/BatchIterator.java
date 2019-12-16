package org.unidata.mdm.system.type.batch;

import org.unidata.mdm.system.context.CommonRequestContext;

/**
 * @author Mikhail Mikhailov
 * Simple batch iterator.
 */
public interface BatchIterator<T extends CommonRequestContext> {
    /**
     * If there are more elements to iterate.
     * @return true, if so, false otherwise
     */
    boolean hasNext();
    /**
     * Next context iteration.
     * @return next context
     */
    T next();
    /**
     * Removes current element.
     */
    void remove();
}
