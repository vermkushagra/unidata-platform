package com.unidata.mdm.backend.service.maintenance.dto;

import java.io.Serializable;

/**
 * @author ilya.bykov 
 * System mode.
 * TODO: at the moment covers only maintenance and normal modes.
 * Need to be extended.
 */
public class SystemMode implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/** The message. */
	private String message;

	/** The status. */
	private ModeEnum mode;

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message
	 *            the new message
	 */
	public SystemMode withMessage(String message) {
		this.message = message;
		return this;
	}

	/**
	 * Gets the mode.
	 *
	 * @return the mode
	 */
	public ModeEnum getModeEnum() {
		return mode;
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode
	 *            the new mode
	 */
	public SystemMode withModeEnum(ModeEnum mode) {
		this.mode = mode;
		return this;
	}

	/**
	 * The Enum Status.
	 */
	public enum ModeEnum {

		/** The maintenance mode. */
		MAINTENANCE,
		/** The normal mode. */
		NORMAL
	}

}
