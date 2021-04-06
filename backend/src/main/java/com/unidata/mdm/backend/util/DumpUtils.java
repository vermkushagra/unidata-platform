package com.unidata.mdm.backend.util;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unidata.mdm.backend.common.configuration.DumpTargetFormat;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.service.dump.jaxb.JaxbDataRecordUtils;
import com.unidata.mdm.backend.service.dump.protostuff.Schemas;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;

/**
 * @author Mikhail Mikhailov
 * Dump / extract record to / from a destination.
 */
public final class DumpUtils {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DumpUtils.class);
    /**
     * Constructor.
     */
    private DumpUtils() {
        super();
    }
    /**
     * Dumps to {@link DumpTargetFormat#PROTOSTUFF} target.
     * @param record the record
     * @return bytes
     */
    public static byte[] dumpToProtostuff(DataRecord record) {

        if (record == null) {
            return null;
        }

        return ProtostuffIOUtil.toByteArray(record, Schemas.DATA_RECORD_SCHEMA, LinkedBuffer.allocate());
    }
    /**
     * Restores from {@link DumpTargetFormat#PROTOSTUFF} target.
     * @param buf the buffer to restore.
     * @return record
     */
    public static DataRecord restoreFromProtostuff(byte[] buf) {

        if (buf != null && buf.length > 0) {
            SerializableDataRecord record = new SerializableDataRecord();
            ProtostuffIOUtil.mergeFrom(buf, record, Schemas.DATA_RECORD_SCHEMA);
            return record;
        }

        return null;
    }
    /**
     * Restores from {@link DumpTargetFormat#PROTOSTUFF} target.
     * @param buf the buffer to restore.
     * @return record
     */
    public static DataRecord restoreFromProtostuff(InputStream in) {

        if (in != null) {
            try {
                SerializableDataRecord record = new SerializableDataRecord();
                ProtostuffIOUtil.mergeFrom(in, record, Schemas.DATA_RECORD_SCHEMA);
                return record;
            } catch (IOException e) {
                LOGGER.error("Failed to restore data record from input stream.", e);
            }
        }

        return null;
    }
    /**
     * Dumps to {@link DumpTargetFormat#JAXB} target.
     * @param record the record to dump
     * @param hint class hint
     * @return string
     */
    public static String dumpOriginRecordToJaxb(DataRecord record) {

        if (record == null) {
            return null;
        }

        return JaxbUtils.marshalOriginRecord(JaxbDataRecordUtils.to(record, null, com.unidata.mdm.data.OriginRecord.class));
    }
    /**
     * Dumps to {@link DumpTargetFormat#JAXB} target.
     * @param record the record to dump
     * @param hint class hint
     * @return string
     */
    public static String dumpOriginClassifierToJaxb(DataRecord record) {

        if (record == null) {
            return null;
        }

        return JaxbUtils.marshalOriginClassifier(JaxbDataRecordUtils.to(record, null, com.unidata.mdm.data.OriginClassifierRecord.class));
    }
    /**
     * Dumps to {@link DumpTargetFormat#JAXB} target.
     * @param record the record to dump
     * @param hint class hint
     * @return string
     */
    public static String dumpOriginRelationToJaxb(DataRecord record) {

        if (record == null) {
            return null;
        }

        return JaxbUtils.marshalRelationTo(JaxbDataRecordUtils.to(record, null, com.unidata.mdm.data.RelationTo.class));
    }
    /**
     * Restores from {@link DumpTargetFormat#JAXB} target.
     * @param record the record to dump
     * @param hint class hint
     * @return string
     */
    public static DataRecord restoreOriginRecordFromJaxb(String content) {

        if (content == null || content.length() == 0) {
            return null;
        }

        return JaxbDataRecordUtils.from(JaxbUtils.unmarshalOriginRecord(content));
    }
    /**
     * Restores from {@link DumpTargetFormat#JAXB} target.
     * @param record the record to dump
     * @param hint class hint
     * @return string
     */
    public static DataRecord restoreOriginClassifierFromJaxb(String content) {

        if (content == null || content.length() == 0) {
            return null;
        }

        return JaxbDataRecordUtils.from(JaxbUtils.unmarshalOriginClassifier(content));
    }
    /**
     * Restores from {@link DumpTargetFormat#JAXB} target.
     * @param record the record to dump
     * @param hint class hint
     * @return string
     */
    public static DataRecord restoreOriginRelationFromJaxb(String content) {

        if (content == null || content.length() == 0) {
            return null;
        }

        return JaxbDataRecordUtils.from(JaxbUtils.unmarshalRelationTo(content));
    }
}
