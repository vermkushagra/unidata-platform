/**
 *
 */
package com.unidata.mdm.backend.service.data.listener.record;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.unidata.mdm.backend.common.context.MergeRequestContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.integration.exits.MergeListener;
import com.unidata.mdm.backend.conf.impl.MergeImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.listener.DataRecordAfterExecutor;
import com.unidata.mdm.backend.common.types.EtalonRecord;


/**
 * @author Mikhail Mikhailov
 *
 */
public class DataRecordMergeUserExitAfterExecutor implements DataRecordAfterExecutor<MergeRequestContext> {

    /**
     * Configuration service.
     */
    @Autowired
    private ConfigurationService configurationService;

    /**
     * Constructor.
     */
    public DataRecordMergeUserExitAfterExecutor() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean execute(MergeRequestContext ctx) {
        EtalonRecord master = ctx.getFromStorage(StorageId.DATA_MERGE_ETALON_RECORD);
        List<EtalonRecord> duplicates = ctx.getFromStorage(StorageId.DATA_MERGE_DUPLICATES);
        MergeImpl upsert = configurationService.getMerge();
        if (upsert != null && master != null && duplicates != null) {
            MergeListener listener = upsert.getAfterMergeInstances()
                    .get(master.getInfoSection().getEntityName());
            if (listener != null) {
                listener.afterMerge(master, duplicates, ctx);
            }
        }
        return true;
    }

}
