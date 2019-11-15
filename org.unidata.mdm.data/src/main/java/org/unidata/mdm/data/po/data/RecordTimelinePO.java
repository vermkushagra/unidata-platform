package org.unidata.mdm.data.po.data;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.unidata.mdm.core.po.ObjectPO;
import org.unidata.mdm.data.po.keys.RecordKeysPO;

/**
 * @author Mikhail Mikhailov
 * Timeline vistory version.
 */
public class RecordTimelinePO implements ObjectPO {
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
    private RecordKeysPO keys;
    /**
     * Vistory records (timeline).
     */
    private List<RecordVistoryPO> vistory;
    /**
     * Constructor.
     */
    public RecordTimelinePO() {
        super();
    }
    /**
     * Utility constructor.
     * @param versions the versions to hold.
     */
    public RecordTimelinePO(List<RecordVistoryPO> versions) {
        this.vistory = versions;
    }
    /**
     * @return the keys
     */
    public RecordKeysPO getKeys() {
        return keys;
    }
    /**
     * @param keys the keys to set
     */
    public void setKeys(RecordKeysPO keys) {
        this.keys = keys;
    }
    /**
     * @return the vistory
     */
    public List<RecordVistoryPO> getVistory() {
        return Objects.isNull(vistory) ? Collections.emptyList() : vistory;
    }
    /**
     * @param recordEtalonId the recordEtalonId to set
     */
    public void setVistory(List<RecordVistoryPO> vistory) {
        this.vistory = vistory;
    }
}
