package com.unidata.mdm.backend.exchange.def.csv;

import com.unidata.mdm.backend.exchange.def.RelatesToRelation;


/**
 * @author Mikhail Mikhailov
 *
 */
public class CsvRelatesToRelation extends RelatesToRelation {

    /**
     * SVUID.
     */
    private static final long serialVersionUID = 8019597268020773086L;
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
     * Constructor.
     */
    public CsvRelatesToRelation() {
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
