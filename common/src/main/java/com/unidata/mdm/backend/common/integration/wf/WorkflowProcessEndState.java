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

package com.unidata.mdm.backend.common.integration.wf;

/**
 * @author Mikhail Mikhailov
 * Process completion state.
 */
public class WorkflowProcessEndState {

    /**
     * Tells the caller, whether to submit the changes (true), or discard (false).
     */
    private boolean complete;
    /**
     * Describe denial or acceptance reason.
     */
    private String message;
    /**
     * Constructor.
     */
    public WorkflowProcessEndState() {
        super();
    }
    /**
     * Constructor.
     * @param complete signal 'save changes' state
     * @param message optional end message
     */
    public WorkflowProcessEndState(boolean complete, String message) {
        super();
        this.complete = complete;
        this.message = message;
    }
    /**
     * @return the complete
     */
    public boolean isComplete() {
        return complete;
    }
    /**
     * @param complete the complete to set
     */
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
