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

package com.unidata.mdm.backend.po;

import com.unidata.mdm.backend.service.model.ModelType;

/**
 * @author Michael Yashin. Created on 26.05.2015.
 */
public class MetaModelPO extends AbstractPO {
    /**
     * Table name.
     */
    public static final String TABLE_NAME = "meta_model";
    /**
     * ID.
     */
    public static final String FIELD_ID = "id";
    /**
     * Storage Id.
     */
    public static final String FIELD_STORAGE_ID = "storage_fkey";
    /**
     * Name.
     */
    public static final String FIELD_TYPE = "type";
    /**
     * Version.
     */
    public static final String FIELD_VERSION = "version";
    /**
     * Data.
     */
    public static final String FIELD_DATA = "data";

    /**
     * ID.
     */
    protected String id;

    /**
     * Storage id.
     */
    protected String storageId;
    /**
     * Name.
     */
    protected ModelType type;
    /**
     * Version.
     */
    protected long version;
    /**
     * XML data.
     */
    protected String data;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStorageId() {
        return storageId;
    }

    public void setStorageId(String storageId) {
        this.storageId = storageId;
    }

    public ModelType getType() {
        return type;
    }

    public void setType(ModelType type) {
        this.type = type;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
