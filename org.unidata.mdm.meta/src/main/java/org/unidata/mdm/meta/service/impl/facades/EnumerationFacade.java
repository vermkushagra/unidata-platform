package org.unidata.mdm.meta.service.impl.facades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.springframework.stereotype.Component;
import org.unidata.mdm.meta.EnumerationDataType;
import org.unidata.mdm.meta.context.UpdateModelRequestContext;
import org.unidata.mdm.meta.type.ModelType;
import org.unidata.mdm.meta.type.info.impl.EnumerationInfoHolder;
import org.unidata.mdm.meta.util.MetaJaxbUtils;

@Component
public class EnumerationFacade extends AbstractModelElementFacade<EnumerationInfoHolder, EnumerationDataType> {

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
        return MetaJaxbUtils.marshalEnumeration(modelElement);
    }

    @Nonnull
    @Override
    public EnumerationInfoHolder convertToWrapper(@Nonnull EnumerationDataType modelElement, @Nonnull UpdateModelRequestContext ctx) {
        return new EnumerationInfoHolder(modelElement);
    }
}
