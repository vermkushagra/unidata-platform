package com.unidata.mdm.backend.api.rest.dto.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 11.06.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQRRaiseDefinition {
    protected PhaseDefinition phase;
    protected String functionRaiseErrorPort;

    protected String messagePort;

    protected String messageText;

    protected String severityPort;

    protected String severityValue;

    protected String categoryPort;

    protected String categoryText;

    public String getFunctionRaiseErrorPort() {
        return functionRaiseErrorPort;
    }

    public void setFunctionRaiseErrorPort(String functionRaiseErrorPort) {
        this.functionRaiseErrorPort = functionRaiseErrorPort;
    }

    public String getMessagePort() {
        return messagePort;
    }

    public void setMessagePort(String messagePort) {
        this.messagePort = messagePort;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSeverityPort() {
        return severityPort;
    }

    public void setSeverityPort(String severityPort) {
        this.severityPort = severityPort;
    }

    public String getSeverityValue() {
        return severityValue;
    }

    public void setSeverityValue(String severityValue) {
        this.severityValue = severityValue;
    }

    public String getCategoryPort() {
        return categoryPort;
    }

    public void setCategoryPort(String categoryPort) {
        this.categoryPort = categoryPort;
    }

    public String getCategoryText() {
        return categoryText;
    }

    public void setCategoryText(String categoryText) {
        this.categoryText = categoryText;
    }

    public PhaseDefinition getPhase() {
        return phase;
    }

    public void setPhase(PhaseDefinition phase) {
        this.phase = phase;
    }
}
