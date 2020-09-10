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

package com.unidata.mdm.backend.service.security.license;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.javax0.license3j.licensor.HardwareBinder;
import com.javax0.license3j.licensor.License;
import com.unidata.mdm.backend.common.license.EditionType;
import com.unidata.mdm.backend.common.license.OperationMode;
import com.unidata.mdm.backend.service.security.LicenseServiceExt;

/**
 * @author Dmitry Kopin on 02.05.2017.
 */
@Service
public class LicenseServiceImpl implements LicenseServiceExt {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseServiceImpl.class);
    /**
     * Main initialization. Read keyring and license. Check, decrypt, verify digest.
     */
    @Override
    public void afterContextRefresh() {
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public OperationMode getOperationMode() {
        return OperationMode.PRODUCTION_MODE;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public EditionType getEditionType() {
        return EditionType.STANDARD_EDITION;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwner() {
        return "open source";
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getExpirationDate() {
        return LocalDateTime.MAX;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getModules() {
        return new String[0];
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLicenseValid() {
        return true;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHardwareIdValid() {
        return true;
    }
}
