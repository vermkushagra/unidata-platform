package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunction;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionGroup;
import com.unidata.mdm.backend.api.rest.dto.meta.CleanseFunctionType;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author Michael Yashin. Created on 21.05.2015.
 */
@ConverterQualifier
@Component
public class CleanseFunctionGroupConverter implements Converter<CleanseFunctionGroupDef, CleanseFunctionGroup> {
    @Override
    public CleanseFunctionGroup convert(CleanseFunctionGroupDef source) {
        CleanseFunctionGroup result = new CleanseFunctionGroup();
        result.setName(source.getGroupName());
        result.setDescription(source.getDescription());
        for (Object element : source.getGroupOrCleanseFunctionOrCompositeCleanseFunction()) {
            if (element instanceof CleanseFunctionGroupDef) {
                CleanseFunctionGroup group = convert((CleanseFunctionGroupDef)element);
                result.getGroups().add(group);
            }
            if (element instanceof CleanseFunctionDef) {
                CleanseFunction function = convertFunction((CleanseFunctionDef) element);
                result.getFunctions().add(function);
            }
        }
        return result;
    }

    protected CleanseFunction convertFunction(CleanseFunctionDef functionDef) {
        CleanseFunction function = new CleanseFunction();
        function.setName(functionDef.getFunctionName());
        function.setDescription(functionDef.getDescription());
        function.setJavaClass(functionDef.getJavaClass());
        if (functionDef instanceof CompositeCleanseFunctionDef) {
            function.setType(CleanseFunctionType.COMPOSITE_FUNCTION);
        } else {
            function.setType(CleanseFunctionType.BASIC_FUNCTION);
        }
        return function;
    }

}
