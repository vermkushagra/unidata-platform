package org.unidata.mdm.core.context;

import java.util.Collection;

import org.unidata.mdm.system.context.RequestFragmentContext;

/**
 * @author Mikhail Mikhailov
 * The fragments holder.
 */
final class RequestFragmentHolder {
    /**
     * Single context was supplied for id.
     */
    private final RequestFragmentContext<?> single;
    /**
     * Multiple contexts were supplied for id.
     */
    private final Collection<RequestFragmentContext<?>> multiple;
    /**
     * Constructor.
     * @param single the fragment
     */
    private RequestFragmentHolder(RequestFragmentContext<?> single) {
        super();
        this.single = single;
        this.multiple = null;
    }
    /**
     * Constructor.
     * @param multiple contexts
     */
    private RequestFragmentHolder(Collection<RequestFragmentContext<?>> multiple) {
        super();
        this.single = null;
        this.multiple = multiple;
    }
    /**
     * @return the single
     */
    public RequestFragmentContext<?> getSingle() {
        return single;
    }
    /**
     * @return the multiple
     */
    public Collection<RequestFragmentContext<?>> getMultiple() {
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
    public static RequestFragmentHolder of(RequestFragmentContext<?> single) {
        return new RequestFragmentHolder(single);
    }
    /**
     * Creates holder instance.
     * @param multiple fragments
     * @return holder
     */
    public static RequestFragmentHolder of(Collection<RequestFragmentContext<?>> multiple) {
        return new RequestFragmentHolder(multiple);
    }
}