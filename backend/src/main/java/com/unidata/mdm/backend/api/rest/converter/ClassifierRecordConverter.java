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
                    .approvalState(ro.getApproval() != null ? ApprovalState.valueOf(ro.getApproval()) : null)
                    .classifierEtalonKey(ro.getEtalonId())
                    .classifierName(ro.getClassifierName())
                    .classifierNodeId(ro.getClassifierNodeId())
                    .inactivateEtalon(true)
                    .build();
        }

        // Put data
        SerializableDataRecord result = new SerializableDataRecord(
                ro.getSimpleAttributes().size() +
                ro.getComplexAttributes().size());

        result.addAll(SimpleAttributeConverter.from(ro.getSimpleAttributes()));
        result.addAll(ComplexAttributeConverter.from(ro.getComplexAttributes()));

        return UpsertClassifierDataRequestContext.builder()
                .classifier(result)
                .approvalState(ro.getApproval() != null ? ApprovalState.valueOf(ro.getApproval()) : null)
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
        ComplexAttributeConverter.to(ec.getComplexAttributes(), ro.getComplexAttributes());

        ro.setEtalonId(ec.getInfoSection().getClassifierEtalonKey());
        ro.setClassifierNodeId(ec.getInfoSection().getNodeId());
        ro.setClassifierName(ec.getInfoSection().getClassifierName());
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
