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

import com.unidata.mdm.backend.api.rest.dto.data.EtalonIntegralRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecordInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;

/**
 * @author Mikhail Mikhailov
 * Converts {@link EtalonIntegralRecordRO} type relations.
 */
public class IntegralRecordEtalonConverter {

    /**
     * Constructor.
     */
    private IntegralRecordEtalonConverter() {
        super();
    }

    /**
     * Converts to the REST type.
     * @param source the source
     * @return target
     */
    public static EtalonIntegralRecordRO to(EtalonRelation source) {

        if (source == null) {
            return null;
        }

        EtalonIntegralRecordRO target = new EtalonIntegralRecordRO();
        EtalonRecordRO record = DataRecordEtalonConverter
            .to(new EtalonRecordImpl()
                    .withDataRecord(source)
                    .withInfoSection(new EtalonRecordInfoSection()
                            .withApproval(source.getInfoSection().getApproval())
                            .withCreateDate(source.getInfoSection().getCreateDate())
                            .withCreatedBy(source.getInfoSection().getCreatedBy())
                            .withEntityName(source.getInfoSection().getToEntityName())
                            .withEtalonKey(source.getInfoSection().getToEtalonKey())
                            .withValidFrom(source.getInfoSection().getValidFrom())
                            .withValidTo(source.getInfoSection().getValidTo())), null, null);

        target.setEtalonRecord(record);
        target.setEtalonId(source.getInfoSection().getRelationEtalonKey());
        target.setRelName(source.getInfoSection().getRelationName());
        target.setCreateDate(source.getInfoSection().getCreateDate());
        target.setCreatedBy(source.getInfoSection().getCreatedBy());
        target.setStatus(source.getInfoSection().getStatus().name());
        target.setUpdateDate(source.getInfoSection().getUpdateDate());
        target.setUpdatedBy(source.getInfoSection().getUpdatedBy());

        return target;
    }

    /**
     * Converts from the REST type.
     * @param source the source
     * @return target
     */
    public static DataRecord from(EtalonIntegralRecordRO source) {

        if (source == null) {
            return null;
        }

        return DataRecordEtalonConverter.from(source.getEtalonRecord());
    }

    /**
     * Converts a list of {@link RelationToRO} type relations.
     * @param source the source list
     * @param target the destination
     */
    public static List<EtalonIntegralRecordRO> to(List<EtalonRelation> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<EtalonIntegralRecordRO> target = new ArrayList<>();
        for (EtalonRelation r : source) {
            target.add(to(r));
        }

        return target;
    }

    /**
     * Converts a list of {@link RelationToRO} type relations.
     * @param source the source list
     * @param target the destination
     * @param relName relation name
     */
    public static List<DataRecord> from(List<EtalonIntegralRecordRO> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<DataRecord> target = new ArrayList<>();
        for (EtalonIntegralRecordRO r : source) {
            target.add(from(r));
        }

        return target;
    }
}
