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
     * The digest.
     */
    private static final byte [] DIGEST = new byte[] {
            (byte)0x9D,
            (byte)0x22, (byte)0xDF, (byte)0x96, (byte)0x01, (byte)0x90, (byte)0x28, (byte)0xBD, (byte)0xD4,
            (byte)0x7F, (byte)0x2B, (byte)0x02, (byte)0xA1, (byte)0x6A, (byte)0xD4, (byte)0x99, (byte)0x25,
            (byte)0x30, (byte)0x6F, (byte)0x9D, (byte)0x4E, (byte)0x99, (byte)0xE5, (byte)0xF9, (byte)0x86,
            (byte)0xDB, (byte)0x02, (byte)0x80, (byte)0x31, (byte)0x5F, (byte)0xA2, (byte)0xEE, (byte)0xB8,
            (byte)0x6E, (byte)0x43, (byte)0xDE, (byte)0x92, (byte)0xAC, (byte)0x60, (byte)0x6A, (byte)0xC3,
            (byte)0x06, (byte)0x6D, (byte)0xE8, (byte)0xC4, (byte)0x8F, (byte)0xC4, (byte)0xF2, (byte)0x18,
            (byte)0x92, (byte)0xEF, (byte)0x15, (byte)0xBB, (byte)0x46, (byte)0x97, (byte)0xCA, (byte)0x17,
            (byte)0x26, (byte)0xC4, (byte)0x9B, (byte)0x5A, (byte)0x08, (byte)0x4B, (byte)0xF4,
    };
    /**
     * @author Mikhail Mikhailov
     * Supported fields in the license file.
     */
    enum LicenseFields {
        /**
         * Owner. The organization, that owns the license.
         */
        FIELD_OWNER("owner"),
        /**
         * Valid until timestamp without TZ.
         */
        FIELD_EXPIRATION_DATE("valid-until"),
        /**
         * Edition type. STANDARD, PERFORMANCE, etc.
         */
        FIELD_EDITION_TYPE("edition-type"),
        /**
         * Current operation mode. Production, development, etc.
         */
        FIELD_OPERATION_MODE("operation-mode"),
        /**
         * Active allowed modules enumeration.
         */
        FIELD_MODULES("modules"),
        /**
         * Hardware checks, that have to be performed to match current hardware key.
         */
        FIELD_HARDWARE_CHECKS("hw-checks"),
        /**
         * Hardware key, that must match the hardware, where the platform is running.
         */
        FIELD_HARDWARE_KEY("hw-key"),
        /**
         * Allowed nw ifaces.
         */
        FIELD_HW_IFS_ALLOWED("hw-network-interfaces-allowed"),
        /**
         * Denied nw ifaces.
         */
        FIELD_HW_IFS_DENIED("hw-network-interfaces-denied");
        /**
         * Constructor.
         * @param tag the tag to use
         */
        private LicenseFields(String tag) {
            this.tag = tag;
        }
        /**
         * Tag value.
         */
        private final String tag;
        /**
         * @return the tag
         */
        public String tag() {
            return tag;
        }
    }

    /**
     * @author Mikhail Mikhailov
     * Supported fields for license hardware checks.
     */
    enum LicenseHardwareChecks {
        /**
         * Arch.
         */
        HW_CHECK_ARCHITECTURE("architecture"),
        /**
         * Host name.
         */
        HW_CHECK_HOST_NAME("host-name"),
        /**
         * Perform nw checks or not.
         */
        HW_CHECK_NETWORK("network");
        /**
         * Safe create from tag value.
         * @param val the tag value
         * @return enum or null
         */
        public static LicenseHardwareChecks ofTagValue(String val) {

            for (int i = 0; i < values().length; i++) {
                if (values()[i].tag().equals(val)) {
                    return values()[i];
                }
            }

            return null;
        }
        /**
         * Constructor.
         * @param tag the tag to use
         */
        private LicenseHardwareChecks(String tag) {
            this.tag = tag;
        }
        /**
         * Tag value.
         */
        private final String tag;
        /**
         * @return the tag
         */
        public String tag() {
            return tag;
        }
    }
    /**
     * First check if that
     */
    @Value("${unidata.licensing.gpg.license.file:/license/license.bin}")
    private String licenseFilePath;
    /**
     * License owner.
     */
    private String owner;
    /**
     * valid-until of the current license.
     */
    private LocalDateTime expirationDate;
    /**
     * Current edition type.
     */
    private EditionType editionType;
    /**
     * Current operation mode.
     */
    private OperationMode operationMode;
    /**
     * Modules names.
     */
    private String[] modules = {};
    /**
     * Hardware checks to perform, according to the license.
     */
    private final Map<LicenseHardwareChecks, Boolean> hwChecks = new EnumMap<>(LicenseHardwareChecks.class);
    /**
     * The hardware key, which may have been defined (optional).
     */
    private String hwKey = null;
    /**
     * Allowed interfaces.
     */
    private String hwNetworkInterfacesAllowed = null;
    /**
     * Denied interfaces.
     */
    private String hwNetworkInterfacesDenied = null;
    /**
     * Main initialization. Read keyring and license. Check, decrypt, verify digest.
     */
    @Override
    public void afterContextRefresh() {

        // 1. License itself
        License license = new License();
        try {
            license.loadKeyRingFromResource("license/pubring.gpg", DIGEST);
        } catch (IOException e) {
            LOGGER.warn("IO exception caught, while loading pubring.", e);
            throw new IllegalAccessError("License error: license/pubring.gpg not found");
        }

        InputStream is = this.getClass().getResourceAsStream(licenseFilePath);
        try {
            if (null != is){
                license.setLicenseEncoded(is, "utf-8");
            } else { // Try as file
                license.setLicenseEncodedFromFile(licenseFilePath, "utf-8");
            }
        } catch (IOException | PGPException e) {
            LOGGER.warn("IO/PGP exception caught, while parsing license.", e);
            throw new IllegalAccessError("License error: Provided license file [" + licenseFilePath + "] not found or not valid!");
        }

        // 2. Owner
        owner = license.getFeature(LicenseFields.FIELD_OWNER.tag());

        // 3. Expiration date
        expirationDate = LocalDateTime.parse(
                license.getFeature(LicenseFields.FIELD_EXPIRATION_DATE.tag()),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // 4. Edition type
        final String editionTypeAsString = license.getFeature(LicenseFields.FIELD_EDITION_TYPE.tag());
        if (StringUtils.isBlank(editionTypeAsString)) {
            editionType = EditionType.STANDARD_EDITION;
        } else {
            editionType = EditionType.ofTagValue(editionTypeAsString);
        }

        // 5. Operation mode
        final String operationModeAsString = license.getFeature(LicenseFields.FIELD_OPERATION_MODE.tag());
        if (StringUtils.isBlank(operationModeAsString)) {
            operationMode = OperationMode.PRODUCTION_MODE;
        } else {
            operationMode = OperationMode.ofTagValue(operationModeAsString);
        }

        // 6. Modules
        final String modulesString = license.getFeature(LicenseFields.FIELD_MODULES.tag());
        if (StringUtils.isNoneBlank(modulesString)) {
            modules = modulesString.split(";");
        }

        // 7. Requested HW checks
        hwKey = license.getFeature(LicenseFields.FIELD_HARDWARE_KEY.tag());
        hwNetworkInterfacesAllowed = license.getFeature(LicenseFields.FIELD_HW_IFS_ALLOWED.tag());
        hwNetworkInterfacesDenied = license.getFeature(LicenseFields.FIELD_HW_IFS_DENIED.tag());

        Optional.ofNullable(license.getFeature(LicenseFields.FIELD_HARDWARE_CHECKS.tag()))
                .map(s -> Arrays.asList(s.toLowerCase().split(",")))
                .map(l -> l.stream()
                        .map(StringUtils::trim)
                        .map(LicenseHardwareChecks::ofTagValue)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(lhc -> lhc, lhc -> Boolean.TRUE)))
                .ifPresent(hwChecks::putAll);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public OperationMode getOperationMode() {
        return operationMode;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public EditionType getEditionType() {
        return editionType;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String getOwner() {
        return owner;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getModules() {
        return modules;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLicenseValid() {
        return !LocalDateTime.now().isAfter(expirationDate);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHardwareIdValid() {

        if (StringUtils.isNotBlank(hwKey) && !StringUtils.equals("*", hwKey)) {

            final HardwareBinder hardwareBinder = new HardwareBinder();
            if (!hwChecks.isEmpty()) {

                if (hwChecks.get(LicenseHardwareChecks.HW_CHECK_ARCHITECTURE) != Boolean.TRUE) {
                    hardwareBinder.ignoreArchitecture();
                }

                if (hwChecks.get(LicenseHardwareChecks.HW_CHECK_HOST_NAME) != Boolean.TRUE) {
                    hardwareBinder.ignoreHostName();
                }

                if (hwChecks.get(LicenseHardwareChecks.HW_CHECK_NETWORK) != Boolean.TRUE) {
                    hardwareBinder.ignoreNetwork();
                } else {

                    if (hwNetworkInterfacesAllowed != null) {
                        hardwareBinder.interfaceAllowed(hwNetworkInterfacesAllowed);
                    }

                    if (hwNetworkInterfacesDenied != null) {
                        hardwareBinder.interfaceDenied(hwNetworkInterfacesDenied);
                    }
                }
            }

            return hardwareBinder.assertUUID(hwKey);
        }

        return true;
    }
}
