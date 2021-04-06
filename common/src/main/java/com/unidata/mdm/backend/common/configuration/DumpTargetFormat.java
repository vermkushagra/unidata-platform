package com.unidata.mdm.backend.common.configuration;

/**
 * @author Mikhail Mikhailov
 * Target format for data dumping.
 */
public enum DumpTargetFormat {

    JAXB,       // Native SOAP
    PROTOSTUFF  // Native binary.
}
