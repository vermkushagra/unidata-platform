package com.unidata.mdm.backend.service.data.listener.relation;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.GetRelationRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.service.data.relations.CommonRelationsComponent;
import com.unidata.mdm.meta.RelationDef;

/**
 * Executor responsible for modifying relations have an alias key.
 */
public class RelationGetCheckKeysBeforeExecutor implements DataRecordBeforeExecutor<GetRelationRequestContext> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RelationGetCheckKeysBeforeExecutor.class);
    /**
     * Common relations component.
     */
    @Autowired
    private CommonRelationsComponent commonRelationsComponent;
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(GetRelationRequestContext gCtx) {

        RelationDef relationDef = gCtx.getFromStorage(StorageId.RELATIONS_META_DEF);
        RelationKeys keys = commonRelationsComponent.ensureAndGetRelationKeys(relationDef.getName(), gCtx);
        if (Objects.isNull(keys)) {

            final String message
                = "Relation get: relation of type [{}] not found by supplied keys - relation etalon id [{}], relation origin id [{}], "
                + "etalon id: [{}], origin id [{}], external id [{}], source system [{}], name [{}]";

            LOGGER.warn(message,
                    relationDef.getName(),
                    gCtx.getRelationEtalonKey(),
                    gCtx.getRelationOriginKey(),
                    gCtx.getEtalonKey(),
                    gCtx.getOriginKey(),
                    gCtx.getExternalId(),
                    gCtx.getSourceSystem(),
                    gCtx.getEntityName());

            throw new DataProcessingException(message, ExceptionId.EX_DATA_RELATIONS_GET_NOT_FOUND_BY_SUPPLIED_KEYS,
                    relationDef.getName(),
                    gCtx.getRelationEtalonKey(),
                    gCtx.getRelationOriginKey(),
                    gCtx.getEtalonKey(),
                    gCtx.getOriginKey(),
                    gCtx.getExternalId(),
                    gCtx.getSourceSystem(),
                    gCtx.getEntityName());
        }

        return true;
    }
}
