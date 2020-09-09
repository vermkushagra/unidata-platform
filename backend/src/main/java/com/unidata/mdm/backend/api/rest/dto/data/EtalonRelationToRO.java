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

package com.unidata.mdm.backend.api.rest.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Mikhail Mikhailov
 * Etalon relation REST definition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtalonRelationToRO extends AbstractRelationToRO {
    /**
     * Etalon id of the RELATION.
     */
    private String etalonId;
    /**
     * Etalon id of the TO side.
     */
    private String etalonIdTo;
    /**
     * Etalon display name for 'To side'.
     */
    private String etalonDisplayNameTo;

    /**
     * Constructor.
     */
    public EtalonRelationToRO() {
        super();
    }

    /**
     * @return the etalonId
     */
    public String getEtalonId() {
        return etalonId;
    }

    /**
     * @param etalonId the etalonId to set
     */
    public void setEtalonId(String etalonId) {
        this.etalonId = etalonId;
    }

    /**
     * @return the etalonIdTo
     */
    public String getEtalonIdTo() {
        return etalonIdTo;
    }

    /**
     * @param etalonIdTo the etalonIdTo to set
     */
    public void setEtalonIdTo(String etalonIdTo) {
        this.etalonIdTo = etalonIdTo;
    }


    public String getEtalonDisplayNameTo() {
        return etalonDisplayNameTo;
    }

    public void setEtalonDisplayNameTo(String etalonDisplayNameTo) {
        this.etalonDisplayNameTo = etalonDisplayNameTo;
    }
}
