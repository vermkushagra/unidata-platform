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
