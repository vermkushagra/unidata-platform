package com.unidata.migration.model;

import com.beust.jcommander.Parameter;

public class ModelMigrationParams {

    @Parameter(names = "--c-model", description = "Path to model", validateWith = PathValidator.class)
    private String currentModelPath;

    public String getCurrentModelPath() {
        return currentModelPath;
    }

    public void setCurrentModelPath(String currentModelPath) {
        this.currentModelPath = currentModelPath;
    }
}
