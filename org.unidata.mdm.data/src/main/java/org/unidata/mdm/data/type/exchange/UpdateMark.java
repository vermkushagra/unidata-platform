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

package org.unidata.mdm.data.type.exchange;

import java.io.Serializable;

import org.unidata.mdm.data.type.exchange.csv.CsvUpdateMark;
import org.unidata.mdm.data.type.exchange.db.DbUpdateMark;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author Mikhail Mikhailov
 * Update mark.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonSubTypes({
    @Type(value = CsvUpdateMark.class, name = "CSV"),
    @Type(value = DbUpdateMark.class, name = "DB")
})
public class UpdateMark implements Serializable {
    /**
     * Type of the update mark type.
     */
    private UpdateMarkType updateMarkType;
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -3632123578573270563L;
    /**
     * @return the updateMarkType
     */
    public UpdateMarkType getUpdateMarkType() {
        return updateMarkType;
    }
    /**
     * @param updateMarkType the updateMarkType to set
     */
    public void setUpdateMarkType(UpdateMarkType updateMarkType) {
        this.updateMarkType = updateMarkType;
    }

}
