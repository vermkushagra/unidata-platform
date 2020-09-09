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

package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.meta.DQRSourceSystemRef;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yashin. Created on 11.06.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQROriginsDefinition {

    protected List<String> sourceSystems = new ArrayList<>();
    protected boolean all;

    public List<String> getSourceSystems() {
        return sourceSystems;
    }

    public void setSourceSystems(List<String> sourceSystems) {
        this.sourceSystems = sourceSystems;
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }
}
