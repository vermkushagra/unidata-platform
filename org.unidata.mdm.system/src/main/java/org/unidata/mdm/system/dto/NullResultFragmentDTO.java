package org.unidata.mdm.system.dto;

/**
 * @author Mikhail Mikhailov on Oct 3, 2019
 */
public class NullResultFragmentDTO implements ResultFragment<NullResultFragmentDTO> {
    /**
     * Null fragment id.
     */
    public static final ResultFragmentId<NullResultFragmentDTO> ID
        = new ResultFragmentId<>("NULL_RESULT", NullResultFragmentDTO::new);
    /**
     * Null fragment singleton.
     */
    public static final NullResultFragmentDTO NULL_FRAGMENT_RESULT = new NullResultFragmentDTO();
    /**
     * Constructor.
     */
    private NullResultFragmentDTO() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ResultFragmentId<NullResultFragmentDTO> getFragmentId() {
        return ID;
    }
}
