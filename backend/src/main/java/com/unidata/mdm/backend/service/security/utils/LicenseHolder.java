package com.unidata.mdm.backend.service.security.utils;

import java.text.ParseException;

import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javax0.license3j.licensor.License;

/**
 * @author Dmitry Kopin on 02.05.2017.
 */
public class LicenseHolder {

    private LicenseHolder(){

    }
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseHolder.class);

    private static final String EXPIRATION_DATE = "valid-until";
    private static final String VERSION = "edition";

    private static final FastDateFormat LICENSE_FORMAT
            = FastDateFormat.getInstance("yyyy-MM-dd");

    private static License lic;

    public static void setLic(License license) {
        lic = license;
    }

    public static String getExpirationDate(){
        try {
         return LICENSE_FORMAT.format(LICENSE_FORMAT.parse(lic.getFeature(EXPIRATION_DATE)));
        } catch (ParseException exc){
            LOGGER.warn("Can't parse license expiration date");
            return null;
        }
    }


    public static String getVersion(){
        return lic.getFeature(VERSION);
    }

}
