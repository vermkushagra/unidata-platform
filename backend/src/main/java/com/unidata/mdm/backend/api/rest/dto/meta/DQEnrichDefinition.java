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
