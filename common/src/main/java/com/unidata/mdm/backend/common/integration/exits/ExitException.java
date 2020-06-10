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
