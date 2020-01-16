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

import org.unidata.mdm.core.type.data.impl.SimpleAttributesDiff;
import org.unidata.mdm.system.context.StorageCapableContext;
import org.unidata.mdm.system.context.StorageId;

/**
 * Simple diff to prev, diff to draft context.
 * @author Mikhail Mikhailov on Nov 1, 2019
 */
public interface SimpleAttributesDiffContext extends StorageCapableContext {
    /**
     * Diff to draft.
     */
    StorageId SID_DIFF_TO_DRAFT = new StorageId("DIFF_TO_DRAFT");
    /**
     * Diff to previous.
     */
    StorageId SID_DIFF_TO_PREVIOUS = new StorageId("DIFF_TO_PREVIOUS");
    /**
     * Gets diff to draft.
     * @return diff to draft
     */
    default SimpleAttributesDiff diffToDraft() {
        return getFromStorage(SID_DIFF_TO_DRAFT);
    }
    /**
     * Sets diff to draft.
     * @param diff to draft
     */
    default void diffToDraft(SimpleAttributesDiff diff) {
        putToStorage(SID_DIFF_TO_DRAFT, diff);
    }
    /**
     * Gets diff to previous.
     * @return diff to previous
     */
    default SimpleAttributesDiff diffToPrevious() {
        return getFromStorage(SID_DIFF_TO_PREVIOUS);
    }
    /**
     * Sets diff to previous.
     * @param diff to previous
     */
    default void diffToPrevious(SimpleAttributesDiff diff) {
        putToStorage(SID_DIFF_TO_PREVIOUS, diff);
    }
}
