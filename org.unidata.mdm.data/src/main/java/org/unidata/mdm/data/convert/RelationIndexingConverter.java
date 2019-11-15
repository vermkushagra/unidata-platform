package org.unidata.mdm.data.convert;

import java.time.LocalDateTime;
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
import org.unidata.mdm.meta.type.search.EntityIndexType;
import org.unidata.mdm.meta.type.search.RelationFromIndexId;
import org.unidata.mdm.meta.type.search.RelationHeaderField;
import org.unidata.mdm.search.type.indexing.Indexing;
import org.unidata.mdm.search.type.indexing.IndexingField;
import org.unidata.mdm.search.util.SearchUtils;
import org.unidata.mdm.system.util.ConvertUtils;

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

    public static List<Indexing> convert(Collection<EtalonRelation> relations) {

        if (CollectionUtils.isEmpty(relations)) {
            return Collections.emptyList();
        }

        List<Indexing> result = new ArrayList<>(relations.size() * 2);
        for (EtalonRelation relation : relations) {

            EtalonRelationInfoSection infoSection = relation.getInfoSection();
            List<IndexingField> fields = new ArrayList<>(RelationHeaderField.values().length + relation.getSize());

            // 1. Common
            final LocalDateTime vf = ConvertUtils.date2LocalDateTime(infoSection.getValidFrom());
            final LocalDateTime vt = ConvertUtils.date2LocalDateTime(infoSection.getValidTo());
            final LocalDateTime ua = ConvertUtils.date2LocalDateTime(infoSection.getUpdateDate());
            final LocalDateTime ca = ConvertUtils.date2LocalDateTime(infoSection.getCreateDate());

            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.REL_NAME.getName(), infoSection.getRelationName()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.REL_TYPE.getName(), infoSection.getRelationType().name()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_ETALON_ID.getName(), infoSection.getRelationEtalonKey()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_FROM_ETALON_ID.getName(), infoSection.getFromEtalonKey().getId()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_TO_ETALON_ID.getName(), infoSection.getToEtalonKey().getId()));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_PERIOD_ID.getName(), PeriodIdUtils.periodIdFromDate(infoSection.getValidTo())));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_PENDING.getName(), infoSection.getApproval() == ApprovalState.PENDING));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_DELETED.getName(), infoSection.getStatus() == RecordStatus.INACTIVE));

            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_FROM.getName(), vf, lts -> lts == null ? SearchUtils.ES_MIN_FROM : ConvertUtils.localDateTime2UTCAndFormat(lts)));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_TO.getName(), vt, lts -> lts == null ? SearchUtils.ES_MAX_TO : ConvertUtils.localDateTime2UTCAndFormat(lts)));
            fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_CREATED_AT.getName(), ca, ConvertUtils::localDateTime2UTCAndFormat));

            if (infoSection.getUpdateDate() != null) {
                fields.add(IndexingField.of(EntityIndexType.RELATION, RelationHeaderField.FIELD_UPDATED_AT.getName(), ua, ConvertUtils::localDateTime2UTCAndFormat));
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
