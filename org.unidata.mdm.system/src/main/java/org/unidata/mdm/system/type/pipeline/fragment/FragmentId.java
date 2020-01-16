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

package org.unidata.mdm.system.type.pipeline.fragment;

import java.util.function.Supplier;

/**
 * Generic fragment ID.
 * @author Mikhail Mikhailov on Dec 11, 2019
 */
public class FragmentId<F extends Fragment<F>> {
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
    public FragmentId(String name, Supplier<F> s) {
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
