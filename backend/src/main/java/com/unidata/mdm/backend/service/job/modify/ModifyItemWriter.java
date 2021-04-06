package com.unidata.mdm.backend.service.job.modify;

import com.unidata.mdm.backend.common.context.DeleteClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.DeleteRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertClassifiersDataRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRelationsRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.DeleteClassifiersDTO;
import com.unidata.mdm.backend.common.dto.DeleteRelationsDTO;
import com.unidata.mdm.backend.common.dto.UpsertClassifiersDTO;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.dto.UpsertRelationsDTO;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.service.data.RecordsServiceComponent;
import com.unidata.mdm.backend.service.data.classifiers.ClassifiersServiceComponent;
import com.unidata.mdm.backend.service.data.relations.RelationsServiceComponent;
import com.unidata.mdm.backend.service.job.AbstractUnidataWriter;
import com.unidata.mdm.backend.service.job.exchange.in.ImportDataJobUtils;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRecordSet;
import com.unidata.mdm.backend.service.job.exchange.in.types.ImportRelationSet;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * The Class ModifyItemWriter.
 */
@Component("modifyWriter")
@StepScope
public class ModifyItemWriter extends AbstractUnidataWriter<Pair<ImportRecordSet, ImportRelationSet>> {

    /**
     * The Constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyItemWriter.class);

    @Autowired
    private RecordsServiceComponent recordsServiceComponent;

    @Autowired
    private SearchServiceExt searchService;

    /**
     * Classifier service component.
     */
    @Autowired
    private ClassifiersServiceComponent classifiersServiceComponent;

    /**
     * The relations service component.
     */
    @Autowired
    private RelationsServiceComponent relationsServiceComponent;


