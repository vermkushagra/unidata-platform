package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.meta.ConcatExternalIdGenerationStrategyRO;
import com.unidata.mdm.backend.api.rest.dto.meta.ExternalIdGenerationStrategyRO;
import com.unidata.mdm.backend.api.rest.dto.meta.RandomExternalIdGenerationStrategyRO;
import com.unidata.mdm.backend.api.rest.dto.meta.SequenceExternalIdGenerationStrategyRO;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.AbstractExternalIdGenerationStrategyDef;
import com.unidata.mdm.meta.ConcatenatedExternalIdGenerationStrategyDef;

/**
 * @author Mikhail Mikhailov
 * Converter.
 */
public class ExternalIdGenerationStrategyConverter {
    /**
     * Constructor.
     */
    private ExternalIdGenerationStrategyConverter() {
        super();
    }
    /**
     * From JAXB to REST.
     * @param source the source to convert
     * @return REST object
     */
    public static ExternalIdGenerationStrategyRO to(AbstractExternalIdGenerationStrategyDef source) {

        if (source == null) {
            return null;
        }

        switch (source.getStrategyType()) {
        case CONCAT:
            ConcatenatedExternalIdGenerationStrategyDef concatSource = (ConcatenatedExternalIdGenerationStrategyDef) source;
            ConcatExternalIdGenerationStrategyRO concatTarget = new ConcatExternalIdGenerationStrategyRO();
            concatTarget.setSeparator(concatSource.getSeparator());
            concatTarget.setAttributes(concatSource.getAttributes());
            return concatTarget;
        case RANDOM:
            return new RandomExternalIdGenerationStrategyRO();
        case SEQUENCE:
            return new SequenceExternalIdGenerationStrategyRO();
        default:
            break;
        }

        return null;
    }
    /**
     * From REST to JAXB.
     * @param source the source to convert
     * @return JAXB object
     */
    public static AbstractExternalIdGenerationStrategyDef from(ExternalIdGenerationStrategyRO source) {

        if (source == null) {
            return null;
        }

        switch (source.getStrategyType()) {
        case CONCAT:
            ConcatenatedExternalIdGenerationStrategyDef concatTarget
                = JaxbUtils.getMetaObjectFactory().createConcatenatedExternalIdGenerationStrategyDef();
            ConcatExternalIdGenerationStrategyRO concatSource = (ConcatExternalIdGenerationStrategyRO) source;
            concatTarget.setSeparator(concatSource.getSeparator());
            concatTarget.getAttributes().addAll(concatSource.getAttributes());
            return concatTarget;
        case RANDOM:
            return JaxbUtils.getMetaObjectFactory().createRandomExternalIdGenerationStrategyDef();
        case SEQUENCE:
            return JaxbUtils.getMetaObjectFactory().createSequenceExternalIdGenerationStrategyDef();
        default:
            break;
        }

        return null;
    }
}
