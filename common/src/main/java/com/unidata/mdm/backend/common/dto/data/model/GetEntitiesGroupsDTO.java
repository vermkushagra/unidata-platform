package com.unidata.mdm.backend.common.dto.data.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;

/**
 * @author Mikhail Mikhailov
 * Entities groups transfer object.
 */
public class GetEntitiesGroupsDTO {
    /**
     * Groups.
     */
    private Map<String, EntitiesGroupDef> groups;
    /**
     * Nested.
     */
    private Map<EntitiesGroupDef, Pair<List<EntityDef>, List<LookupEntityDef>>> nested;
    /**
     * Constructor.
     * @param groups groups
     * @param nested nested
     */
    public GetEntitiesGroupsDTO(Map<String, EntitiesGroupDef> groups, Map<EntitiesGroupDef, Pair<List<EntityDef>, List<LookupEntityDef>>> nested) {
        super();
        this.groups = groups;
        this.nested = nested;
    }
    /**
     * @return the groups
     */
    @Nonnull
    public Map<String, EntitiesGroupDef> getGroups() {
        return Objects.isNull(groups) ? Collections.emptyMap() : groups;
    }
    /**
     * @return the nested
     */
    @Nonnull
    public Map<EntitiesGroupDef, Pair<List<EntityDef>, List<LookupEntityDef>>> getNested() {
        return Objects.isNull(nested) ? Collections.emptyMap() : nested;
    }

    public Pair<List<EntityDef>, List<LookupEntityDef>> getNestedObjects(String id) {
        EntitiesGroupDef def = getGroups().get(id);
        return Objects.isNull(def) ? null : getNested().get(def);
    }

    public List<EntityDef> getNestedEntities(String id) {
        Pair<List<EntityDef>, List<LookupEntityDef>> deps = getNestedObjects(id);
        if (Objects.nonNull(deps)) {
             return Objects.isNull(deps.getLeft()) ? Collections.emptyList() : deps.getLeft();
        }

        return Collections.emptyList();
    }

    public List<LookupEntityDef> getNestedLookupEntities(String id) {
        Pair<List<EntityDef>, List<LookupEntityDef>> deps = getNestedObjects(id);
        if (Objects.nonNull(deps)) {
             return Objects.isNull(deps.getRight()) ? Collections.emptyList() : deps.getRight();
        }

        return Collections.emptyList();
    }
}
