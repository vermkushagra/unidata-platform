package com.unidata.mdm.backend.util.predicate;

import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.unidata.mdm.meta.NestedEntityDef;

/**
 * @author Michael Yashin. Created on 28.05.2015.
 */
public class NestedEntityDefName implements Predicate<NestedEntityDef> {

    private String name;

    public NestedEntityDefName(String name) {
        this.name = name;
    }

    @Override
    public boolean test(NestedEntityDef nestedEntityDef) {
        if(nestedEntityDef==null){
            return false;
        }
        return StringUtils.equals(name, nestedEntityDef.getName());
    }

    @Override
    public Predicate<NestedEntityDef> and(Predicate<? super NestedEntityDef> other) {
        return null;
    }

    @Override
    public Predicate<NestedEntityDef> negate() {
        return null;
    }

    @Override
    public Predicate<NestedEntityDef> or(Predicate<? super NestedEntityDef> other) {
        return null;
    }
}
