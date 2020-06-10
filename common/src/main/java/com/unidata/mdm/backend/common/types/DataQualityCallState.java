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

package com.unidata.mdm.backend.common.types;

import java.util.List;

/**
 * @author Alexey Tsarapkin
 */
public class DataQualityCallState {
    /**
     * Model path
     */
    private String path;
    /**
     * DQ port
     */
    private String port;
    /**
     * Port values
     */
    private List<Attribute> value;

    public DataQualityCallState(String path, String port, List<Attribute> value) {
        this.path = path;
        this.port = port;
        this.value = value;
    }

    /**
     * Model path
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * Set model path
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Get DQ port
     * @return
     */
    public String getPort() {
        return port;
    }

    /**
     * Set DQ port
     * @param port
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Get port values
     * @return
     */
    public List<Attribute> getValue() {
        return value;
    }

    /**
     * Set port values
     * @param value
     */
    public void setValue(List<Attribute> value) {
        this.value = value;
    }
}
