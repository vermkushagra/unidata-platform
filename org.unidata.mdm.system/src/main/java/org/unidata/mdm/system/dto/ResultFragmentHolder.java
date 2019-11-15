package org.unidata.mdm.system.dto;

import java.util.Collection;

/**
 * @author Mikhail Mikhailov
 * The fragments holder.
 */
final class ResultFragmentHolder {
    /**
     * Single context was supplied for id.
     */
    private final ResultFragment<?> single;
    /**
     * Multiple contexts were supplied for id.
     */
    private final Collection<ResultFragment<?>> multiple;
    /**
     * Constructor.
     * @param single the fragment
     */
    private ResultFragmentHolder(ResultFragment<?> single) {
        super();
        this.single = single;
        this.multiple = null;
    }
    /**
     * Constructor.
     * @param multiple contexts
     */
    private ResultFragmentHolder(Collection<ResultFragment<?>> multiple) {
        super();
        this.single = null;
        this.multiple = multiple;
    }
    /**
     * @return the single
     */
    public ResultFragment<?> getSingle() {
        return single;
    }
    /**
     * @return the multiple
     */
    public Collection<ResultFragment<?>> getMultiple() {
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
    public static ResultFragmentHolder of(ResultFragment<?> single) {
        return new ResultFragmentHolder(single);
    }
    /**
     * Creates holder instance.
     * @param multiple fragments
     * @return holder
     */
    public static ResultFragmentHolder of(Collection<ResultFragment<?>> multiple) {
        return new ResultFragmentHolder(multiple);
    }
}