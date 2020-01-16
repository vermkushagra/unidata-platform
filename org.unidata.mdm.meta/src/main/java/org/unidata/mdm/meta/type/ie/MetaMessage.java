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

package org.unidata.mdm.meta.type.ie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MetaMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The status. */
	private MetaStatus status;

	/** The messages. */
	private List<String> messages;

	public MetaStatus getStatus() {
		return status;
	}

	public void setStatus(MetaStatus status) {
		this.status = status;
	}
	public MetaMessage withStatus(MetaStatus status){
		this.status = status;
		return this;
	}

	public MetaMessage withMessage(String... messages) {
		if (messages != null) {
			for (String message : messages) {
				getMessages().add(message);
			}
		}
		return this;
	}

	public List<String> getMessages() {
		if (this.messages == null) {
			this.messages = new ArrayList<>();
		}
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

}
