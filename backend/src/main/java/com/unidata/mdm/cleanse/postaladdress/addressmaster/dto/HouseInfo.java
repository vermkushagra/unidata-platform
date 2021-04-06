package com.unidata.mdm.cleanse.postaladdress.addressmaster.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represent house (building) address part info
 *
 * @author Pavel Alexeev.
 * @created 2016-01-19 11:29.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HouseInfo {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Structure{

        @JsonIgnoreProperties(ignoreUnknown = true)
        public class StructureStatus {

            @JsonProperty("STRUCTURE_STATUS_CODE")
            private Integer STRUCTURE_STATUS_CODE;

            @JsonProperty("STRUCTURE_STATUS_SHORT_DESC")
            private String STRUCTURE_STATUS_SHORT_DESC; // f.e. "литер"

            @JsonProperty("STRUCTURE_STATUS_LONG_DESC")
            private String STRUCTURE_STATUS_LONG_DESC; // f.e. "Литер"

            public Integer getSTRUCTURE_STATUS_CODE() {
                return STRUCTURE_STATUS_CODE;
            }

            public void setSTRUCTURE_STATUS_CODE(Integer STRUCTURE_STATUS_CODE) {
                this.STRUCTURE_STATUS_CODE = STRUCTURE_STATUS_CODE;
            }

            public String getSTRUCTURE_STATUS_SHORT_DESC() {
                return STRUCTURE_STATUS_SHORT_DESC;
            }

            public void setSTRUCTURE_STATUS_SHORT_DESC(String STRUCTURE_STATUS_SHORT_DESC) {
                this.STRUCTURE_STATUS_SHORT_DESC = STRUCTURE_STATUS_SHORT_DESC;
            }

            public String getSTRUCTURE_STATUS_LONG_DESC() {
                return STRUCTURE_STATUS_LONG_DESC;
            }

            public void setSTRUCTURE_STATUS_LONG_DESC(String STRUCTURE_STATUS_LONG_DESC) {
                this.STRUCTURE_STATUS_LONG_DESC = STRUCTURE_STATUS_LONG_DESC;
            }

        }

        @JsonProperty("STRUCTURE_NUM")
        private String STRUCTURE_NUM; // Like "A"

        @JsonProperty("STRUCTURE_STATUS")
        private StructureStatus STRUCTURE_STATUS;

        public String getSTRUCTURE_NUM() {
            return STRUCTURE_NUM;
        }

        public void setSTRUCTURE_NUM(String STRUCTURE_NUM) {
            this.STRUCTURE_NUM = STRUCTURE_NUM;
        }

        public StructureStatus getSTRUCTURE_STATUS() {
            return STRUCTURE_STATUS;
        }

        public void setSTRUCTURE_STATUS(StructureStatus STRUCTURE_STATUS) {
            this.STRUCTURE_STATUS = STRUCTURE_STATUS;
        }

    }

    @JsonProperty("HOUSE_GUID")
    private UUID HOUSE_GUID;

    @JsonProperty("HOUSE_ID")
    private Long HOUSE_ID;

    @JsonProperty("HOUSE_NUMBER")
    private String HOUSE_NUMBER;
    /**
     * корпус
     */
    @JsonProperty("BUILDING_NUMBER")
    private String BUILDING_NUMBER;

    @JsonProperty("STRUCTURE")
    private Structure STRUCTURE;

    @JsonProperty("ZIPCODE")
    private Long ZIPCODE;

    public UUID getHOUSE_GUID() {
        return HOUSE_GUID;
    }

    public void setHOUSE_GUID(UUID HOUSE_GUID) {
        this.HOUSE_GUID = HOUSE_GUID;
    }

    public Long getHOUSE_ID() {
        return HOUSE_ID;
    }

    public void setHOUSE_ID(Long HOUSE_ID) {
        this.HOUSE_ID = HOUSE_ID;
    }

    public String getHOUSE_NUMBER() {
        return HOUSE_NUMBER;
    }

    public void setHOUSE_NUMBER(String HOUSE_NUMBER) {
        this.HOUSE_NUMBER = HOUSE_NUMBER;
    }

    public Structure getSTRUCTURE() {
        return STRUCTURE;
    }

    public void setSTRUCTURE(Structure STRUCTURE) {
        this.STRUCTURE = STRUCTURE;
    }

    public Long getZIPCODE() {
        return ZIPCODE;
    }

    public void setZIPCODE(Long ZIPCODE) {
        this.ZIPCODE = ZIPCODE;
    }

    public String getBUILDING_NUMBER() {
        return BUILDING_NUMBER;
    }

    public void setBUILDING_NUMBER(String BUILDING_NUMBER) {
        this.BUILDING_NUMBER = BUILDING_NUMBER;
    }
}
