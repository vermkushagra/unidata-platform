package com.unidata.mdm.backend.service.data.listener.relation;

import com.unidata.mdm.backend.common.context.DeleteRelationRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.DeleteRelationListener;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.EtalonRelation;
import com.unidata.mdm.backend.common.types.EtalonRelationInfoSection;
import com.unidata.mdm.backend.common.types.RelationType;
import com.unidata.mdm.backend.common.types.impl.EtalonRelationImpl;
import com.unidata.mdm.backend.conf.impl.DeleteImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.RelationDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Dmitry Kopin
 * Delete relation user exit 'after' listener.
 */
public class RelationDeleteUserExitAfterExecutor implements DataRecordAfterExecutor<DeleteRelationRequestContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationDeleteUserExitAfterExecutor.class);

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Constructor.
     */
    public RelationDeleteUserExitAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(DeleteRelationRequestContext ctx) {
        Boolean result = true;
        DeleteImpl delete = configurationService.getDelete();

        if (delete != null) {
            DeleteRelationListener listener = delete.getAfterRelationDeactivationInstances()
                    .get(ctx.relationKeys().getRelationName());
            if (listener != null) {
                RelationKeys relationKeys = ctx.relationKeys();
                RelationDef relationDef = ctx.getFromStorage(StorageId.RELATIONS_META_DEF);
                EtalonRelation etalon = new EtalonRelationImpl()
                        .withDataRecord(null)
                        .withInfoSection(new EtalonRelationInfoSection()
                                .withStatus(relationKeys.getEtalonStatus())
                                .withApproval(relationKeys.getEtalonState())
                                .withValidFrom(ctx.getValidFrom())
                                .withValidTo(ctx.getValidTo())
                                .withRelationEtalonKey(relationKeys.getEtalonId())
                                .withRelationName(relationKeys.getRelationName())
                                .withFromEtalonKey(relationKeys.getFrom().getEtalonKey())
                                .withFromEntityName(relationKeys.getFrom().getEntityName())
                                .withToEtalonKey(relationKeys.getTo().getEtalonKey())
                                .withToEntityName(relationKeys.getTo().getEntityName())
                                .withType(relationDef != null ? RelationType.valueOf(relationDef.getRelType().name()) : null));


                ExitResult exitResult = listener.afterRelationDeactivation(etalon, ctx);

                if(exitResult == null){
                    return result;
                }

                if (ExitResult.Status.WARNING.equals(exitResult.getStatus())) {
                    LOGGER.warn(String.format("After Delete relation user exit for listener %s and relation %s has warnings : %s",
                            listener.getClass().getSimpleName(), ctx.relationKeys().getRelationName(), exitResult.getWarningMessage()));
                    List<ErrorInfoDTO> errors = ctx.getFromStorage(StorageId.PROCESS_ERRORS);
                    if(errors == null){
                        errors = new ArrayList<>();
                    }
                    ErrorInfoDTO errorInfo = new ErrorInfoDTO();
                    errorInfo.setSeverity(ErrorInfoDTO.Severity.LOW);
                    errorInfo.setUserMessage(MessageUtils.getMessage("app.data.delete.relation.after.user.exit.error",
                            exitResult.getWarningMessage()));
                    errors.add(errorInfo);
                    ctx.putToStorage(StorageId.PROCESS_ERRORS, errors);
                }

                if(ExitResult.Status.ERROR.equals(exitResult.getStatus())){
                    throw new DataProcessingException("Error occurred during run after delete relation user exit",
                            ExceptionId.EX_DATA_DELETE_RELATION_AFTER_USER_EXIT_ERROR,
                            exitResult.getWarningMessage());
                }
            }
        }

        return result;
    }

}
