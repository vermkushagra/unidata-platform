package org.unidata.mdm.data.po.data;

/**
 * @author Mikhail Mikhailov
 * Relation etalon record.
 */
public class RelationEtalonRemapToPO extends RelationEtalonPO {
    /**
     * Etalon ID to.
     */
    public static final String FIELD_NEW_ETALON_ID_TO = "etalon_id_to_new";
    /**
     * Origin ID to.
     */
    private String newEtalonIdTo;
    /**
     * Constructor.
     */
    public RelationEtalonRemapToPO() {
        super();
    }
    /**
     * @return the newEtalonIdTo
     */
    public String getNewEtalonIdTo() {
        return newEtalonIdTo;
    }
    /**
     * @param newEtalonIdTo the newEtalonIdTo to set
     */
    public void setNewEtalonIdTo(String newEtalonIdTo) {
        this.newEtalonIdTo = newEtalonIdTo;
    }
}
