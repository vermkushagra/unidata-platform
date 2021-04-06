package com.unidata.mdm.backend.service.configuration;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.unidata.mdm.backend.common.configuration.ConfigurationConstants;
import com.unidata.mdm.backend.common.configuration.DumpTargetFormat;
import com.unidata.mdm.backend.common.configuration.PlatformConfiguration;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;

/**
 * @author Mikhail Mikhailov
 * Platform configuration.
 */
@Component
public class PlatformConfigurationImpl implements PlatformConfiguration, InitializingBean {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformConfigurationImpl.class);
    /**
     * Node ID. Fail, if null.
     */
    @Value("${" + ConfigurationConstants.UNIDATA_NODE_ID_PROPERTY + ":@null}")
    private String nodeId;
    /**
     * Version string. Fail, if null.
     */
    @Value("${" + ConfigurationConstants.PLATFORM_VERSION_PROPERTY + ":@null}")
    private String versionString;
    /**
     * The default dump target format.
     */
    @Value("${" + ConfigurationConstants.UNIDATA_DUMP_TARGET_FORMAT_PROPERTY + ":JAXB}")
    private DumpTargetFormat dumpTargetFormat;
    /**
     * Whether simon monitoring enabled or not.
     */
    @Value("${" + ConfigurationConstants.SIMON_STATISTIC_ENABLED_PROPERTY + ":false}")
    private boolean simonEnabled;
    /**
     * Major number.
     */
    private int platformMajor;
    /**
     * Minor number.
     */
    private int platformMinor;
    /**
     * Revision number.
     */
    private int platformRevision;
    /**
     * V1 generator.
     */
    private TimeBasedGenerator v1Generator;
    /**
     * V4 generator.
     */
    private RandomBasedGenerator v4Generator;
    /**
     * Constructor.
     */
    PlatformConfigurationImpl() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int getPlatformMajor() {
        return platformMajor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPlatformMinor() {
        return platformMinor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPlatformRevision() {
        return platformRevision;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNodeId() {
        return nodeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DumpTargetFormat getDumpTargetFormat() {
        return dumpTargetFormat;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String v1IdString() {
        return v1Generator.generate().toString();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UUID v1Id() {
        return v1Generator.generate();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String v4IdString() {
        return v4Generator.generate().toString();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UUID v4Id() {
        return v4Generator.generate();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        if (StringUtils.isBlank(nodeId) || nodeId.length() !=  12 || !StringUtils.containsOnly(nodeId, "0123456789abcdefABCDEF")) {
            final String message = "backend.properties either doesn't contain 'unidata.node.id' property or is not a 12 chracters hex number! Exiting!";
            LOGGER.error(message);
            throw new SystemRuntimeException(message, ExceptionId.EX_SYSTEM_NODE_ID_UNDEFINED);
        }

        initIdGenerators();
        initVersionNumbers();
        initMonitoring();
    }
    /**
     * Initializes node id
     */
    private void initIdGenerators() {

        byte[] octets = new byte[6];
        for (int i = 0; i < nodeId.length(); i += 2) {
            String octetAsString = nodeId.substring(i, i + 2);
            byte octet = (byte) Integer.parseInt(octetAsString, 16);
            octets[i / 2] = octet;
        }
        octets[0] |= (byte) 0x01; // Set multicast bit.

        this.v1Generator = Generators.timeBasedGenerator(new EthernetAddress(octets));
        this.v4Generator = Generators.randomBasedGenerator();
    }
    /**
     * Initializes version numbers.
     */
    private void initVersionNumbers() {

        if (StringUtils.isBlank(versionString)) {
            final String message = "backend.properties doesn't contain 'unidata.platform.version' property or platform version is not defined. Exiting!";
            LOGGER.error(message);
            throw new SystemRuntimeException(message, ExceptionId.EX_SYSTEM_PLATFORM_VERSION_UNDEFINED);
        }

        boolean versionFieldValid = false;
        String[] versionTokens = StringUtils.split(versionString, '.');
        if (versionTokens.length >= 2 && versionTokens.length <= 3) {
            try {
                platformMajor = Integer.parseInt(versionTokens[0]);
                platformMinor = Integer.parseInt(versionTokens[1]);
                platformRevision = versionTokens.length > 2 ? Integer.parseInt(versionTokens[2]) : 0;
                versionFieldValid = true;
            } catch (NumberFormatException e) {
                LOGGER.warn("NumberFormatException caught while parsing version number. Input {}.", versionString, e);
            }
        }

        if (!versionFieldValid) {
            final String message = "'unidata.platform.version' property contains invalid data. Version must be given in the major.minor[.revision optional] (all integer numbers) format. Exiting!";
            LOGGER.error(message);
            throw new SystemRuntimeException(message, ExceptionId.EX_SYSTEM_PLATFORM_VERSION_INVALID);
        }
    }
    /**
     * Initializes monitoring bits. Simon only so far.
     */
    private void initMonitoring() {
        MeasurementPoint.setEnabled(simonEnabled);
    }
}
