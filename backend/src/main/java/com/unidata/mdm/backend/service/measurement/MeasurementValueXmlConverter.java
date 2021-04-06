package com.unidata.mdm.backend.service.measurement;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.service.measurement.data.MeasurementUnit;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.MeasurementUnitDef;
import com.unidata.mdm.meta.MeasurementValueDef;
import com.unidata.mdm.meta.MeasurementValues;

public class MeasurementValueXmlConverter {

    /**
     * Measurement value {@link QName}.
     */
    private static final QName MEASUREMENT_VALUE_QNAME = new QName("http://meta.mdm.unidata.com/", "MeasurementValues", "measurementValues");

    private static final Comparator<MeasurementUnit> MEASUREMENT_UNIT_COMPARATOR = (o1, o2) -> Integer.compare(o1.getOrder(),o2.getOrder());

    @Nonnull
    public static MeasurementValue convert(@Nonnull MeasurementValueDef measurementValueDef) {
        MeasurementValue measurementValue = new MeasurementValue();
        measurementValue.setShortName(measurementValueDef.getShortName());
        measurementValue.setId(measurementValueDef.getId());
        measurementValue.setName(measurementValueDef.getDisplayName());
        int order = 0;
        Map<String, MeasurementUnit> units = new HashMap<>();
        for (MeasurementUnitDef unitDef : measurementValueDef.getUnit()) {
            MeasurementUnit unit = convert(unitDef);
            unit.setOrder(order);
            unit.setValueId(measurementValue.getId());
            Object prevValue = units.put(unitDef.getId(), unit);
            if (prevValue != null) {
                throw new BusinessException("Duplicate unit ids", ExceptionId.EX_MEASUREMENT_UNITS_IDS_DUPLICATED,
                        unitDef.getId() , measurementValue.getName());
            }
            order++;
        }
        measurementValue.setMeasurementUnits(units);
        MeasurementUnit base = measurementValue.getMeasurementUnits().stream().filter(MeasurementUnit::isBase).findAny().orElse(null);
        measurementValue.setBaseUnitId(base == null ? null : base.getId());
        return measurementValue;
    }

    @Nonnull
    public static MeasurementUnit convert(@Nonnull MeasurementUnitDef unitDef) {
        MeasurementUnit measurementUnitDto = new MeasurementUnit();
        measurementUnitDto.setName(unitDef.getDisplayName());
        measurementUnitDto.setId(unitDef.getId());
        measurementUnitDto.setShortName(unitDef.getShortName());
        measurementUnitDto.setBase(unitDef.isBase());
        measurementUnitDto.setConvectionFunction(unitDef.getConvectionFunction());
        return measurementUnitDto;
    }

    @Nonnull
    public static MeasurementValueDef convert(@Nonnull MeasurementValue measurementValueDef) {
        MeasurementValueDef measurementValue = new MeasurementValueDef();
        measurementValue.setShortName(measurementValueDef.getShortName());
        measurementValue.setId(measurementValueDef.getId());
        measurementValue.setDisplayName(measurementValueDef.getName());
        measurementValue.withUnit(measurementValueDef.getMeasurementUnits()
                                                      .stream()
                                                      .sequential()
                                                      .sorted(MEASUREMENT_UNIT_COMPARATOR)
                                                      .map(MeasurementValueXmlConverter::convert)
                                                      .collect(Collectors.toList()));
        return measurementValue;
    }

    @Nonnull
    public static MeasurementUnitDef convert(@Nonnull MeasurementUnit unitDef) {
        MeasurementUnitDef measurementUnitDef = new MeasurementUnitDef();
        measurementUnitDef.setDisplayName(unitDef.getName());
        measurementUnitDef.setId(unitDef.getId());
        measurementUnitDef.setShortName(unitDef.getShortName());
        measurementUnitDef.withBase(unitDef.isBase());
        measurementUnitDef.setConvectionFunction(unitDef.getConvectionFunction());
        return measurementUnitDef;
    }

    @Nonnull
    public static byte[] convertToByteArray(@Nonnull MeasurementValues valueDef) throws JAXBException {
        JAXBElement<MeasurementValues> jaxb = new JAXBElement<>(MEASUREMENT_VALUE_QNAME, MeasurementValues.class, null, valueDef);
        StringWriter sw = new StringWriter();
        Marshaller marshaller = JaxbUtils.getMetaContext().createMarshaller();
        marshaller.marshal(jaxb, sw);
        return sw.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Nonnull
    public static MeasurementValues convert(@Nonnull File file) throws JAXBException {
        return JaxbUtils.getMetaContext().createUnmarshaller().unmarshal(new StreamSource(file), MeasurementValues.class).getValue();
    }

}
