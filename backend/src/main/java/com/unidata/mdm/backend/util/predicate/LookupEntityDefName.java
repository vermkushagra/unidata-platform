package com.unidata.mdm.backend.util.predicate;

import java.util.function.Predicate;

import com.unidata.mdm.meta.LookupEntityDef;

/**
 * @author Michael Yashin. Created on 28.05.2015.
 */
public class LookupEntityDefName implements Predicate<LookupEntityDef> {

    private String name;

    public LookupEntityDefName(String name) {
        this.name = name;
    }

    @Override
    public boolean test(LookupEntityDef lookupEntityDef) {
        return name.equals(lookupEntityDef.getName());
    }

    @Override
    public Predicate<LookupEntityDef> and(Predicate<? super LookupEntityDef> other) {
        return null;
    }

    @Override
    public Predicate<LookupEntityDef> negate() {
        return null;
    }

    @Override
    public Predicate<LookupEntityDef> or(Predicate<? super LookupEntityDef> other) {
        return null;
    }
}
