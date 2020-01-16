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

package org.unidata.mdm.system.context;

import java.util.Collection;

import org.unidata.mdm.system.type.pipeline.fragment.InputFragment;

/**
 * @author Mikhail Mikhailov
 * The fragments holder.
 */
final class InputFragmentHolder {
    /**
     * Single context was supplied for id.
     */
    private final InputFragment<?> single;
    /**
     * Multiple contexts were supplied for id.
     */
    private final Collection<? extends InputFragment<?>> multiple;
    /**
     * Constructor.
     * @param single the fragment
     */
    private InputFragmentHolder(InputFragment<?> single) {
        super();
        this.single = single;
        this.multiple = null;
    }
    /**
     * Constructor.
     * @param multiple contexts
     */
    private InputFragmentHolder(Collection<? extends InputFragment<?>> multiple) {
        super();
        this.single = null;
        this.multiple = multiple;
    }
    /**
     * @return the single
     */
    public InputFragment<?> getSingle() {
        return single;
    }
    /**
     * @return the multiple
     */
    public Collection<? extends InputFragment<?>> getMultiple() {
        return multiple;
    }
    /**
     * Check for having a single fragment for id.
     * @return true for single, false otherwise
     */
    public boolean isSingle() {
        return single != null && multiple == null;
    }
    /**
     * Check for having multiple fragments for id.
     * @return true for multiple, false otherwise
     */
    public boolean isMultiple() {
        return single == null && multiple != null;
    }
    /**
     * Creates holder instance.
     * @param single the fragment
     * @return holder
     */
    public static InputFragmentHolder of(InputFragment<?> single) {
        return new InputFragmentHolder(single);
    }
    /**
     * Creates holder instance.
     * @param multiple fragments
     * @return holder
     */
    public static InputFragmentHolder of(Collection<? extends InputFragment<?>> multiple) {
        return new InputFragmentHolder(multiple);
    }
}