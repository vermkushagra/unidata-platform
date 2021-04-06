package com.unidata.mdm.backend.service.wf;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Denis Kostovarov
 */
public class EmailEventListener implements ActivitiEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailEventListener.class);

    @Override
    public void onEvent(ActivitiEvent event) {

        if (event.getType() == ActivitiEventType.JOB_EXECUTION_FAILURE) {
            LOGGER.warn("Failed to send email notification for processInstanceId " + event.getProcessInstanceId());
        }

        if (event.getType() == ActivitiEventType.JOB_EXECUTION_SUCCESS) {
            LOGGER.debug("Send notification succeeded for processInstanceId " + event.getProcessInstanceId());
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
