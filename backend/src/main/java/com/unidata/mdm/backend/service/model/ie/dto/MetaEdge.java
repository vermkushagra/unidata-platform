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

package com.unidata.mdm.backend.service.model.ie.dto;

import java.io.Serializable;

/**
 * The Class MetaEdge.
 *
 * @param <T>
 *            the generic type
 */
public class MetaEdge<T extends MetaVertex> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new meta edge.
	 */
	private MetaEdge() {
		super();
	}

	/**
	 * Instantiates a new meta edge.
	 *
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 */
	public MetaEdge(T from, T to) {
		this();
		this.from = from;
		this.to = to;
	}

	/** The from. */
	private T from;

	/** The to. */
	private T to;

	/** The existence. */
	private MetaExistence existence;

	/**
	 * Gets the from.
	 *
	 * @return the from
	 */
	public T getFrom() {
		return from;
	}

	/**
	 * Gets the to.
	 *
	 * @return the to
	 */
	public T getTo() {
		return to;
	}

	/**
	 * Gets the existence.
	 *
	 * @return the existence
	 */
	public MetaExistence getExistence() {
		return existence;
	}

	/**
	 * Sets the existence.
	 *
	 * @param existence
	 *            the new existence
	 */
	public void setExistence(MetaExistence existence) {
		this.existence = existence;
	}

}
