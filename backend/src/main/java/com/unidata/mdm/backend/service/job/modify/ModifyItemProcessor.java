package com.unidata.mdm.backend.service.job.modify;

import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.FIELD_FROM_ETALON_ID;
import static com.unidata.mdm.backend.service.search.util.RelationHeaderField.REL_NAME;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.dto.SearchResultHitDTO;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.service.SearchService;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRecordSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRelationSet;
import com.unidata.mdm.backend.service.search.util.RelationHeaderField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.DeleteClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.GetClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifierDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext.UpsertRelationRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.GetClassifiersDTO;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.EtalonClassifier;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.etalon.EtalonRecordsComponent;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;


/**
 * Map search hit to upsert request context.
 */
@Component("modifyProcessor")
@StepScope
public class ModifyItemProcessor implements ItemProcessor<String, Pair<ImportRecordSet, ImportRelationSet>> {

    /**
     * The etalon record.
     */
    private EtalonRecord etalonRecord;

    /**
     * The classifiers.
     */
    private List<EtalonClassifier> classifiers;

    /**
     * The relations.
     */
    private List<EtalonRelation> relations;

    /**
     * The entity name.
     */
    private String entityName;

    /**
     * The operation id.
     */
    private String operationId;

    /**
     * The operation executor.
     */
    private String operationExecutor;

    /**
     * The as of.
     */
    private Date asOf;

    /**
     * The meta model service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * The etalon records component.
     */
    @Autowired
    private EtalonRecordsComponent etalonRecordsComponent;

    /**
     * The classifiers service component.
     */
    @Autowired
    private ClassifiersServiceComponent classifiersServiceComponent;

    @Autowired
    private SearchService searchService;

    /* (non-Javadoc)
     * @see org.springframework.batch.item.ItemProcessor#process(java.lang.Object)
     */
    @Override
    public Pair<ImportRecordSet, ImportRelationSet> process(String etalonId) throws Exception {

        EtalonRecord record = etalonRecordsComponent.loadEtalonData(etalonId, asOf, null, null, null, false, true);

        if (isNull(record)) {
            return null;
        }

        ApprovalState recordApprovalState = record.getInfoSection().getApproval();
        ImportRecordSet importRecordSet = null;

        if (etalonRecord.getSize() > 0) {
            importRecordSet = new ImportRecordSet(null);
            etalonRecord.getSimpleAttributes().stream().map(SerializableDataRecord::of).forEach(record::addAttribute);
            etalonRecord.getArrayAttributes().stream().map(SerializableDataRecord::of).forEach(record::addAttribute);
            etalonRecord.getCodeAttributes().stream().map(SerializableDataRecord::of).forEach(record::addAttribute);

            for (ComplexAttribute complexAttribute : etalonRecord.getComplexAttributes()) {
                ComplexAttribute complex = record.getComplexAttribute(complexAttribute.getName());
                if (complex == null) {
                    record.addAttribute(complexAttribute);
                } else {
                    complex.getRecords().clear();
                    complex.getKeyAttributes().clear();
                    complex.getRecords().addAll(complexAttribute.getRecords());
                }
            }

            Date validTo = record.getInfoSection().getValidTo();
            Date validFrom = record.getInfoSection().getValidFrom();

            UpsertRequestContext.UpsertRequestContextBuilder builder
                    = new UpsertRequestContext.UpsertRequestContextBuilder()
                    .record(record)
                    .batchUpsert(true)
                    .auditLevel(AuditLevel.AUDIT_ERRORS)
                    .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                    .entityName(entityName)
                    .validFrom(validFrom)
                    .validTo(validTo)
                    .etalonKey(record.getInfoSection().getEtalonKey())
                    .returnEtalon(true);

            if (recordApprovalState == ApprovalState.PENDING) {
                builder.approvalState(recordApprovalState);
            }


            UpsertRequestContext upsertRequestContext = builder.build();
            upsertRequestContext.setOperationId(operationId);

            importRecordSet.setRecordUpsert(upsertRequestContext);
        }

        if (CollectionUtils.isNotEmpty(classifiers)) {
            if (importRecordSet == null) {
                importRecordSet = new ImportRecordSet(null);
            }

            importRecordSet.setClassifiersUpsert(processClassifiers(classifiers, record.getInfoSection().getEtalonKey().getId()));
            importRecordSet.setClassifiersDelete(processDeleteClassifiers(classifiers, record.getInfoSection().getEtalonKey().getId()));
        }

        ImportRelationSet importRelationSet = null;
        if (CollectionUtils.isNotEmpty(relations)) {
            importRelationSet = new ImportRelationSet(null);
            importRelationSet.setRelationsUpsert(processRelations(relations, record));
            importRelationSet.setRelationsDelete(processDeleteRelations(relations, record));
        }

        return Pair.of(importRecordSet, importRelationSet);
    }

