/**
 * 
 */
package com.unidata.mdm.backend.common.integration.exits;

/**
 * @author mikhail
 * User exit exception type.
 */
public class ExitException extends RuntimeException {

	/**
	 * SVUID.
	 */
	private static final long serialVersionUID = -6340218501531788802L;

	/**
	 * Exit state.
	 */
	private final ExitState exitState;

	/**
	 * Ctor.
	 * @param exitState the exit state
	 * @param message the message
	 */
	public ExitException(ExitState exitState, String message) {
		super(message);
		this.exitState = exitState;
	}

	/**
	 * Ctor.
	 * @param exitState the exit state
	 * @param message the message
	 * @param cause the cause
	 * @param cause
	 */
	public ExitException(ExitState exitState, String message, Throwable cause) {
		super(message, cause);
		this.exitState = exitState;
	}

	/**
	 * Ctor.
	 * @param exitState the exit state
	 * @param message the message
	 * @param cause the cause
	 * @param enableSuppression flag
	 * @param writableStackTrace flag
	 */
	public ExitException(ExitState exitState, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.exitState = exitState;
	}

	/**
	 * Gets exit state.
	 * @return the state
	 */
	public ExitState getExitState() {
		return exitState;
	}

}
