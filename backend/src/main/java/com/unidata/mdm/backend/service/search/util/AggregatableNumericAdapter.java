package com.unidata.mdm.backend.service.search.util;

import com.unidata.mdm.backend.common.search.types.Aggregatable;

/**
 * @author Mikhail Mikhailov
 * Numeric adapter.
 */
public class AggregatableNumericAdapter<T extends Number> implements Aggregatable {
    /**
     * The value.
     */
    private T value;
    /**
     * Discard.
     */
    private boolean discard;
    /**
     * Stop aggregation.
     */
    private boolean stop;
    /**
     * Constructor.
     * @param value initial value
     */
    public AggregatableNumericAdapter(T value) {
        super();
        this.value = value;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean discard() {
        return discard;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean stop() {
        return stop;
    }
    /**
     * @return the value
     */
    public T value() {
        return value;
    }
    /**
     * @param value the value to set
     */
    public void setValue(T value) {
        this.value = value;
    }
    /**
     * @param discard the discard to set
     */
    public void setDiscard(boolean discard) {
        this.discard = discard;
    }
    /**
     * @param stop the stop to set
     */
    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
