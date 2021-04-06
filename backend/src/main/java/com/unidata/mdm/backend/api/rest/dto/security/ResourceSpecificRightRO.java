package com.unidata.mdm.backend.api.rest.dto.security;


/**
 * @author Mikhail Mikhailov
 * Resource specific, justified by security level resource access rights.
 */
public class ResourceSpecificRightRO extends RightRO {

    /** Virtual "restore" right */
    private boolean restore;

    /** Virtual "merge" right */
    private boolean merge;

    /**
     * Constructor.
     */
    public ResourceSpecificRightRO() {
        super();
    }

    /**
     * @return the restore
     */
    public boolean isRestore() {
        return restore;
    }

    /**
     * @param restore the restore to set
     */
    public void setRestore(boolean restore) {
        this.restore = restore;
    }


    /**
     * @return the merge
     */
    public boolean isMerge() {
        return merge;
    }


    /**
     * @param merge the merge to set
     */
    public void setMerge(boolean merge) {
        this.merge = merge;
    }
}
