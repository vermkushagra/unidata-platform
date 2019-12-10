package org.unidata.mdm.data.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.core.type.data.RecordStatus;
import org.unidata.mdm.core.util.PeriodIdUtils;
import org.unidata.mdm.data.type.data.EtalonRelation;
import org.unidata.mdm.data.type.data.EtalonRelationInfoSection;
import org.unidata.mdm.data.type.data.RelationType;
import org.unidata.mdm.data.type.keys.RelationKeys;
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.meta.type.search.RelationFromIndexId;
import org.unidata.mdm.meta.type.search.RelationHeaderField;
import org.unidata.mdm.search.type.indexing.Indexing;
import org.unidata.mdm.search.type.indexing.IndexingField;

/**
 * @author Mikhail Mikhailov on Oct 12, 2019
 * Relations
 */
public final class RelationIndexingConverter extends AbstractIndexingConverter {

    /**
     * Constructor.
     */
    private RelationIndexingConverter() {
        super();
    }

    public static List<Indexing> convert(RelationKeys keys, Collection<EtalonRelation> relations) {

        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }

        List<Indexing> result = new ArrayList<>(relations.size() * 2);
        for (EtalonRelation relation : relations) {

            EtalonRelationInfoSection infoSection = relation.getInfoSection();
            List<IndexingField> fields = new ArrayList<>(RelationHeaderField.values().length + relation.getSize());

            // 1. Common
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.REL_NAME.getName(), infoSection.getRelationName()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.REL_TYPE.getName(), infoSection.getRelationType().name()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_ETALON_ID.getName(), infoSection.getRelationEtalonKey()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_FROM_ETALON_ID.getName(), infoSection.getFromEtalonKey().getId()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_TO_ETALON_ID.getName(), infoSection.getToEtalonKey().getId()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_PERIOD_ID.getName(), PeriodIdUtils.periodIdFromDate(infoSection.getValidTo())));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_PENDING.getName(), infoSection.getApproval() == ApprovalState.PENDING));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_DELETED.getName(), keys.getEtalonKey().getStatus() == RecordStatus.INACTIVE));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_INACTIVE.getName(), infoSection.getStatus() == RecordStatus.INACTIVE));

            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_FROM.getName(), infoSection.getValidFrom()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_TO.getName(), infoSection.getValidTo()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_CREATED_AT.getName(), infoSection.getCreateDate()));

            if (infoSection.getUpdateDate() != null) {
                fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_UPDATED_AT.getName(), infoSection.getUpdateDate()));
            }

            // 1.1. save relation attributes data
            if(RelationType.CONTAINS != infoSection.getRelationType() && relation.getSize()> 0){
                fields.addAll(AbstractIndexingConverter.buildRecord(relation));
            }

            // 2. From
            List<IndexingField> fromFields = new ArrayList<>(fields);
            fromFields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_DIRECT.getName(), true));

            result.add(new Indexing(EntityIndexType.RELATION,
                    RelationFromIndexId.of(
                            infoSection.getFromEntityName(),
                            infoSection.getRelationName(),
                            infoSection.getFromEtalonKey().getId(),
                            infoSection.getToEtalonKey().getId(),
                            PeriodIdUtils.ensureDateValue(infoSection.getValidTo())))
                        .withFields(fromFields));

            // 2. To
            List<IndexingField> toFields = new ArrayList<>(fields);
            toFields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_DIRECT.getName(), false));

            result.add(new Indexing(EntityIndexType.RELATION,
                    RelationFromIndexId.of(
                            infoSection.getToEntityName(),
                            infoSection.getRelationName(),
                            infoSection.getFromEtalonKey().getId(),
                            infoSection.getToEtalonKey().getId(),
                            PeriodIdUtils.ensureDateValue(infoSection.getValidTo())))
                        .withFields(toFields));
        }

        return result;
    }
}
