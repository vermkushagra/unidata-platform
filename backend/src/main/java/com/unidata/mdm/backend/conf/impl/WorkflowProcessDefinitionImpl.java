package com.unidata.mdm.backend.conf.impl;

import javax.xml.bind.annotation.XmlTransient;

import com.unidata.mdm.backend.common.integration.wf.WorkflowProcessSupport;
import com.unidata.mdm.conf.WorkflowProcessDefinition;

/**
 * @author Mikhail Mikhailov
 * Process support description.
 */
@SuppressWarnings("serial")
public class WorkflowProcessDefinitionImpl extends WorkflowProcessDefinition {

    /**
     * Process support instance.
     */
    @XmlTransient
    private transient WorkflowProcessSupport support;

    /**
     * Constructor.
     */
    public WorkflowProcessDefinitionImpl() {
        super();
    }

    /**
     * @return the support
     */
    public WorkflowProcessSupport getSupport() {
        return support;
    }

    /**
     * @param support the support to set
     */
    public void setSupport(WorkflowProcessSupport support) {
        this.support = support;
    }
}
