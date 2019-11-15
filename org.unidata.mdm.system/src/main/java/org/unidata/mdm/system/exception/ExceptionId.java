package org.unidata.mdm.system.exception;

import java.util.Objects;
/**
 * @author Alexander Malyshev
 *
 * The exception ID class.
 */
public final class ExceptionId {
    /**
     * Unique exception code. Must be used only once in the app.
     */
    private final String code;
    /**
     * Translation code of a localized message (not the final message).
     */
    private final String message;
    /**
     * Constructor.
     * @param code the code.
     * @param message the translation code of a localized message.
     */
    public ExceptionId(String code, String message) {
        this.code = code;
        this.message = message;
    }
    /**
     * Msg code.
     * @return code
     */
    public String code() {
        return code;
    }
    /**
     * Translation code.
     * @return translation code
     */
    public String message() {
        return message;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExceptionId)) {
            return false;
        }
        ExceptionId that = (ExceptionId) o;
        return Objects.equals(code, that.code);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return code;
    }
}