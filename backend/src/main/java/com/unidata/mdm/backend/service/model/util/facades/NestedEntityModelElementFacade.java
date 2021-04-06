package com.unidata.mdm.backend.service.model.util.facades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.model.util.wrappers.NestedEntityWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.NestedEntityDef;

@Component
public class NestedEntityModelElementFacade extends AbstractModelElementFacade<NestedEntityWrapper, NestedEntityDef> {

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.NESTED_ENTITY;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull NestedEntityDef modelElement) {
        return modelElement.getName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull NestedEntityDef modelElement) {
        return JaxbUtils.marshalNestedEntity(modelElement);
    }

    @Override
    public void verifyModelElement(NestedEntityDef modelElement) {
        super.verifyModelElement(modelElement);
        modelElement.getSimpleAttribute().stream().forEach(attr-> checkSimpleAttribute(attr, modelElement.getDisplayName()));
        validateCustomProperties(modelElement.getCustomProperties());
    }

    @Nonnull
    @Override
    public NestedEntityWrapper convertToWrapper(@Nonnull NestedEntityDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        Map<String, AttributeInfoHolder> attrs = ModelUtils.createAttributesMap(modelElement, ctx.getNestedEntityUpdate());
        return new NestedEntityWrapper(modelElement, modelElement.getName(), attrs);
    }
}
