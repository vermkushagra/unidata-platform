package com.unidata.migration.model;

import java.io.File;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class PathValidator implements IParameterValidator {

    public void validate(String name, String value) throws ParameterException {
        if (!new File(value).exists()) {
            throw new ParameterException("model is not present");
        }
    }
}
