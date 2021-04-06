package com.unidata.mdm.backend.service.model.util.facades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.wrappers.CleanseFunctionRootGroupWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;

@Component
public class CleanseFunctionModelElementFacade extends AbstractModelElementFacade<CleanseFunctionRootGroupWrapper, CleanseFunctionGroupDef> {

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.CLEANSE_FUNCTION_GROUP;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull CleanseFunctionGroupDef modelElement) {
        return modelElement.getGroupName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull CleanseFunctionGroupDef modelElement) {
        return JaxbUtils.marshalCleanseFunctionGroup(modelElement);
    }

    @Nonnull
    @Override
    public CleanseFunctionRootGroupWrapper convertToWrapper(@Nonnull CleanseFunctionGroupDef modelElement, @Nonnull UpdateModelRequestContext ctx) {
        return new CleanseFunctionRootGroupWrapper(modelElement);
    }
}
