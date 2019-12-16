package org.unidata.mdm.system.type.pipeline.fragment;

/**
 * Plain, untyped fragment.
 * @author Mikhail Mikhailov on Dec 11, 2019
 */
public interface Fragment<F extends Fragment<F>> {
    /**
     * Gets its fragement ID.
     * @return the ID
     */
    FragmentId<F> fragmentId();
}
