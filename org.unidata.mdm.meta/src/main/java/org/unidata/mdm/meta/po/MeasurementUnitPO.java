package org.unidata.mdm.meta.po;

public class MeasurementUnitPO {

    private String id;
    private String shortName;
    private String name;
    private String valueId;
    private boolean isBase = false;
    private String convectionFunction;
    private int order;

    public String getConvectionFunction() {
        return convectionFunction;
    }

    public void setConvectionFunction(String convectionFunction) {
        this.convectionFunction = convectionFunction;
    }

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
