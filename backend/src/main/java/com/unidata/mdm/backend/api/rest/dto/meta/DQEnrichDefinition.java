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

package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The Class DQEnrichDefinition.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQEnrichDefinition {

/** The source system. */
private String sourceSystem;

/** The action. */
private DQActionDefinition action;

/** The phase. */
private PhaseDefinition phase;

/**
 * Gets the source system.
 *
 * @return the source system
 */
public String getSourceSystem() {
    return sourceSystem;
}

/**
 * Sets the source system.
 *
 * @param sourceSystem the new source system
 */
public void setSourceSystem(String sourceSystem) {
    this.sourceSystem = sourceSystem;
}

/**
 * Gets the action.
 *
 * @return the action
 */
public DQActionDefinition getAction() {
    return action;
}

/**
 * Sets the action.
 *
 * @param action the new action
 */
public void setAction(DQActionDefinition action) {
    this.action = action;
}

/**
 * Gets the phase.
 *
 * @return the phase
 */
public PhaseDefinition getPhase() {
    return phase;
}

/**
 * Sets the phase.
 *
 * @param phase the new phase
 */
public void setPhase(PhaseDefinition phase) {
    this.phase = phase;
}
}
