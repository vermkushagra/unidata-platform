

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

package com.unidata.mdm.backend.service.job.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.service.data.export.DataExportService;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.common.service.UserService;

/**
 * Export partitioner.
 */
public class ExportPartitioner implements Partitioner {

    /** The stat service. */
    @Autowired
    @Qualifier(value = "xlsxExportService")
    private DataExportService dataExportService;

    /** The data record service. */
    @Autowired
    private DataRecordsService dataRecordService;

    /** The user service. */
    @Autowired
    private UserService userService;

    /** The job parameter holder. */
    @Autowired
    private ComplexJobParameterHolder jobParameterHolder;

    /** The meta model service. */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /** The mrctx key. */
    private String mrctxKey;

    /** The user name key. */
    private String userNameKey;
    /* (non-Javadoc)
     * @see org.springframework.batch.core.partition.support.Partitioner#partition(int)
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        //extract job parameters
        GetMultipleRequestContext mrCTX = jobParameterHolder.getComplexParameterAndRemove(getMrctxKey());
        String userName = jobParameterHolder.getComplexParameterAndRemove(getUserNameKey());
        //create xlsx file
        ByteArrayOutputStream data = dataExportService.exportData(mrCTX);
        //create user event
        boolean isLookup = metaModelService.isLookupEntity(mrCTX.getEntityName());
        String displayName = isLookup ? metaModelService.getLookupEntityById(mrCTX.getEntityName()).getDisplayName() :
                metaModelService.getEntityById(mrCTX.getEntityName()).getEntity().getDisplayName();
        UpsertUserEventRequestContext uueCtx
                = new UpsertUserEventRequestContextBuilder()
                .login(userName)
                .type("XLSX_EXPORT")
                .content(MessageUtils.getMessage(isLookup
                        ? UserMessageConstants.DATA_EXPORT_LOOKUP_RESULT
                        : UserMessageConstants.DATA_EXPORT_ENTITY_RESULT, displayName))
                .build();
        UserEventDTO userEventDTO = userService.upsert(uueCtx);
        //save exported file and attach it to the early created user event
        SaveLargeObjectRequestContext slorCTX
                = new SaveLargeObjectRequestContextBuilder()
                .eventKey(userEventDTO.getId())
                .mimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .binary(true)
                .inputStream(new ByteArrayInputStream(data.toByteArray()))
                .filename(mrCTX.getEntityName()+".xlsx")
                .build();
        dataRecordService.saveLargeObject(slorCTX);
        return new HashMap<>();
    }

    /**
     * Gets the mrctx key.
     *
     * @return the mrctxKey
     */
    public String getMrctxKey() {
        return mrctxKey;
    }

    /**
     * Sets the mrctx key.
     *
     * @param mrctxKey
     *            the mrctxKey to set
     */
    public void setMrctxKey(String mrctxKey) {
        this.mrctxKey = mrctxKey;
    }

    /**
     * Gets the user name key.
     *
     * @return the userNameKey
     */
    public String getUserNameKey() {
        return userNameKey;
    }

    /**
     * Sets the user name key.
     *
     * @param userNameKey
     *            the userNameKey to set
     */
    public void setUserNameKey(String userNameKey) {
        this.userNameKey = userNameKey;
    }
}
