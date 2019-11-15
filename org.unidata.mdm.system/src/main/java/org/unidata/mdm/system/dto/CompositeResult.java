package org.unidata.mdm.system.dto;

import java.util.Collection;

import javax.annotation.Nullable;

/**
 * @author Mikhail Mikhailov on Oct 2, 2019
 * Composite DTO.
 */
public interface CompositeResult {
    /**
     * Gets a fragment by fragment id.
     * @param f the fragment id
     * @return fragment DTO or null
     */
    @Nullable
    <C extends ResultFragment<C>> C fragment(ResultFragmentId<C> f);
    /**
     * Gets a collection of contexts of the same types by fragment id f.
     * @param f the fragment id
     * @return fragment DTO or empty collection
     */
    <C extends ResultFragment<C>> Collection<C> fragments(ResultFragmentId<C> f);
    /**
     * Adds a fragment to this composite DTO.
     * @param r the fragment DTO
     */
    void fragment(ResultFragment<?> r);
    /**
     * Adds multiple fragments of the same type to this composite DTO.
     * @param r fragments
     */
    void fragments(Collection<ResultFragment<?>> r);
}
