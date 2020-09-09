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

package com.unidata.mdm.backend.service.registration.keys;

import java.io.Serializable;

/**
 * Unique over system key, one of linked elements of system
 */
public interface UniqueRegistryKey extends Serializable {

    /**
     * @return type of unique key
     */
    Type keyType();

    /**
     * Types of uniquer registry keys in system
     */
    //todo replace russian text to keys from message source
    enum Type {
        ENTITY("реестр"),
        LOOKUP_ENTITY("справочник"),
        RELATION("связь"),
        CLASSIFIER("класификатор"),
        ATTRIBUTE("атрибут"),
        DQ("правило качества"),
        MATCHING_RULE("правило сопоставления"),
        MATCHING_GROUP("группа правил сопоставления"),
        MEASUREMENT_VALUE("единицы измерения");

        private String description;

        Type(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
