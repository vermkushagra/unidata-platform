/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
