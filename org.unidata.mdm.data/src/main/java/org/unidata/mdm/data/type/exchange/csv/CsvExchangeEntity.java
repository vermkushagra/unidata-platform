/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 * 
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

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
