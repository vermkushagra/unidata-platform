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

package com.unidata.mdm.backend.util;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Michael Yashin. Created on 17.04.2015.
 */
public class DiffResult<T> {
    @SuppressWarnings("unchecked")
    private Collection<T> added = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private Collection<T> updated = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private Collection<T> unchanged = Collections.EMPTY_LIST;
    @SuppressWarnings("unchecked")
    private Collection<T> deleted = Collections.EMPTY_LIST;

    public Collection<T> getAdded() {
        return added;
    }

    public void setAdded(Collection<T> added) {
        this.added = added;
    }

    public Collection<T> getUpdated() {
        return updated;
    }

    public void setUpdated(Collection<T> updated) {
        this.updated = updated;
    }

    public Collection<T> getUnchanged() {
        return unchanged;
    }

    public void setUnchanged(Collection<T> unchanged) {
        this.unchanged = unchanged;
    }

    public Collection<T> getDeleted() {
        return deleted;
    }

    public void setDeleted(Collection<T> deleted) {
        this.deleted = deleted;
    }
}
