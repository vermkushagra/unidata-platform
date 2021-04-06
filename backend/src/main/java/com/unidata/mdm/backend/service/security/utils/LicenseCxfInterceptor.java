package com.unidata.mdm.backend.service.security.utils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.bouncycastle.openpgp.PGPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.javax0.license3j.licensor.License;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.LicenseException;
import com.unidata.mdm.backend.service.configuration.AfterContextRefresh;

/**
 * @author Pavel Alexeev.
 * @created 2016-02-24 20:37.
 */
public class LicenseCxfInterceptor extends AbstractPhaseInterceptor<Message> implements AfterContextRefresh {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseCxfInterceptor.class);

    /**
     * First check if that
     */
    @Value("${unidata.licensing.gpg.license.file:/license/license.bin}")
    private String licenseFilePath;

    private LocalDateTime validUntil = null;

    private byte [] digest = new byte[] {
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

    public LicenseCxfInterceptor() throws IOException, PGPException {
        super(Phase.RECEIVE);
        LOGGER.info("Register security interceptor {}", LicenseCxfInterceptor.class.getName());
    }

    /**
     * Main initialization. Read keyring and license. Check, decrypt, verify digest.
     */
    @Override
    public void afterContextRefresh(){
        License lic = new License();
        LicenseHolder.setLic(lic);

        try {
            lic.loadKeyRingFromResource("license/pubring.gpg", digest);
        } catch (IOException e) {
            throw new IllegalAccessError("License error: license/pubring.gpg not found");
        }
        InputStream is = this.getClass().getResourceAsStream(licenseFilePath);
        try {
            if (null != is){
                lic.setLicenseEncoded(is, "utf-8");
            }
            else { // Try as file
                lic.setLicenseEncodedFromFile(licenseFilePath, "utf-8");
            }
        } catch (IOException | PGPException e) {
            throw new IllegalAccessError("License error: Provided license file [" + licenseFilePath + "] not found or not valid!");
        }

        validUntil = LocalDateTime.parse(lic.getFeature("valid-until"), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        isLicenseValid();
    }

    /**
     * For now very simple check - license date validity. All other attributes ignored.
     *
     * @return true if it valid and is not outdated
     */
    private boolean isLicenseValid() {
        return !LocalDateTime.now().isAfter(validUntil);
    }

    @Override
    public void handleMessage(Message inMessage) throws Fault {
        if (!isLicenseValid()) {
            throw new LicenseException("You have no valid licenses installed!",
                    ExceptionId.EX_SECURITY_LICENSE_INVALID,
                    validUntil);
        }
    }
}
