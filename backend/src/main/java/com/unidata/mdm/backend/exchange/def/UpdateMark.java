package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.unidata.mdm.backend.exchange.def.csv.CsvUpdateMark;
import com.unidata.mdm.backend.exchange.def.db.DbUpdateMark;

/**
 * @author Mikhail Mikhailov
 * Update mark.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = CsvUpdateMark.class, name = "CSV"),
    @Type(value = DbUpdateMark.class, name = "DB")
})
public class UpdateMark implements Serializable {
    /**
     * Type of the update mark type.
     */
    private UpdateMarkType updateMarkType;
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -3632123578573270563L;
    /**
     * @return the updateMarkType
     */
    public UpdateMarkType getUpdateMarkType() {
        return updateMarkType;
    }
    /**
     * @param updateMarkType the updateMarkType to set
     */
    public void setUpdateMarkType(UpdateMarkType updateMarkType) {
        this.updateMarkType = updateMarkType;
    }

}
