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

package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;

import com.unidata.mdm.backend.api.rest.dto.data.DiffToPreviousAttributeRO;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.TypeOfChange;

/**
 * @author Mikhail Mikhailov
 * Diff tables converter.
 */
public class RecordDiffStateConverter {

    /**
     * Constructor.
     */
    private RecordDiffStateConverter() {
        super();
    }

    public static List<DiffToPreviousAttributeRO> to (Map<String, Map<TypeOfChange, Attribute>> source) {

        if (MapUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<DiffToPreviousAttributeRO> target = new ArrayList<>(source.size());
        for (Entry<String, Map<TypeOfChange, Attribute>> entry : source.entrySet()) {

            // Map size is 1 normally
            Entry<TypeOfChange, Attribute> value = entry.getValue().entrySet().iterator().next();
            DiffToPreviousAttributeRO result = new DiffToPreviousAttributeRO();
            result.setAction(value.getKey().name());
            result.setPath(entry.getKey());

            Attribute attr = value.getValue();
            if (attr != null) {
                switch (attr.getAttributeType()) {
                case ARRAY:
                    ArrayAttribute<?> arrayAttr = attr.narrow();
                    result.setOldArrayValue(ArrayAttributeConverter.to(arrayAttr));
                    break;
                case CODE:
                    CodeAttribute<?> codeAttr = attr.narrow();
                    result.setOldCodeValue(CodeAttributeConverter.to(codeAttr));
                    break;
                case COMPLEX:
                    ComplexAttribute complexAttr = attr.narrow();
                    result.setOldComplexValue(ComplexAttributeConverter.to(complexAttr));
                    break;
                case SIMPLE:
                    SimpleAttribute<?> simpleAttr = attr.narrow();
                    result.setOldSimpleValue(SimpleAttributeConverter.to(simpleAttr));
                    break;
                }
            }

            target.add(result);
        }


        return target;
    }
}
