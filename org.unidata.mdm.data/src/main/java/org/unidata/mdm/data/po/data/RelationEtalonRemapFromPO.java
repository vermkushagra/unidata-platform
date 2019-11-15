/**
 *
 */
package org.unidata.mdm.data.po.data;

/**
 * @author Mikhail Mikhailov
 * Relation etalon record.
 */
public class RelationEtalonRemapFromPO extends RelationEtalonPO {
    /**
     * Etalon id from.
     */
    public static final String FIELD_NEW_ETALON_ID_FROM = "etalon_id_from_new";
    /**
     * Etalon id from.
     */
    private String newEtalonIdFrom;
    /**
     * Constructor.
     */
    public RelationEtalonRemapFromPO() {
        super();
    }
    /**
     * @param newEtalonIdFrom the newEtalonIdFrom to set
     */
    public void setNewEtalonIdFrom(String newEtalonIdFrom) {
        this.newEtalonIdFrom = newEtalonIdFrom;
    }
    /**
     * @return the newEtalonIdFrom
     */
    public String getNewEtalonIdFrom() {
        return newEtalonIdFrom;
    }
}
