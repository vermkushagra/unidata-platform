package org.unidata.mdm.meta.dto;

import org.unidata.mdm.meta.LookupEntityDef;

/**
 * @author Mikhail Mikhailov on Nov 29, 2019
 */
public class GetModelLookupDTO {
    /**
     * The lookup.
     */
    private LookupEntityDef lookup;
    /**
     * Constructor.
     */
    public GetModelLookupDTO() {
        super();
    }
    /**
     * Constructor.
     * @param lookup the lookup.
     */
    public GetModelLookupDTO(LookupEntityDef lookup) {
        super();
        this.lookup = lookup;
    }
    /**
     * @return the lookup
     */
    public LookupEntityDef getLookup() {
        return lookup;
    }
    /**
     * @param lookup the lookup to set
     */
    public void setLookup(LookupEntityDef lookup) {
        this.lookup = lookup;
    }
}
