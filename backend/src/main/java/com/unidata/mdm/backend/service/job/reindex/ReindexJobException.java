package com.unidata.mdm.backend.service.job.reindex;

import com.unidata.mdm.backend.service.job.JobException;

/**
 * @author Denis Kostovarov
 * TODO remove this in favor of standard UD exception(s).
 */
public class ReindexJobException extends JobException {
    public ReindexJobException(String msg) {
        super(msg);
    }

    public ReindexJobException(Exception exc) {
        super(exc);
    }
}
