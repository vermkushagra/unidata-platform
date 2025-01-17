package com.unidata.mdm.backend.api.rest.converter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.api.rest.dto.meta.SourceSystemDefinition;
import com.unidata.mdm.backend.api.rest.dto.meta.SourceSystemList;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.meta.SourceSystemDef;

/**
 * The Class SourceSystemConverter. Convert instance of {@see SourceSystemDef}
 * to {@see SourceSystemDefinition}.
 *
 */
@ConverterQualifier
@Component
public class SourceSystemConverter implements
	Converter<List<SourceSystemDef>, SourceSystemList> {

    private static final Comparator<SourceSystemDefinition> COMPARATOR = (o1, o2) ->
            String.CASE_INSENSITIVE_ORDER.compare(o1.getName(),o2.getName());

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.core.convert.converter.Converter#convert(java.lang
     * .Object)
     */
    @Override
    public SourceSystemList convert(List<SourceSystemDef> source) {
        if (source == null) {
            return null;
        }

        String adminSystemName = null;
        SourceSystemList target = new SourceSystemList();
        for (SourceSystemDef sourceSystemDef : source) {
            target.addSourceSystem(SourceSystemConverter.to(sourceSystemDef));
            if (sourceSystemDef.isAdmin()) {
                adminSystemName = sourceSystemDef.getName();
            }
        }
        target.setAdminSystemName(adminSystemName);
        Collections.sort(target.getSourceSystem(), COMPARATOR);
        return target;
    }

    /**
     * Convert instance of {@see SourceSystemDef} to {@see
     * SourceSystemDefinition}.
     *
     * @param source
     *            the source
     * @return the source system definition
     */
    public static SourceSystemDefinition to(SourceSystemDef source) {
        if (source == null) {
            return null;
        }
        SourceSystemDefinition target = new SourceSystemDefinition();
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setWeight(source.getWeight() == null ? 0 : source.getWeight()
        	.intValue());
        target.setCustomProperties(AbstractEntityDefinitionConverter.to(source.getCustomProperties()));
        return target;
    }

    /**
     * Convert instance of {@see SourceSystemDef} to {@see
     * SourceSystemDefinition}.
     *
     * @param source
     *            the source
     * @return the source system definition
     */
    public static List<SourceSystemDefinition> to(List<SourceSystemDef> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<SourceSystemDefinition> target = new ArrayList<>();
        for (SourceSystemDef s : source) {
        	target.add(to(s));
        }

        return target;
    }

    /**
     * Converts from RO to system.
     * @param source the source
     * @return system object
     */
    public static SourceSystemDef from(SourceSystemDefinition source) {
    	if (source == null) {
    		return null;
    	}

    	SourceSystemDef target = JaxbUtils.getMetaObjectFactory().createSourceSystemDef();
    	target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setWeight(BigInteger.valueOf(source.getWeight()));
        target.withCustomProperties(ToCustomPropertyDefConverter.convert(source.getCustomProperties()));
    	return target;
    }

    /**
     * Convert instance of {@see SourceSystemDef} to {@see
     * SourceSystemDefinition}.
     *
     * @param source
     *            the source
     * @return the source system definition
     */
    public static List<SourceSystemDef> from(List<SourceSystemDefinition> source) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }

        List<SourceSystemDef> target = new ArrayList<>();
        for (SourceSystemDefinition s : source) {
        	target.add(from(s));
        }

        return target;
    }
}
