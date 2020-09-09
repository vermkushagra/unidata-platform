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

package com.unidata.mdm.backend.service.classifier;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * It is a wrapper over list, but with idea that elements of list are sequence elements of an abstract tree.
 *
 * @param <T>
 */
public class Branch<T> {

    /**
     * Sequence elements which represent branch of tree.
     */
    @Nonnull
    private final List<T> path;

    /**
     * Is from root branch, or from leaf
     */
    private boolean direction;

    public Branch(@Nonnull List<T> path, boolean fromRoot) {
        this.path = path;
        this.direction = fromRoot;
    }

    @Nonnull
    public List<T> getPath() {
        return Collections.unmodifiableList(path);
    }

    /**
     * Change branch direction
     */
    public Branch<T> reverse() {
        Collections.reverse(path);
        direction = !direction;
        return this;
    }

    /**
     * @return direction of branch. true if the first element of path is root element
     */
    public boolean isToRoot() {
        return !direction;
    }

    /**
     * @return direction of branch. true if the first element of path is leaf element
     */
    public boolean isFromRoot() {
        return direction;
    }
}
