package com.unidata.mdm.backend.api.rest.dto.data;

import java.util.List;

/**
 * @author Mikhail Mikhailov
 * Keys RO.
 */
public class RecordKeysRO {
    /**
     * Etalon id.
     */
    private String etalonId;
    /**
     * Origin keys.
     */
    private List<OriginKeyRO> originKeys;
    /**
     * Constructor.
     */
    public RecordKeysRO() {
        super();
    }
    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }
    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }
    /**
     * @return the originKeys
     */
    public List<OriginKeyRO> getOriginKeys() {
        return originKeys;
    }
    /**
     * @param originKeys the originKeys to set
     */
    public void setOriginKeys(List<OriginKeyRO> originKeys) {
        this.originKeys = originKeys;
    }


}
