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

package com.unidata.mdm.backend.api.rest.dto;

import java.util.ArrayList;
import java.util.List;


/**
 * This response should be returned to frontend in case of update. This is
 * needed because sencha doesn't recognize empty responses.
 */
public class UpdateResponse {

    /**  Is successful?. */
    private boolean success;

    /** Updated record id. */
    private String id;
    
    /** List with errors. */
    private List<ErrorInfo> errors;
    
    /** List with additional parameters. */
    private List<Param> params;

    /**
     * Instantiates a new update response.
     *
     * @param success
     *            is update successful?
     * @param id
     *            updated record id.
     */
    public UpdateResponse(boolean success, String id) {
        this.success = success;
        this.id = id;
    }
    /**
     * Instantiates a new update response.
     *
     * @param success
     *            is update successful?
     * @param id
     *            updated record id.
     * @param params 
     *            list with additional parameters.
     */
    public UpdateResponse(boolean success, String id, List<Param> params) {
        this.success = success;
        this.id = id;
        this.params = params;
    }
    /**
     * Checks if is success.
     *
     * @return true, if is success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Error info.
     * 
     * @return list with errors.
     */
    public List<ErrorInfo> getErrors() {
        return errors;
    }

    /**
     * Set errors.
     * 
     * @param errors
     *            list with errors.
     */
    public void setErrors(List<ErrorInfo> errors) {
        this.errors = errors;
    }

    /**
     * Gets the params.
     *
     * @return the params
     */
    public List<Param> getParams() {
        if (this.params == null) {
            this.params = new ArrayList<Param>();
        }
        return params;
    }

    /**
     * Sets the params.
     *
     * @param params the new params
     */
    public void setParams(List<Param> params) {
        this.params = params;
    }

    /**
     * Adds the param.
     *
     * @param param the param
     */
    public void addParam(Param param) {
        getParams().add(param);
    }

}
