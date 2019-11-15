package org.unidata.mdm.soap.core.exception;

import org.unidata.mdm.system.exception.ExceptionId;

/**
 * @author Alexander Malyshev
 */
public final class CoreSoapExceptionIds {
    private CoreSoapExceptionIds() { }

    public static final ExceptionId EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE =
            new ExceptionId("EX_SYSTEM_JAXB_TYPE_FACTORY_INIT_FAILURE", "");

    public static final ExceptionId EX_SYSTEM_JAXB_CONTEXT_INIT_FAILURE =
            new ExceptionId("EX_SYSTEM_JAXB_CONTEXT_INIT_FAILURE", "");
}
