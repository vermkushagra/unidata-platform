package com.unidata.mdm.backend.util.predicate;

import com.unidata.mdm.meta.RelationDef;

import java.util.function.Predicate;

/**
 * @author Michael Yashin. Created on 28.05.2015.
 */
public class RelationDefSource implements Predicate<RelationDef> {
    private String source;

    public RelationDefSource(String source) {
        this.source = source;
    }

    @Override
    public boolean test(RelationDef relationDef) {
        return source.equals(relationDef.getFromEntity());
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
