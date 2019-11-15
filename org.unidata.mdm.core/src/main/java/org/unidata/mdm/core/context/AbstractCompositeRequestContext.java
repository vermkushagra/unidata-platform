package org.unidata.mdm.core.context;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.unidata.mdm.system.context.CommonRequestContext;
import org.unidata.mdm.system.context.CompositeRequestContext;
import org.unidata.mdm.system.context.RequestFragmentContext;
import org.unidata.mdm.system.context.RequestFragmentId;

/**
 * @author Mikhail Mikhailov
 * Composite context.
 */
public class AbstractCompositeRequestContext extends CommonRequestContext implements CompositeRequestContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2683127071228358568L;
    /**
     * Fragments map.
     */
    protected final transient Map<RequestFragmentId<? extends RequestFragmentContext<?>>, RequestFragmentHolder> fragments;
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
    public <C extends RequestFragmentContext<C>> C fragment(RequestFragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return null;
        }

        RequestFragmentHolder h = fragments.get(f);
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
    public <C extends RequestFragmentContext<C>> Collection<C> fragments(RequestFragmentId<C> f) {

        if (MapUtils.isEmpty(fragments)) {
            return Collections.emptyList();
        }

        RequestFragmentHolder h = fragments.get(f);
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
        extends CommonRequestContextBuilder<X> {
        /**
         * Fragments map.
         */
        private Map<RequestFragmentId<? extends RequestFragmentContext<?>>, RequestFragmentHolder> fragments;
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
        public X fragment(Supplier<? extends RequestFragmentContext<?>> s) {

            RequestFragmentContext<?> f = s.get();
            if (Objects.nonNull(f)) {

                if (fragments == null) {
                    fragments = new IdentityHashMap<>();
                }

                fragments.put(f.getFragmentId(), RequestFragmentHolder.of(f));
            }
            return self();
        }
        /**
         * Adds a fragment collection for an ID using a supplier.
         * @param s the supplier
         * @return self
         */
        public X fragments(Supplier<Collection<RequestFragmentContext<?>>> s) {

            Collection<RequestFragmentContext<?>> fs = s.get();
            if (Objects.nonNull(fs) && CollectionUtils.isNotEmpty(fs)) {

                if (fragments == null) {
                    fragments = new IdentityHashMap<>();
                }

                fragments.put(fs.iterator().next().getFragmentId(), RequestFragmentHolder.of(fs));
            }
            return self();
        }
    }
}
