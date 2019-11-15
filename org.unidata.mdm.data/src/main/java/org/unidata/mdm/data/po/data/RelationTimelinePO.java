package org.unidata.mdm.data.po.data;

import java.util.List;

import org.unidata.mdm.core.po.ObjectPO;
import org.unidata.mdm.data.po.keys.RelationKeysPO;

/**
 * @author Mikhail Mikhailov
 * Timeline vistory version.
 */
public class RelationTimelinePO implements ObjectPO {
    /**
     * Keys element, may be null.
     */
    public static final String FIELD_KEYS = "keys";
    /**
     * Timeline data - array of vistory objects. May be null.
     */
    public static final String FIELD_VISTORY_DATA = "vistory_data";
    /**
     * Keys object.
     */
    private RelationKeysPO keys;
    /**
     * Vistory records (timeline).
     */
    private List<RelationVistoryPO> vistory;
    /**
     * Constructor.
     */
    public RelationTimelinePO() {
        super();
    }
    /**
     * Utility constructor.
     * @param versions the versions to hold.
     */
    public RelationTimelinePO(List<RelationVistoryPO> versions) {
        this.vistory = versions;
    }
    /**
     * @return the keys
     */
    public RelationKeysPO getKeys() {
        return keys;
    }
    /**
     * @param keys the keys to set
     */
    public void setKeys(RelationKeysPO keys) {
        this.keys = keys;
    }
    /**
     * @return the vistory
     */
    public List<RelationVistoryPO> getVistory() {
        return vistory;
    }
    /**
     * @param recordEtalonId the recordEtalonId to set
     */
    public void setVistory(List<RelationVistoryPO> vistory) {
        this.vistory = vistory;
    }
}
