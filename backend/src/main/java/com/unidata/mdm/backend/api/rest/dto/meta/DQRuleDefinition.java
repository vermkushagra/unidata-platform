package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Michael Yashin. Created on 11.06.2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DQRuleDefinition {

    protected String id;
    protected String name;
    protected String cleanseFunctionName;
    protected String complexAttributeName;
    protected String description;
    protected int order;
    List<DQRMappingDefinition> inputs = new ArrayList<DQRMappingDefinition>();
    List<DQRMappingDefinition> outputs = new ArrayList<DQRMappingDefinition>();
    List<DQApplicableDefinition> applicable = new ArrayList<DQApplicableDefinition>();
    protected DQROriginsDefinition origins;
    protected boolean isValidation;
    protected boolean isEnrichment;
    protected DQRRaiseDefinition raise;
    protected DQEnrichDefinition enrich;
    protected List<DQTypeDefinition> dqType = new ArrayList<DQTypeDefinition>();
    protected boolean special;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCleanseFunctionName() {
        return cleanseFunctionName;
    }

    public void setCleanseFunctionName(String cleanseFunctionName) {
        this.cleanseFunctionName = cleanseFunctionName;
    }

    public String getComplexAttributeName() {
        return complexAttributeName;
    }

    public void setComplexAttributeName(String complexAttributeName) {
        this.complexAttributeName = complexAttributeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public DQROriginsDefinition getOrigins() {
        return origins;
    }

    public void setOrigins(DQROriginsDefinition origins) {
        this.origins = origins;
    }

    public DQRRaiseDefinition getRaise() {
        return raise;
    }

    public void setRaise(DQRRaiseDefinition raise) {
        this.raise = raise;
    }

    /**
     * @return the inputs
     */
    public List<DQRMappingDefinition> getInputs() {
        return inputs;
    }

    /**
     * @param inputs
     *            the inputs to set
     */
    public void setInputs(List<DQRMappingDefinition> inputs) {
        this.inputs = inputs;
    }

    /**
     * @return the outputs
     */
    public List<DQRMappingDefinition> getOutputs() {
        return outputs;
    }

    /**
     * @param outputs
     *            the outputs to set
     */
    public void setOutputs(List<DQRMappingDefinition> outputs) {
        this.outputs = outputs;
    }

    /**
	 * @return the applicable
	 */
	public List<DQApplicableDefinition> getApplicable() {
		return applicable;
	}

	/**
	 * @param applicable the applicable to set
	 */
	public void setApplicable(List<DQApplicableDefinition> applicable) {
		this.applicable = applicable;
	}

	public DQEnrichDefinition getEnrich() {
        return enrich;
    }

    public void setEnrich(DQEnrichDefinition enrich) {
        this.enrich = enrich;
    }

    public List<DQTypeDefinition> getDqType() {
        return dqType;
    }

    public void setDqType(List<DQTypeDefinition> dqType) {
        this.dqType = dqType;
    }

    /**
     * @return the special
     */
    public boolean isSpecial() {
        return special;
    }

    /**
     * @param special the special to set
     */
    public void setSpecial(boolean special) {
        this.special = special;
    }

    public boolean isIsValidation() {
        return raise!=null;
    }



    public boolean isIsEnrichment() {
        return enrich!=null;
    }


}
