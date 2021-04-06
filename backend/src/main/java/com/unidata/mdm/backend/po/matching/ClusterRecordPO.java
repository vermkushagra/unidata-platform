package com.unidata.mdm.backend.po.matching;

import java.util.Date;

public class ClusterRecordPO {
    private Long clusterId;
    private String etalonId;
    private Date etalonDate;
    private int matchingRate;

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

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
