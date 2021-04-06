package com.unidata.mdm.backend.service.data.listener.record;

import java.util.ArrayList;
import java.util.List;

import com.unidata.mdm.backend.common.context.SplitContext;
import com.unidata.mdm.backend.common.context.StorageId;
import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.integration.exits.BeforeSplitListener;
import com.unidata.mdm.backend.common.integration.exits.ExitResult;
import com.unidata.mdm.backend.conf.impl.SplitImpl;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.data.listener.DataRecordBeforeExecutor;
import com.unidata.mdm.backend.util.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DataRecordSplitUserExitBeforeExecutor implements DataRecordBeforeExecutor<SplitContext> {


    private static final Logger LOGGER = LoggerFactory.getLogger(DataRecordSplitUserExitBeforeExecutor.class);

    private final ConfigurationService configurationService;

    @Autowired
    public DataRecordSplitUserExitBeforeExecutor(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public boolean execute(SplitContext splitContext) {
        SplitImpl split = configurationService.getSplit();
        if (split != null) {
            BeforeSplitListener listener = split.getBeforeSplitInstances().get(splitContext.getEntityName());
            if(listener == null){
                return true;
            }

            ExitResult exitResult = listener.beforeSplit(splitContext);

            if (exitResult == null) {
                return true;
            }

            if (ExitResult.Status.WARNING.equals(exitResult.getStatus())) {
                LOGGER.warn("User exit for split listener {} and entity {} has warnings : {}",
                        listener.getClass().getSimpleName(), splitContext.getEntityName(), exitResult.getWarningMessage());
                List<ErrorInfoDTO> errors = splitContext.getFromStorage(StorageId.PROCESS_ERRORS);
                if(errors == null){
                    errors = new ArrayList<>();
                }
                ErrorInfoDTO errorInfo = new ErrorInfoDTO();
                errorInfo.setSeverity(ErrorInfoDTO.Severity.LOW);
                errorInfo.setUserMessage(MessageUtils.getMessage(ExceptionId.EX_SPLIT_USER_EXIT_BEFORE_ERROR.getCode(),
                        exitResult.getWarningMessage()));
                errors.add(errorInfo);
                splitContext.putToStorage(StorageId.PROCESS_ERRORS, errors);
            }

            if (ExitResult.Status.ERROR.equals(exitResult.getStatus())) {
                throw new DataProcessingException("Error occurred during run before split data user exit",
                        ExceptionId.EX_SPLIT_USER_EXIT_BEFORE_ERROR,
                        exitResult.getWarningMessage());
            }
        }

        return true;
    }
}
