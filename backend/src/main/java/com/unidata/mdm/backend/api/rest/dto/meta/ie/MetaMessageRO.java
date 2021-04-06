package com.unidata.mdm.backend.api.rest.dto.meta.ie;

import java.util.List;


/**
 * The Class MetaMessageRO.
 * @author ilya.bykov
 */
public class MetaMessageRO {
	
	/** The status. */
	private MetaStatusRO status;
	
	/** The messages. */
	private List<String> messages;

	/**
	 * Gets the messages.
	 *
	 * @return the messages
	 */
	public List<String> getMessages() {
		return messages;
	}

	/**
	 * Sets the messages.
	 *
	 * @param messages the new messages
	 */
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public MetaStatusRO getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(MetaStatusRO status) {
		this.status = status;
	}

}
