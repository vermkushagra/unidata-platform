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
