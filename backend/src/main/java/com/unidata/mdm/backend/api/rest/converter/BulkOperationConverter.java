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
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.bulk.RemoveRelationsFromBulkOperationsRO;
import com.unidata.mdm.backend.service.bulk.RemoveRelationsFromConfiguration;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.bulk.BulkOperationBaseRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.ExportRecordsToXlsBulkOperationRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.ImportRecordsFromXlsBulkOperationRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.ModifyRecordsBulkOperationRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.RemoveRecordsBulkOperationRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.RepublishRecordsBulkOperationRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRelationToRO;
import com.unidata.mdm.backend.common.bulk.BulkOperationConfiguration;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonClassifierInfoSection;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.impl.EtalonClassifierImpl;
import com.unidata.mdm.backend.common.types.impl.EtalonRecordImpl;
import com.unidata.mdm.backend.common.types.impl.EtalonRelationImpl;
import com.unidata.mdm.backend.service.bulk.ExportToXlsConfiguration;
import com.unidata.mdm.backend.service.bulk.ImportFromXlsConfiguration;
import com.unidata.mdm.backend.service.bulk.ModifyRecordsConfiguration;
import com.unidata.mdm.backend.service.bulk.RemoveRecordsConfiguration;
import com.unidata.mdm.backend.service.bulk.RepublishRecordsConfiguration;

/**
 * @author Mikhail Mikhailov
 * Bulk operation configuration converter.
 */
public class BulkOperationConverter {

    /**
     * Constructor.
     */
    private BulkOperationConverter() {
        super();
    }

    /**
     * To system 'modify' configuration from REST.
     * @param source REST
     * @return system
     */
    private static ModifyRecordsConfiguration from(ModifyRecordsBulkOperationRO source) {
        ModifyRecordsConfiguration target = new ModifyRecordsConfiguration();
        DataRecord etalonRecord = DataRecordEtalonConverter.from(source.getEtalonRecordRO());
        target.setPartiallyFilledRecord(new EtalonRecordImpl(etalonRecord));
        target.setClassifierRecords(source.getEtalonRecordRO() == null || CollectionUtils.isEmpty(source.getEtalonRecordRO().getClassifiers())
                ? Collections.emptyList()
                : source.getEtalonRecordRO().getClassifiers()
                    .stream()
                    .map(cls -> new EtalonClassifierImpl()
                        .withDataRecord(ClassifierRecordConverter.from(cls, true))
                        .withInfoSection(new EtalonClassifierInfoSection()
                            .withClassifierName(cls.getClassifierName())
                            .withNodeId(cls.getClassifierNodeId())
                            .withApproval(cls.getApproval() == null ? ApprovalState.APPROVED : ApprovalState.valueOf(cls.getApproval()))))
                    .collect(Collectors.toList()));
        target.setEtalonRelations(from(source.getRelations()));
        return target;
    }

    /**
     * From rest relation format to internal relation format.
     * 
     * @param source relations from rest api.
     * @return relations in internal format.
     */
    private static List<EtalonRelation> from(List<EtalonRelationToRO> source) {
        if (source == null || source.size() == 0) {
            return null;
        }
        List<EtalonRelation> target = new ArrayList<>();
        for (EtalonRelationToRO s : source) {
            EtalonRelationImpl rel = new EtalonRelationImpl(RelationToEtalonConverter.from(s));
            rel.setInfoSection(new EtalonRelationInfoSection().withType(RelationType.REFERENCES)
                    .withToEtalonKey(EtalonKey.builder().id(s.getEtalonIdTo()).build()).withRelationName(s.getRelName()));
            target.add(rel);
        }
        return target;
    }
    /**
     * To system 'republish' configuration from REST.
     * @param source REST
     * @return system
     */
    private static RepublishRecordsConfiguration from(RepublishRecordsBulkOperationRO source) {

        RepublishRecordsConfiguration target = new RepublishRecordsConfiguration();
        // TODO implement.

        return target;
    }

    /**
     * To system 'import from XLS' configuration from REST.
     * @param source REST
     * @return system
     */
    private static ImportFromXlsConfiguration from(ImportRecordsFromXlsBulkOperationRO source) {

        ImportFromXlsConfiguration target = new ImportFromXlsConfiguration();
        // TODO implement.

        return target;
    }

    /**
     * To system 'export to XLS' configuration from REST.
     * @param source REST
     * @return system
     */
    private static ExportToXlsConfiguration from(ExportRecordsToXlsBulkOperationRO source) {

        ExportToXlsConfiguration target = new ExportToXlsConfiguration();
        // TODO implement.

        return target;
    }

    /**
     * To system 'remove' configuration from REST.
     * @param source REST
     * @return system
     */
    private static RemoveRecordsConfiguration from(RemoveRecordsBulkOperationRO source) {

        RemoveRecordsConfiguration target = new RemoveRecordsConfiguration();
        target.setWipeRecords(source.isWipe());

        return target;
    }

    private static RemoveRelationsFromConfiguration from(RemoveRelationsFromBulkOperationsRO source) {
        return new RemoveRelationsFromConfiguration(source.getRelationsNames());
    }

    /**
     * From REST to system.
     * @param source REST
     * @return system
     */
    public static BulkOperationConfiguration from(BulkOperationBaseRO source) {

        if (source == null) {
            return null;
        }

        switch (source.getType()) {
        case MODIFY_RECORDS:
            return from((ModifyRecordsBulkOperationRO) source);
        case REPUBLISH_RECORDS:
            return from((RepublishRecordsBulkOperationRO) source);
        case EXPORT_RECORDS_TO_XLS:
            return from((ExportRecordsToXlsBulkOperationRO) source);
        case IMPORT_RECORDS_FROM_XLS:
            return from((ImportRecordsFromXlsBulkOperationRO) source);
        case REMOVE_RECORDS:
            return from((RemoveRecordsBulkOperationRO) source);
        case REMOVE_RELATIONS_FROM:
            return from((RemoveRelationsFromBulkOperationsRO) source);
        default:
            break;
        }

        return null;
    }
}
