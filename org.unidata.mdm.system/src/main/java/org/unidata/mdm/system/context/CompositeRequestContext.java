package org.unidata.mdm.system.context;

import java.util.Collection;

import javax.annotation.Nullable;

/**
 * @author Mikhail Mikhailov
 * Composite context, i. e. potentially consisting of several fragments.
 */
public interface CompositeRequestContext {
    /**
     * Gets a fragment by fragment id.
     * @param f the fragment id
     * @return context or null
     */
    @Nullable
    <C extends RequestFragmentContext<C>> C fragment(RequestFragmentId<C> f);

    /**
     * Gets a collection of contexts of the same types by fragment id f.
     * @param f the fragment id
     * @return contexts or empty collection
     */
    <C extends RequestFragmentContext<C>> Collection<C> fragments(RequestFragmentId<C> f);
}
