package com.unidata.mdm.meta;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

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
