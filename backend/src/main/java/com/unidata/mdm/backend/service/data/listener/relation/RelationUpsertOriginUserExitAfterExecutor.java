package com.unidata.mdm.backend.service.data.listener.relation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.context.UpsertRelationRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.common.integration.exits.UpsertRelationListener;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.types.DataShift;
import com.unidata.mdm.backend.common.types.OriginRecord;
import com.unidata.mdm.backend.common.types.OriginRelation;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.UpsertAction;
import com.unidata.mdm.backend.common.types.impl.OriginRecordImpl;
import com.unidata.mdm.backend.conf.impl.UpsertImpl;
import com.unidata.mdm.backend.po.OriginsVistoryRelationsPO;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.service.data.origin.OriginRecordsComponent;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.backend.service.data.util.DataRecordUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.RelType;
import com.unidata.mdm.meta.RelationDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author Dmitry Kopin
 * User exit 'after' upsert relation executor.
 */
public class RelationUpsertOriginUserExitAfterExecutor implements DataRecordAfterExecutor<UpsertRelationRequestContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationUpsertOriginUserExitAfterExecutor.class);
    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;
    /**
     * Common relation service..
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * Origin records component.
     */
    @Autowired
    private OriginRecordsComponent originRecordsComponent;
    /**
     * Constructor.
     */
    public RelationUpsertOriginUserExitAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(UpsertRelationRequestContext ctx) {

        UpsertAction action = ctx.getFromStorage(StorageId.RELATIONS_UPSERT_EXACT_ACTION);

        if (ctx.isBypassExtensionPoints() || action == UpsertAction.NO_ACTION) {
            return true;
        }

        MeasurementPoint.start();
        try {

            UpsertImpl upsert = configurationService.getUpsert();
            if (upsert != null) {

                RelationDef relationDef = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
                UpsertRelationListener listener = upsert.getAfterOriginRelationUpsertInstances().get(relationDef.getName());
                if (listener != null) {

                    ExitResult exitResult = null;
                    RelationKeys keys = ctx.relationKeys();
                    OriginRelation originRelation = ctx.getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
                    if (action == UpsertAction.UPDATE) {
                        exitResult = listener.afterOriginRelationUpdate(originRelation, ctx);
                    } else if (action == UpsertAction.INSERT) {

                        originRelation.getInfoSection()
                            .withRelationOriginKey(keys.getOriginId())
                            .withStatus(keys.getOriginStatus())
                            .withApproval(keys.getEtalonState());

                        exitResult = listener.afterOriginRelationInsert(originRelation, ctx);
                    }

                    if (exitResult == null) {
                        return true;
                    }

                    if (ExitResult.Status.WARNING.equals(exitResult.getStatus())) {
                        LOGGER.warn("User exit for listener {} and relation {} has warnings : {}",
                                listener.getClass().getSimpleName(), relationDef.getName(), exitResult.getWarningMessage());
                        List<ErrorInfoDTO> errors = ctx.getFromStorage(StorageId.PROCESS_ERRORS);
                        if(errors == null){
                            errors = new ArrayList<>();
                        }
                        ErrorInfoDTO errorInfo = new ErrorInfoDTO();
                        errorInfo.setSeverity(ErrorInfoDTO.Severity.LOW);
                        errorInfo.setUserMessage(MessageUtils.getMessage("app.data.upsert.relation.after.user.exit.error",
                                exitResult.getWarningMessage()));
                        errors.add(errorInfo);
                        ctx.putToStorage(StorageId.PROCESS_ERRORS, errors);
                    }

                    if (ExitResult.Status.ERROR.equals(exitResult.getStatus())) {
                        throw new DataProcessingException("Error occurred during run after upsert relation user exit",
                                ExceptionId.EX_DATA_UPSERT_RELATION_AFTER_USER_EXIT_ERROR,
                                exitResult.getWarningMessage());
                    }

                    if (exitResult.isWasModified()) {

                        Date from = ctx.getValidFrom();
                        Date to = ctx.getValidTo();

                        if (relationDef.getRelType() == RelType.CONTAINS) {

                            UpsertRequestContext uCtx = ctx.getFromStorage(StorageId.RELATIONS_CONTAINMENT_CONTEXT);
                            OriginRecord or = new OriginRecordImpl().withDataRecord(originRelation);
                            originRecordsComponent.putVersion(uCtx, or, DataShift.REVISED);
                        } else {

                            OriginsVistoryRelationsPO version
                                = DataRecordUtils.newRelationsVistoryRecordPO(ctx, keys.getOriginId(), from, to, originRelation,
                                        RecordStatus.ACTIVE,
                                        DataShift.REVISED);

                            commonRelationsComponent.putVersion(ctx, version);
                        }
                    }
                }
            }

            return true;

        } finally {
            MeasurementPoint.stop();
        }
    }

}
