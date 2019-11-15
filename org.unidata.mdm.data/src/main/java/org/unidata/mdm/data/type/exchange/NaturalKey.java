/**
 *
 */
package org.unidata.mdm.data.type.exchange;

import java.io.Serializable;

import org.unidata.mdm.data.type.exchange.csv.CsvNaturalKey;
import org.unidata.mdm.data.type.exchange.db.DbNaturalKey;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
