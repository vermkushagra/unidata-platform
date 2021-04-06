package com.unidata.mdm.backend.api.rest.converter.clsf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.unidata.mdm.backend.api.rest.dto.clsf.ClsfNodeRO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;


/**
 * The Class ClsfNodeDTOToROConverter.
 */
public class ClsfNodeDTOToROConverter {
	
	/** The Constant CLSF_NODE_COMPARATOR. */
	private static final Comparator<ClsfNodeRO> CLSF_NODE_COMPARATOR = new Comparator<ClsfNodeRO>() {

		@Override
		public int compare(ClsfNodeRO o1, ClsfNodeRO o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			if(o1.getName()==null||o2.getName()==null){
				return 0;
			}
			return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
		}

	};
	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @param clsfName
	 *            the clsf name
	 * @return the clsf node RO
	 */
	public static ClsfNodeRO convert(ClsfNodeDTO source, String clsfName) {
		if (source == null) {
			return null;
		}
		ClsfNodeRO target = new ClsfNodeRO();
		target.setChildCount(source.getChildCount());
		target.setChildren(convert(source.getChildren(), clsfName));
		target.setClassifierName(clsfName);
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());
		target.setId(source.getNodeId());
		List<ClsfNodeAttrDTO> inheritedAttrs = source.getNodeAttrs().parallelStream().filter(na -> na.isInherited())
				.collect(Collectors.toList());
		Map<String, ClsfNodeAttrDTO> map = new HashMap<String, ClsfNodeAttrDTO>();
		for (ClsfNodeAttrDTO ia : inheritedAttrs) {
			if(!map.containsKey(ia.getAttrName())||ia.getDefaultValue()!=null){
				map.put(ia.getAttrName(), ia);
			}
		}
		inheritedAttrs.clear();
		inheritedAttrs.addAll(map.values().stream().collect(Collectors.toList()));
		List<ClsfNodeAttrDTO> ownAttrs = source.getNodeAttrs().parallelStream().filter(na -> !na.isInherited())
				.collect(Collectors.toList());
		target.setInheritedNodeAttrs(ClsfNodeAttrDTOToROConverter.convert(inheritedAttrs));
		target.setNodeAttrs(ClsfNodeAttrDTOToROConverter.convert(ownAttrs));
		target.setName(source.getName());
		target.setOwnNodeAttrs(source.isHasOwnAttrs());
		target.setParentId(source.getParentId());
		return target;
	}

	/**
	 * Convert.
	 *
	 * @param source
	 *            the source
	 * @param clsfName
	 *            the clsf name
	 * @return the list
	 */
	public static List<ClsfNodeRO> convert(List<ClsfNodeDTO> source, String clsfName) {
		if (source == null) {
			return null;
		}
		List<ClsfNodeRO> target = new ArrayList<>();
		for (ClsfNodeDTO element : source) {
			target.add(convert(element, clsfName));
		}
		target.sort(CLSF_NODE_COMPARATOR);
		return target;
	}

	/**
	 * Distinct by key.
	 *
	 * @param <T> the generic type
	 * @param keyExtractor the key extractor
	 * @return the predicate
	 */
	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();	
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
