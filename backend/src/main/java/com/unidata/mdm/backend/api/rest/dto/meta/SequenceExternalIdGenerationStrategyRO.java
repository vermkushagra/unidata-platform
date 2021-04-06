package com.unidata.mdm.backend.api.rest.dto.meta;

/**
 * @author Mikhail Mikhailov
 * Sequence strategy mapped type descriptor.
 * No fields so far.
 */
public class SequenceExternalIdGenerationStrategyRO extends ExternalIdGenerationStrategyRO {
    /**
     * Constructor.
     */
    public SequenceExternalIdGenerationStrategyRO() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalIdGenerationTypeRO getStrategyType() {
        return ExternalIdGenerationTypeRO.SEQUENCE;
    }
}
