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

package com.unidata.mdm.backend.converter.classifiers;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeArrayAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.classifier.AbstractClassifierAttributeDef;
import com.unidata.mdm.classifier.ArrayAttributeWithOptionalValueDef;
import com.unidata.mdm.classifier.ClassifierValueDef;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

@ConverterQualifier
@Component
public class ClassifierArrayAttrDefConvert implements Converter<ClsfNodeArrayAttrDTO, ArrayAttributeWithOptionalValueDef> {

    @Autowired
    private Converter<ClsfNodeAttrDTO, AbstractClassifierAttributeDef> converter;

    @Override
    public ArrayAttributeWithOptionalValueDef convert(ClsfNodeArrayAttrDTO source) {
        ArrayAttributeWithOptionalValueDef attr = (ArrayAttributeWithOptionalValueDef) converter.convert(source);
        attr.setUnique(source.isUnique());
        attr.setNullable(source.isNullable());
        attr.setMask(null);
        attr.setOrder(null);

        if (CollectionUtils.isNotEmpty(source.getValues())) {
            attr.withValues(
                    source.getValues().stream()
                            .map(valueConvertor(source))
                            .collect(Collectors.toList())
            );
        }
        return attr;
    }

    private Function<Object, ClassifierValueDef> valueConvertor(ClsfNodeArrayAttrDTO source) {
        return (value) -> {
            if (value == null) {
                return null;
            }
            ClassifierValueDef target = JaxbUtils.getClassifierObjectFactory().createClassifierValueDef();
            target.setPath(source.getAttrName());
            if (source.getLookupEntityCodeAttributeType() != null) {
                switch (source.getLookupEntityCodeAttributeType()) {
                    case INTEGER:
                        target.withIntValue(TypeConverter.toLong(value));
                        break;
                    case STRING:
                        target.withStringValue((String) value);
                        break;
                }
                return target;
            }
            if (source.getDataType() == null) {
                throw new SystemRuntimeException("data type of classifier attribute should be defined",
                        ExceptionId.EX_DATA_CLASSIFIER_ATTRIBUTE_INCORRECT);
            }
            try {
                switch (source.getDataType()) {
                    case BOOLEAN:
                        target.withBoolValue(TypeConverter.toBoolean(value));
                        break;
                    case DATE:
                        target.withDateValue(TypeConverter.toDate(value));
                        break;
                    case TIME:
                        target.withTimeValue(TypeConverter.toTime(value));
                        break;
                    case TIMESTAMP:
                        target.withTimestampValue(TypeConverter.toTimestamp(value));
                        break;
                    case INTEGER:
                        target.withIntValue(TypeConverter.toLong(value));
                        break;
                    case NUMBER:
                        target.withNumberValue(TypeConverter.toDouble(value));
                        break;
                    case STRING:
                        target.withStringValue((String) value);
                        break;
                    case BLOB:
                        throw new SystemRuntimeException("BLOB data type is not supported!",
                                ExceptionId.EX_DATA_CLASSIFIER_ATTRIBUTE_INCORRECT);
                    case CLOB:
                        throw new SystemRuntimeException("CLOB data type is not supported!",
                                ExceptionId.EX_DATA_CLASSIFIER_ATTRIBUTE_INCORRECT);
                    default:
                        break;
                }
            }
            catch (SystemRuntimeException sre) {
                throw sre;
            }
            catch (Exception ex) {
                throw new SystemRuntimeException(
                        "Can't convert " + source.getAttrName() + " value " + value + " of type " + source.getDataType(),
                        ExceptionId.EX_CLASSIFIER_WRONG_ATTRIBUTE_VALUE_OF_TYPE,
                        source.getAttrName(), value, source.getDataType()
                );
            }
            return target;
        };
    }
}
