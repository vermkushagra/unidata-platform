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

/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.data.DQErrorRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.DataQualityError;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;

/**
 * @author Mikhail Mikhailov Golden record to REST golden record converter.
 */
public class DataRecordEtalonConverter {

    /**
     * Constructor.
     */
    private DataRecordEtalonConverter() {
        super();
    }

    /**
     * Converts a golden record to a REST golden record.
     * @param record the record
     * @param errors errors occurred, may be null
     * @param duplicates duplicates which may possibly exist
     * @return REST
     */
    public static EtalonRecordRO to(EtalonRecord record, List<DataQualityError> errors, List<String> duplicates) {

        if (record == null) {
            return null;
        }

        EtalonRecordRO result = new EtalonRecordRO();

        result.setDuplicateIds(duplicates);

        // Informational
        EtalonRecordInfoSection info = record.getInfoSection();
        if (info != null) {
            // Key
            result.setEtalonId(info.getEtalonKey().getId());

            result.setStatus(info.getStatus() != null ? info.getStatus().name() : null);
            result.setApproval(info.getApproval() != null ? info.getApproval().name() : null);
            result.setValidFrom(ConvertUtils.date2LocalDateTime(info.getValidFrom()));
            result.setValidTo(ConvertUtils.date2LocalDateTime(info.getValidTo()));
            result.setCreateDate(info.getCreateDate());
            result.setUpdateDate(info.getUpdateDate());
            result.setEntityName(info.getEntityName());
            result.setCreatedBy(info.getCreatedBy());
            result.setUpdatedBy(info.getUpdatedBy());

            result.setGsn(info.getEtalonKey().getGsn() == null ? null : info.getEtalonKey().getGsn().toString());
        }

        // Attributes
        SimpleAttributeConverter.to(record.getSimpleAttributes(), result.getSimpleAttributes());
        ComplexAttributeConverter.to(record.getComplexAttributes(), result.getComplexAttributes());
        result.getCodeAttributes().addAll(CodeAttributeConverter.to(record.getCodeAttributes()));
        result.getArrayAttributes().addAll(ArrayAttributeConverter.to(record.getArrayAttributes()));

        copyDQErrors(errors, result.getDqErrors());

        return result;
    }

    /**
     * System to REST list converter.
     * @param source the source
     * @param target the target
     */
    public static void to(List<EtalonRecord> source, List<EtalonRecordRO> target) {
        if (source == null) {
            return;
        }

        for (EtalonRecord r : source) {
            target.add(to(r, null, null));
        }
    }

    /**
     *
     * @param record
     * @return
     */
    public static DataRecord from(EtalonRecordRO record) {

        if (record == null) {
            return null;
        }

        SerializableDataRecord result = new SerializableDataRecord(
                record.getSimpleAttributes().size() +
                record.getCodeAttributes().size() +
                record.getArrayAttributes().size() +
                record.getComplexAttributes().size() + 1);

        result.addAll(SimpleAttributeConverter.from(record.getSimpleAttributes()));
        result.addAll(CodeAttributeConverter.from(record.getCodeAttributes()));
        result.addAll(ArrayAttributeConverter.from(record.getArrayAttributes()));
        result.addAll(ComplexAttributeConverter.from(record.getComplexAttributes()));

        return result;
    }

    /**
     * Converts DQ errors.
     * @param errors the source
     * @param target the target
     */
    public static void copyDQErrors(List<DataQualityError> errors, List<DQErrorRO> target) {

        if (CollectionUtils.isEmpty(errors)) {
            return;
        }

        for (DataQualityError sourceError : errors) {
            target.add(toDQErrorRO(sourceError));
        }
    }

    /**
     * Convert DQ error
     * @param sourceError source
     * @return target
     */
    public static DQErrorRO toDQErrorRO(DataQualityError sourceError) {

        DQErrorRO targetError = new DQErrorRO();
        targetError.setCategory(sourceError.getCategory());
        targetError.setMessage(sourceError.getMessage());
        targetError.setRuleName(sourceError.getRuleName());
        targetError.setSeverity(sourceError.getSeverity() != null ? sourceError.getSeverity().name() : null);
        targetError.setPaths(sourceError.getValues().stream().map(Pair::getKey).collect(Collectors.toList()));

        return targetError;
    }
}
