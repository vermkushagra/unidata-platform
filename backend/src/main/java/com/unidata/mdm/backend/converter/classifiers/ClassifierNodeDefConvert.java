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
 * The Class ClassifierNodeDefConvert.
 */
@ConverterQualifier
@Component
public class ClassifierNodeDefConvert implements Converter<ClassifierNodeDef, ClsfNodeDTO> {

    /** The attr converter. */
    @Autowired
    private Converter<SimpleAttributeWithOptionalValueDef, ClsfNodeAttrDTO> attrConverter;

    /* (non-Javadoc)
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public ClsfNodeDTO convert(ClassifierNodeDef source) {
    	ClsfNodeDTO node = new ClsfNodeDTO();
        node.setDescription(source.getDescription());
        node.setName(source.getName());
        node.setCode(source.getCode());
        node.setClsfName(source.getClassifierName());
        node.setNodeId(source.getId());
        node.setParentId(source.getParentId());
        List<ClsfNodeAttrDTO> attrs = source.getAttributes().stream()
                .map(attr -> attrConverter.convert(attr))
                .collect(Collectors.toList());
        node.setNodeAttrs(attrs);
        return node;
    }
}
