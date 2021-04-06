package com.unidata.mdm.backend.converter.classifiers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierNodeDef;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;


/**
 * The Class ClassifierNodeConverter.
 */
@ConverterQualifier
@Component
public class ClassifierNodeConverter implements Converter<ClsfNodeDTO, ClassifierNodeDef> {

    /** The attr converter. */
    @Autowired
    private Converter<ClsfNodeAttrDTO, SimpleAttributeWithOptionalValueDef> attrConverter;

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public ClassifierNodeDef convert(ClsfNodeDTO source) {
        ClassifierNodeDef nodeDef = new ClassifierNodeDef();
        nodeDef.setDescription(source.getDescription());
        nodeDef.setName(source.getName());
        nodeDef.setCode(source.getCode());
        nodeDef.setClassifierName(source.getClsfName());
        nodeDef.setId(source.getNodeId());
        nodeDef.setParentId(source.getParentId());
        List<SimpleAttributeWithOptionalValueDef> attrs = source.getNodeAttrs().stream()
                .map(attr -> attrConverter.convert(attr))
                .collect(Collectors.toList());
        nodeDef.withAttributes(attrs);
        return nodeDef;
    }
}
