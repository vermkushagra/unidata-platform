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

import com.unidata.mdm.backend.api.rest.dto.bulk.BulkOperationInformationBaseRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.ExportToXlsInformationRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.ImportFromXlsInformationRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.ModifyRecordsInformationRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.RemoveRelationsFromInformationRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.RemoveRecordsInformationRO;
import com.unidata.mdm.backend.api.rest.dto.bulk.RepublishRecordsInformationRO;
import com.unidata.mdm.backend.dto.bulk.BulkOperationInformationDTO;
import com.unidata.mdm.backend.dto.bulk.ExportToXlsInformationDTO;
import com.unidata.mdm.backend.dto.bulk.ImportFromXlsInformationDTO;
import com.unidata.mdm.backend.dto.bulk.ModifyRecordsInformationDTO;
import com.unidata.mdm.backend.dto.bulk.RemoveRelationsFromInformationDTO;
import com.unidata.mdm.backend.dto.bulk.RemoveRecordsInformationDTO;
import com.unidata.mdm.backend.dto.bulk.RepublishRecordsInformationDTO;

/**
 * @author Mikhail Mikhailov
 *
 */
public class BulkOperationInformationConverter {

    /**
     * Constructor.
     */
    private BulkOperationInformationConverter() {
        super();
    }

    /**
     * To system 'modify' configuration to REST.
     * @param source system
     * @return REST
     */
    private static BulkOperationInformationBaseRO from(ModifyRecordsInformationDTO source) {

        ModifyRecordsInformationRO target = new ModifyRecordsInformationRO();
        // TODO implement.

        return target;
    }

    /**
     * To system 'republish' configuration to REST.
     * @param source system
     * @return REST
     */
    private static BulkOperationInformationBaseRO from(RepublishRecordsInformationDTO source) {

        RepublishRecordsInformationRO target = new RepublishRecordsInformationRO();
        // TODO implement.

        return target;
    }

    /**
     * To system 'import from XLS' configuration to REST.
     * @param source system
     * @return REST
     */
    private static BulkOperationInformationBaseRO from(ImportFromXlsInformationDTO source) {

        ImportFromXlsInformationRO target = new ImportFromXlsInformationRO();
        // TODO implement.

        return target;
    }

    /**
     * To system 'export to XLS' configuration to REST.
     * @param source system
     * @return REST
     */
    private static BulkOperationInformationBaseRO from(ExportToXlsInformationDTO source) {

        ExportToXlsInformationRO target = new ExportToXlsInformationRO();
        // TODO implement.

        return target;
    }

    /**
     * To system 'remove' configuration to REST.
     *
     * @param source system
     * @return REST
     */
    private static BulkOperationInformationBaseRO from(RemoveRecordsInformationDTO source) {
        RemoveRecordsInformationRO target = new RemoveRecordsInformationRO();
        // TODO implement.

        return target;
    }

    private static BulkOperationInformationBaseRO from(RemoveRelationsFromInformationDTO source) {
        return new RemoveRelationsFromInformationRO();
    }

    /**
     * From conversion.
     * @param source DTO
     * @return REST or null
     */
    public static BulkOperationInformationBaseRO from(BulkOperationInformationDTO source) {

        if (source != null) {
            switch (source.getType()) {
            case MODIFY_RECORDS:
                return from((ModifyRecordsInformationDTO) source);
            case REPUBLISH_RECORDS:
                return from((RepublishRecordsInformationDTO) source);
            case EXPORT_RECORDS_TO_XLS:
                return from((ExportToXlsInformationDTO) source);
            case IMPORT_RECORDS_FROM_XLS:
                return from((ImportFromXlsInformationDTO) source);
            case REMOVE_RECORDS:
                return from((RemoveRecordsInformationDTO) source);
            case REMOVE_RELATIONS_FROM:
                return from((RemoveRelationsFromInformationDTO) source);
            default:
                break;
            }
        }

        return null;
    }
}
