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
