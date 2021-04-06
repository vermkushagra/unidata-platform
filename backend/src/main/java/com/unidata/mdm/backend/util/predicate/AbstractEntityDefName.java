package com.unidata.mdm.backend.util.predicate;

import java.util.function.Predicate;

import com.unidata.mdm.meta.AbstractEntityDef;

/**
 * @author Michael Yashin. Created on 28.05.2015.
 */
public class AbstractEntityDefName implements Predicate<AbstractEntityDef> {

    private String name;

    public AbstractEntityDefName(String name) {
        this.name = name;
    }

    @Override
    public boolean test(AbstractEntityDef abstractEntityDef) {
        return name.equals(abstractEntityDef.getName());
    }

    @Override
    public Predicate<AbstractEntityDef> and(Predicate<? super AbstractEntityDef> other) {
        return null;
    }

    @Override
    public Predicate<AbstractEntityDef> negate() {
        return null;
    }

    @Override
    public Predicate<AbstractEntityDef> or(Predicate<? super AbstractEntityDef> other) {
        return null;
    }
}
