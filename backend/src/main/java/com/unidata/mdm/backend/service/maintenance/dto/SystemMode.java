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
