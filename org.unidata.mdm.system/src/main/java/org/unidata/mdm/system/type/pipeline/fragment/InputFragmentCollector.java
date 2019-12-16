package org.unidata.mdm.system.type.pipeline.fragment;

import java.util.Collection;
import java.util.function.Supplier;
/**
 * Gets input fragments.
 * @author Mikhail Mikhailov on Dec 11, 2019
 */
public interface InputFragmentCollector <X extends InputFragmentCollector<X>> {
    /**
     * Adds a fragment from this composite.
     * @param r the fragment DTO
     */
    X fragment(InputFragment<?> f);
    /**
     * Adds multiple fragments of the same type from this composite.
     * @param r fragments
     */
    X fragments(Collection<? extends InputFragment<?>> f);
    /**
     * Adds a fragment from this composite using a supplier.
     * @param r the fragment DTO
     */
    X fragment(Supplier<? extends InputFragment<?>> f);
    /**
     * Adds multiple fragments of the same type from this composite using a supplier.
     * @param r fragments
     */
    X fragments(Supplier<Collection<? extends InputFragment<?>>> f);
}
