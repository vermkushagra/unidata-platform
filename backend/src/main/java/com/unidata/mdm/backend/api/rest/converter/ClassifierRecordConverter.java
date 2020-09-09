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
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.data.EtalonClassifierRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.OriginClassifierRecordRO;
import com.unidata.mdm.backend.common.context.ClassifierIdentityContext;
import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.dto.EtalonClassifierDTO;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.OriginClassifier;
import com.unidata.mdm.backend.common.types.RecordStatus;

public class ClassifierRecordConverter {

    private ClassifierRecordConverter() {
        super();
    }

    public static ClassifierIdentityContext from(EtalonClassifierRecordRO ro) {

        if (ro == null) {
            return null;
        }

        // Delete / unclassify
        if (Objects.isNull(ro.getClassifierNodeId()) && Objects.nonNull(ro.getEtalonId())) {
            return DeleteClassifierDataRequestContext.builder()
                    .classifierEtalonKey(ro.getEtalonId())
                    .classifierName(ro.getClassifierName())
                    .classifierNodeId(ro.getClassifierNodeId())
                    .inactivateEtalon(true)
                    .build();
        }

        // Put data
        SerializableDataRecord result = new SerializableDataRecord(
                ro.getSimpleAttributes().size()
                        + ro.getArrayAttributes().size()
                        + ro.getComplexAttributes().size()
        );

        result.addAll(SimpleAttributeConverter.from(ro.getSimpleAttributes()));
        result.addAll(ArrayAttributeConverter.from(ro.getArrayAttributes()));
        result.addAll(ComplexAttributeConverter.from(ro.getComplexAttributes()));

        return UpsertClassifierDataRequestContext.builder()
                .classifier(result)
                .classifierEtalonKey(ro.getEtalonId())
                .classifierName(ro.getClassifierName())
                .classifierNodeId(ro.getClassifierNodeId())
                .build();
    }

    /**
     * Converts from REST to system.
     * @param record the record
     * @return record
     */
    public static DataRecord from(EtalonClassifierRecordRO record, boolean noContext) {

        if (Objects.isNull(record)) {
            return null;
        }

        SerializableDataRecord result = new SerializableDataRecord(
                record.getSimpleAttributes().size() +
                record.getArrayAttributes().size() +
                record.getComplexAttributes().size() + 1);

        result.addAll(SimpleAttributeConverter.from(record.getSimpleAttributes()));
        result.addAll(ArrayAttributeConverter.from(record.getArrayAttributes()));
        result.addAll(ComplexAttributeConverter.from(record.getComplexAttributes()));

        return result;
    }

    public static EtalonClassifierRecordRO to(EtalonClassifierDTO dto) {

        if (Objects.isNull(dto) || Objects.isNull(dto.getEtalon())) {
            return null;
        }

        EtalonClassifierRecordRO ro = new EtalonClassifierRecordRO();
        EtalonClassifier ec = dto.getEtalon();
        SimpleAttributeConverter.to(ec.getSimpleAttributes(), ro.getSimpleAttributes());
        ro.setArrayAttributes(ArrayAttributeConverter.to(ec.getArrayAttributes()));
        ComplexAttributeConverter.to(ec.getComplexAttributes(), ro.getComplexAttributes());

        ro.setEtalonId(ec.getInfoSection().getClassifierEtalonKey());
        ro.setClassifierNodeId(ec.getInfoSection().getNodeId());
        ro.setClassifierName(ec.getInfoSection().getClassifierName());
        ro.setApproval(ec.getInfoSection().getApproval() == null
                ? ApprovalState.APPROVED.name()
                : ec.getInfoSection().getApproval().name());
        ro.setStatus(ec.getInfoSection().getStatus() == null
                ? RecordStatus.ACTIVE.name()
                : ec.getInfoSection().getStatus().name());

        return ro;
    }

    public static OriginClassifierRecordRO to(OriginClassifier ocl) {

        if (Objects.isNull(ocl)) {
            return null;
        }

        OriginClassifierRecordRO ro = new OriginClassifierRecordRO();
        SimpleAttributeConverter.to(ocl.getSimpleAttributes(), ro.getSimpleAttributes());
        ro.setArrayAttributes(ArrayAttributeConverter.to(ocl.getArrayAttributes()));
        ComplexAttributeConverter.to(ocl.getComplexAttributes(), ro.getComplexAttributes());

        ro.setOriginId(ocl.getInfoSection().getClassifierOriginKey());
        ro.setClassifierNodeId(ocl.getInfoSection().getNodeId());
        ro.setClassifierName(ocl.getInfoSection().getClassifierName());
        ro.setStatus(ocl.getInfoSection().getStatus() == null
                ? RecordStatus.ACTIVE.name()
                : ocl.getInfoSection().getStatus().name());

        return ro;
    }

    public static List<ClassifierIdentityContext> from(List<EtalonClassifierRecordRO> records) {

        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }

        List<ClassifierIdentityContext> result = new ArrayList<>(records.size());
        for (EtalonClassifierRecordRO ro : records) {
            ClassifierIdentityContext c = from(ro);
            if (c != null) {
                result.add(c);
            }
        }

        return result;
    }

    public static List<EtalonClassifierRecordRO> to(List<? extends EtalonClassifierDTO> records) {

        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }

        List<EtalonClassifierRecordRO> result = new ArrayList<>(records.size());
        for (EtalonClassifierDTO dto : records) {
            EtalonClassifierRecordRO ro = to(dto);
            if (ro != null) {
                result.add(ro);
            }
        }

        return result;
    }
}
