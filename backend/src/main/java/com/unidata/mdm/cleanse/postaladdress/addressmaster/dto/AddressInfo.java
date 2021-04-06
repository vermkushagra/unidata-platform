package com.unidata.mdm.cleanse.postaladdress.addressmaster.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represent address part like District, Region, City, Street...
 *
 * @author Pavel Alexeev.
 * @created 2016-01-19 11:26.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressInfo {
    private UUID aoGuid;
    private Long aoId;
    private String elementTypeName;
    private String allPostalCodes;
    private String formalName;
    private Integer level;
    private String zipCode;
    private Boolean finite;

    public UUID getAoGuid() {
        return aoGuid;
    }

    public void setAoGuid(UUID aoGuid) {
        this.aoGuid = aoGuid;
    }

    public Long getAoId() {
        return aoId;
    }

    public void setAoId(Long aoId) {
        this.aoId = aoId;
    }

    public String getElementTypeName() {
        return elementTypeName;
    }

    public void setElementTypeName(String elementTypeName) {
        this.elementTypeName = elementTypeName;
    }

    public String getFormalName() {
        return formalName;
    }

    public void setFormalName(String formalName) {
        this.formalName = formalName;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getAllPostalCodes() {
        return allPostalCodes;
    }

    public void setAllPostalCodes(String allPostalCodes) {
        this.allPostalCodes = allPostalCodes;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Boolean getFinite() {
        return finite;
    }

    public void setFinite(Boolean finite) {
        this.finite = finite;
    }
}
