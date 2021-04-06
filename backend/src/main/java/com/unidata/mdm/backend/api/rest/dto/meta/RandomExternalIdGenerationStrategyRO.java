package com.unidata.mdm.backend.api.rest.dto.meta;

/**
 * @author Mikhail Mikhailov
 * Random strategy mapped type descriptor.
 * No fields so far.
 */
public class RandomExternalIdGenerationStrategyRO extends ExternalIdGenerationStrategyRO {
    /**
     * Constructor.
     */
    public RandomExternalIdGenerationStrategyRO() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalIdGenerationTypeRO getStrategyType() {
        return ExternalIdGenerationTypeRO.RANDOM;
    }
}
