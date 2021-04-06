package com.unidata.mdm.backend.service.model.util.wrappers;

import java.util.Collection;
import java.util.HashMap;

import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

public class EntitiesGroupWrapper extends ModelWrapper {

    private final EntitiesGroupDef entitiesGroupDef;

    private final String wrapperId;

    private final HashMap<String, EntityDef> nestedEntities = new HashMap<>();

    private final HashMap<String, LookupEntityDef> nestedLookupEntities = new HashMap<>();

    public EntitiesGroupWrapper(EntitiesGroupDef entitiesGroupDef, String wrapperId) {
        this.entitiesGroupDef = entitiesGroupDef;
        this.wrapperId = wrapperId;
    }

    @Override
    public String getUniqueIdentifier() {
        return entitiesGroupDef.getGroupName();
    }

    @Override
    public Long getVersionOfWrappedElement() {
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
