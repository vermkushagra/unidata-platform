package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.ErrorInfo;
import com.unidata.mdm.backend.common.dto.ErrorInfoDTO;

/**
 * @author Dmitrii Kopin
 */
public class ErrorInfoToRestErrorInfoConverter {

    /**
     * Constructor.
     */
    private ErrorInfoToRestErrorInfoConverter() {
        super();
    }

    /**
     * Converts error info dto to rest
     *
     * @param source error info dto object
     * @return REST object
     */
    public static ErrorInfo convert(ErrorInfoDTO source) {

        if (source == null) {
            return null;
        }

        ErrorInfo target;
        if(source.getType() != null){
            target = new ErrorInfo(ErrorInfo.Type.valueOf(source.getType()));
        } else {
            target = new ErrorInfo();
        }

        target.setErrorCode(source.getErrorCode());
        target.setUserMessage(source.getUserMessage());
        target.setUserMessageDetails(source.getUserMessageDetails());
        if(source.getSeverity() != null){
            target.setSeverity(ErrorInfo.Severity.valueOf(source.getSeverity().name()));
        }
        return target;
    }
}
