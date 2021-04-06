package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.unidata.mdm.backend.exchange.def.csv.CsvSystemKey;
import com.unidata.mdm.backend.exchange.def.db.DbSystemKey;

/**
 * @author Mikhail Mikhailov
 * Unidata system key (basically UD etalon id).
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = CsvSystemKey.class, name = "CSV"),
    @Type(value = DbSystemKey.class, name = "DB")
})
public class SystemKey implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 3989688801077729874L;

    /**
     * Constructor.
     */
    public SystemKey() {
        super();
    }
}
