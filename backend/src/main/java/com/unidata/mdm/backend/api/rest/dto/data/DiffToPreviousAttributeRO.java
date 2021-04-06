package com.unidata.mdm.backend.api.rest.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiffToPreviousAttributeRO {

    private String path;

    private String action;

    private SimpleAttributeRO oldSimpleValue;

    private ComplexAttributeRO oldComplexValue;

    private CodeAttributeRO oldCodeValue;

    private ArrayAttributeRO oldArrayValue;
    /**
     * Constructor.
     */
    public DiffToPreviousAttributeRO() {
        super();
    }
    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }
    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }
    /**
     * @return the oldSimpleValue
     */
    public SimpleAttributeRO getOldSimpleValue() {
        return oldSimpleValue;
    }
    /**
     * @param oldSimpleValue the oldSimpleValue to set
     */
    public void setOldSimpleValue(SimpleAttributeRO oldSimpleValue) {
        this.oldSimpleValue = oldSimpleValue;
    }
    /**
     * @return the oldComplexValue
     */
    public ComplexAttributeRO getOldComplexValue() {
        return oldComplexValue;
    }
    /**
     * @param oldComplexValue the oldComplexValue to set
     */
    public void setOldComplexValue(ComplexAttributeRO oldComplexValue) {
        this.oldComplexValue = oldComplexValue;
    }
    /**
     * @return the oldCodeValue
     */
    public CodeAttributeRO getOldCodeValue() {
        return oldCodeValue;
    }
    /**
     * @param oldCodeValue the oldCodeValue to set
     */
    public void setOldCodeValue(CodeAttributeRO oldCodeValue) {
        this.oldCodeValue = oldCodeValue;
    }
    /**
     * @return the oldArrayValue
     */
    public ArrayAttributeRO getOldArrayValue() {
        return oldArrayValue;
    }
    /**
     * @param oldArrayValue the oldArrayValue to set
     */
    public void setOldArrayValue(ArrayAttributeRO oldArrayValue) {
        this.oldArrayValue = oldArrayValue;
    }

}
