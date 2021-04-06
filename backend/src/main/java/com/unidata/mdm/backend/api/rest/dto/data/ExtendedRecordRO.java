package com.unidata.mdm.backend.api.rest.dto.data;


import java.util.Map;

/**
 *
 */
public class ExtendedRecordRO {

    /**
     * nested record
     */
    private NestedRecordRO record;

    /**
     * Attribute winner map
     */
    private Map<String, String> attributeWinnersMap;

    private String winnerEtalonId;

    /**
     * Constructor
     */
    public ExtendedRecordRO() {
    }

    /**
     * @param record record
     * @param attributeWinnersMap - attribute winner map.
     */
    public ExtendedRecordRO(NestedRecordRO record, Map<String, String> attributeWinnersMap) {
        this.record = record;
        this.attributeWinnersMap = attributeWinnersMap;
    }

    public NestedRecordRO getRecord() {
        return record;
    }

    public void setRecord(NestedRecordRO record) {
        this.record = record;
    }

    public Map<String, String> getAttributeWinnersMap() {
        return attributeWinnersMap;
    }

    public void setAttributeWinnersMap(Map<String, String> accessoryMap) {
        this.attributeWinnersMap = accessoryMap;
    }

    public String getWinnerEtalonId() {
        return winnerEtalonId;
    }

    public void setWinnerEtalonId(String winnerEtalonId) {
        this.winnerEtalonId = winnerEtalonId;
    }
}
