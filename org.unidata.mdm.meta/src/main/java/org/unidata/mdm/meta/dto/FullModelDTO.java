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

package org.unidata.mdm.meta.dto;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.unidata.mdm.meta.MeasurementValues;
import org.unidata.mdm.meta.Model;

/**
 * The Class FullModelDTO.
 * @author ilya.bykov
 */
public class FullModelDTO {

    /** The override. */
    private boolean override;
    /** The model. */
    private Model model;
    private String storageId;

    // TODO: @Modules
//    /** The matching user settings. */
//    private MatchingUserSettings matchingSettings;

    /** The measurement values. */
    private MeasurementValues measurementValues;

    // TODO: @Modules
//    /** The cleanse functions. */
//    private CleanseFunctionGroupDef cleanseFunctions;

    private Map<Pair<String, String>, ByteBuffer> customCfs;

    // TODO: @Modules
//    /** The clsfs. */
//    private List<ClsfDTO> clsfs;
//
//    /** The clsfs to import. */
//    private List<FullClassifierDef> clsfsToImport;
//
//    private Security security;
//
//    private ModuleSecurityDef moduleSecurity;

    /**
     * Gets the model.
     *
     * @return the model
     */
    public Model getModel() {
        return model;
    }

    /**
     * With model.
     *
     * @param model
     *            the model
     * @return the full model DTO
     */
    public FullModelDTO withModel(Model model) {
        this.model = model;
        return this;
    }

    // TODO: @Modules
//    /**
//     * Gets the matching user settings.
//     *
//     * @return the matching user settings
//     */
//    public MatchingUserSettings getMatchingSettings() {
//        return matchingSettings;
//    }

    // TODO: @Modules
//    /**
//     * With matching user settings.
//     *
//     * @param matchingSettings the matching settings
//     * @return the full model DTO
//     */
//    public FullModelDTO withMatchingSettings(MatchingUserSettings matchingSettings) {
//        this.matchingSettings = matchingSettings;
//        return this;
//    }

    /**
     * Gets the measurement values.
     *
     * @return the measurement values
     */
    public MeasurementValues getMeasurementValues() {
        return measurementValues;
    }

    /**
     * With measurement values.
     *
     * @param measurementValues
     *            the measurement values
     * @return the full model DTO
     */
    public FullModelDTO withMeasurementValues(MeasurementValues measurementValues) {
        this.measurementValues = measurementValues;
        return this;
    }

// TODO: @Modules
//    /**
//     * Gets the cleanse functions.
//     *
//     * @return the cleanse functions
//     */
//    public CleanseFunctionGroupDef getCleanseFunctions() {
//        return cleanseFunctions;
//    }
//
//    /**
//     * With cleanse functions.
//     *
//     * @param cleanseFunctions
//     *            the cleanse functions
//     * @return the full model DTO
//     */
//    public FullModelDTO withCleanseFunctions(CleanseFunctionGroupDef cleanseFunctions) {
//        this.cleanseFunctions = cleanseFunctions;
//        return this;
//    }
//
//    /**
//     * Gets the clsfs.
//     *
//     * @return the clsfs
//     */
//    public List<ClsfDTO> getClsfs() {
//        if(this.clsfs==null){
//            this.clsfs = new ArrayList<>();
//        }
//        return clsfs;
//    }
//
//    /**
//     * With clsfs.
//     *
//     * @param clsfs
//     *            the clsfs
//     * @return the full model DTO
//     */
//    public FullModelDTO withClsfs(List<ClsfDTO> clsfs) {
//        this.clsfs = clsfs;
//        return this;
//    }
//
//    /**
//     * Gets the clsfs to import.
//     *
//     * @return the clsfs to import
//     */
//    public List<FullClassifierDef> getClsfsToImport() {
//        if(clsfsToImport==null){
//            this.clsfsToImport = new ArrayList<>();
//        }
//        return clsfsToImport;
//    }
//
//    /**
//     * With clsf to import.
//     *
//     * @param clsfsToImport the clsfs to import
//     * @return the full model DTO
//     */
//    public FullModelDTO withClsfToImport(List<FullClassifierDef> clsfsToImport) {
//        this.clsfsToImport = clsfsToImport;
//        return this;
//    }

    public Map<Pair<String, String>, ByteBuffer> getCustomCfs() {
        return customCfs;
    }

    public FullModelDTO withCustomCfs(Map<Pair<String, String>, ByteBuffer> customCfs) {
        this.customCfs = customCfs;
        return this;
    }

    /**
     * Checks if is override.
     *
     * @return true, if is override
     */
    public boolean isOverride() {
        return override;
    }

    /**
     * Sets the override.
     *
     * @param override the new override
     */
    public void setOverride(boolean override) {
        this.override = override;
    }

    public String getStorageId() {
        return storageId;
    }

    public FullModelDTO withStorageId(String storageId) {
        this.storageId = storageId;
        return this;
    }

    // TODO: @Modules
//    public Security getSecurity() {
//        return security;
//    }
//
//    public FullModelDTO withSecurity(final Security security) {
//        this.security = security;
//        return this;
//    }
//
//    public ModuleSecurityDef getModuleSecurity() {
//        return moduleSecurity;
//    }
//
//    public FullModelDTO withModuleSecurity(ModuleSecurityDef moduleSecurity) {
//        this.moduleSecurity = moduleSecurity;
//        return this;
//    }
}
