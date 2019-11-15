package org.unidata.mdm.system.type.support;

import java.util.AbstractSet;
import java.util.IdentityHashMap;
import java.util.Iterator;

/**
 * @author Mikhail Mikhailov
 * Simple identity hash set.
 */
public class IdentityHashSet<T> extends AbstractSet<T> {
    /**
     * The content.
     */
    private final IdentityHashMap<T, Boolean> content;
    /**
     * Constructor.
     */
    public IdentityHashSet() {
        super();
        content = new IdentityHashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<T> iterator() {
        return content.keySet().iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return content.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return content.containsKey(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(T o) {
        return content.put(o, Boolean.TRUE) == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return content.remove(o) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        content.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return content.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o instanceof IdentityHashSet) {

            IdentityHashSet<?> other = (IdentityHashSet<?>) o;
            return other.content.equals(this.content);
        }

        return false;
    }
}
