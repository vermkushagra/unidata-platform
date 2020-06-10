package com.unidata.mdm.backend.common.context;

import com.unidata.mdm.backend.common.keys.EtalonKey;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.types.ApprovalState;

public class SplitContext extends CommonSendableContext implements RecordIdentityContext, ApprovalStateSettingContext {

    private final OriginKey originKey;

    private EtalonKey oldEtalonKey;

    private EtalonKey newEtalonKey;

    /**
     * Force approval state.
     */
    private ApprovalState approvalState;

    public SplitContext(final OriginKey originKey) {
        this.originKey = originKey;
    }

    public SplitContext(final OriginKey originKey, final EtalonKey oldEtalonKey) {
        this.originKey = originKey;
        this.oldEtalonKey = oldEtalonKey;
    }

    @Override
    public RecordKeys keys() {
        return RecordKeys.builder()
                .etalonKey(newEtalonKey != null ? newEtalonKey : oldEtalonKey)
                .originKey(originKey)
                .build();
    }

    @Override
    public StorageId keysId() {
        return StorageId.DATA_GET_KEYS;
    }

    @Override
    public String getEtalonKey() {
        return newEtalonKey != null ? newEtalonKey.getId() : oldEtalonKey.getId();
    }

    public String getOriginKey() {
        return originKey.getId();
    }

    @Override
    public String getExternalId() {
        return originKey.getExternalId();
    }

    @Override
    public String getEntityName() {
        return originKey.getEntityName();
    }

    @Override
    public String getSourceSystem() {
        return originKey.getSourceSystem();
    }

    public EtalonKey getNewEtalonKey() {
        return newEtalonKey;
    }

    public SplitContext setNewEtalonKey(final EtalonKey newEtalonKey) {
        this.newEtalonKey = newEtalonKey;
        return this;
    }

    public EtalonKey getOldEtalonKey() {
        return oldEtalonKey;
    }

    public SplitContext setOldEtalonKey(final EtalonKey oldEtalonKey) {
        this.oldEtalonKey = oldEtalonKey;
        return this;
    }

    @Override
    public ApprovalState getApprovalState() {
        return approvalState;
    }

    public SplitContext setApprovalState(ApprovalState approvalState) {
        this.approvalState = approvalState;
        return this;
    }
}
