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
