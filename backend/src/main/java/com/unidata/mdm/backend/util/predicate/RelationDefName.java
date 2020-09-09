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

package com.unidata.mdm.backend.util.predicate;

import java.util.function.Predicate;

import com.unidata.mdm.meta.RelationDef;

/**
 * @author Michael Yashin. Created on 28.05.2015.
 */
public class RelationDefName implements Predicate<RelationDef> {
    private String name;

    public RelationDefName(String name) {
        this.name = name;
    }

    @Override
    public boolean test(RelationDef relationDef) {
        return name.equals(relationDef.getName());
    }

    @Override
    public Predicate<RelationDef> and(Predicate<? super RelationDef> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<RelationDef> negate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<RelationDef> or(Predicate<? super RelationDef> other) {
        throw new UnsupportedOperationException();
    }
}
