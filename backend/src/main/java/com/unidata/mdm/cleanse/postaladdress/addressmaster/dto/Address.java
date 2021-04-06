package com.unidata.mdm.cleanse.postaladdress.addressmaster.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Entity represent answer record from http://addressmaster.ru/index.html
 *
 * @author Pavel Alexeev.
 * @created 2016-01-19 11:22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {
    public enum MatchStatus{
        REJECTED, ACCEPTED_ONLY_STREET_RECOGNIZED, GOOD_ONLY_STREET_RECOGNIZED, ACCEPTED, GOOD, PERFECT
    }

    @JsonProperty("ADDRESS_GUID")
    UUID ADDRESS_GUID;

    @JsonProperty("ADDRESS_ID")
    Long ADDRESS_ID;

    @JsonProperty("ADDRESS_INFO")
    List<AddressInfo> ADDRESS_INFO;

    @JsonProperty("HOUSE_INFO")
    HouseInfo HOUSE_INFO;

    @JsonProperty("MATCH_STATUS")
    MatchStatus MATCH_STATUS;

    // String because there may be several values joined by space like "199034 199178"
    @JsonProperty("ZIPCODE")
    String ZIPCODE;

    @JsonProperty("DEBUG_INFO")
    DebugInfo DEBUG_INFO;

    public UUID getADDRESS_GUID() {
        return ADDRESS_GUID;
    }

    public void setADDRESS_GUID(UUID ADDRESS_GUID) {
        this.ADDRESS_GUID = ADDRESS_GUID;
    }

    public Long getADDRESS_ID() {
        return ADDRESS_ID;
    }

    public void setADDRESS_ID(Long ADDRESS_ID) {
        this.ADDRESS_ID = ADDRESS_ID;
    }

    public List<AddressInfo> getADDRESS_INFO() {
        return ADDRESS_INFO;
    }

    public void setADDRESS_INFO(List<AddressInfo> ADDRESS_INFO) {
        this.ADDRESS_INFO = ADDRESS_INFO;
    }

    public HouseInfo getHOUSE_INFO() {
        return HOUSE_INFO;
    }

    public void setHOUSE_INFO(HouseInfo HOUSE_INFO) {
        this.HOUSE_INFO = HOUSE_INFO;
    }

    public MatchStatus getMATCH_STATUS() {
        return MATCH_STATUS;
    }

    public void setMATCH_STATUS(MatchStatus MATCH_STATUS) {
        this.MATCH_STATUS = MATCH_STATUS;
    }

    public String getZIPCODE() {
        return ZIPCODE;
    }

    public void setZIPCODE(String ZIPCODE) {
        this.ZIPCODE = ZIPCODE;
    }

    public DebugInfo getDEBUG_INFO() {
        return DEBUG_INFO;
    }

    /**
     * By some reason it is String
     */
    public void setDEBUG_INFO(String DEBUG_INFO) {
        this.DEBUG_INFO = new DebugInfo(DEBUG_INFO);
    }

    /**
     * Reimplementation of JavaScript method _updateAddressWithMetadata from http://addressmaster.ru/js/UIController.js
     * @TODO have worth place it on server side for service on my mind
     * And I not sure it is correct as it include not all parts of address
     */
    public String getFullAddressString(){
        final StringBuilder res = new StringBuilder(ZIPCODE);

        res.append(", ").append(getIntermediateAddressString());

        String part = "";
        part = getHouseNumberString();
        if (part.length() > 0){
            res.append(", ").append(part);
        }
        part = getBuildingNumberString();
        if (part.length() > 0){
            res.append(", ").append(part);
        }
        part = getBuildingStructureString();
        if (part.length() > 0){
            res.append(", ").append(part);
        }

        return res.toString();
    }

    public String getHouseNumberString() {
        if (null != HOUSE_INFO && null != HOUSE_INFO.getHOUSE_NUMBER()) {
            return "дом " + HOUSE_INFO.getHOUSE_NUMBER();
        }
        return "";
    }

    public String getBuildingNumberString() {
        if (null != HOUSE_INFO && null != HOUSE_INFO.getBUILDING_NUMBER()) {
            return "корпус " + HOUSE_INFO.getBUILDING_NUMBER();
        }
        return "";
    }

    public String getBuildingStructureString() {
        if (null != HOUSE_INFO && null != HOUSE_INFO.getSTRUCTURE()) {
            return HOUSE_INFO.getSTRUCTURE().getSTRUCTURE_STATUS().getSTRUCTURE_STATUS_SHORT_DESC() + " " + HOUSE_INFO.getSTRUCTURE().getSTRUCTURE_NUM();
        }
        return "";
    }

    /**
     * Return intermediate part of address as string. Basically it is part between zip code and building information.
     * In structured way it stored in {@link #ADDRESS_INFO} and amount of element may vary.
     *
     * @return Result of concatenation all elements, joined by ","
     */
    public String getIntermediateAddressString() {
        final List<String> res = new ArrayList<>();
        if (null != ADDRESS_INFO){
            ADDRESS_INFO.forEach(addressInfo -> {
                res.add(addressInfo.getElementTypeName() + " " + addressInfo.getFormalName());
            });
            return String.join(", ", res);
        }
        return "";
    }


}
