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

package com.unidata.mdm.backend.common.integration.auth;

/**
 * Security exception occurs if something went wrong during authentication, authorization or profile fetching.
 *
 * @author Denis Kostovarov
 */
public class SecurityDataProviderException extends RuntimeException {
    static final long serialVersionUID = 1344547890545496749L;

    private SecurityState securityState;

    public SecurityDataProviderException(SecurityState securityState) {
        this.securityState = securityState;
    }

    public SecurityDataProviderException(SecurityState securityState, String message) {
        super(message);
        this.securityState = securityState;
    }

    public SecurityDataProviderException(SecurityState securityState, String message, Throwable cause) {
        super(message, cause);
        this.securityState = securityState;
    }

    public SecurityDataProviderException(SecurityState securityState, Throwable cause) {
        super(cause);
        this.securityState = securityState;
    }

    public SecurityDataProviderException(SecurityState securityState, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.securityState = securityState;
    }

    public SecurityState getSecurityState() {
        return securityState;
    }
}
