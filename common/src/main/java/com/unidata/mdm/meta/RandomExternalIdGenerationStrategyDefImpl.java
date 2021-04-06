package com.unidata.mdm.meta;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

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
