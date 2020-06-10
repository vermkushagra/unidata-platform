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

package com.unidata.mdm.backend.common.context;

import java.util.Date;

import com.unidata.mdm.backend.common.audit.AuditLevel;
import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RelationKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;

/**
 * @author Mikhail Mikhailov
 * Delete relation context.
 */
public class DeleteRelationRequestContext
    extends AbstractRelationToRequestContext
    implements MutableValidityRangeContext, ApprovalStateSettingContext  {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2751466540755521772L;
    /**
     * Relation etalon id.
     */
    private final String relationEtalonKey;
    /**
     * Relation origin id.
     */
    private final String relationOriginKey;
    /**
     * Etalon key.
     */
    private final String etalonKey;
    /**
     * Origin key.
     */
    private final String originKey;
    /**
     * Origin foreign id.
     */
    private final String externalId;
    /**
     * Entity name.
     */
    private final String entityName;
    /**
     * Origin name.
     */
    private final String sourceSystem;
    /**
     * Valid from for this set.
     */
    private Date validFrom;
    /**
     * Valid to for this set.
     */
    private Date validTo;
    /**
     * Name of the relation.
     */
    private final String relationName;
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
    private DeleteRelationRequestContext(DeleteRelationRequestContextBuilder b) {
        super(b.parentContext);
        this.etalonKey = b.etalonKey;
        this.originKey = b.originKey;
        this.externalId = b.externalId;
        this.entityName = b.entityName;
        this.sourceSystem = b.sourceSystem;
        this.relationName = b.relationName;
        this.validFrom = b.validFrom;
        this.validTo = b.validTo;
        this.relationEtalonKey = b.relationEtalonKey;
        this.relationOriginKey = b.relationOriginKey;
        this.approvalState = b.approvalState;
        this.auditLevel = b.auditLevel;

        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_WIPE, b.wipe);
        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_PERIOD, b.inactivatePeriod);
        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_ORIGIN, b.inactivateOrigin);
        flags.set(ContextUtils.CTX_FLAG_INACTIVATE_ETALON, b.inactivateEtalon);
        flags.set(ContextUtils.CTX_FLAG_WORKFLOW_ACTION, b.workflowAction);
        flags.set(ContextUtils.CTX_FLAG_SUPPRESS_AUDIT, b.suppressAudit);
        flags.set(ContextUtils.CTX_FLAG_BATCH_UPSERT, b.batchUpsert);
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
     * @return the validFrom
     */
    @Override
    public Date getValidFrom() {
        return validFrom;
    }

    /**
     * @return the validTo
     */
    @Override
    public Date getValidTo() {
        return validTo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValidFrom(Date from) {
        this.validFrom = from;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValidTo(Date to) {
        this.validTo = to;
    }

    /**
     * @return the relationName
     */
    public String getRelationName() {
        return relationName;
    }


    /**
     * @return the relationEtalonKey
     */
    @Override
    public String getRelationEtalonKey() {
        return relationEtalonKey;
    }


    /**
     * @return the relationOriginKey
     */
    @Override
    public String getRelationOriginKey() {
        return relationOriginKey;
    }

    /**
     * Whether the context has relation name and type set.
     * @return true if so, false otherwise
     */
    public boolean isValid() {
        return relationName != null;
    }

    /**
     * @return the approvalState
     */
    @Override
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
     * @return the inactivatePeriod
     */
    public boolean isInactivatePeriod() {
        return flags.get(ContextUtils.CTX_FLAG_INACTIVATE_PERIOD);
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
     * {@inheritDoc}
     */
    @Override
    public RelationKeys relationKeys() {
        return getFromStorage(relationKeysId());
    }
    /**
     * Gets builder.
     * @return builder
     */
    public static DeleteRelationRequestContextBuilder builder() {
        return new DeleteRelationRequestContextBuilder();
    }
    /**
     * @author Mikhail Mikhailov
     * Delete relation request bulder class.
     */
    public static class DeleteRelationRequestContextBuilder {
        /**
         * Valid from for this set.
         */
        private Date validFrom;
        /**
         * Valid to for this set.
         */
        private Date validTo;
        /**
         * Name of the relation.
         */
        private String relationName;
        /**
         * Golden key.
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
         * Origin name.
         */
        private String sourceSystem;
        /**
         * Relation etalon id.
         */
        private String relationEtalonKey;
        /**
         * Relation origin id.
         */
        private String relationOriginKey;
        /**
         * A version for inactive period should be put above all.
         */
        private boolean inactivatePeriod;
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

        private CommonDependableContext parentContext;

        /**
         * Constructor.
         */
        public DeleteRelationRequestContextBuilder() {
           super();
        }

        /**
         * @param relationEtalonKey the relationEtalonKey to set
         */
        public DeleteRelationRequestContextBuilder relationEtalonKey(String relationEtalonKey) {
            this.relationEtalonKey = relationEtalonKey;
            return this;
        }

        /**
         * @param relationOriginKey the relationOriginKey to set
         */
        public DeleteRelationRequestContextBuilder relationOriginKey(String relationOriginKey) {
            this.relationOriginKey = relationOriginKey;
            return this;
        }

        /**
         * @param relationName the relationName to set
         * @return self
         */
        public DeleteRelationRequestContextBuilder relationName(String relationName) {
            this.relationName = relationName;
            return this;
        }

        /**
         * @param validFrom the validFrom to set
         */
        public DeleteRelationRequestContextBuilder validFrom(Date validFrom) {
            this.validFrom = validFrom;
            return this;
        }


        /**
         * @param validTo the validTo to set
         */
        public DeleteRelationRequestContextBuilder validTo(Date validTo) {
            this.validTo = validTo;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteRelationRequestContextBuilder etalonKey(String etalonKey) {
            this.etalonKey = etalonKey;
            return this;
        }

        /**
         * @param etalonKey the goldenKey to set
         */
        public DeleteRelationRequestContextBuilder etalonKey(EtalonKey etalonKey) {
            this.etalonKey = etalonKey != null ? etalonKey.getId() : null;
            return this;
        }

        /**
         * @param originKey the goldenKey to set
         */
        public DeleteRelationRequestContextBuilder originKey(OriginKey originKey) {
            this.originKey = originKey != null ? originKey.getId() : null;
            this.externalId = originKey != null ? originKey.getExternalId() : null;
            this.sourceSystem = originKey != null ? originKey.getSourceSystem() : null;
            this.entityName = originKey != null ? originKey.getEntityName() : null;
            return this;
        }

        /**
         * @param originKey the goldenKey to set
         */
        public DeleteRelationRequestContextBuilder originKey(String originKey) {
            this.originKey = originKey;
            return this;
        }

        /**
         * @param externalId the externalId to set
         */
        public DeleteRelationRequestContextBuilder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        /**
         * @param entityName the entityName to set
         */
        public DeleteRelationRequestContextBuilder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        /**
         * @param sourceSystem the sourceSystem to set
         */
        public DeleteRelationRequestContextBuilder sourceSystem(String sourceSystem) {
            this.sourceSystem = sourceSystem;
            return this;
        }
        /**
         * Inactivate period.
         * @param inactivatePeriod
         * @return
         */
        public DeleteRelationRequestContextBuilder inactivatePeriod(boolean inactivatePeriod) {
            this.inactivatePeriod = inactivatePeriod;
            return this;
        }
        /**
         * Inactivate origin flag.
         * @param inactivateOrigin
         * @return self
         */
        public DeleteRelationRequestContextBuilder inactivateOrigin(boolean inactivateOrigin) {
            this.inactivateOrigin = inactivateOrigin;
            return this;
        }
        /**
         * Inactivate etalon flag.
         * @param inactivateEtalon
         * return self
         */
        public DeleteRelationRequestContextBuilder inactivateEtalon(boolean inactivateEtalon) {
            this.inactivateEtalon = inactivateEtalon;
            return this;
        }
        /**
         * @param approvalState
         * @return
         */
        public DeleteRelationRequestContextBuilder approvalState(ApprovalState approvalState) {
            this.approvalState = approvalState;
            return this;
        }
        /**
         * Wipe flag.
         * @param wipe the physical delete flag
         * @return self
         */
        public DeleteRelationRequestContextBuilder wipe(boolean wipe) {
            this.wipe = wipe;
            return this;
        }
        /**
         * @param workflowAction workflow action/rollback state signal
         * @return
         */
        public DeleteRelationRequestContextBuilder workflowAction(boolean workflowAction) {
            this.workflowAction = workflowAction;
            return this;
        }
       /**
        *
        * @param auditLevel - sets the audit level for this context
        * @return self
        */
        public DeleteRelationRequestContextBuilder auditLevel(short auditLevel) {
            this.auditLevel = auditLevel;
            return this;
        }
        /**
        *
        * @param suppressAudit - sets audit suppressed
        * @return self
        */
        public DeleteRelationRequestContextBuilder suppressAudit(boolean suppressAudit) {
            this.suppressAudit = suppressAudit;
            return this;
        }
        /**
         * @param batchUpsert the flag
         * @return self
         */
        public DeleteRelationRequestContextBuilder batchUpsert(boolean batchUpsert) {
            this.batchUpsert = batchUpsert;
            return this;
        }

        public DeleteRelationRequestContextBuilder parentContext(final CommonDependableContext parentContext) {
            this.parentContext = parentContext;
            return this;
        }

        /**
         * Builder method.
         * @return context
         */
        public DeleteRelationRequestContext build() {
            return new DeleteRelationRequestContext(this);
        }
    }
}
