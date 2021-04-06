/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.backend.api.rest.dto.data.OriginRecordRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;

/**
 * @author Mikhail Mikhailov
 * Golden record to REST golden record converter.
 */
public class DataRecordOriginConverter {

    /**
     * Constructor.
     */
    private DataRecordOriginConverter() {
        super();
    }

    /**
     * Converts a golden record to a REST golden record.
     * @param source the record to convert
     * @return REST
     */
    public static OriginRecordRO to(OriginRecord source) {
        OriginRecordRO target = new OriginRecordRO();

        populateMainInformation(source, target);
        populateAttributeInformation(source, target);

        return target;
    }

    /**
     * Converts a golden record to a REST golden record with extended information.
     * @param source the record to convert
     * @return REST
     */
    public static OriginRecordRO to(OriginRecord source, EtalonRecord etalonRecord) {
        OriginRecordRO target = new OriginRecordRO();

        populateMainInformation(source, target);
        populateExtendedAttributeInformation(source, target, etalonRecord);

        return target;
    }

    /**
     * main populator for origin record ro
     * @param source source record to populate
     * @param target target record to populate
     */
    protected static void populateMainInformation(OriginRecord source, OriginRecordRO target) {

        OriginRecordInfoSection info = source.getInfoSection();

        if (info != null) {
            // Key
            if(info.getOriginKey() != null){
                target.setOriginId(info.getOriginKey().getId()); // System ID
                target.setExternalId(info.getOriginKey().getExternalId()); // Foreign ID
                target.setSourceSystem(info.getOriginKey().getSourceSystem()); // Source system
                target.setEntityName(info.getOriginKey().getEntityName()); // Entity name
                target.setGsn(info.getOriginKey().getGsn() == null ? null : info.getOriginKey().getGsn().toString()); // Entity Gsn
            } else {
                target.setOriginId(null); // System ID
                target.setExternalId(null); // Foreign ID
                target.setSourceSystem(null); // Source system
                target.setEntityName(null); // Entity name
                target.setGsn(null); // Entity Gsn
            }


            // Informational
            target.setValidFrom(ConvertUtils.date2LocalDateTime(info.getValidFrom()));
            target.setValidTo(ConvertUtils.date2LocalDateTime(info.getValidTo()));
            target.setCreateDate(info.getCreateDate());
            target.setUpdateDate(info.getUpdateDate());
            target.setCreatedBy(info.getCreatedBy());
            target.setUpdatedBy(info.getUpdatedBy());
            target.setRevision(info.getRevision());
            target.setStatus(info.getStatus() != null ? info.getStatus().name() : "");

        }
    }

    /**
     * attribute populator for origin record ro
     * @param source source record to populate
     * @param source target record to populate
     */
    protected static void populateAttributeInformation(OriginRecord source, OriginRecordRO target) {
        SimpleAttributeConverter.to(source.getSimpleAttributes(), target.getSimpleAttributes());
        ComplexAttributeConverter.to(source.getComplexAttributes(), target.getComplexAttributes());
        target.getCodeAttributes().addAll(CodeAttributeConverter.to(source.getCodeAttributes()));
        target.getArrayAttributes().addAll(ArrayAttributeConverter.to(source.getArrayAttributes()));
    }

    /**
     * attribute populator for origin record ro
     * @param source source record to populate
     * @param target target record to populate
     * @param etalonRecord etalon record
     */
    protected static void populateExtendedAttributeInformation(OriginRecord source, OriginRecordRO target, EtalonRecord etalonRecord) {
        if(etalonRecord != null &&
                source.getInfoSection().getOriginKey() != null &&
                StringUtils.isNotEmpty(source.getInfoSection().getOriginKey().getExternalId())){
            OriginKey originKey = source.getInfoSection().getOriginKey();
            SimpleAttributeConverter.to(source.getSimpleAttributes(), target.getSimpleAttributes(), etalonRecord, originKey);
            ComplexAttributeConverter.to(source.getComplexAttributes(), target.getComplexAttributes(), etalonRecord, originKey);
            target.getCodeAttributes().addAll(CodeAttributeConverter.to(source.getCodeAttributes(), etalonRecord, originKey));
            target.getArrayAttributes().addAll(ArrayAttributeConverter.to(source.getArrayAttributes(), etalonRecord, originKey));
        } else {
            populateAttributeInformation(source, target);
        }
    }

    /**
     * Converts from REST to system.
     * @param record the record
     * @return record
     */
    public static DataRecord from(OriginRecordRO record) {

        if (Objects.isNull(record)) {
            return null;
        }

        SerializableDataRecord result = new SerializableDataRecord(
                record.getSimpleAttributes().size() +
                record.getCodeAttributes().size() +
                record.getArrayAttributes().size() +
                record.getComplexAttributes().size() + 1);

        result.addAll(SimpleAttributeConverter.from(record.getSimpleAttributes()));
        result.addAll(CodeAttributeConverter.from(record.getCodeAttributes()));
        result.addAll(ArrayAttributeConverter.from(record.getArrayAttributes()));
        result.addAll(ComplexAttributeConverter.from(record.getComplexAttributes()));

        return result;
    }
}
