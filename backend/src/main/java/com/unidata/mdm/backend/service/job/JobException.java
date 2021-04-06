package com.unidata.mdm.backend.service.job;

/**
 * @author Denis Kostovarov
 * TODO remove this in favor of standard UD exception(s).
 */
public class JobException extends Exception {
    public JobException(String msg) {
        super(msg);
    }

    public JobException(Exception exc) {
        super(exc);
    }
}
