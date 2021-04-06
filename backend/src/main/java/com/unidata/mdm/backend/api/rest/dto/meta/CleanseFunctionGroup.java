package com.unidata.mdm.backend.api.rest.dto.meta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yashin. Created on 20.05.2015.
 */
public class CleanseFunctionGroup extends CleanseFunctionTreeElement{
    protected List<CleanseFunction> functions = new ArrayList<>();
    protected List<CleanseFunctionGroup> groups = new ArrayList<>();

    public List<CleanseFunction> getFunctions() {
        return functions;
    }

    public void setFunctions(List<CleanseFunction> functions) {
        this.functions = functions;
    }

    public List<CleanseFunctionGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<CleanseFunctionGroup> groups) {
        this.groups = groups;
    }
}
