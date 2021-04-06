package com.unidata.mdm.backend.service.job.exchange.in;

import static java.util.Objects.nonNull;

import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;
import com.unidata.mdm.backend.common.types.CodeAttributeAlias;
import com.unidata.mdm.backend.exchange.def.ContainmentRelation;
import com.unidata.mdm.backend.exchange.def.ExchangeEntity;
import com.unidata.mdm.backend.exchange.def.ExchangeObject;
import com.unidata.mdm.backend.exchange.def.ExchangeTemporalFieldTransformer;
import com.unidata.mdm.backend.exchange.def.RelatesToRelation;
import com.unidata.mdm.backend.exchange.def.VersionRange;
import com.unidata.mdm.backend.service.job.common.AbstractJobStepExecutionListener;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Mikhail Mikhailov
 * Import data step execution listener.
 */
@StepScope
public class ImportDataJobStepExecutionListener extends AbstractJobStepExecutionListener {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDataJobConstants.IMPORT_JOB_LOGGER_NAME);
    /**
     * HZ innstance.
     */
    @Autowired
    private HazelcastInstance hazelcastInstance;
    /**
     * Unidata data source. Needed to write digest.
     */
    @Qualifier("unidataDataSource")
    @Autowired
    private DataSource unidataDataSource;
    /**
     * Exchange object id.
     */
    @Value("#{stepExecutionContext[" + ImportDataJobConstants.PARAM_EXCHANGE_OBJECT_ID + "]}")
    private String exchangeObjectId;
    /**
     * No updates expected (TEST).
     */
    @Value("#{jobParameters[" + ImportDataJobConstants.PARAM_INITIAL_LOAD + "]}")
    private boolean initialLoad;
    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeStep(StepExecution stepExecution) {

        if (Objects.isNull(exchangeObjectId)) {
            final String message = "Exchange object id is null, step target undefined. Step cannot be executed";
            LOGGER.warn(message);
            throw new JobException(message, ExceptionId.EX_DATA_IMPORT_STEP_TARGET_UNDEFINED);
        }

        if (ImportDataJobUtils.getStepState() == null) {

            // Provoke NPE, if something went wrong.
            final String objectId = ImportDataJobUtils.getObjectReferenceName(runId, exchangeObjectId);
            Object obj = hazelcastInstance.getMap(ImportDataJobConstants.EXCHANGE_OBJECTS_MAP_NAME).get(objectId);

            ImportDataJobStepExecutionState parameters = new ImportDataJobStepExecutionState();
            parameters.setExchangeObject((ExchangeObject) obj);

            if (parameters.exchangeObjectIsEntity()) {

                ExchangeEntity entity = parameters.getExchangeObject();
                Collection<CodeAttributeAlias> aliasCodeAttributePointers = entity.getFields().stream()
                        .filter(field -> field.getRefToAttribute() != null)
                        .map(field -> new CodeAttributeAlias(field.getRefToAttribute(), field.getName()))
                        .collect(Collectors.toList());

                parameters.setCodeAttributeAliases(aliasCodeAttributePointers);
                parameters.setFrom(extractFrom(entity.getVersionRange()));
                parameters.setTo(extractTo(entity.getVersionRange()));

            } else {

                if (parameters.exchangeObjectIsContainmentRelation()) {

                    ContainmentRelation containment = parameters.getExchangeObject();
                    parameters.setFrom(extractFrom(containment.getEntity().getVersionRange()));
                    parameters.setTo(extractTo(containment.getEntity().getVersionRange()));
                } else {

                    RelatesToRelation relTo = parameters.getExchangeObject();
                    parameters.setFrom(extractFrom(relTo.getVersionRange()));
                    parameters.setTo(extractTo(relTo.getVersionRange()));
                }

                String fromSourceSystem = stepExecution.getExecutionContext()
                        .getString(ImportDataJobConstants.PARAM_FROM_SOURCE_SYSTEM);
                String fromEntityName = stepExecution.getExecutionContext()
                        .getString(ImportDataJobConstants.PARAM_FROM_ENTITY_NAME);

                parameters.setFromSourceSystem(fromSourceSystem);
                parameters.setFromEntityName(fromEntityName);
            }

            ImportDataJobUtils.setStepState(parameters);
        }

        super.authenticateIfNeeded();
        super.beforeStep(stepExecution);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        super.afterStep(stepExecution);

        // 1. Collect counters
        ImportDataJobStepExecutionState sp = ImportDataJobUtils.removeStepState();
        if (sp.getFailed() > 0) {
            IAtomicLong fCounter = hazelcastInstance.getAtomicLong(
                    sp.exchangeObjectIsEntity()
                    ? ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_FAIL_COUNTER)
                    : ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_FAIL_COUNTER));
            fCounter.addAndGet(sp.getFailed());
        }

        if (sp.getInserted() > 0) {
            IAtomicLong iCounter = hazelcastInstance.getAtomicLong(
                    sp.exchangeObjectIsEntity()
                    ? ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_INSERT_COUNTER)
                    : ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_INSERT_COUNTER));
            iCounter.addAndGet(sp.getInserted());
        }

        if (sp.getUpdated() > 0) {
            IAtomicLong uCounter = hazelcastInstance.getAtomicLong(
                    sp.exchangeObjectIsEntity()
                    ? ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_UPDATE_COUNTER)
                    : ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_UPDATE_COUNTER));
            uCounter.addAndGet(sp.getUpdated());
        }

        if (sp.getSkept() > 0) {
            IAtomicLong sCounter = hazelcastInstance.getAtomicLong(
                    sp.exchangeObjectIsEntity()
                    ? ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_SKIP_COUNTER)
                    : ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_SKIP_COUNTER));
            sCounter.addAndGet(sp.getSkept());
        }

        if (sp.getDeleted() > 0) {
            IAtomicLong dCounter = hazelcastInstance.getAtomicLong(
                    sp.exchangeObjectIsEntity()
                    ? ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RECORDS_DELETE_COUNTER)
                    : ImportDataJobUtils.getObjectReferenceName(runId, ImportDataJobConstants.IMPORT_JOB_RELATIONS_DELETE_COUNTER));
            dCounter.addAndGet(sp.getDeleted());
        }

        super.clearAuthentication();
        return stepExecution.getExitStatus();
    }
    /**
     * Extracts 'from; from the definition.
     * @param versionRange the range
     * @return date
     */
    private Date extractFrom(@Nullable VersionRange versionRange) {
        Date result = null;
        if (nonNull(versionRange) && nonNull(versionRange.getValidFrom()) && nonNull(versionRange.getValidFrom().getValue())) {
            result =  ExchangeTemporalFieldTransformer.ISO801MillisStringToDate(versionRange.getValidFrom().getValue().toString());
        }
        return result;
    }
    /**
     * Extracts 'from; from the definition.
     * @param versionRange the range
     * @return date
     */
    private Date extractTo(@Nullable VersionRange versionRange) {
        Date result = null;
        if (nonNull(versionRange) && nonNull(versionRange.getValidTo()) && nonNull(versionRange.getValidTo().getValue())) {
            result = ExchangeTemporalFieldTransformer.ISO801MillisStringToDate(versionRange.getValidTo().getValue().toString());
        }
        return result;
    }
}
