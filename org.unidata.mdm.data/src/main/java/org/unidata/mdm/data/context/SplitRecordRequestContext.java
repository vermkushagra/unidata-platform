package org.unidata.mdm.data.context;

import org.unidata.mdm.core.context.ApprovalStateSettingContext;
import org.unidata.mdm.core.type.data.ApprovalState;
import org.unidata.mdm.data.type.keys.RecordEtalonKey;
import org.unidata.mdm.data.type.keys.RecordKeys;
import org.unidata.mdm.data.type.keys.RecordOriginKey;

public class SplitRecordRequestContext extends AbstractRecordIdentityContext implements ApprovalStateSettingContext {
    /**
     * SVUID
     */
    private static final long serialVersionUID = -2906573613407139325L;

    private RecordEtalonKey oldEtalonKey;

    private RecordEtalonKey newEtalonKey;

    /**
     * Force approval state.
     */
    private ApprovalState approvalState;

    protected SplitRecordRequestContext(SplitRequestContextBuilder b) {
        super(b);
        this.oldEtalonKey = b.oldEtalonKey;
    }

    @Override
    public RecordKeys keys() {
        return RecordKeys.builder()
                .etalonKey(newEtalonKey != null ? newEtalonKey : oldEtalonKey)
                .originKey(RecordOriginKey.builder()
                        .id(getOriginKey())
                        .externalId(getExternalId())
                        .entityName(getEntityName())
                        .sourceSystem(getSourceSystem())
                        .enrichment(isEnrichmentKey())
                        .build())
                .entityName(getEntityName())
                .shard(getShard())
                .build();
    }

    // TODO: @Modules
//    @Override
//    public StorageId keysId() {
//        return StorageId.DATA_GET_KEYS;
//    }

    @Override
    public String getEtalonKey() {
        return newEtalonKey != null ? newEtalonKey.getId() : oldEtalonKey.getId();
    }

    public RecordEtalonKey getNewEtalonKey() {
        return newEtalonKey;
    }

    public SplitRecordRequestContext setNewEtalonKey(final RecordEtalonKey newEtalonKey) {
        this.newEtalonKey = newEtalonKey;
        return this;
    }

    public RecordEtalonKey getOldEtalonKey() {
        return keys().getEtalonKey();
    }

    // TODO: @Modules
//    public Date splitDate() {
//        return getFromStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP);
//    }
//
//    public void splitDate(Date date) {
//        putToStorage(StorageId.DATA_UPSERT_RECORD_TIMESTAMP, date);
//    }
//
//    public String systemOriginKey() {
//        return getFromStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD);
//    }
//
//    public void systemOriginKey(String systemOriginKey) {
//        putToStorage(StorageId.DATA_UPSERT_ORIGIN_RECORD, systemOriginKey);
//    }

    @Override
    public ApprovalState getApprovalState() {
        return approvalState;
    }

    public SplitRecordRequestContext setApprovalState(ApprovalState approvalState) {
        this.approvalState = approvalState;
        return this;
    }

    public static SplitRequestContextBuilder builder() {
        return new SplitRequestContextBuilder();
    }

    public static class SplitRequestContextBuilder
        extends AbstractRecordIdentityContextBuilder<SplitRequestContextBuilder> {

        private RecordEtalonKey oldEtalonKey;

        protected SplitRequestContextBuilder() {
            super();
        }

        public SplitRequestContextBuilder oldEtalonKey(final RecordEtalonKey oldEtalonKey) {
            this.oldEtalonKey = oldEtalonKey;
            return self();
        }

        @Override
        public SplitRecordRequestContext build() {
            return new SplitRecordRequestContext(this);
        }
    }
}
