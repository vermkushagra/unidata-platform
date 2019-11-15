package org.unidata.mdm.data.type.exchange.csv;

import org.unidata.mdm.data.type.exchange.ExchangeField;

/**
 * @author Mikhail Mikhailov
 * Import parts, specific to CSV format.
 */
public class CsvExchangeField extends ExchangeField {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = -7482226123273085242L;
    /**
     * Column index.
     */
    private Integer index;

    /**
     * @return the index
     */
    public Integer getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(Integer index) {
        this.index = index;
    }

    /**
     * Constructor.
     */
    public CsvExchangeField() {
        super();
    }

}
