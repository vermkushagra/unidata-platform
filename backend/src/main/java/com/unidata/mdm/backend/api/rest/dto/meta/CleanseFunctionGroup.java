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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yashin. Created on 20.05.2015.
 */
public class CleanseFunctionGroup extends CleanseFunctionTreeElement{
    protected List<CleanseFunction> functions = new ArrayList<>();
    protected List<CleanseFunctionGroup> groups = new ArrayList<>();

    public List<CleanseFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<CleanseFunction> functions) {
        this.functions = functions;
    }

    public List<CleanseFunctionGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CleanseFunctionGroup> groups) {
        this.groups = groups;
    }
}
