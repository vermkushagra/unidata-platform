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

import static java.util.Objects.nonNull;

import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.ExtendedRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.OriginRecordRO;
import com.unidata.mdm.backend.common.dto.GetRecordDTO;
import com.unidata.mdm.backend.common.types.OriginRecord;

import java.util.List;
import java.util.stream.Collectors;

public class EtalonPreviewConverter {

    public static ExtendedRecordRO convert(GetRecordDTO record) {
        ExtendedRecordRO extendedRecordRO = new ExtendedRecordRO();
        extendedRecordRO.setAttributeWinnersMap(record.getAttributeWinnersMap());
        if (nonNull(record.getEtalon())) {
            EtalonRecordRO nestedRecordRO = new EtalonRecordRO();
            SimpleAttributeConverter.to(record.getEtalon().getSimpleAttributes(), nestedRecordRO.getSimpleAttributes());
            ComplexAttributeConverter.to(record.getEtalon().getComplexAttributes(), nestedRecordRO.getComplexAttributes());

            nestedRecordRO.setClassifiers(ClassifierRecordConverter.to(record.getClassifiers().values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList())));

            nestedRecordRO.getCodeAttributes().addAll(CodeAttributeConverter.to(record.getEtalon().getCodeAttributes()));
            nestedRecordRO.getArrayAttributes().addAll(ArrayAttributeConverter.to(record.getEtalon().getArrayAttributes()));
            extendedRecordRO.setRecord(nestedRecordRO);
        } else if (nonNull(record.getOrigins()) && record.getOrigins().size() == 1) {
            OriginRecord originRecord = record.getOrigins().get(0);
            OriginRecordRO nestedRecordRO = new OriginRecordRO();
            SimpleAttributeConverter.to(originRecord.getSimpleAttributes(), nestedRecordRO.getSimpleAttributes());
            ComplexAttributeConverter.to(originRecord.getComplexAttributes(), nestedRecordRO.getComplexAttributes());
            nestedRecordRO.getCodeAttributes().addAll(CodeAttributeConverter.to(record.getEtalon().getCodeAttributes()));
            nestedRecordRO.getArrayAttributes().addAll(ArrayAttributeConverter.to(record.getEtalon().getArrayAttributes()));
            extendedRecordRO.setRecord(nestedRecordRO);
        }
        return extendedRecordRO;
    }

}
