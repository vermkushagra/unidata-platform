package org.unidata.mdm.system.dto;

import java.util.Collection;

import org.unidata.mdm.system.type.pipeline.fragment.OutputFragment;

/**
 * @author Mikhail Mikhailov
 * The fragments holder.
 */
final class OutputFragmentHolder {
    /**
     * Single context was supplied for id.
     */
    private final OutputFragment<?> single;
    /**
     * Multiple contexts were supplied for id.
     */
    private final Collection<? extends OutputFragment<?>> multiple;
    /**
     * Constructor.
     * @param single the fragment
     */
    private OutputFragmentHolder(OutputFragment<?> single) {
        super();
        this.single = single;
        this.multiple = null;
    }
    /**
     * Constructor.
     * @param multiple contexts
     */
    private OutputFragmentHolder(Collection<? extends OutputFragment<?>> multiple) {
        super();
        this.single = null;
        this.multiple = multiple;
    }
    /**
     * @return the single
     */
    public OutputFragment<?> getSingle() {
        return single;
    }
    /**
     * @return the multiple
     */
    public Collection<? extends OutputFragment<?>> getMultiple() {
        return multiple;
    }
    /**
     * Check for having a single fragment for id.
     * @return true for single, false otherwise
     */
    public boolean isSingle() {
        return single != null && multiple == null;
    }
    /**
     * Check for having multiple fragments for id.
     * @return true for multiple, false otherwise
     */
    public boolean isMultiple() {
        return single == null && multiple != null;
    }
    /**
     * Creates holder instance.
     * @param single the fragment
     * @return holder
     */
    public static OutputFragmentHolder of(OutputFragment<?> single) {
        return new OutputFragmentHolder(single);
    }
    /**
     * Creates holder instance.
     * @param multiple fragments
     * @return holder
     */
    public static OutputFragmentHolder of(Collection<? extends OutputFragment<?>> multiple) {
        return new OutputFragmentHolder(multiple);
    }
}