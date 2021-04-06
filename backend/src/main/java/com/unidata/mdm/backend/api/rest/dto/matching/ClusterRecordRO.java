package com.unidata.mdm.backend.api.rest.dto.matching;

import java.util.Date;

public class ClusterRecordRO {
    private String etalonId;
    private Date etalonDate;
    private int matchingRate;

    public String getEtalonId() {
        return etalonId;
    }

    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    public Date getEtalonDate() {
        return etalonDate;
    }

    public void setEtalonDate(Date etalonDate) {
        this.etalonDate = etalonDate;
    }

    public int getMatchingRate() {
        return matchingRate;
    }

    public void setMatchingRate(int matchingRate) {
        this.matchingRate = matchingRate;
    }
}
