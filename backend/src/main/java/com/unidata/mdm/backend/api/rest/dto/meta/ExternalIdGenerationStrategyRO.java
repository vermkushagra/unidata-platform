package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Mikhail Mikhailov
 * Base class for gen. type REST descriptors.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = RandomExternalIdGenerationStrategyRO.class, name = "RANDOM"),
    @Type(value = ConcatExternalIdGenerationStrategyRO.class, name = "CONCAT"),
    @Type(value = SequenceExternalIdGenerationStrategyRO.class, name = "SEQUENCE")
})
public abstract class ExternalIdGenerationStrategyRO {
    /**
     * Constructor.
     */
    public ExternalIdGenerationStrategyRO() {
        super();
    }
    /**
     * @return the strategyType
     */
    public abstract ExternalIdGenerationTypeRO getStrategyType();
}
