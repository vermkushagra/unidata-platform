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

package com.unidata.mdm.backend.notification.events;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Mikhail Mikhailov
 * Classifiers meta model changes.
 */
public class ClassifierChangeEvent implements Serializable {
    /**
     * @author Mikhail Mikhailov
     * Event type.
     */
    public enum ClassifierChangesEventType {
        REMOVE,
        REFRESH
    }
    /**
     * SVUID.
     */
    private static final long serialVersionUID = 4647186931885581818L;
    /**
     * The event type.
     */
    private final ClassifierChangesEventType eventType;
    /**
     * Classifier names to process.
     */
    private final Collection<String> classifierNames;
    /**
     * Constructor.
     * @param eventType the event type
     * @param classifierNames the names to process for that type
     */
    public ClassifierChangeEvent(ClassifierChangesEventType eventType, Collection<String> classifierNames) {
        super();
        this.eventType = eventType;
        this.classifierNames = classifierNames;
    }
    /**
     * @return the eventType
     */
    public ClassifierChangesEventType getEventType() {
        return eventType;
    }
    /**
     * @return the classifierNames
     */
    public Collection<String> getClassifierNames() {
        return classifierNames;
    }
}
