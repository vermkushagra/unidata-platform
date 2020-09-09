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

package com.unidata.mdm.backend.api.rest.dto.security;


/**
 * @author Mikhail Mikhailov
 * Resource specific, justified by security level resource access rights.
 */
public class ResourceSpecificRightRO extends RightRO {

    /** Virtual "restore" right */
    private boolean restore;

    /** Virtual "merge" right */
    private boolean merge;

    /**
     * Constructor.
     */
    public ResourceSpecificRightRO() {
        super();
    }

    /**
     * @return the restore
     */
    public boolean isRestore() {
        return restore;
    }

    /**
     * @param restore the restore to set
     */
    public void setRestore(boolean restore) {
        this.restore = restore;
    }


    /**
     * @return the merge
     */
    public boolean isMerge() {
        return merge;
    }


    /**
     * @param merge the merge to set
     */
    public void setMerge(boolean merge) {
        this.merge = merge;
    }
}
