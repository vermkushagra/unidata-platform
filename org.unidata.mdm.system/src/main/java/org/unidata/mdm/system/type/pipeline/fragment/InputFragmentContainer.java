package org.unidata.mdm.system.type.pipeline.fragment;

import java.util.Collection;
/**
 * Gets input fragments.
 * @author Mikhail Mikhailov on Dec 11, 2019
 */
public interface InputFragmentContainer {
    /**
     * Gets a fragment from this composite.
     * @param r the fragment DTO
     */
    <F extends InputFragment<F>> F fragment(FragmentId<F> f);
    /**
     * Gets multiple fragments of the same type from this composite.
     * @param r fragments
     */
    <F extends InputFragment<F>> Collection<F> fragments(FragmentId<F> f);
}
