package com.unidata.mdm.backend.service.data.listener.record;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.ComplexAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.MeasuredSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.meta.AttributeMeasurementSettingsDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SimpleDataType;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.unidata.mdm.backend.service.model.util.ModelUtils.getAttributePath;
import static org.apache.commons.lang3.tuple.Pair.of;

/**
 * Measured attributes normalizer
 */
public abstract class AbstractDataRecordUpsertMeasurementMetaSetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataRecordUpsertMeasurementMetaSetter.class);
    /**
     * Measurement service
     */
    @Autowired
    private MetaMeasurementService measurementService;

    /**
     * Meta model service
     */
    @Autowired
    private MetaModelServiceExt modelService;


    protected void processDataRecord(DataRecord record, String entityName, String parentName) {
        processSimpleAttrs(record.getAllAttributes(), entityName, parentName);
        record.getAllAttributes()
              .stream()
              .filter(attr -> attr.getAttributeType() == Attribute.AttributeType.COMPLEX)
              .map(attr -> (ComplexAttributeImpl) attr)
              .map(attr -> extractDataRecords(attr, parentName))
              .flatMap(Collection::stream)
              .forEach(pair -> processDataRecord(pair.getValue(), entityName, pair.getKey()));
    }

    private Collection<Pair<String, DataRecord>> extractDataRecords(ComplexAttributeImpl attribute, String parentName) {
        String attrName = attribute.getName();
        String fullName = getAttributePath(parentName, attrName);
        return attribute.getRecords().stream().map(rec -> of(fullName, rec)).collect(Collectors.toList());
    }

    private void processSimpleAttrs(Collection<Attribute> attributes, String entityName, String parentName) {
        attributes.stream()
                  .filter(attr -> attr.getAttributeType() == Attribute.AttributeType.SIMPLE)
                  .filter(attr -> isNumber((AbstractSimpleAttribute<?>) attr))
                  .map(attr -> of((AbstractSimpleAttribute<?>) attr,
                          (SimpleAttributeDef) modelService.getAttributeByPath(entityName,
                                  getAttributePath(parentName, attr.getName()))))
                  .filter(pair -> pair.getValue() != null)
                  .filter(pair -> pair.getValue().getSimpleDataType() == SimpleDataType.MEASURED)
                  .forEach(pair -> normalize(pair.getKey(), pair.getValue()));
    }

    private boolean isNumber(AbstractSimpleAttribute<?> attr) {
        SimpleAttribute.DataType attrType = attr.getDataType();
        return attrType == SimpleAttribute.DataType.MEASURED || attrType == SimpleAttribute.DataType.NUMBER;
    }

    private void normalize(AbstractSimpleAttribute<?> attr, SimpleAttributeDef simpleAttributeDef) {
        DataRecord record = attr.getRecord();
        AttributeMeasurementSettingsDef measureSettings = simpleAttributeDef.getMeasureSettings();
        if (measureSettings == null) {
            throw new BusinessException("Attribute settings is not define",
                    ExceptionId.EX_DATA_UPSERT_MEASUREMENT_VALUE_UNAVAILABLE, attr.getName(),
                    simpleAttributeDef.getDisplayName());
        }

        MeasuredSimpleAttributeImpl measuredAttribute = getMeasuredAttrs(attr);
        MeasurementValue value = measurementService.getValueById(measureSettings.getValueId());
        if (value == null) {
            throw new BusinessException("Value doesn't exist", ExceptionId.EX_MEASUREMENT_VALUE_DOESNT_EXIST,
                    measureSettings.getValueId());
        }

        if (measuredAttribute.isMeasurementMetaDataDefine()) {
            //check value and unit
            boolean isValueSame = measuredAttribute.getValueId().equals(value.getId());
            if (!isValueSame) {
                throw new BusinessException("Attribute value is not the same",
                        ExceptionId.EX_DATA_UPSERT_WRONG_MEASUREMENT_VALUES, measuredAttribute.getValueId());
            }
            boolean isUnitCorrect = value.present(measuredAttribute.getInitialUnitId());
            if (!isUnitCorrect) {
                throw new BusinessException("Value doesn't contain unit ",
                        ExceptionId.EX_DATA_UPSERT_MEASUREMENT_UNIT_UNAVAILABLE, attr.getName(),
                        measuredAttribute.getInitialUnitId());
            }
        } else {
            //set default
            measuredAttribute.withInitialUnitId(value.getBaseUnitId());
            measuredAttribute.withValueId(value.getId());
            if (measuredAttribute.getInitialValue() == null && measuredAttribute.getValue() != null) {
                try {
                    //Ability work with types like a long , int, float, or even correct string.
                    Object untypedValue  =  measuredAttribute.getValue();
                    String stringValue = untypedValue.toString();
                    Double doubleValue = Double.parseDouble(stringValue);
                    measuredAttribute.withInitialValue(doubleValue);
                    measuredAttribute.withValue(doubleValue);
                } catch (Exception e) {
                    LOGGER.error("Incorrect measurement value type", e);
                    throw new BusinessException("Incorrect measurement value type",
                            ExceptionId.EX_DATA_UPSERT_ENRICH_MEASUREMENT_VALUE_IS_INCORRECT, attr.getName(),
                            measuredAttribute.getValue());
                }
            }
        }

        record.addAttribute(measuredAttribute);
    }

    private MeasuredSimpleAttributeImpl getMeasuredAttrs(AbstractSimpleAttribute<?> attr) {
        SimpleAttribute.DataType attrType = attr.getDataType();
        if (attrType == SimpleAttribute.DataType.NUMBER) {
            //from dq
            return new MeasuredSimpleAttributeImpl((NumberSimpleAttributeImpl) attr);
        } else {
            return (MeasuredSimpleAttributeImpl) attr;
        }
    }
}
