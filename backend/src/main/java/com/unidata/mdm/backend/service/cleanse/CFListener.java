package com.unidata.mdm.backend.service.cleanse;

import java.io.IOException;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * The listener interface for receiving CF events. The class that is interested
 * in processing a CF event implements this interface, and the object created
 * with that class is registered with a component using the component's
 * <code>addCFListener<code> method. When the CF event occurs, that object's
 * appropriate method is invoked.
 *
 * @see CFEvent
 */
public class CFListener implements MessageListener<String> {

	/** The cleanse function service. */
	private CleanseFunctionServiceExt cleanseFunctionService;
	private String localMemberUUID;

	/**
	 * Instantiates a new CF listener.
	 *
	 * @param cleanseFunctionService
	 *            the cleanse function service
	 * @param localMemberUUID 
	 */
	public CFListener(CleanseFunctionServiceExt cleanseFunctionService, String localMemberUUID) {
		this.cleanseFunctionService = cleanseFunctionService;
		this.localMemberUUID = localMemberUUID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hazelcast.core.MessageListener#onMessage(com.hazelcast.core.Message)
	 */
	@Override
	public void onMessage(Message<String> message) {
		try {
			if(message.getPublishingMember().getUuid().equals(localMemberUUID)) {
				return;
			}
			this.cleanseFunctionService.loadAndInit(message.getMessageObject());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
