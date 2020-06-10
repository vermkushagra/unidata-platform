package com.unidata.mdm.backend.common.dto.statistic.dq;

import java.util.Date;

/**
 * @author Alexey Tsarapkin
 */
public class StatisticInfoDTO {
    private String entityName;
    private String typeName;
    private String dimension1;
    private String dimension2;
    private String dimension3;
    private Date atDate;
    private Long count;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getDimension1() {
        return dimension1;
    }

    public void setDimension1(String dimension1) {
        this.dimension1 = dimension1;
    }

    public String getDimension2() {
        return dimension2;
    }

    public void setDimension2(String dimension2) {
        this.dimension2 = dimension2;
    }

    public String getDimension3() {
        return dimension3;
    }

    public void setDimension3(String dimension3) {
        this.dimension3 = dimension3;
    }

    public Date getAtDate() {
        return atDate;
    }

    public void setAtDate(Date atDate) {
        this.atDate = atDate;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}
