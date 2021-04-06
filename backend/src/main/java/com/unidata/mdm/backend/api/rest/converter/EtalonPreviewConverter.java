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
