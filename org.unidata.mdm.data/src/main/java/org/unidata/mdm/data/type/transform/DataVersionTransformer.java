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

package org.unidata.mdm.data.type.transform;

import org.unidata.mdm.data.type.data.OriginRecord;

/**
 * @author Mikhail Mikhailov
 * Interface for stale data transformations.
 */
public abstract class DataVersionTransformer {
    /**
     * This member's major.
     */
    private int major;
    /**
     * This member's minor.
     */
    private int minor;
    /**
     * Next member.
     */
    private DataVersionTransformer next;
    /**
     * Constructor.
     * @param major the major
     * @param minor the minor
     */
    public DataVersionTransformer(int major, int minor) {
        super();
        this.major = major;
        this.minor = minor;
    }
    /**
     * Transform re cord if needed.
     * @param record the record to transform
     */
    public void transform(OriginRecord record) {

        boolean apply = false;
        if (record.getInfoSection().getMajor() < this.major
        || (record.getInfoSection().getMajor() == this.major && record.getInfoSection().getMinor() < this.minor)) {
            apply = true;
        }

        if (apply) {
            apply(record);
        }

        if (next != null) {
            next.transform(record);
        }
    }
    /**
     * @param next the next to set
     */
    public void setNext(DataVersionTransformer next) {
        this.next = next;
    }
    /**
     * Transorm record if necessary and give it to the chain.
     * @param record the record to transform
     */
    public abstract void apply(OriginRecord record);
}
