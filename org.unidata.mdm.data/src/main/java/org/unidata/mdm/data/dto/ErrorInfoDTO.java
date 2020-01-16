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

package org.unidata.mdm.data.dto;

import java.io.Serializable;

public class ErrorInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Type {
        VALIDATION_ERROR,
        INTERNAL_ERROR,
        AUTHENTICATION_ERROR,
        AUTHORIZATION_ERROR,
        USER_ALREADY_EXIST,
        USER_CANNOT_BE_DEACTIVATED
    }

    public enum Severity{
    	LOW("Низкая"),
    	NORMAL("Средняя"),
    	HIGH("Высокая"),
    	CRITICAL("Максимальная");

        private String displayName;

        Severity(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private Type type;
    private Severity severity;
    private String errorCode;

    private String userMessage;

    private String userMessageDetails;

    public ErrorInfoDTO() {
        super();
    }

    public ErrorInfoDTO(Type type) {
        if (type != null) {
            this.type = type;
        }
    }

    public String getType() {
        if (type != null) {
            return type.name();
        }
        return null;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }


    public String getUserMessageDetails() {
        return userMessageDetails;
    }

    public ErrorInfoDTO setUserMessageDetails(String userMessageDetails) {
        this.userMessageDetails = userMessageDetails;
        return this;
    }

	/**
	 * @return the severity
	 */
	public Severity getSeverity() {
		if(this.severity==null){
			return Severity.NORMAL;
		}
		return severity;
	}

	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
}
