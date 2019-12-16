package org.unidata.mdm.system.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragment;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragmentCollector;
import org.unidata.mdm.system.type.pipeline.fragment.OutputFragmentContainer;

/**
 * @author Mikhail Mikhailov on Nov 8, 2019
 */
@NotThreadSafe
public abstract class AbstractCompositeResult implements OutputFragmentContainer, OutputFragmentCollector<AbstractCompositeResult> {
    /**
     * Fragments map.
     */
    protected Map<FragmentId<? extends OutputFragment<?>>, OutputFragmentHolder> fragments;
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
    public <C extends OutputFragment<C>> C fragment(FragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return null;
        }

        OutputFragmentHolder h = fragments.get(f);
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
    public <C extends OutputFragment<C>> Collection<C> fragments(FragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return Collections.emptyList();
        }

        OutputFragmentHolder h = fragments.get(f);
        if (Objects.isNull(h) || !h.isMultiple()) {
            return Collections.emptyList();
        }

        return (Collection<C>) h.getMultiple();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCompositeResult fragment(Supplier<? extends OutputFragment<?>> s) {

        OutputFragment<?> r = s.get();
        if (Objects.nonNull(r)) {
            if (Objects.isNull(fragments)) {
                fragments = new IdentityHashMap<>();
            }

            fragments.put(r.fragmentId(), OutputFragmentHolder.of(r));
        }

        return this;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCompositeResult fragments(Supplier<Collection<? extends OutputFragment<?>>> s) {

        Collection<? extends OutputFragment<?>> fs = s.get();
        if (CollectionUtils.isNotEmpty(fs)) {
            if (Objects.isNull(fragments)) {
                fragments = new IdentityHashMap<>();
            }

            fragments.put(fs.iterator().next().fragmentId(), OutputFragmentHolder.of(fs));
        }

        return this;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCompositeResult fragment(OutputFragment<?> f) {
        return fragment(() -> f);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractCompositeResult fragments(Collection<? extends OutputFragment<?>> f) {
        return fragments(() -> f);
    }
}
