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

import javax.annotation.Nonnull;

/**
 * Unique key help identify classifier in system, as a classifier
 */
public class ClassifierRegistryKey implements UniqueRegistryKey {

    /**
     * Classifier name
     */
    @Nonnull
    private final String classifierName;

    /**
     * Constructor
     *
     * @param classifierName - classifier name
     */
    public ClassifierRegistryKey(@Nonnull String classifierName) {
        this.classifierName = classifierName;
    }

    /**
     * @return classifier name
     */
    @Nonnull
    public String getClassifierName() {
        return classifierName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassifierRegistryKey)) return false;

        ClassifierRegistryKey that = (ClassifierRegistryKey) o;

        return classifierName.equals(that.classifierName);
    }

    @Override
    public int hashCode() {
        return classifierName.hashCode();
    }

    /**
     * @return type of key
     */
    @Override
    public Type keyType() {
        return Type.CLASSIFIER;
    }

    @Override
    public String toString() {
        return "{" +
                "classifierName='" + classifierName + '\'' +
                '}';
    }
}
