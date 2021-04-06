

package com.unidata.mdm.backend.service.job.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.unidata.mdm.backend.common.context.GetMultipleRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.service.data.export.DataExportService;
import com.unidata.mdm.backend.service.job.ComplexJobParameterHolder;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.security.UserService;

/**
 * Export partitioner.
 */
public class ExportPartitioner implements Partitioner {

    /** The stat service. */
    @Autowired
    @Qualifier(value = "xlsxExportService")
    private DataExportService dataExportService;

    /** The data record service. */
    @Autowired
    private DataRecordsService dataRecordService;

    /** The user service. */
    @Autowired
    private UserService userService;

    /** The job parameter holder. */
    @Autowired
    private ComplexJobParameterHolder jobParameterHolder;

    /** The meta model service. */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /** The mrctx key. */
    private String mrctxKey;

    /** The user name key. */
    private String userNameKey;
    /* (non-Javadoc)
     * @see org.springframework.batch.core.partition.support.Partitioner#partition(int)
     */
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        //extract job parameters
        GetMultipleRequestContext mrCTX = jobParameterHolder.getComplexParameterAndRemove(getMrctxKey());
        String userName = jobParameterHolder.getComplexParameterAndRemove(getUserNameKey());
        //create xlsx file
        ByteArrayOutputStream data = dataExportService.exportData(mrCTX);
        //create user event
        boolean isLookup = metaModelService.isLookupEntity(mrCTX.getEntityName());
        String displayName = isLookup ? metaModelService.getLookupEntityById(mrCTX.getEntityName()).getDisplayName() :
                metaModelService.getEntityById(mrCTX.getEntityName()).getEntity().getDisplayName();
        UpsertUserEventRequestContext uueCtx
                = new UpsertUserEventRequestContextBuilder()
                .login(userName)
                .type("XLSX_EXPORT")
                .content(MessageUtils.getMessage(isLookup
                        ? UserMessageConstants.DATA_EXPORT_LOOKUP_RESULT
                        : UserMessageConstants.DATA_EXPORT_ENTITY_RESULT, displayName))
                .build();
        UserEventDTO userEventDTO = userService.upsert(uueCtx);
        //save exported file and attach it to the early created user event
        SaveLargeObjectRequestContext slorCTX
                = new SaveLargeObjectRequestContextBuilder()
                .eventKey(userEventDTO.getId())
                .mimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .binary(true)
                .inputStream(new ByteArrayInputStream(data.toByteArray()))
                .filename(mrCTX.getEntityName()+".xlsx")
                .build();
        dataRecordService.saveLargeObject(slorCTX);
        return new HashMap<>();
    }

    /**
     * Gets the mrctx key.
     *
     * @return the mrctxKey
     */
    public String getMrctxKey() {
        return mrctxKey;
    }

    /**
     * Sets the mrctx key.
     *
     * @param mrctxKey
     *            the mrctxKey to set
     */
    public void setMrctxKey(String mrctxKey) {
        this.mrctxKey = mrctxKey;
    }

    /**
     * Gets the user name key.
     *
     * @return the userNameKey
     */
    public String getUserNameKey() {
        return userNameKey;
    }

    /**
     * Sets the user name key.
     *
     * @param userNameKey
     *            the userNameKey to set
     */
    public void setUserNameKey(String userNameKey) {
        this.userNameKey = userNameKey;
    }
}
