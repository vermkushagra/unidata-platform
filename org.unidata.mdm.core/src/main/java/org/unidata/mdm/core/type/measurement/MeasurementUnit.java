package org.unidata.mdm.core.type.measurement;

import java.io.Serializable;

/**
 * Measurement unit it is a data structure which describe values like: grams, meters, volts etc
 */
public class MeasurementUnit implements Serializable{
    /**
     * Generated SVUID.
     */
    private static final long serialVersionUID = -385975928865040621L;
    /**
     * User defined unique id of unit
     */
    private String id;
    /**
     * User defined short display name
     */
    private String shortName;
    /**
     * User defined display name
     */
    private String name;
    /**
     * Id of related measurement value
     */
    private String valueId;
    /**
     * Flag which show it is unit base, or not. (Can be only one base unit in value)
     */
    private boolean isBase = false;
    /**
     * Conversion function to base unti, for base unit it equals (value).
     * Example: value*1000
     */
    private String convertionFunction;
    /**
     * Order in measurement value
     */
    private int order;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValueId() {
        return valueId;
    }

    public void setValueId(String valueId) {
        this.valueId = valueId;
    }

    public boolean isBase() {
        return isBase;
    }

    public void setBase(boolean base) {
        isBase = base;
    }

    public String getConvertionFunction() {
        return convertionFunction;
    }

    public void setConvertionFunction(String convectionFunction) {
        this.convertionFunction = convectionFunction;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "MeasurementUnit{" +
                "id='" + id + '\'' +
                ", shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", valueId='" + valueId + '\'' +
                ", isBase=" + isBase +
                ", convectionFunction='" + convertionFunction + '\'' +
                '}';
    }
}
