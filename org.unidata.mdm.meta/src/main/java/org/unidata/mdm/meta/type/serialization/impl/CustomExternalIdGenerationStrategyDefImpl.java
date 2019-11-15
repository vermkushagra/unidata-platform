package org.unidata.mdm.meta.type.serialization.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.unidata.mdm.meta.CustomExternalIdGenerationStrategyDef;
import org.unidata.mdm.meta.ExternalIdGenerationStrategyType;

/**
 * @author Dmitrii Kopin
 * Constant value setter.
 */
@XmlType(name = "CustomExternalIdGenerationStrategyDefImpl", namespace = "http://meta.mdm.unidata.com/")
@XmlAccessorType(XmlAccessType.FIELD)
public class CustomExternalIdGenerationStrategyDefImpl extends CustomExternalIdGenerationStrategyDef {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1116224606996232507L;

    /**
     * Constructor.
     */
    public CustomExternalIdGenerationStrategyDefImpl() {
        super();
        setStrategyType(ExternalIdGenerationStrategyType.CUSTOM);
    }

}