    /**
     * Process relations.
     *
     * @param source the source
     * @param record the record
     * @return the list
     */
    private UpsertRelationsRequestContext processRelations(List<EtalonRelation> source, EtalonRecord record) {
        if (source == null) {
            return null;
        }
        Map<String, List<UpsertRelationRequestContext>> map = new HashMap<>();
        for (EtalonRelation rel : source) {
            if (rel.getInfoSection().getToEtalonKey() != null && rel.getInfoSection().getToEtalonKey().getId() != null) {
                UpsertRelationRequestContext ctx = new UpsertRelationRequestContextBuilder()
                        .relation(rel)
                        .etalonKey(rel.getInfoSection().getToEtalonKey().getId())
                        .relationName(rel.getInfoSection().getRelationName())
                        .entityName(rel.getInfoSection().getToEntityName())
                        .validFrom(record.getInfoSection().getValidFrom())
                        .validTo(record.getInfoSection().getValidTo())
                        .sourceSystem(metaModelService.getAdminSourceSystem().getName())
                        .auditLevel(AuditLevel.AUDIT_ERRORS)
                        .batchUpsert(true)
                        .build();

                if (!map.containsKey(ctx.getRelationName())) {
                    map.put(ctx.getRelationName(), new ArrayList<>());
                }
                map.get(ctx.getRelationName()).add(ctx);
            }
        }
        if (MapUtils.isNotEmpty(map)) {
            return UpsertRelationsRequestContext.builder()
                    .relations(map)
                    .entityName(record.getInfoSection().getEntityName())
                    .etalonKey(record.getInfoSection().getEtalonKey()).build();
        } else {
            return null;
        }
    }


    /**
     * Process delete relations.
     *
     * @param source the source
     * @param record the record
     * @return the list
     */
    private DeleteRelationsRequestContext processDeleteRelations(List<EtalonRelation> source, EtalonRecord record) {
        if (source == null) {
            return null;
        }
        Map<String, List<DeleteRelationRequestContext>> map = new HashMap<>();
        for (EtalonRelation rel : source) {
            if (rel.getInfoSection().getToEtalonKey() == null || rel.getInfoSection().getToEtalonKey().getId() == null) {
                Set<String> ids = getRelationsByTimelineAndFromEtalonId(record.getInfoSection().getEtalonKey().getId(),
                        record.getInfoSection().getValidFrom(),
                        record.getInfoSection().getValidTo(),
                        rel.getInfoSection().getRelationName(),
                        record.getInfoSection().getEntityName());
                if (!map.containsKey(rel.getInfoSection().getRelationName())) {
                    map.put(rel.getInfoSection().getRelationName(), new ArrayList<>());
                }
                ids.forEach(id -> {
                    DeleteRelationRequestContext dctcx = new DeleteRelationRequestContext.DeleteRelationRequestContextBuilder()
                            .relationEtalonKey(id)
                            .inactivateEtalon(true)
                            .relationName(rel.getInfoSection().getRelationName())
                            .batchUpsert(true)
                            .build();
                    map.get(dctcx.getRelationName()).add(dctcx);
                });
            }
        }
        if (MapUtils.isNotEmpty(map)) {
            return DeleteRelationsRequestContext.builder()
                    .relations(map)
                    .entityName(record.getInfoSection().getEntityName())
                    .etalonKey(record.getInfoSection().getEtalonKey()).build();
        } else {
            return null;
        }
    }

    /**
     * Process classifiers.
     *
     * @param classifiers    the classifiers
     * @param parentEtalonId the parent etalon id
     * @return the list
     */
    private UpsertClassifiersDataRequestContext processClassifiers(List<EtalonClassifier> classifiers,
                                                                   String parentEtalonId) {

        if (CollectionUtils.isEmpty(classifiers)) {
            return null;
        }

        Map<String, List<UpsertClassifierDataRequestContext>> result = new HashMap<>(classifiers.size());
        for (EtalonClassifier ec : classifiers) {
            if (StringUtils.isNotEmpty(ec.getInfoSection().getNodeId())) {
                List<UpsertClassifierDataRequestContext> clsForUpsert;

                if (!result.containsKey(ec.getInfoSection().getClassifierName())) {
                    clsForUpsert = new ArrayList<>();
                    result.put(ec.getInfoSection().getClassifierName(), clsForUpsert);
                } else {
                    clsForUpsert = result.get(ec.getInfoSection().getClassifierName());
                }
                clsForUpsert.add(UpsertClassifierDataRequestContext.builder()
                        .batchUpsert(true)
                        .auditLevel(AuditLevel.AUDIT_ERRORS)
                        .classifier(ec)
                        .classifierName(ec.getInfoSection().getClassifierName())
                        .classifierNodeId(ec.getInfoSection().getNodeId())
                        .etalonKey(parentEtalonId)
                        .build());
            }
        }
        if (MapUtils.isNotEmpty(result)) {
            return UpsertClassifiersDataRequestContext.builder()
                    .classifiers(result)
                    .build();
        } else {
            return null;
        }

    }