    /* (non-Javadoc)
     * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
     */
    @Override
    public void write(List<? extends Pair<ImportRecordSet, ImportRelationSet>> items) throws Exception {
        List<UpsertRequestContext> upsertRecords = new ArrayList<>();
        List<UpsertClassifiersDataRequestContext> upsertClassifiers = new ArrayList<>();
        List<DeleteClassifiersDataRequestContext> deleteClassifiers = new ArrayList<>();
        List<UpsertRelationsRequestContext> upsertRelations = new ArrayList<>();
        List<DeleteRelationsRequestContext> deleteRelations = new ArrayList<>();

        ModifyItemJobStepExecutionState state = ImportDataJobUtils.getStepState();

        for (Pair<ImportRecordSet, ImportRelationSet> item : items) {
            if (item.getLeft() != null) {
                if (item.getLeft().getRecordUpsert() != null) {
                    upsertRecords.add(item.getLeft().getRecordUpsert());
                }
                if (item.getLeft().getClassifiersUpsert() != null) {
                    upsertClassifiers.add(item.getLeft().getClassifiersUpsert());
                }
                if (item.getLeft().getClassifiersDelete() != null) {
                    deleteClassifiers.add(item.getLeft().getClassifiersDelete());
                }
            }

            if(item.getRight() != null){
                if (item.getRight().getRelationsUpsert() != null){
                    upsertRelations.add(item.getRight().getRelationsUpsert());
                }
                if( item.getRight().getRelationsDelete() != null){
                    deleteRelations.add(item.getRight().getRelationsDelete());
                }
            }
        }

        processRecords(upsertRecords, state);
        processClassifiers(upsertClassifiers, deleteClassifiers, state);
        processRelations(upsertRelations, deleteRelations, state);

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private void processClassifiers(List<UpsertClassifiersDataRequestContext> upsertClassifiers, List<DeleteClassifiersDataRequestContext> deleteClassifiers, ModifyItemJobStepExecutionState state) {
        try {
        List<UpsertClassifiersDTO> upsertResult = classifiersServiceComponent.batchUpsertClassifiers(upsertClassifiers);
            long submitted = upsertClassifiers.stream()
                    .flatMap(ctx -> ctx.getClassifiers().values().stream())
                    .mapToLong(Collection::size)
                    .sum();
            long updatedClassifiers = upsertResult.stream()
                    .mapToLong(value -> value.getClassifiers()
                            .values()
                            .stream()
                            .mapToLong(value1 -> value1.stream().count())
                            .sum())
                    .sum();
            state.incrementModifyClassifiers(updatedClassifiers);
            state.incrementSkeptClassifiers(submitted - updatedClassifiers);
        } catch (Exception e) {
            long failedClassifiers = upsertClassifiers.stream()
                    .mapToLong(value -> value.getClassifiers()
                            .values()
                            .stream()
                            .mapToLong(value1 -> value1.stream().count())
                            .sum())
                    .sum();
            state.incrementFailedRecords(failedClassifiers);
            String message = getErrorMessage(e);
            LOGGER.error("Error during upsert records. {}", message);
        }

        try {
            List<DeleteClassifiersDTO> deleteResult = classifiersServiceComponent.batchDeleteClassifiers(deleteClassifiers);
            long submitted = deleteClassifiers.stream()
                    .flatMap(ctx -> ctx.getClassifiers().values().stream())
                    .mapToLong(Collection::size)
                    .sum();
            long deletedClassifiers = deleteResult.stream()
                    .mapToLong(value -> value.getClassifiers()
                            .values()
                            .stream()
                            .mapToLong(value1 -> value1.stream().count())
                            .sum())
                    .sum();
            state.incrementDeletedRecords(deletedClassifiers);
            state.incrementSkeptClassifiers(submitted - deletedClassifiers);
        } catch (Exception e) {
            long failedClassifiers = deleteClassifiers.stream()
                    .mapToLong(value -> value.getClassifiers()
                            .values()
                            .stream()
                            .mapToLong(value1 -> value1.stream().count())
                            .sum())
                    .sum();
            state.incrementFailedRecords(failedClassifiers);
            String message = getErrorMessage(e);
            LOGGER.error("Error during upsert records. {}", message);
        }
    }

    private void processRecords(List<UpsertRequestContext> upsertRecords,  ModifyItemJobStepExecutionState state) {
        try {
            List<UpsertRecordDTO> records = recordsServiceComponent.batchUpsertRecords(upsertRecords);
            long updated = records.stream().filter(dto -> dto.getAction() == UpsertAction.UPDATE).count();
            state.incrementModifyRecords(updated);
            state.incrementSkeptRecords(upsertRecords.size() - updated);
        } catch (Exception e) {
            state.incrementFailedRecords(upsertRecords.size());
            String message = getErrorMessage(e);
            LOGGER.error("Error during upsert records. {}", message);
        }
    }

    private void processRelations(List<UpsertRelationsRequestContext> upsertRelations,
                                  List<DeleteRelationsRequestContext> deleteRelations,
                                  ModifyItemJobStepExecutionState state) {
        try {
            List<UpsertRelationsDTO> records = relationsServiceComponent.batchUpsertRelations(upsertRelations);
            long submitted = upsertRelations.stream()
                    .flatMap(ctx -> ctx.getRelations().values().stream())
                    .mapToLong(Collection::size)
                    .sum();

            long updated = records.stream()
                    .mapToLong(value -> value.getRelations()
                            .values()
                            .stream()
                            .mapToLong(value1 -> value1.stream().count())
                            .sum())
                    .sum();
            state.incrementModifyRelations(updated);
            state.incrementSkeptRelations(submitted - updated);
        } catch (Exception e) {
            long failed = upsertRelations.stream()
                    .mapToLong(value -> value.getRelations()
                            .values()
                            .stream()
                            .mapToLong(value1 -> value1.stream().count())
                            .sum())
                    .sum();
            state.incrementFailedRelations(failed);
            String message = getErrorMessage(e);
            LOGGER.error("Error during upsert records. {}", message);
        }

        try {
            List<DeleteRelationsDTO> records = relationsServiceComponent.batchDeleteRelations(deleteRelations);
            long submitted = deleteRelations.stream()
                    .flatMap(ctx -> ctx.getRelations().values().stream())
                    .mapToLong(Collection::size)
                    .sum();
            long deleted = records.stream()
                    .mapToLong(value -> value.getRelations()
                            .values()
                            .stream()
                            .mapToLong(value1 -> value1.stream().count())
                            .sum())
                    .sum();
            state.incrementDeletedRelations(deleted);
            state.incrementSkeptRelations(submitted - deleted);
        } catch (Exception e) {
            long failed = upsertRelations.stream()
                    .mapToLong(value -> value.getRelations()
                            .values()
                            .stream()
                            .mapToLong(value1 -> value1.stream().count())
                            .sum())
                    .sum();
            state.incrementFailedRelations(failed);
            String message = getErrorMessage(e);
            LOGGER.error("Error during upsert records. {}", message);
        }
    }

}
