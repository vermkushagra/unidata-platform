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

package com.unidata.mdm.backend.service.cleanse.composite;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.types.Attribute;

/**
 * @author Mikhail Mikhailov
 * Input/Output/Intermediate values holder.
 */
public class CompositeFunctionParam {
    /**
     * The values hold.
     */
    private List<Attribute> attributes;
    /**
     * Constructor.
     */
    public CompositeFunctionParam(CleanseFunctionInputParam ip) {
        super();
        this.attributes = Objects.isNull(ip) || ip.isEmpty() ? Collections.emptyList() : ip.getAttributes();
    }
    /**
     * Constructor.
     */
    public CompositeFunctionParam(CleanseFunctionOutputParam op) {
        super();
        this.attributes = Objects.isNull(op)? Collections.emptyList() : Collections.singletonList(op.getSingleton());
    }
    /**
     * @return the values
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }
    /**
     * Produces input param of the value hold.
     * @param portName the port name to use
     * @return input param
     */
    public CleanseFunctionInputParam toInputParam(String portName) {
        return CleanseFunctionInputParam.of(portName, attributes);
    }
    /**
     * Produces output param of the value hold.
     * @param portName the port name to use
     * @return output param
     */
    public CleanseFunctionOutputParam toOutputParam(String portName) {
        return CleanseFunctionOutputParam.of(portName, attributes.isEmpty() ? null : attributes.get(0));
    }
}
