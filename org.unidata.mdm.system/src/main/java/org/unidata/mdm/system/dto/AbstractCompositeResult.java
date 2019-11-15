package org.unidata.mdm.system.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

/**
 * @author Mikhail Mikhailov on Nov 8, 2019
 */
@NotThreadSafe
public abstract class AbstractCompositeResult implements CompositeResult {
    /**
     * Fragments map.
     */
    protected Map<ResultFragmentId<? extends ResultFragment<?>>, ResultFragmentHolder> fragments;
    /**
     * Constructor.
     */
    public AbstractCompositeResult() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends ResultFragment<C>> C fragment(ResultFragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return null;
        }

        ResultFragmentHolder h = fragments.get(f);
        if (Objects.isNull(h) || !h.isSingle()) {
            return null;
        }

        return (C) h.getSingle();
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends ResultFragment<C>> Collection<C> fragments(ResultFragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return Collections.emptyList();
        }

        ResultFragmentHolder h = fragments.get(f);
        if (Objects.isNull(h) || !h.isMultiple()) {
            return Collections.emptyList();
        }

        return (Collection<C>) h.getMultiple();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void fragment(ResultFragment<?> r) {

        if (Objects.nonNull(r)) {
            if (Objects.isNull(fragments)) {
                fragments = new IdentityHashMap<>();
            }

            fragments.put(r.getFragmentId(), ResultFragmentHolder.of(r));
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void fragments(Collection<ResultFragment<?>> r) {

        if (CollectionUtils.isNotEmpty(r)) {
            if (Objects.isNull(fragments)) {
                fragments = new IdentityHashMap<>();
            }

            fragments.put(r.iterator().next().getFragmentId(), ResultFragmentHolder.of(r));
        }
    }
}
