package org.unidata.mdm.system.type.pipeline.fragment;

import java.util.Collection;
/**
 * Gets input fragments.
 * @author Mikhail Mikhailov on Dec 11, 2019
 */
public interface OutputFragmentContainer {
    /**
     * Gets a fragment from this composite.
     * @param r the fragment DTO
     */
    <F extends OutputFragment<F>> F fragment(FragmentId<F> f);
    /**
     * Gets multiple fragments of the same type from this composite.
     * @param r fragments
     */
    <F extends OutputFragment<F>> Collection<F> fragments(FragmentId<F> f);
}
