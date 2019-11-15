package org.unidata.mdm.meta.service.impl.facades;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.meta.ComplexAttributesHolderEntityDef;
import org.unidata.mdm.meta.RelType;
import org.unidata.mdm.meta.RelationDef;
import org.unidata.mdm.meta.context.DeleteModelRequestContext;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.exception.MetaExceptionIds;
import org.unidata.mdm.meta.service.impl.ModelCache;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.meta.type.info.impl.RelationInfoHolder;
import org.unidata.mdm.meta.util.MetaJaxbUtils;
import org.unidata.mdm.meta.util.ModelUtils;
import org.unidata.mdm.system.exception.PlatformBusinessException;

@Component
public class RelationModelElementFacade extends AbstractModelElementFacade<RelationInfoHolder, RelationDef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationModelElementFacade.class);

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.RELATION;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull RelationDef modelElement) {
        return modelElement.getName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull RelationDef modelElement) {
        return MetaJaxbUtils.marshalRelation(modelElement);
    }

    @Override
    public boolean isUniqueModelElementId(RelationDef modelElement) {
        boolean isUnique = super.isUniqueModelElementId(modelElement);
        if (!isUnique) return false;
        //check has cache other relations with the same name and if has check that they from the same entity otherwise return false!
        RelationDef cachedRel = metaModelService.getRelationsList().stream().filter(rel -> rel.getName().equals(modelElement.getName())).findAny().orElse(null);
        String from = modelElement.getFromEntity();
        return cachedRel == null || cachedRel.getFromEntity().equals(from);
    }

    @Override
    public void updateVersion(RelationDef modelElement) {
        //because relations everytime removed during entity update.
        modelElement.setVersion(INITIAL_VERSION);
    }

    @Override
    public void verifyModelElement(RelationDef modelElement) {
        super.verifyModelElement(modelElement);
        if (StringUtils.isBlank(modelElement.getFromEntity())
                || StringUtils.isBlank(modelElement.getToEntity())) {
            throw new PlatformBusinessException("From and to entities of a relation must be defined.",
                    MetaExceptionIds.EX_META_RELATION_SIDE_IS_ABSENT,
                    modelElement.getName(), modelElement.getFromEntity(), modelElement.getToEntity());
        }
        validateCustomProperties(modelElement.getCustomProperties());
        // TODO: Commented out in scope of UN-11834. Reenable ASAP.
        // modelElement.getDataQualities().forEach(dq -> validateCustomProperties(dq.getCustomProperties()));
    }

    @Nonnull
    @Override
    public RelationInfoHolder convertToWrapper(@Nonnull RelationDef modelElement, @Nonnull UpdateModelRequestContext ctx) {

        ComplexAttributesHolderEntityDef attrsHolder = modelElement;
        if (modelElement.getRelType() == RelType.CONTAINS) {

            attrsHolder = ctx.getEntityUpdate().stream()
                .filter(e -> modelElement.getToEntity().equals(e.getName()))
                .findFirst()
                .orElseThrow(() -> {
                    final String message = "The 'to' side containment entity '{}' of the relation '{}' not found in update.";
                    LOGGER.warn(message, modelElement.getToEntity(), modelElement.getName());
                    return new PlatformBusinessException(message,
                            MetaExceptionIds.EX_META_TO_CONTAINMENT_ENTITY_NOT_FOUND_UPDATE,
                            modelElement.getToEntity(), modelElement.getName()
                    );
                });
        }

        Map<String, AttributeModelElement> attrs = ModelUtils.createAttributesMap(attrsHolder, ctx.getNestedEntityUpdate());
        return new RelationInfoHolder(modelElement, modelElement.getName(), attrs);
    }

    @Nullable
    @Override
    public RelationInfoHolder removeFromCache(@Nonnull String uniqueIdentifier, @Nonnull DeleteModelRequestContext deleteModelRequestContext, @Nonnull ModelCache modelCache) {
        RelationInfoHolder relation = super.removeFromCache(uniqueIdentifier, deleteModelRequestContext, modelCache);
        if (relation != null) {
            String from = relation.getRelation().getFromEntity();
            String to = relation.getRelation().getToEntity();
            EntityInfoHolder fromEntity = (EntityInfoHolder) modelCache.getCache().get(EntityInfoHolder.class).get(from);
            if (fromEntity != null) {
                fromEntity.getRelationsFrom().remove(relation);
            }
            EntityInfoHolder toEntity = (EntityInfoHolder) modelCache.getCache().get(EntityInfoHolder.class).get(to);
            if (toEntity != null) {
                toEntity.getRelationsTo().remove(relation);
            }
        }
        return relation;
    }
}
