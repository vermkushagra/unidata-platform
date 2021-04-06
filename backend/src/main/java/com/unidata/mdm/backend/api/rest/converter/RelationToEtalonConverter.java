package com.unidata.mdm.backend.api.rest.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.data.ComplexAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.EtalonRelationToRO;
import com.unidata.mdm.backend.api.rest.dto.data.SimpleAttributeRO;
import com.unidata.mdm.backend.common.ConvertUtils;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.meta.RelationDef;

/**
 * @author Mikhail Mikhailov
 * Converts {@link EtalonRelationToRO} type relations.
 */
public class RelationToEtalonConverter {

    /**
     * Constructor.
     */
    private RelationToEtalonConverter() {
        super();
    }

    /**
     * Converts to the REST type.
     * @param source the source
     * @return target
     */
    public static EtalonRelationToRO to(EtalonRelation source) {

        if (source == null) {
            return null;
        }

        EtalonRelationToRO target = new EtalonRelationToRO();

        List<ComplexAttributeRO> complexAttributes = new ArrayList<>();
        ComplexAttributeConverter.to(source.getComplexAttributes(), complexAttributes);

        List<SimpleAttributeRO> simpleAttributes = new ArrayList<>();
        SimpleAttributeConverter.to(source.getSimpleAttributes(), simpleAttributes);

        target.setComplexAttributes(complexAttributes);
        target.setSimpleAttributes(simpleAttributes);
        target.setEtalonIdTo(source.getInfoSection().getToEtalonKey() != null ? source.getInfoSection().getToEtalonKey().getId() : null);
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

    public static List<EtalonRelationToRO> to(SearchResultDTO searchResultDTO, RelationDef relationDef){
        List<EtalonRelationToRO> result = new ArrayList<>();
        Set<String> processedRels = new HashSet<>();
        if(CollectionUtils.isNotEmpty(searchResultDTO.getHits())){
            for(SearchResultHitDTO hit : searchResultDTO.getHits()){
                if(!processedRels.contains(hit.getId())){
                    processedRels.add(hit.getId());
                    EtalonRelationToRO target = new EtalonRelationToRO();
                    target.setRelName(relationDef.getName());
                    // right now only active relations from search system
                    target.setStatus(RecordStatus.ACTIVE.value());

                    target.setEtalonId(hit.getId());
                    target.setEtalonIdTo(hit.getFieldValue(RelationHeaderField.FIELD_TO_ETALON_ID.getField()).getFirstValue().toString());

                    target.setCreateDate(SearchUtils.parse(
                            hit.getFieldValue(RelationHeaderField.FIELD_CREATED_AT.getField()).getFirstValue().toString()));
                    target.setUpdateDate(SearchUtils.parse(
                            hit.getFieldValue(RelationHeaderField.FIELD_UPDATED_AT.getField()).getFirstValue().toString()));

                    target.setValidFrom(ConvertUtils.date2LocalDateTime(SearchUtils.parse(
                            hit.getFieldValue(RelationHeaderField.FIELD_FROM.getField()).getFirstValue().toString())));
                    target.setValidTo(ConvertUtils.date2LocalDateTime(SearchUtils.parse(
                            hit.getFieldValue(RelationHeaderField.FIELD_TO.getField()).getFirstValue().toString())));

                    target.setSimpleAttributes(SimpleAttributeConverter.to(hit, relationDef.getName() + ".", relationDef.getSimpleAttribute()));
                    result.add(target);
                }
            }
        }
        return result;
    }

    /**
     * Converts from the REST type.
     * @param source the source
     * @return target
     */
    public static DataRecord from(EtalonRelationToRO source) {

        if (source == null) {
            return null;
        }

        SerializableDataRecord result = new SerializableDataRecord(
                source.getComplexAttributes().size() +
                source.getSimpleAttributes().size());

        List<Attribute> attributes = new ArrayList<>(
                source.getComplexAttributes().size() +
                source.getSimpleAttributes().size());

        attributes.addAll(ComplexAttributeConverter.from(source.getComplexAttributes()));
        attributes.addAll(SimpleAttributeConverter.from(source.getSimpleAttributes()));

        result.addAll(attributes);
        return result;
    }

    /**
     * Converts a list of {@link RelationToRO} type relations.
     * @param source the source list
     * @param target the destination
     */
    public static List<EtalonRelationToRO> to(List<EtalonRelation> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<EtalonRelationToRO> target = new ArrayList<>();
        for (EtalonRelation r : source) {
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
    public static List<DataRecord> from(List<EtalonRelationToRO> source) {

        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<DataRecord> target = new ArrayList<>();
        for (EtalonRelationToRO r : source) {
            target.add(from(r));
        }

        return target;
    }
}
