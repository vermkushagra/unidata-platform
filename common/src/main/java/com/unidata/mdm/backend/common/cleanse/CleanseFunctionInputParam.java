/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
