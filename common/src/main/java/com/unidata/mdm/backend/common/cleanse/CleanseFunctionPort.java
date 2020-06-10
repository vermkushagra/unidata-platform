package com.unidata.mdm.backend.common.cleanse;

/**
 * @author Mikhail Mikhailov
 * The cleanse function port.
 */
public class CleanseFunctionPort {
    /**
     * Name.
     */
    private String name;
    /**
     * Value required or not.
     */
    private boolean required;
    /**
     * Description.
     */
    private String description;
    /**
     * Filtering mode for UPathValue ports.
     */
    private CleanseFunctionPortFilteringMode filteringMode = CleanseFunctionPortFilteringMode.MODE_UNDEFINED;
    /**
     * Constructor.
     */
    public CleanseFunctionPort() {
        super();
    }

}
