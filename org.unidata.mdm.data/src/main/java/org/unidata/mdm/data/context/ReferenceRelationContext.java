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

package org.unidata.mdm.data.context;

import java.util.List;

import org.unidata.mdm.core.type.timeline.Timeline;
import org.unidata.mdm.data.type.data.OriginRelation;
import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;

/**
 * @author Mikhail Mikhailov on Dec 9, 2019
 */
public interface ReferenceRelationContext extends StorageCapableContext {
    /**
     * Previous state of the references from this relation type and this record..
     */
    StorageId SID_PREVIOUS_REFERENCES = new StorageId("PREVIOUS_REFERENCES");
    /**
     * Next state of the references from this relation type and this record..
     */
    StorageId SID_NEXT_REFERENCES = new StorageId("NEXT_REFERENCES");
    /**
     * Get previous refs state
     * @return state
     */
    default List<Timeline<OriginRelation>> previousReferences() {
        return getFromStorage(SID_PREVIOUS_REFERENCES);
    }
    /**
     * Get previous refs state
     * @return state
     */
    default List<Timeline<OriginRelation>> nextReferences() {
        return getFromStorage(SID_NEXT_REFERENCES);
    }
    /**
     * Put previous state
     * @param timelines the state
     */
    default void previousReferences(List<Timeline<OriginRelation>> timelines) {
        putToStorage(SID_PREVIOUS_REFERENCES, timelines);
    }
    /**
     * Put previous state
     * @param timelines the state
     */
    default void nextReferences(List<Timeline<OriginRelation>> timelines) {
        putToStorage(SID_NEXT_REFERENCES, timelines);
    }
}
