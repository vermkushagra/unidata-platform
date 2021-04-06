package com.unidata.mdm.api.wsdl.v4;

import java.util.Locale;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.MessageImpl;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class NonStandardErrorInterceptor extends AbstractPhaseInterceptor<MessageImpl> {

    /**
     * Russian locale.
     */
    private static final Locale RU = new Locale("ru");

    @Autowired
    private MessageSource messageSource;

    public NonStandardErrorInterceptor() {
        super(Phase.PRE_LOGICAL);
    }

    @Override
    public void handleMessage(MessageImpl message) throws Fault {
        Exception fault = message.getContent(Exception.class);
        if (fault == null || !(fault instanceof Fault)) {
            return;
        }
        Throwable faultCause = fault.getCause();
        if (faultCause instanceof NumberFormatException) {
            String error = messageSource.getMessage("app.incorrect.xml.attribute.number",
                    new Object[] { fault.getMessage() }, fault.getMessage(), RU);
            ((Fault) fault).setMessage(error);
        }
    }
}
