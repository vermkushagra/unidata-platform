package com.unidata.mdm.backend.service.initializer;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.unidata.mdm.backend.dto.initializer.ActionTypeDTO;
import com.unidata.mdm.backend.dto.initializer.SystemElementDTO;

public class SystemEventListener implements MessageListener<SystemElementDTO> {

	@Override
	public void onMessage(Message<SystemElementDTO> message) {
		//TODO: right now only cleanse functions supported
		ActionTypeDTO action = message.getMessageObject().getAction();
	
		
	}

}
