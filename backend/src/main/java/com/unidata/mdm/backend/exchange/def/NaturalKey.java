/**
 *
 */
package com.unidata.mdm.backend.exchange.def;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.unidata.mdm.backend.exchange.def.csv.CsvNaturalKey;
import com.unidata.mdm.backend.exchange.def.db.DbNaturalKey;

/**
 * @author Mikhail Mikhailov
 *
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = CsvNaturalKey.class, name = "CSV"),
    @Type(value = DbNaturalKey.class, name = "DB")
})
public class NaturalKey implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4314450826469038456L;

    /**
     * Constructor.
     */
    public NaturalKey() {
        super();
    }
}
