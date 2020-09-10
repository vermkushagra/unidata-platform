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

package com.unidata.mdm.backend.service.security.utils;

import java.io.IOException;
import java.util.Objects;

import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.LicenseException;
import com.unidata.mdm.backend.common.service.LicenseService;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Pavel Alexeev.
 * @created 2016-02-24 20:37.
 */
public class LicenseCxfInterceptor extends AbstractPhaseInterceptor<Message> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseCxfInterceptor.class);
    /**
     * License service instance.
     */
    @Autowired
    private LicenseService licenseService;

    public LicenseCxfInterceptor() throws IOException, PGPException {
        super(Phase.RECEIVE);
        LOGGER.info("Register security interceptor {}", LicenseCxfInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message inMessage) {
    }
}
