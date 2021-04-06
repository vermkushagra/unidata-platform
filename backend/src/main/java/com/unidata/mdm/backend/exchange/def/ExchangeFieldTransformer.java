/**
 *
 */
package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Mikhail Mikhailov
 *
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
        @Type(value = ExchangeRegexFieldTransformer.class, name = "REGEX_SPLITTER"),
        @Type(value = ExchangeTemporalFieldTransformer.class, name = "TEMPORAL_PARSER"),
        @Type(value = ExchangeNoiseRemoveTransformer.class, name = "NOISE_REMOVER")})
public abstract class ExchangeFieldTransformer implements Serializable {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5310354113397140212L;

    /**
     * Ctor.
     */
    public ExchangeFieldTransformer() {
        super();
    }

    /**
     * Implement in subclasses.
     * @return transformation result.
     */
    public abstract String transform(String input);

}
