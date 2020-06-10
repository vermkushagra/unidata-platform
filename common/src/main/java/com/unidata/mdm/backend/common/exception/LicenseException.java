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
