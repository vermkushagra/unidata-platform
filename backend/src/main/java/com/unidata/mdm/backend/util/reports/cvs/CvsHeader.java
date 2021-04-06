package com.unidata.mdm.backend.util.reports.cvs;

import javax.annotation.Nonnull;

/**
 * Csv header
 */
public interface CvsHeader {
    /**
     * @return header name
     */
    @Nonnull
    String headerName();
}
