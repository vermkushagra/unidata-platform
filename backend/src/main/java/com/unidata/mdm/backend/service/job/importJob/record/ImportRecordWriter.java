package com.unidata.mdm.backend.service.job.importJob.record;

import static java.util.Objects.nonNull;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.CommonSendableContext;
import com.unidata.mdm.backend.common.context.DeleteRequestContext;
import com.unidata.mdm.backend.common.context.UpsertRequestContext;
import com.unidata.mdm.backend.common.dto.UpsertRecordDTO;
import com.unidata.mdm.backend.common.keys.RecordKeys;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.types.ApprovalState;
import com.unidata.mdm.backend.common.types.RecordStatus;
import com.unidata.mdm.backend.common.types.impl.RecordKeysCache;
import com.unidata.mdm.backend.service.job.importJob.AbstractCountingWriter;

@StepScope
public class ImportRecordWriter extends AbstractCountingWriter<CommonSendableContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportRecordWriter.class);

    /**
     * record service
     */
    @Autowired
    private DataRecordsService recordsService;
    /**
     * This run cache.
     */
    private RecordKeysCache keyCache = null;

    @Override
    public void write(List<? extends CommonSendableContext> items) throws Exception {

        setTotal(items.size());
        for (CommonSendableContext context : items) {

            boolean isUpsert = context instanceof UpsertRequestContext;
            try {
                if (isUpsert) {
                    UpsertRequestContext uCtx = (UpsertRequestContext) context;
                    UpsertRecordDTO dto = recordsService.atomicUpsert(uCtx);
                    //special option for command line client!
                    if (nonNull(keyCache) && dto.isEtalon() && dto.isOrigin()) {
                        RecordKeys keys = RecordKeys.builder()
                                                    .etalonKey(dto.getEtalon().getInfoSection().getEtalonKey())
                                                    .originKey(dto.getOrigin().getInfoSection().getOriginKey())
                                                    .entityName(dto.getEtalon().getInfoSection().getEntityName())
                                                    .etalonStatus(dto.getEtalon().getInfoSection().getStatus())
                                                    .originStatus(dto.getOrigin().getInfoSection() != null ?
                                                            dto.getOrigin().getInfoSection().getStatus() :
                                                            RecordStatus.ACTIVE)
                                                    .etalonState(dto.getOrigin().getInfoSection() != null ?
                                                            dto.getOrigin().getInfoSection().getApproval() :
                                                            ApprovalState.APPROVED)
                                                    .build();

                        keyCache.add(keys);
                    }
                } else {
                    DeleteRequestContext dCtx = (DeleteRequestContext) context;
                    recordsService.deleteRecord(dCtx);
                }
            } catch (Exception e) {
                incrementFailed();
                String message = isUpsert
                        ? getErrorMessage(e, (UpsertRequestContext) context)
                        : getErrorMessage(e);
                LOGGER.error("Error during upsert record. {}", message);
            }
        }
    }

    public void setRecordsService(DataRecordsService recordsService) {
        this.recordsService = recordsService;
    }

    public void setKeyCache(RecordKeysCache keyCache) {
        this.keyCache = keyCache;
    }
}
