package org.unidata.mdm.data.type.exchange;

import java.io.Serializable;

import org.unidata.mdm.data.type.exchange.csv.CsvUpdateMark;
import org.unidata.mdm.data.type.exchange.db.DbUpdateMark;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
