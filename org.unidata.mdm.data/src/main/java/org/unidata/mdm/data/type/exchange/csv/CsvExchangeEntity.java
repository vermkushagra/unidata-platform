package org.unidata.mdm.data.type.exchange.csv;

import org.unidata.mdm.data.type.exchange.ExchangeEntity;

/**
 * @author Mikhail Mikhailov
 * Fields, specific to CSV import.
 */
public class CsvExchangeEntity extends ExchangeEntity {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 2426723000991644046L;
    /**
     * File system resource.
     */
    private String resource;
    /**
     * Field separator.
     */
    private String separator;
    /**
     * Resource' charset (UTF-8 if not given).
     */
    private String charset;
    /**
     * Ctor.
     */
    public CsvExchangeEntity() {
        super();
    }


    /**
     * @return the resource
     */
    public String getResource() {
        return resource;
    }


    /**
     * @param resource the resource to set
     */
    public void setResource(String resource) {
        this.resource = resource;
    }



    /**
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }



    /**
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }



    /**
     * @return the charset
     */
    public String getCharset() {
        return charset;
    }



    /**
     * @param charset the charset to set
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }

}
