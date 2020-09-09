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

package com.unidata.mdm.backend.service.data.binary.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import com.unidata.mdm.backend.service.data.binary.LargeObjectsServiceComponent;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.unidata.mdm.backend.common.context.DeleteLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.FetchLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.dto.LargeObjectDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.dao.LargeObjectsDao;
import com.unidata.mdm.backend.po.BinaryLargeObjectPO;
import com.unidata.mdm.backend.po.CharacterLargeObjectPO;
import com.unidata.mdm.backend.po.LargeObjectPO;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.AutodeleteTempFileInputStream;
import com.unidata.mdm.backend.util.FileUtils;
import com.unidata.mdm.backend.util.IdUtils;

/**
 * @author Mikhail Mikhailov
 * LOB component.
 */
@Component
public class LargeObjectsServiceComponentImpl implements LargeObjectsServiceComponent{

    /**
     * LOB DAO.
     */
    @Autowired
    private LargeObjectsDao largeObjectsDao;

    @Override
    public LargeObjectDTO fetchLargeObject(FetchLargeObjectRequestContext ctx){

        try {
            LargeObjectPO po = largeObjectsDao.fetchLargeObjectById(ctx.getRecordKey(), ctx.isBinary());

            File temp = File.createTempFile("unidata-lob-fetch-", ".out");
            FileOutputStream fis = new FileOutputStream(temp);

            int count;
            byte[] buf = new byte[FileUtils.DEFAULT_BUFFER_SIZE];
            while ((count = po.getData().read(buf, 0, buf.length)) != -1) {
                fis.write(buf, 0, count);
            }
            fis.close();

            return new LargeObjectDTO(new AutodeleteTempFileInputStream(temp),
                    po.getId(),
                    po.getFileName(),
                    po.getMimeType(),
                    po.getSize());

        } catch (IOException e) {
            throw new DataProcessingException("Unable to load LOB data.", ExceptionId.EX_DATA_CANNOT_LOAD_LOB, e);
        }
    }

    @Override
    public byte[] fetchLargeObjectByteArray(FetchLargeObjectRequestContext ctx){
        try {
            LargeObjectPO po = largeObjectsDao.fetchLargeObjectById(ctx.getRecordKey(), ctx.isBinary());
            return IOUtils.toByteArray(po.getData());
        } catch (IOException e) {
            throw new DataProcessingException("Unable to load LOB data.", ExceptionId.EX_DATA_CANNOT_LOAD_LOB, e);
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

            po.setEtalonId(ctx.getGoldenKey());
            po.setOriginId(ctx.getOriginKey());
            po.setEventId(ctx.getEventKey());
            po.setField(ctx.getAttribute());
            po.setFileName(URLEncoder.encode(ctx.getFilename(), StandardCharsets.UTF_8.name()));
            po.setMimeType(ctx.getMimeType());

            if (ctx.getInputStream() != null) {
                po.setData(ctx.getInputStream());
                po.setSize(ctx.getInputStream().available());
            }

            largeObjectsDao.upsertLargeObject(po, ctx.isBinary());

            return new LargeObjectDTO(null, po.getId(), po.getFileName(), po.getMimeType(), po.getSize());
        } catch (IOException e) {
            throw new DataProcessingException("Unable to save LOB data.", ExceptionId.EX_DATA_CANNOT_SAVE_LOB, e);
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
