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

package com.unidata.mdm.backend.common.exception;

import java.time.LocalDateTime;

/**
 * @author Mikhail Mikhailov
 * License exception mark.
 */
public class LicenseException extends SystemRuntimeException {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -1544438852504865706L;

    private LocalDateTime expirationDate;

    /**
     * Constructor.
     * @param message
     * @param id
     * @param expirationDate
     */
    public LicenseException(String message, ExceptionId id, LocalDateTime expirationDate) {
        super(message, id);
        this.expirationDate = expirationDate;
    }
    /**
     * Constructor.
     * @param message
     * @param id
     * @param args
     */
    public LicenseException(String message, ExceptionId id, LocalDateTime expirationDate, Object... args) {
        super(message, id, args);
        this.expirationDate = expirationDate;
    }

    /**
     * Constructor.
     * @param message
     * @param cause
     * @param id
     * @param args
     */
    public LicenseException(String message, Throwable cause, ExceptionId id, LocalDateTime expirationDate, Object... args) {
        super(message, cause, id, args);
        this.expirationDate = expirationDate;
    }

    /**
     * Constructor.
     * @param cause
     * @param id
     * @param args
     */
    public LicenseException(Throwable cause, ExceptionId id, LocalDateTime expirationDate, Object... args) {
        super(cause, id, args);
        this.expirationDate = expirationDate;
    }

    /**
     * Get license expiration date
     * @return expirationDate
     */
    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

}