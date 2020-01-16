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

package org.unidata.mdm.core.type.data.impl;

import org.unidata.mdm.core.type.data.LargeValue;

/**
 * @author Mikhail Mikhailov
 * Binary large value holder object.
 */
public abstract class AbstractLargeValue implements LargeValue {

    /**
     * Data.
     */
    protected byte[] data;
    /**
     * Record id.
     */
    protected String id;
    /**
     * File name
     */
    protected String fileName;
    /**
     * MIME type.
     */
    protected String mimeType;
    /**
     * Size.
     */
    protected long size;

    /**
     * Constructor.
     */
    protected AbstractLargeValue() {
        super();
    }

    /**
     * Gets the value of the data property.
     *
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMimeType() {
        return mimeType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getSize() {
        return size;
    }

    /**
     * Sets the value of the data property.
     *
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setData(byte[] value) {
        this.data = value;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Sets the value of the fileName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Sets the value of the mimeType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Sets the value of the size property.
     *
     * @param value
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setSize(Long value) {
        this.size = value;
    }
}
