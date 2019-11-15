package org.unidata.mdm.meta.type.serialization.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.unidata.mdm.meta.ConcatenatedExternalIdGenerationStrategyDef;
import org.unidata.mdm.meta.ExternalIdGenerationStrategyType;

/**
 * @author Mikhail Mikhailov
 * Constant value setter.
 */
@XmlType(name = "ConcatenatedExternalIdGenerationStrategyDefImpl", namespace = "http://meta.mdm.unidata.com/")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConcatenatedExternalIdGenerationStrategyDefImpl extends ConcatenatedExternalIdGenerationStrategyDef {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 1616229607996532507L;

    /**
     * Constructor.
     */
    public ConcatenatedExternalIdGenerationStrategyDefImpl() {
        super();
        setStrategyType(ExternalIdGenerationStrategyType.CONCAT);
    }

}
