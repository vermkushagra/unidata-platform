package com.unidata.mdm.backend.common.record;

import java.util.Iterator;
import java.util.Map.Entry;

import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.AttributeIterator;

/**
 * @author Mikhail Mikhailov
 * Thin iterator wrapper to allow traverse -> remove actions.
 */
public class AttributeIteratorImpl implements AttributeIterator {

    /**
     * The underlaying iterator.
     */
    private final Iterator<Entry<String, Attribute>> it;
    /**
     * Constructor.
     * @param it iterator.
     */
    AttributeIteratorImpl(Iterator<Entry<String, Attribute>> it) {
        this.it = it;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNext() {
        return it.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Attribute next() {
        return it.next().getValue();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove() {
        it.remove();
    }
}
