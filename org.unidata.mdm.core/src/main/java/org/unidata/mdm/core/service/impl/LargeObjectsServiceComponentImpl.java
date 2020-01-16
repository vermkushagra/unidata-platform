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

package org.unidata.mdm.core.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.unidata.mdm.core.context.DeleteLargeObjectRequestContext;
import org.unidata.mdm.core.context.FetchLargeObjectRequestContext;
import org.unidata.mdm.core.context.SaveLargeObjectRequestContext;
import org.unidata.mdm.core.dao.LargeObjectsDao;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.po.BinaryLargeObjectPO;
import org.unidata.mdm.core.po.CharacterLargeObjectPO;
import org.unidata.mdm.core.po.LargeObjectPO;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.util.AutodeleteTempFileInputStream;
import org.unidata.mdm.core.util.FileUtils;
import org.unidata.mdm.core.util.SecurityUtils;
import org.unidata.mdm.core.dto.LargeObjectDTO;
import org.unidata.mdm.core.service.LargeObjectsServiceComponent;
import org.unidata.mdm.system.exception.PlatformFailureException;
import org.unidata.mdm.system.util.IdUtils;


/**
 * @author Mikhail Mikhailov
 * LOB component.
 */
@Component
public class LargeObjectsServiceComponentImpl implements LargeObjectsServiceComponent {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LargeObjectsServiceComponentImpl.class);
    /**
     * LOB DAO.
     */
    @Autowired
    private LargeObjectsDao largeObjectsDao;

    @Override
    public LargeObjectDTO fetchLargeObject(FetchLargeObjectRequestContext ctx) {

        LargeObjectPO po = largeObjectsDao.fetchLargeObjectById(ctx.getRecordKey(), ctx.isBinary());
        if (Objects.isNull(po)) {
            return throwLoadFailure(ctx.isBinary(), null, ctx.getRecordKey());
        }

        try {

            File temp = File.createTempFile("unidata-lob-fetch-", ".out");
            try (FileOutputStream fis = new FileOutputStream(temp)) {

                int count;
                byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
                while ((count = po.getData().read(buf, 0, buf.length)) != -1) {
                    fis.write(buf, 0, count);
                }

                return new LargeObjectDTO(new AutodeleteTempFileInputStream(temp),
                        po.getId(),
                        po.getFileName(),
                        po.getMimeType(),
                        po.getSize());
            }

        } catch (IOException e) {
            return throwLoadFailure(ctx.isBinary(), e, ctx.getRecordKey());
        }
    }

    @Override
    public byte[] fetchLargeObjectByteArray(FetchLargeObjectRequestContext ctx) {

        try {
            LargeObjectPO po = largeObjectsDao.fetchLargeObjectById(ctx.getRecordKey(), ctx.isBinary());
            return IOUtils.toByteArray(po.getData());
        } catch (IOException e) {
            return throwLoadFailure(ctx.isBinary(), e, ctx.getRecordKey());
        }
    }

    private<T> T throwLoadFailure(boolean binary, Throwable cause, String objectId) {

        final String message = "Unable to load "
                + (binary ? "binary" : "character")
                + " LOB data. Object id [" + objectId + "].";

        if (cause != null) {
            LOGGER.warn(message, cause);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_DATA_CANNOT_LOAD_LOB, cause);
        } else {
            LOGGER.warn(message);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_DATA_CANNOT_LOAD_LOB);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LargeObjectDTO saveLargeObject(SaveLargeObjectRequestContext ctx) {

        try {

            String user = SecurityUtils.getCurrentUserName();
            LargeObjectPO po = ctx.isBinary() ? new BinaryLargeObjectPO() : new CharacterLargeObjectPO();
            Date now = new Date();

            if (ctx.getRecordKey() == null) {
                po.setId(IdUtils.v1String());
                po.setCreateDate(now);
                po.setCreatedBy(user);
                po.setState(ApprovalState.PENDING);
            } else {
                po.setId(ctx.getRecordKey());
                po.setUpdateDate(now);
                po.setUpdatedBy(user);
                if (ctx.isGolden() || ctx.isOrigin()) {
                    po.setState(ApprovalState.APPROVED);
                }
            }

            po.setClassifierId(ctx.getGoldenKey());
            po.setRecordId(ctx.getOriginKey());
            po.setEventId(ctx.getEventKey());
            po.setField(ctx.getAttribute());
            po.setFileName(ctx.getFilename());
            po.setMimeType(ctx.getMimeType());

            if (ctx.getInputStream() != null) {
                po.setData(ctx.getInputStream());
                po.setSize(ctx.getInputStream().available());
            }

            largeObjectsDao.upsertLargeObject(po);

            return new LargeObjectDTO(null, po.getId(), po.getFileName(), po.getMimeType(), po.getSize());
        } catch (IOException e) {
            throw new PlatformFailureException("Unable to save LOB data.", CoreExceptionIds.EX_DATA_CANNOT_SAVE_LOB, e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLargeObject(DeleteLargeObjectRequestContext ctx) {
        return largeObjectsDao.deleteLargeObject(ctx.getRecordKey(), ctx.getAttribute(), ctx.isBinary());
    }

    @Override
    public boolean checkExistLargeObject(FetchLargeObjectRequestContext ctx) {
        return largeObjectsDao.checkLargeObject(ctx.getRecordKey(), ctx.isBinary());
    }

}
