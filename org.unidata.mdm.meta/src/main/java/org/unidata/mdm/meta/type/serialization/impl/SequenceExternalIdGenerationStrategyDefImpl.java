package org.unidata.mdm.meta.type.serialization.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.unidata.mdm.meta.ExternalIdGenerationStrategyType;
import org.unidata.mdm.meta.SequenceExternalIdGenerationStrategyDef;

/**
 * @author Mikhail Mikhailov
 * Sequence value setter.
 */
@XmlType(name = "SequenceExternalIdGenerationStrategyDefImpl", namespace = "http://meta.mdm.unidata.com/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SequenceExternalIdGenerationStrategyDefImpl extends SequenceExternalIdGenerationStrategyDef {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -2287999896195527610L;

    /**
     * Constructor.
     */
    public SequenceExternalIdGenerationStrategyDefImpl() {
        super();
        setStrategyType(ExternalIdGenerationStrategyType.SEQUENCE);
    }

}
