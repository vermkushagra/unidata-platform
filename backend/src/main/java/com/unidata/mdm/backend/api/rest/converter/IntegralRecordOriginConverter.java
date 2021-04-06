/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.data.EtalonRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.OriginIntegralRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.OriginRecordRO;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRecordInfoSection;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;

/**
 * @author Mikhail Mikhailov
 *
 */
public class IntegralRecordOriginConverter {

    /**
     * Constructor.
     */
    private IntegralRecordOriginConverter() {
        super();
    }

    /**
     * Converts to the REST type.
     * @param source the source
     * @return target
     */
    public static OriginIntegralRecordRO to(OriginRelation source) {

        if (source == null) {
            return null;
        }

        OriginIntegralRecordRO target = new OriginIntegralRecordRO();
        OriginRecordRO record = DataRecordOriginConverter
            .to(new OriginRecordImpl()
                    .withDataRecord(source)
                    .withInfoSection(new OriginRecordInfoSection()
                            .withApproval(source.getInfoSection().getApproval())
                            .withCreateDate(source.getInfoSection().getCreateDate())
                            .withCreatedBy(source.getInfoSection().getCreatedBy())
                            .withValidFrom(source.getInfoSection().getValidFrom())
                            .withValidTo(source.getInfoSection().getValidTo())
                            .withOriginKey(source.getInfoSection().getToOriginKey())));

        target.setOriginRecord(record);
        target.setOiginId(source.getInfoSection().getRelationOriginKey());
        target.setRelName(source.getInfoSection().getRelationName());
        target.setCreateDate(source.getInfoSection().getCreateDate());
        target.setCreatedBy(source.getInfoSection().getCreatedBy());
        target.setStatus(source.getInfoSection().getStatus().name());
        target.setUpdateDate(source.getInfoSection().getUpdateDate());
        target.setUpdatedBy(source.getInfoSection().getUpdatedBy());

        return target;
    }

    /**
     * Converts {@link EtalonRecordRO} type relation.
     * @param source the source
     * @param relName relation name
     * @return target
     */
    public static DataRecord from(OriginIntegralRecordRO source) {

        if (source == null) {
            return null;
        }

        return DataRecordOriginConverter.from(source.getOriginRecord());
    }

    /**
     * Converts a list of relations.
     * @param source the source list
     * @param target the destination
     * @param relName relation name
     */
    public static List<OriginIntegralRecordRO> to(List<OriginRelation> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<OriginIntegralRecordRO> target = new ArrayList<>();
        for (OriginRelation r : source) {
            target.add(to(r));
        }

        return target;
    }

    /**
     * Converts a list of relations.
     * @param source the source list
     * @param target the destination
     * @param relName relation name
     */
    public static List<DataRecord> from(List<OriginIntegralRecordRO> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<DataRecord> target = new ArrayList<>();
        for (OriginIntegralRecordRO r : source) {
            target.add(from(r));
        }

        return target;
    }
}
