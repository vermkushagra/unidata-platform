package com.unidata.mdm.backend.common.types.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.unidata.mdm.backend.common.upath.UPath;
import com.unidata.mdm.meta.DQRuleDef;

/**
 * @author Mikhail Mikhailov
 * Impl class for DQ rules mapping.
 */
public class DQRuleDefImpl extends DQRuleDef {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -5892527502736003893L;
    /**
     * Compiled UPath.
     */
    private transient UPath upath;
    /**
     * Input mappings.
     */
    private transient Map<String, DQRMappingDefImpl> input;
    /**
     * Output mappings.
     */
    private transient Map<String, DQRMappingDefImpl> output;
    /**
     * Constructor.
     */
    public DQRuleDefImpl() {
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
    /**
     * @return the input
     */
    public Map<String, DQRMappingDefImpl> getInput() {
        return Objects.isNull(input) ? Collections.emptyMap() : input;
    }
    /**
     * @param input the input to set
     */
    public void setInput(Map<String, DQRMappingDefImpl> input) {
        this.input = input;
    }
    /**
     * @return the output
     */
    public Map<String, DQRMappingDefImpl> getOutput() {
        return Objects.isNull(output) ? Collections.emptyMap() : output;
    }
    /**
     * @param output the output to set
     */
    public void setOutput(Map<String, DQRMappingDefImpl> output) {
        this.output = output;
    }
}
