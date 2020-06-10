package com.unidata.mdm.backend.common.types.impl;

import com.unidata.mdm.backend.common.upath.UPath;
import com.unidata.mdm.meta.DQRMappingDef;

/**
 * @author Mikhail Mikhailov
 * Impl class for DQ rules mapping.
 */
public class DQRMappingDefImpl extends DQRMappingDef {
    /**
     * Compiled UPath.
     */
    private transient UPath upath;
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -8380108177476527552L;
    /**
     * Constructor.
     */
    public DQRMappingDefImpl() {
        super();
    }
    /**
     * @return the upath
     */
    public UPath getUpath() {
        return upath;
    }
    /**
     * @param upath the upath to set
     */
    public void setUpath(UPath upath) {
        this.upath = upath;
    }
}
