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

package org.unidata.mdm.data.serialization;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.data.convert.DataJaxbConverter;
import org.unidata.mdm.data.serialization.protostuff.Schemas;
import org.unidata.mdm.data.util.DataJaxbUtils;
import org.unidata.mdm.system.type.format.DumpTargetFormat;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;

/**
 * @author Mikhail Mikhailov on Oct 21, 2019
 */
public final class DataSerializer {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSerializer.class);
    /**
     * Constructor.
     */
    private DataSerializer() {
        super();
    }
    /**
     * Dumps to {@link DumpTargetFormat#PROTOSTUFF} target.
     * @param record the record
     * @return bytes
     */
    public static byte[] toProtostuff(DataRecord record) {

        if (record == null) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }

        return ProtostuffIOUtil.toByteArray(record, Schemas.DATA_RECORD_SCHEMA, LinkedBuffer.allocate());
    }
    /**
     * Restores from {@link DumpTargetFormat#PROTOSTUFF} target.
     * @param buf the buffer to restore.
     * @return record
     */
    public static DataRecord fromProtostuff(byte[] buf) {

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
    public static DataRecord fromProtostuff(InputStream in) {

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

        return DataJaxbUtils.marshalOriginRecord(DataJaxbConverter.to(record, null, org.unidata.mdm.data.OriginRecord.class));
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

        return DataJaxbUtils.marshalRelationTo(DataJaxbConverter.to(record, null, org.unidata.mdm.data.RelationTo.class));
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

        return DataJaxbConverter.from(DataJaxbUtils.unmarshalOriginRecord(content));
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

        return DataJaxbConverter.from(DataJaxbUtils.unmarshalRelationTo(content));
    }
}
