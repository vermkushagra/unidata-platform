package com.unidata.mdm.backend.common.cleanse;

import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.upath.UPathIncompletePath;
import com.unidata.mdm.backend.common.upath.UPathResult;

/**
 * @author Mikhail Mikhailov
 * Input param type.
 */
public class CleanseFunctionInputParam extends CleanseFunctionParam {
    /**
     * List of incomplete filtering attempts.
     */
    private List<UPathIncompletePath> incomplete;
    /**
     * @return the values
     */
    public List<Attribute> getAttributes() {
        return values;
    }
    /**
     * Creates input param.
     * @param portName the name of the port
     * @param value singleton attribute value
     * @return param
     */
    public static CleanseFunctionInputParam of(String portName, Attribute value) {
        return new CleanseFunctionInputParam(portName, Collections.singletonList(value));
    }
    /**
     * Creates input param.
     * @param portName the name of the port
     * @param values attributes value
     * @return param
     */
    public static CleanseFunctionInputParam of(String portName, List<Attribute> values) {
        return new CleanseFunctionInputParam(portName, values);
    }
    /**
     * Creates input param.
     * @param portName the name of the port
     * @param upathResult UPath execution result
     * @return param
     */
    public static CleanseFunctionInputParam of(String portName, UPathResult upathResult) {
        CleanseFunctionInputParam param = new CleanseFunctionInputParam(portName, upathResult.getAttributes());
        param.incomplete = upathResult.getIncomplete().isEmpty() ? Collections.emptyList() : upathResult.getIncomplete();
        return param;
    }
    /**
     * Constructor.
     * @param portName the name of the port
     * @param values the values to hold
     */
    private CleanseFunctionInputParam(String portName, List<Attribute> values) {
        super(ParamType.INPUT, portName, values);
    }
    /**
     * @return the incomplete
     */
    public List<UPathIncompletePath> getIncomplete() {
        return incomplete == null ? Collections.emptyList() : incomplete;
    }
}
