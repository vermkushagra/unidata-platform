package org.unidata.mdm.meta.type.serialization.impl;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.unidata.mdm.meta.ExternalIdGenerationStrategyType;
import org.unidata.mdm.meta.RandomExternalIdGenerationStrategyDef;

/**
 * @author Mikhail Mikhailov
 * Constant value setter.
 */
@XmlType(name = "RandomExternalIdGenerationStrategyDefImpl", namespace = "http://meta.mdm.unidata.com/")
@XmlAccessorType(XmlAccessType.FIELD)
public class RandomExternalIdGenerationStrategyDefImpl extends RandomExternalIdGenerationStrategyDef {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -2412134604063448415L;

    /**
     * Constructor.
     */
    public RandomExternalIdGenerationStrategyDefImpl() {
        super();
        setStrategyType(ExternalIdGenerationStrategyType.RANDOM);
    }
}
