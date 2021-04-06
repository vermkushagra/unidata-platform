/**
 * Date: 11.03.2016
 */

package com.unidata.mdm.backend.service.security.utils;

import java.io.IOException;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * No-op interceptor used only to override beans in main spring config.
 *
 * @author amagdenko
 */
public class NoopInterceptor extends AbstractPhaseInterceptor<Message> {

    public NoopInterceptor() throws IOException {
        super(Phase.RECEIVE);
    }
    @Override
    public void handleMessage(Message message) throws Fault {
        // No-op
    }
}
