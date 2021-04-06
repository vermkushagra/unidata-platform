package com.unidata.mdm.backend.service.model.util.facades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ModelType;
import com.unidata.mdm.backend.service.model.util.wrappers.EnumerationWrapper;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.EnumerationDataType;

@Component
public class EnumerationFacade extends AbstractModelElementFacade<EnumerationWrapper, EnumerationDataType> {

    @Nonnull
    @Override
    public ModelType getModelType() {
        return ModelType.ENUMERATION;
    }

    @Nullable
    @Override
    public String getModelElementId(@Nonnull EnumerationDataType modelElement) {
        return modelElement.getName();
    }

    @Nonnull
    @Override
    protected String getMarshaledData(@Nonnull EnumerationDataType modelElement) {
        return JaxbUtils.marshalEnumeration(modelElement);
    }

    @Nonnull
    @Override
    public EnumerationWrapper convertToWrapper(@Nonnull EnumerationDataType modelElement, @Nonnull UpdateModelRequestContext ctx) {
        return new EnumerationWrapper(modelElement);
    }
}
