package com.unidata.mdm.backend.util.predicate;

import java.util.function.Predicate;

import com.unidata.mdm.meta.RelationDef;

/**
 * @author Michael Yashin. Created on 28.05.2015.
 */
public class RelationDefName implements Predicate<RelationDef> {
    private String name;

    public RelationDefName(String name) {
        this.name = name;
    }

    @Override
    public boolean test(RelationDef relationDef) {
        return name.equals(relationDef.getName());
    }

    @Override
    public Predicate<RelationDef> and(Predicate<? super RelationDef> other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<RelationDef> negate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Predicate<RelationDef> or(Predicate<? super RelationDef> other) {
        throw new UnsupportedOperationException();
    }
}
