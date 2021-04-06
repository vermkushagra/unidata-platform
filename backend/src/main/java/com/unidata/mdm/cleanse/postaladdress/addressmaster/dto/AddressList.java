package com.unidata.mdm.cleanse.postaladdress.addressmaster.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Top-level answer on address parsing and cleaning for service http://addressmaster.ru/index.html
 *
 * @author Pavel Alexeev.
 * @created 2016-01-19 12:07.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressList {

    @JsonProperty("DEBUG_INFO")
    private String DEBUG_INFO;

    private List<Address> addressList;

    public String getDEBUG_INFO() {
        return DEBUG_INFO;
    }

    public void setDEBUG_INFO(String DEBUG_INFO) {
        this.DEBUG_INFO = DEBUG_INFO;
    }

    public List<Address> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
    }
}
