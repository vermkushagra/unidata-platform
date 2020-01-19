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
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.unidata.mdm.system.type.pipeline.fragment.FragmentId;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragment;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentCollector;
import org.unidata.mdm.system.type.pipeline.fragment.InputFragmentContainer;

/**
 * @author Mikhail Mikhailov
 * Composite context.
 */
public class AbstractCompositeRequestContext extends CommonRequestContext implements InputFragmentContainer {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2683127071228358568L;
    /**
     * Fragments map.
     */
    protected final transient Map<FragmentId<? extends InputFragment<?>>, InputFragmentHolder> fragments;
    /**
     * Constructor.
     * @param b
     */
    public AbstractCompositeRequestContext(AbstractCompositeRequestContextBuilder<?> b) {
        super(b);
        this.fragments = b.fragments;
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public <C extends InputFragment<C>> C fragment(FragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return null;
        }

        InputFragmentHolder h = fragments.get(f);
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
    public <C extends InputFragment<C>> Collection<C> fragments(FragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return Collections.emptyList();
        }

        InputFragmentHolder h = fragments.get(f);
        if (Objects.isNull(h) || !h.isMultiple()) {
            return Collections.emptyList();
        }

        return (Collection<C>) h.getMultiple();
    }
    /**
     * Parent builder.
     * @author Mikhail Mikhailov
     *
     * @param <X>
     */
    public abstract static class AbstractCompositeRequestContextBuilder<X extends AbstractCompositeRequestContextBuilder<X>>
        extends CommonRequestContextBuilder<X> implements InputFragmentCollector<X> {
        /**
         * Fragments map.
         */
        private Map<FragmentId<? extends InputFragment<?>>, InputFragmentHolder> fragments;
        /**
         * Default constructor.
         */
        protected AbstractCompositeRequestContextBuilder() {
            super();
        }
        /**
         * Copy constructor.
         * @param other the object to copy fields from
         */
        protected AbstractCompositeRequestContextBuilder(AbstractCompositeRequestContext other) {
            super(other);
            this.fragments = other.fragments;
        }
        /**
         * Adds a singleton fragment for an ID using a supplier.
         * @param s the supplier
         * @return self
         */
        @Override
        public X fragment(Supplier<? extends InputFragment<?>> s) {

            InputFragment<?> f = s.get();
            if (Objects.nonNull(f)) {

                if (fragments == null) {
                    fragments = new IdentityHashMap<>();
                }

                fragments.put(f.fragmentId(), InputFragmentHolder.of(f));
            }
            return self();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public X fragment(InputFragment<?> f) {
            return fragment(() -> f);
        }
        /**
         * Adds a fragment collection for an ID using a supplier.
         * @param s the supplier
         * @return self
         */
        @Override
        public X fragments(Supplier<Collection<? extends InputFragment<?>>> s) {

            Collection<? extends InputFragment<?>> fs = s.get();
            if (Objects.nonNull(fs) && CollectionUtils.isNotEmpty(fs)) {

                if (fragments == null) {
                    fragments = new IdentityHashMap<>();
                }

                fragments.put(fs.iterator().next().fragmentId(), InputFragmentHolder.of(fs));
            }
            return self();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public X fragments(Collection<? extends InputFragment<?>> f) {
            return fragments(() -> f);
        }
    }
}
