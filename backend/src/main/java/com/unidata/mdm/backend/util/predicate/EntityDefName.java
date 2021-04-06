package com.unidata.mdm.backend.util.predicate;

import java.util.function.Predicate;

import com.unidata.mdm.meta.EntityDef;

/**
 * @author Michael Yashin. Created on 28.05.2015.
 */
public class EntityDefName implements Predicate<EntityDef> {

    private String name;

    public EntityDefName(String name) {
        this.name = name;
    }

    @Override
    public boolean test(EntityDef entityDef) {
        return name.equals(entityDef.getName());
    }

    @Override
    public Predicate<EntityDef> and(Predicate<? super EntityDef> other) {
        return null;
    }

    @Override
    public Predicate<EntityDef> negate() {
        return null;
    }

    @Override
    public Predicate<EntityDef> or(Predicate<? super EntityDef> other) {
        return null;
    }
}
