package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.settings.CustomStorageRecordRO;
import com.unidata.mdm.backend.common.dto.CustomStorageRecordDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CustomStorageRecordsConverter {

    public static List<CustomStorageRecordRO> convertToRO(List<CustomStorageRecordDTO> source) {
        if(source == null){
            return Collections.emptyList();
        }
        return source.stream().map(CustomStorageRecordsConverter::convertToRO).collect(Collectors.toList());
    }

    public static List<CustomStorageRecordDTO> convertToDTO(List<CustomStorageRecordRO> source) {
        if(source == null){
            return Collections.emptyList();
        }
        return source.stream().map(CustomStorageRecordsConverter::convertToDTO).collect(Collectors.toList());
    }

    public static CustomStorageRecordRO convertToRO(CustomStorageRecordDTO source) {
        CustomStorageRecordRO target = new CustomStorageRecordRO();
        target.setKey(source.getKey());
        target.setUser(source.getUser());
        target.setUpdateDate(source.getUpdateDate());
        target.setValue(source.getValue());
        return target;
    }

    public static CustomStorageRecordDTO convertToDTO(CustomStorageRecordRO source) {
        CustomStorageRecordDTO target = new CustomStorageRecordDTO();
        target.setKey(source.getKey());
        target.setUser(source.getUser());
        target.setUpdateDate(source.getUpdateDate());
        target.setValue(source.getValue());
        return target;
    }

}
