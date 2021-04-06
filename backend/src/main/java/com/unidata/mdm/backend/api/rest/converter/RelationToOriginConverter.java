package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.unidata.mdm.backend.api.rest.dto.data.ComplexAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.OriginRelationToRO;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.OriginRelation;

/**
 * @author Mikhail Mikhailov
 * Converts {@link OriginRelationToRO} type relations.
 */
public class RelationToOriginConverter {

    /**
     * Constructor.
     */
    private RelationToOriginConverter() {
        super();
    }

    /**
     * Converts to the REST type.
     * @param source the source
     * @return target
     */
    public static OriginRelationToRO to(OriginRelation source) {

        if (source == null) {
            return null;
        }

        OriginRelationToRO target = new OriginRelationToRO();

        List<ComplexAttributeRO> complexAttributes = new ArrayList<>();
        ComplexAttributeConverter.to(source.getComplexAttributes(), complexAttributes);

        List<SimpleAttributeRO> simpleAttributes = new ArrayList<>();
        SimpleAttributeConverter.to(source.getSimpleAttributes(), simpleAttributes);

        target.setComplexAttributes(complexAttributes);
        target.setSimpleAttributes(simpleAttributes);
        target.setOriginIdTo(source.getInfoSection().getToOriginKey() != null ? source.getInfoSection().getToOriginKey().getId() : null);
        target.setEntityNameTo(source.getInfoSection().getToOriginKey() != null ? source.getInfoSection().getToOriginKey().getEntityName() : null);
        target.setExternalIdTo(source.getInfoSection().getToOriginKey() != null ? source.getInfoSection().getToOriginKey().getExternalId() : null);
        target.setSourceSystemTo(source.getInfoSection().getToOriginKey() != null ? source.getInfoSection().getToOriginKey().getSourceSystem() : null);
        target.setRelName(source.getInfoSection().getRelationName());
        target.setCreateDate(source.getInfoSection() != null ? source.getInfoSection().getCreateDate() : null);
        target.setCreatedBy(source.getInfoSection() != null ? source.getInfoSection().getCreatedBy() : null);
        target.setUpdateDate(source.getInfoSection() != null ? source.getInfoSection().getUpdateDate() : null);
        target.setUpdatedBy(source.getInfoSection() != null ? source.getInfoSection().getUpdatedBy() : null);
        target.setStatus(source.getInfoSection() != null
                ? (source.getInfoSection().getStatus() != null ? source.getInfoSection().getStatus().value() : null)
                : null);
        target.setValidFrom(source.getInfoSection() != null ? ConvertUtils.date2LocalDateTime(source.getInfoSection().getValidFrom()) : null);
        target.setValidTo(source.getInfoSection() != null ? ConvertUtils.date2LocalDateTime(source.getInfoSection().getValidTo()) : null);

        return target;
    }

    /**
     * Converts from the REST type.
     * @param source the source
     * @return target
     */
    public static DataRecord from(OriginRelationToRO source) {

        if (source == null) {
            return null;
        }

        SerializableDataRecord target = new SerializableDataRecord(
                source.getSimpleAttributes().size() +
                source.getComplexAttributes().size());

        List<Attribute> attributes = new ArrayList<>(
                source.getSimpleAttributes().size() +
                source.getComplexAttributes().size());

        attributes.addAll(ComplexAttributeConverter.from(source.getComplexAttributes()));
        attributes.addAll(SimpleAttributeConverter.from(source.getSimpleAttributes()));

        target.addAll(attributes);
        return target;
    }

    /**
     * Converts a list of {@link RelationToRO} type relations.
     * @param source the source list
     * @param target the destination
     */
    public static List<OriginRelationToRO> to(List<OriginRelation> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<OriginRelationToRO> target = new ArrayList<>();
        for (OriginRelation r : source) {
            target.add(to(r));
        }

        return target;
    }

    /**
     * Converts a list of {@link RelationToRO} type relations.
     * @param source the source list
     * @param target the destination
     * @param relName relation name
     */
    public static List<DataRecord> from(List<OriginRelationToRO> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<DataRecord> target = new ArrayList<>();
        for (OriginRelationToRO r : source) {
            target.add(from(r));
        }

        return target;
    }
}
