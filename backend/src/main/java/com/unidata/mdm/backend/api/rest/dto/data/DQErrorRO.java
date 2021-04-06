package com.unidata.mdm.backend.api.rest.dto.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.api.rest.dto.meta.DQApplicableDefinition;

/**
 * The Class DQErrorRO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQErrorRO {

    /** The severity. */
    private String severity;

    /** The category. */
    private String category;

    /** The message. */
    private String message;

    /** The rule name. */
    private String ruleName;
    private DQApplicableDefinition phase;

    /**
     * Gets the severity.
     *
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the severity.
     *
     * @param severity
     *            the severity to set
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Gets the category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category.
     *
     * @param category
     *            the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     *
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the rule name.
     *
     * @return the ruleName
     */
    public String getRuleName() {
        return ruleName;
    }

    /**
     * Sets the rule name.
     *
     * @param ruleName
     *            the ruleName to set
     */
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    /**
     * @return the phase
     */
    public DQApplicableDefinition getPhase() {
        return phase;
    }

    /**
     * @param phase
     *            the phase to set
     */
    public void setPhase(DQApplicableDefinition phase) {
        this.phase = phase;
    }

}
