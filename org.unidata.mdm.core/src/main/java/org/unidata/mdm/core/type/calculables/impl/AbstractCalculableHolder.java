package org.unidata.mdm.core.type.calculables.impl;

import org.unidata.mdm.core.type.calculables.CalculableHolder;

/**
 * @author Mikhail Mikhailov
 * Abstract parent for holders, for box key primarily.
 */
public abstract class AbstractCalculableHolder<T> implements CalculableHolder<T> {
    /**
     * Saved box key.
     */
    protected String boxKey;
    /**
     * Constructor.
     */
    protected AbstractCalculableHolder() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toBoxKey() {

        if (boxKey == null) {
            boxKey = String.join("|", this.getSourceSystem(), this.getExternalId());
        }

        return boxKey;
    }
}
