/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

/**
 *
 */
package com.unidata.mdm.backend.common.context;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;

/**
 * @author Mikhail Mikhailov
 * Deletes multiple classifiers.
 */
public class DeleteClassifiersDataRequestContext
    extends CommonRequestContext implements RecordIdentityContext {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -8833494823028426839L;
    /**
     * Etalon from key.
     */
    private final String etalonKey;
    /**
     * Origin from key.
     */
    private final String originKey;
    /**
     * Origin from external id.
     */
    private final String externalId;
    /**
     * Entity from name.
     */
    private final String entityName;
    /**
     * Origin from name.
     */
    private final String sourceSystem;
    /**
     * The relations to upsert.
     */
    private final Map<String, List<DeleteClassifierDataRequestContext>> classifiers;
    /**
     * 'Load all for names' support.
     */
    private final List<String> classifierNames;
    /**
     * Force approval state.
     */
    private final ApprovalState approvalState;
    /**
     * Audit level.
     */
    private final short auditLevel;
    /**
     * Constructor.
     */
    private DeleteClassifiersDataRequestContext(DeleteClassifiersDataRequestContextBuilder b) {
        super();
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.classifiers = b.classifiers;
        this.classifierNames = b.classifierNames;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;

        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_WIPE, b.wipe);
        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_ORIGIN, b.inactivateOrigin);
        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_ETALON, b.inactivateEtalon);
        flags.set(ContextUtils.CTX_FLAG_WORKFLOW_ACTION, b.workflowAction);
        flags.set(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT, b.suppressAudit);
        flags.set(ContextUtils.CTX_FLAG_BATCH_UPSERT, b.batchUpsert);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecordKeys keys() {
        return getFromStorage(keysId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageId keysId() {
        return StorageId.RECORDS_RECORD_KEYS;
    }

    /**
     * @return the etalonKey
     */
    @Override
    public String getEtalonKey() {
        return etalonKey;
    }

    /**
     * @return the originKey
     */
    @Override
    public String getOriginKey() {
        return originKey;
    }

    /**
     * @return the externalId
     */
    @Override
    public String getExternalId() {
        return externalId;
    }

    /**
     * @return the entityName
     */
    @Override
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the sourceSystem
     */
    @Override
    public String getSourceSystem() {
        return sourceSystem;
    }

    /**
     * @return the relations
     */
    public Map<String, List<DeleteClassifierDataRequestContext>> getClassifiers() {
        return classifiers == null ? Collections.emptyMap() : this.classifiers;
    }

    /**
     * @return the relationNames
     */
    public List<String> getClassifierNames() {
        return classifierNames == null ? Collections.emptyList() : this.classifierNames;
    }

    /**
     * @return the approvalState
     */
    public ApprovalState getApprovalState() {
        return approvalState;
    }
    /**
     * @return the auditLevel
     */
    public short getAuditLevel() {
        return auditLevel;
    }

    /**
     * @return the inactivateOrigin
     */
    public boolean isInactivateOrigin() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_ORIGIN);
    }

    /**
     * @return the inactivateEtalon
     */
    public boolean isInactivateEtalon() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_ETALON);
    }

    /**
     * @return the skipSuspendWorkflow
     */
    public boolean isWorkflowAction() {
        return flags.get(ContextUtils.CTX_FLAG_WORKFLOW_ACTION);
    }

    /**
     * @return the wipe
     */
    public boolean isWipe() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_WIPE);
    }
    /**
     * @return true, if this context is a part of a batch upsert
     */
    public boolean isBatchUpsert() {
        return flags.get(ContextUtils.CTX_FLAG_BATCH_UPSERT);
    }

    /**
     * @return suppressAudit
     */
    public boolean isSuppressAudit() {
        return flags.get(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT);
    }
    /**
     * Gets new builder.
     * @return builder
     */
    public static DeleteClassifiersDataRequestContextBuilder builder() {
        return new DeleteClassifiersDataRequestContextBuilder();
    }

    /**
     * @author Mikhail Mikhailov
     * Context builder.
     */
    public static class DeleteClassifiersDataRequestContextBuilder {
        /**
         * Etalon key.
         */
        private String etalonKey;
        /**
         * Origin key.
         */
        private String originKey;
        /**
         * Origin foreign id.
         */
        private String externalId;
        /**
         * Entity name.
         */
        private String entityName;
        /**
         * Source system name.
         */
        private String sourceSystem;
        /**
         * The specific classifier data record to get.
         */
        private Map<String, List<DeleteClassifierDataRequestContext>> classifiers;
        /**
         * 'Load all for names' support.
         */
        private List<String> classifierNames;
        /**
         * Inactivate origin flag.
         */
        private boolean inactivateOrigin;
        /**
         * Inactivate etalon flag.
         */
        private boolean inactivateEtalon;
        /**
         * Audit level.
         */
        private short auditLevel = AuditLevel.AUDIT_SUCCESS;
        /**
         * Suppress audit upon upsert.
         */
        private boolean suppressAudit;
        /**
         * This context is participating in a batch upsert. Collect artifacts instead of upserting immediately.
         */
        private boolean batchUpsert;
        /**
         * Force approval state.
         */
        private ApprovalState approvalState;
        /**
         * Wipe flag.
         */
        private boolean wipe;
        /**
         * Skips process and tasks suspending, if set to true.
         */
        private boolean workflowAction;
        /**
         * Constructor.
         */
        private DeleteClassifiersDataRequestContextBuilder() {
            super();
        }
        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteClassifiersDataRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the goldenKey to set
         */
        public DeleteClassifiersDataRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }
        /**
         * @param etalonKey the etalonKey to set
         */
        public DeleteClassifiersDataRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param originKey the etalonKey to set
         */
        public DeleteClassifiersDataRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public DeleteClassifiersDataRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public DeleteClassifiersDataRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public DeleteClassifiersDataRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }

        /**
         * @param classifiers the classifiers to set
         * @return self
         */
        public DeleteClassifiersDataRequestContextBuilder classifiers(Map<String, List<DeleteClassifierDataRequestContext>> classifiers) {
            this.classifiers = classifiers;
            return this;
        }

        /**
         * @param classifierNames the classifierNames to set
         * @return self
         */
        public DeleteClassifiersDataRequestContextBuilder classifierNames(List<String> classifierNames) {
            this.classifierNames = classifierNames;
            return this;
        }

        /**
         * @param classifierNames the classifierNames to set
         * @return self
         */
        public DeleteClassifiersDataRequestContextBuilder classifierNames(String... classifierNames) {
            this.classifierNames = Arrays.asList(classifierNames);
            return this;
        }

        /**
         * Inactivate origin flag.
         * @param inactivateOrigin
         * @return self
         */
        public DeleteClassifiersDataRequestContextBuilder inactivateOrigin(boolean inactivateOrigin) {
            this.inactivateOrigin = inactivateOrigin;
            return this;
        }
        /**
         * Inactivate etalon flag.
         * @param inactivateEtalon
         * return self
         */
        public DeleteClassifiersDataRequestContextBuilder inactivateEtalon(boolean inactivateEtalon) {
            this.inactivateEtalon = inactivateEtalon;
            return this;
        }
        /**
         * @param approvalState
         * @return
         */
        public DeleteClassifiersDataRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }
        /**
         * Wipe flag.
         * @param wipe the physical delete flag
         * @return self
         */
        public DeleteClassifiersDataRequestContextBuilder wipe(boolean wipe) {
            this.wipe = wipe;
            return this;
        }
        /**
         * @param workflowAction workflow action/rollback state signal
         * @return
         */
        public DeleteClassifiersDataRequestContextBuilder workflowAction(boolean workflowAction) {
            this.workflowAction = workflowAction;
            return this;
        }
       /**
        *
        * @param auditLevel - sets the audit level for this context
        * @return self
        */
        public DeleteClassifiersDataRequestContextBuilder auditLevel(short auditLevel) {
            this.auditLevel = auditLevel;
            return this;
        }
        /**
        *
        * @param suppressAudit - sets audit suppressed
        * @return self
        */
        public DeleteClassifiersDataRequestContextBuilder suppressAudit(boolean suppressAudit) {
            this.suppressAudit = suppressAudit;
            return this;
        }
        /**
         * @param batchUpsert the flag
         * @return self
         */
        public DeleteClassifiersDataRequestContextBuilder batchUpsert(boolean batchUpsert) {
            this.batchUpsert = batchUpsert;
            return this;
        }

        /**
         * Builds a context.
         * @return a new context
         */
        public DeleteClassifiersDataRequestContext build() {
            return new DeleteClassifiersDataRequestContext(this);
        }
    }
}
