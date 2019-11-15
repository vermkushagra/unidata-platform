package org.unidata.mdm.meta.service.impl;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.tuple.Pair.of;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.impl.AbstractSimpleAttribute;
import org.unidata.mdm.core.type.data.impl.MeasuredSimpleAttributeImpl;
import org.unidata.mdm.core.type.measurement.MeasurementUnit;
import org.unidata.mdm.meta.service.MeasurementConversionService;
import org.unidata.mdm.meta.service.MetaMeasurementService;

@Component
public class MeasuredAttributeValueConverter {

    /**
     * Measurement service
     */
    @Autowired
    private MetaMeasurementService measurementService;
    /**
     * Measurement conversion service
     */
    @Autowired
    private MeasurementConversionService measurementConversionService;

    /**
     * All measured attributes will be modified,
     * Initial value will be used for calculating base value (value presented in base measurement unit)
     *
     * @param dataRecord - record
     */
    public void enrichMeasuredAttributesByBase(@Nonnull DataRecord dataRecord) {
        dataRecord.getAllAttributesRecursive()
                  .stream()
                  .filter(attr -> attr.getAttributeType() == Attribute.AttributeType.SIMPLE)
                  .filter(attr -> ((AbstractSimpleAttribute<?>) attr).getDataType() == SimpleAttribute.DataType.MEASURED)
                  .map(attr -> (MeasuredSimpleAttributeImpl) attr)
                  .filter(attr -> nonNull(attr.getValue()) && nonNull(attr.getValueId()) && nonNull(attr.getInitialUnitId()))
                  .map(attr -> of(attr, measurementService.getUnitById(attr.getValueId(), attr.getInitialUnitId())))
                  .filter(pair -> pair.getValue() != null)
                  .forEach(pair -> pair.getKey().setValue(getConvertedValue(pair.getKey(), pair.getValue())));
    }

    private Double getConvertedValue(@Nonnull MeasuredSimpleAttributeImpl input,
            @Nullable MeasurementUnit measurementUnit) {
        return measurementConversionService.convert(input.getInitialValue(), measurementUnit);
    }
}
