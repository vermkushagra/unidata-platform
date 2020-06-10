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

package com.unidata.mdm.backend.common.integration.exits;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry Kopin on 19.09.2017.
 */
public class ExitResult {
    /**
     * status for user exit
     */
    private Status status;
    /**
     * list of warnings
     */
    private List<String> warnings;
    /**
     * 'record was changed' flag
     */
    private boolean wasModified;

    public ExitResult() {
        this.status = Status.SUCCESS;
    }

    public ExitResult(Status status) {
        this.status = status;
    }

    public enum Status {
        SUCCESS, WARNING, ERROR
    }

    public void addWarning(String warning) {
        if (warnings == null) {
            warnings = new ArrayList<>();
        }
        warnings.add(warning);
    }

    public String getWarningMessage(){
        return warnings == null ? "" : warnings.toString();
    }

    public Status getStatus() {
        return status;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public boolean isWasModified() {
        return wasModified;
    }

    public void setWasModified(boolean wasModify) {
        this.wasModified = wasModify;
    }
}
