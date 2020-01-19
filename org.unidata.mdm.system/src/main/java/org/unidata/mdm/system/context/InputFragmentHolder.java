package org.unidata.mdm.system.context;

import java.util.Collection;

import org.unidata.mdm.system.type.pipeline.fragment.InputFragment;

/**
 * @author Mikhail Mikhailov
 * The fragments holder.
 */
public final class InputFragmentHolder {
    /**
     * Single context was supplied for id.
     */
    private final InputFragment<?> single;
    /**
     * Multiple contexts were supplied for id.
     */
    private final Collection<? extends InputFragment<?>> multiple;
    /**
     * Constructor.
     * @param single the fragment
     */
    private InputFragmentHolder(InputFragment<?> single) {
        super();
        this.single = single;
        this.multiple = null;
    }
    /**
     * Constructor.
     * @param multiple contexts
     */
    private InputFragmentHolder(Collection<? extends InputFragment<?>> multiple) {
        super();
        this.single = null;
        this.multiple = multiple;
    }
    /**
     * @return the single
     */
    public InputFragment<?> getSingle() {
        return single;
    }
    /**
     * @return the multiple
     */
    public Collection<? extends InputFragment<?>> getMultiple() {
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
    public static InputFragmentHolder of(InputFragment<?> single) {
        return new InputFragmentHolder(single);
    }
    /**
     * Creates holder instance.
     * @param multiple fragments
     * @return holder
     */
    public static InputFragmentHolder of(Collection<? extends InputFragment<?>> multiple) {
        return new InputFragmentHolder(multiple);
    }
}