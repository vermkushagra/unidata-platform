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

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeSimpleAttrDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.classifier.ClassifierValueDef;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * The Class ClassifierAttrConvert.
 */
@ConverterQualifier
@Component
public class ClassifierSimpleAttrConvert implements Converter<ClsfNodeSimpleAttrDTO, ClassifierValueDef> {

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.core.convert.converter.Converter#convert(java.lang.
     * Object)
     */
    @Override
    public ClassifierValueDef convert(ClsfNodeSimpleAttrDTO source) {
        ClassifierValueDef target = JaxbUtils.getClassifierObjectFactory().createClassifierValueDef();
        target.setPath(source.getAttrName());
        if (source.getLookupEntityCodeAttributeType() != null) {
            switch (source.getLookupEntityCodeAttributeType()) {
                case INTEGER:
                    target.withIntValue(TypeConverter.toLong(source.getDefaultValue()));
                    break;
                case STRING:
                    target.withStringValue((String) source.getDefaultValue());
                    break;
            }
            return target;
        }
        if (source.getDataType() == null) {
            throw new SystemRuntimeException("data type of classifier attribute should be defined",
                    ExceptionId.EX_DATA_CLASSIFIER_ATTRIBUTE_INCORRECT);
        }
        switch (source.getDataType()) {
            case BOOLEAN:
                target.withBoolValue(TypeConverter.toBoolean(source.getDefaultValue()));
                break;
            case DATE:
                target.withDateValue(TypeConverter.toDate(source.getDefaultValue()));
                break;
            case TIME:
                target.withTimeValue(TypeConverter.toTime(source.getDefaultValue()));
                break;
            case TIMESTAMP:
                target.withTimestampValue(TypeConverter.toTimestamp(source.getDefaultValue()));
                break;
            case INTEGER:
                target.withIntValue(TypeConverter.toLong(source.getDefaultValue()));
                break;
            case NUMBER:
                target.withNumberValue(TypeConverter.toDouble(source.getDefaultValue()));
                break;
            case STRING:
                target.withStringValue((String) source.getDefaultValue());
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
        return target;
    }
}
