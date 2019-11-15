package org.unidata.mdm.core.type.data.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Objects;

import org.unidata.mdm.core.type.data.SimpleAttribute;

public class MeasuredSimpleAttributeImpl extends NumberSimpleAttributeImpl {

    /**
     * Measured value id
     */
    private String valueId;
    /**
     * User defined measured unit id
     */
    private String initialUnitId;
    /**
     * User defined value of attribute
     */
    private Double initialValue;

    /**
     * Special serialization constructor. Schould not be used otherwise.
     */
    protected MeasuredSimpleAttributeImpl() {
        super();
    }

    public MeasuredSimpleAttributeImpl(String name) {
        super(name);
    }

    public MeasuredSimpleAttributeImpl(String name, Double value) {
        super(name, value);
        this.initialValue = value;
    }

    public MeasuredSimpleAttributeImpl(SimpleAttribute<Double> numberSimpleAttribute) {
        super(numberSimpleAttribute.getName(), numberSimpleAttribute.getValue());
        this.initialValue = numberSimpleAttribute.getValue();
    }

    @Override
    public DataType getDataType() {
        return DataType.MEASURED;
    }

    /**
     * @param valueId - value id
     * @return self
     */
    public MeasuredSimpleAttributeImpl withValueId(String valueId) {
        this.valueId = valueId;
        return this;
    }

    /**
     * @param initialValue - initial value
     * @return self
     */
    public MeasuredSimpleAttributeImpl withInitialValue(Double initialValue) {
        this.initialValue = initialValue;
        return this;
    }

    /**
     * @param initialUnitId - initial unit id
     * @return self
     */
    public MeasuredSimpleAttributeImpl withInitialUnitId(String initialUnitId) {
        this.initialUnitId = initialUnitId;
        return this;
    }

    /**
     * @return value id
     */
    public String getValueId() {
        return valueId;
    }

    /**
     * @return initial value
     */
    public Double getInitialValue() {
        return initialValue;
    }

    /**
     * @return initial unit id
     */
    public String getInitialUnitId() {
        return initialUnitId;
    }

    /**
     * @return true if it is a measured number
     */
    public boolean isMeasurementMetaDataDefine() {
        return !(isBlank(getValueId()) || isBlank(getInitialUnitId()));
    }

    /**
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(getDataType(), getInitialValue(), getValueId(), getInitialUnitId());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (!super.equals(o)) {
            return false;
        }

        MeasuredSimpleAttributeImpl that = (MeasuredSimpleAttributeImpl) o;

        if (valueId != null ? !valueId.equals(that.valueId) : that.valueId != null) {
            return false;
        }

        if (initialUnitId != null ? !initialUnitId.equals(that.initialUnitId) : that.initialUnitId != null) {
            return false;
        }

        return initialValue.equals(that.initialValue);
    }

}
