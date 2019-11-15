package org.unidata.mdm.meta.type.info.impl;

import java.util.Collection;
import java.util.HashMap;

import org.unidata.mdm.core.type.model.IdentityModelElement;
import org.unidata.mdm.meta.EntitiesGroupDef;
import org.unidata.mdm.meta.EntityDef;
import org.unidata.mdm.meta.LookupEntityDef;

public class EntitiesGroupWrapper implements IdentityModelElement {

    private final EntitiesGroupDef entitiesGroupDef;

    private final String wrapperId;

    private final HashMap<String, EntityDef> nestedEntities = new HashMap<>();

    private final HashMap<String, LookupEntityDef> nestedLookupEntities = new HashMap<>();

    public EntitiesGroupWrapper(EntitiesGroupDef entitiesGroupDef, String wrapperId) {
        this.entitiesGroupDef = entitiesGroupDef;
        this.wrapperId = wrapperId;
    }

    @Override
    public String getId() {
        return entitiesGroupDef.getGroupName();
    }

    @Override
    public Long getVersion() {
        return entitiesGroupDef.getVersion();
    }

    public void addLookupEntityToGroup(LookupEntityDef lookupEntityDef) {
        nestedLookupEntities.put(lookupEntityDef.getName(), lookupEntityDef);
    }

    public void addEntityToGroup(EntityDef entity) {
        nestedEntities.put(entity.getName(), entity);
    }

    public Collection<EntityDef> getNestedEntites() {
        return nestedEntities.values();
    }

    public Collection<LookupEntityDef> getNestedLookupEntities() {
        return nestedLookupEntities.values();
    }

    public boolean removeEntity(String entityName) {
        return nestedEntities.remove(entityName) != null;
    }

    public boolean removeLookupEntity(String lookupEntityName) {
        return nestedLookupEntities.remove(lookupEntityName) != null;
    }

    public String getWrapperId() {
        return wrapperId;
    }

    public EntitiesGroupDef getEntitiesGroupDef() {
        return entitiesGroupDef;
    }
}
