package com.unidata.mdm.backend.converter.classifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.converter.ConverterQualifier;
import com.unidata.mdm.classifier.ClassifierDef;
import com.unidata.mdm.classifier.ClassifierNodeDef;
import com.unidata.mdm.classifier.FullClassifierDef;

/**
 * The Class ClassifierToFullDefConverter.
 */
@ConverterQualifier
@Component
public class ClassifierToFullDefConverter implements Converter<ClsfDTO, FullClassifierDef> {

	/** The classifier def converter. */
	@Autowired
	private Converter<ClsfDTO, ClassifierDef> classifierDefConverter;

	/** The classifier node def converter. */
	@Autowired
	private Converter<ClsfNodeDTO, ClassifierNodeDef> classifierNodeDefConverter;

	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public FullClassifierDef convert(ClsfDTO source) {
		FullClassifierDef fullClassifierDef = new FullClassifierDef();
		ClassifierDef classifierDef = classifierDefConverter.convert(source);
		fullClassifierDef.setClassifier(classifierDef);
		List<ClassifierNodeDef> nodes = getAllNodes(source).stream()
				.map(node -> classifierNodeDefConverter.convert(node)).collect(Collectors.toList());
		fullClassifierDef.withClassifierNodes(nodes);
		return fullClassifierDef;
	}

	/**
	 * Gets the all nodes.
	 *
	 * @param source the source
	 * @return the all nodes
	 */
	private List<ClsfNodeDTO> getAllNodes(ClsfDTO source) {
		List<ClsfNodeDTO> result = new ArrayList<>();
		addNodes(result, source.getRootNode());
		return result;
	}

	/**
	 * Adds the nodes.
	 *
	 * @param result the result
	 * @param toAdd the to add
	 */
	private void addNodes(List<ClsfNodeDTO> result, ClsfNodeDTO toAdd) {
		if (toAdd != null) {
			result.add(toAdd);
			if (toAdd.getChildren() != null) {
				for (ClsfNodeDTO clsfNodeDTO : toAdd.getChildren()) {
					addNodes(result, clsfNodeDTO);
				}
			}
		}
	}
}