    /**
     * Process delete classifiers.
     *
     * @param classifiers    the classifiers
     * @param parentEtalonId the parent etalon id
     * @return the list
     */
    private DeleteClassifiersDataRequestContext processDeleteClassifiers(List<EtalonClassifier> classifiers,
                                                                         String parentEtalonId) {

        if (CollectionUtils.isEmpty(classifiers)) {
            return null;
        }

        Map<String, List<DeleteClassifierDataRequestContext>> result = new HashMap<>(classifiers.size());
        for (EtalonClassifier ec : classifiers) {
            // if node id undefined -> unclassify
            if (StringUtils.isEmpty(ec.getInfoSection().getNodeId())) {
                // extract all classifier nodes by classifier name for entity
                GetClassifiersDataRequestContext gCCtx = GetClassifiersDataRequestContext.builder()
                        .etalonKey(parentEtalonId)
                        .classifierNames(ec.getInfoSection().getClassifierName())
                        .build();

                GetClassifiersDTO classifiersDTO = classifiersServiceComponent.getClassifiers(gCCtx);
                if (MapUtils.isNotEmpty(classifiersDTO.getClassifiers())) {
                    List<DeleteClassifierDataRequestContext> classifiersForDelete = new ArrayList<>();
                    classifiersDTO.getClassifiers().values().forEach(etalonClassifiers -> etalonClassifiers
                            .forEach(etalonClassifier -> classifiersForDelete.add(DeleteClassifierDataRequestContext.builder()
                                    .batchUpsert(true)
                                    .inactivateEtalon(true)
                                    .auditLevel(AuditLevel.AUDIT_ERRORS)
                                    .classifierName(ec.getInfoSection().getClassifierName())
                                    .etalonKey(parentEtalonId)
                                    .classifierEtalonKey(etalonClassifier.getClassifierKeys().getEtalonId())
                                    .build())));
                    result.put(ec.getInfoSection().getClassifierName(), classifiersForDelete);
                }
            }
        }

        if (MapUtils.isNotEmpty(result)) {
            return DeleteClassifiersDataRequestContext.builder()
                    .batchUpsert(true)
                    .classifiers(result)
                    .build();
        } else {
            return null;
        }
    }

    private Set<String> getRelationsByTimelineAndFromEtalonId(String etalonId, Date validFrom, Date validTo,
                                                              String relName, String toEntity) {
        FormField fromEtalon = FormField.strictString(FIELD_FROM_ETALON_ID.getField(), etalonId);
        FormField rel_name = FormField.strictString(REL_NAME.getField(), relName);

        List<String> searchFields = new ArrayList<>();
        searchFields.add(RelationHeaderField.FIELD_ETALON_ID.getField());

        SearchRequestContext relationsContext = SearchRequestContext.forEtalonRelation(toEntity)
                .form(FormFieldsGroup.createAndGroup(fromEtalon, rel_name)).count(1000).scrollScan(true)
                .onlyQuery(true).returnFields(searchFields).build();
        return searchService.search(relationsContext).getHits().stream().map(SearchResultHitDTO::getId)
                .collect(Collectors.toSet());
    }

    /**
     * Sets the etalon record.
     *
     * @param etalonRecord the new etalon record
     */
    public void setEtalonRecord(EtalonRecord etalonRecord) {
        this.etalonRecord = etalonRecord;
    }

    /**
     * Sets the classifiers.
     *
     * @param classifiers the classifiers to set
     */
    public void setClassifiers(List<EtalonClassifier> classifiers) {
        this.classifiers = classifiers;
    }

    /**
     * Sets the entity name.
     *
     * @param entityName the new entity name
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Sets the operation id.
     *
     * @param operationId the new operation id
     */
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    /**
     * Sets the as of.
     *
     * @param asOf the new as of
     */
    public void setAsOf(Date asOf) {
        this.asOf = asOf;
    }

    /**
     * Gets the operation executor.
     *
     * @return the operation executor
     */
    public String getOperationExecutor() {
        return operationExecutor;
    }

    /**
     * Sets the operation executor.
     *
     * @param operationExecutor the new operation executor
     */
    public void setOperationExecutor(String operationExecutor) {
        this.operationExecutor = operationExecutor;
    }

    /**
     * Sets the relations.
     *
     * @param relations the new relations
     */
    public void setRelations(List<EtalonRelation> relations) {
        this.relations = relations;
    }
}
