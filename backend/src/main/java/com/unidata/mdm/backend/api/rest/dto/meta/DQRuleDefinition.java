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

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.unidata.mdm.backend.common.dto.CustomPropertyDefinition;

/**
 *
 * @author Michael Yashin. Created on 11.06.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQRuleDefinition {

    // UN-7293
    // protected String id;
    /** The name. */
    // protected String complexAttributeName;
    protected String name;
    
    /** The cleanse function name. */
    protected String cleanseFunctionName;

    /** The description. */
    protected String description;
    
    /** The order. */
    protected int order;
    
    /** The inputs. */
    List<DQRMappingDefinition> inputs = new ArrayList<>();
    
    /** The outputs. */
    List<DQRMappingDefinition> outputs = new ArrayList<>();
    
    /** The applicable. */
    List<DQApplicableDefinition> applicable = new ArrayList<>();
    
    /** The origins. */
    protected DQROriginsDefinition origins;
    
    /** The is validation. */
    protected boolean isValidation;
    
    /** The is enrichment. */
    protected boolean isEnrichment;
    
    /** The raise. */
    protected DQRRaiseDefinition raise;
    
    /** The enrich. */
    protected DQEnrichDefinition enrich;
    
    /** The dq type. */
    protected List<DQTypeDefinition> dqType = new ArrayList<>();
    
    /** The special. */
    protected boolean special;
    
    /** The run type. */
    private DQRuleRunType runType;
    
    /** The execution context path. */
    private String executionContextPath;
    
    /** The execution context. */
    private DQRRuleExecutionContext executionContext;
    
    /** The custom properties. */
    private List<CustomPropertyDefinition> customProperties;
    // UN-7293
    /**
     * Gets the name.
     *
     * @return the name
     */
    /*
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComplexAttributeName() {
        return complexAttributeName;
    }

    public void setComplexAttributeName(String complexAttributeName) {
        this.complexAttributeName = complexAttributeName;
    }
    */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the cleanse function name.
     *
     * @return the cleanse function name
     */
    public String getCleanseFunctionName() {
        return cleanseFunctionName;
    }

    /**
     * Sets the cleanse function name.
     *
     * @param cleanseFunctionName the new cleanse function name
     */
    public void setCleanseFunctionName(String cleanseFunctionName) {
        this.cleanseFunctionName = cleanseFunctionName;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the order.
     *
     * @return the order
     */
    public int getOrder() {
        return order;
    }

    /**
     * Sets the order.
     *
     * @param order the new order
     */
    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * Gets the origins.
     *
     * @return the origins
     */
    public DQROriginsDefinition getOrigins() {
        return origins;
    }

    /**
     * Sets the origins.
     *
     * @param origins the new origins
     */
    public void setOrigins(DQROriginsDefinition origins) {
        this.origins = origins;
    }

    /**
     * Gets the raise.
     *
     * @return the raise
     */
    public DQRRaiseDefinition getRaise() {
        return raise;
    }

    /**
     * Sets the raise.
     *
     * @param raise the new raise
     */
    public void setRaise(DQRRaiseDefinition raise) {
        this.raise = raise;
    }

    /**
     * Gets the inputs.
     *
     * @return the inputs
     */
    public List<DQRMappingDefinition> getInputs() {
        return inputs;
    }

    /**
     * Sets the inputs.
     *
     * @param inputs            the inputs to set
     */
    public void setInputs(List<DQRMappingDefinition> inputs) {
        this.inputs = inputs;
    }

    /**
     * Gets the outputs.
     *
     * @return the outputs
     */
    public List<DQRMappingDefinition> getOutputs() {
        return outputs;
    }

    /**
     * Sets the outputs.
     *
     * @param outputs            the outputs to set
     */
    public void setOutputs(List<DQRMappingDefinition> outputs) {
        this.outputs = outputs;
    }

    /**
     * Gets the applicable.
     *
     * @return the applicable
     */
	public List<DQApplicableDefinition> getApplicable() {
		return applicable;
	}

	/**
	 * Sets the applicable.
	 *
	 * @param applicable the applicable to set
	 */
	public void setApplicable(List<DQApplicableDefinition> applicable) {
		this.applicable = applicable;
	}

	/**
	 * Gets the enrich.
	 *
	 * @return the enrich
	 */
	public DQEnrichDefinition getEnrich() {
        return enrich;
    }

    /**
     * Sets the enrich.
     *
     * @param enrich the new enrich
     */
    public void setEnrich(DQEnrichDefinition enrich) {
        this.enrich = enrich;
    }

    /**
     * Gets the dq type.
     *
     * @return the dq type
     */
    public List<DQTypeDefinition> getDqType() {
        return dqType;
    }

    /**
     * Sets the dq type.
     *
     * @param dqType the new dq type
     */
    public void setDqType(List<DQTypeDefinition> dqType) {
        this.dqType = dqType;
    }

    /**
     * Checks if is special.
     *
     * @return the special
     */
    public boolean isSpecial() {
        return special;
    }

    /**
     * Sets the special.
     *
     * @param special the special to set
     */
    public void setSpecial(boolean special) {
        this.special = special;
    }

    /**
     * Gets the run type.
     *
     * @return the runType
     */
    public DQRuleRunType getRunType() {
        return runType;
    }

    /**
     * Sets the run type.
     *
     * @param runType the runType to set
     */
    public void setRunType(DQRuleRunType runType) {
        this.runType = runType;
    }

    /**
     * Checks if is checks if is validation.
     *
     * @return true, if is checks if is validation
     */
    public boolean isIsValidation() {
        return raise!=null;
    }



    /**
     * Checks if is checks if is enrichment.
     *
     * @return true, if is checks if is enrichment
     */
    public boolean isIsEnrichment() {
        return enrich!=null;
    }

    /**
     * Gets the execution context path.
     *
     * @return the entryPoint
     */
    public String getExecutionContextPath() {
        return executionContextPath;
    }

    /**
     * Sets the execution context path.
     *
     * @param entryPoint the entryPoint to set
     */
    public void setExecutionContextPath(String entryPoint) {
        this.executionContextPath = entryPoint;
    }

    /**
     * Gets the execution context.
     *
     * @return the executionContext
     */
    public DQRRuleExecutionContext getExecutionContext() {
        return executionContext;
    }

    /**
     * Sets the execution context.
     *
     * @param executionContext the executionContext to set
     */
    public void setExecutionContext(DQRRuleExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

	/**
	 * Gets the custom properties.
	 *
	 * @return the custom properties
	 */
	public List<CustomPropertyDefinition> getCustomProperties() {
		return customProperties;
	}

	/**
	 * Sets the custom properties.
	 *
	 * @param customProperties the new custom properties
	 */
	public void setCustomProperties(List<CustomPropertyDefinition> customProperties) {
		this.customProperties = customProperties;
	}


}
