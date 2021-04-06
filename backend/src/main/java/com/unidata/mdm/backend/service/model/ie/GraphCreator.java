package com.unidata.mdm.backend.service.model.ie;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.service.matching.data.MatchingAlgorithm;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.cycle.DirectedSimpleCycles;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.springframework.stereotype.Component;

import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.backend.service.model.ie.dto.FullModelDTO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaAction;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaExistence;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaMessage;
import com.unidata.mdm.backend.service.model.ie.dto.MetaPropKey;
import com.unidata.mdm.backend.service.model.ie.dto.MetaStatus;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.meta.ArrayAttributeDef;
import com.unidata.mdm.meta.AttributeMeasurementSettingsDef;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.ComplexAttributeDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionNodeType;
import com.unidata.mdm.meta.DQRSourceSystemRef;
import com.unidata.mdm.meta.DQRuleDef;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.MeasurementValueDef;
import com.unidata.mdm.meta.MeasurementValues;
import com.unidata.mdm.meta.MergeSettingsDef;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SimpleAttributeDef;
import com.unidata.mdm.meta.SourceSystemDef;

/**
 * The Class GraphCreator.
 */
@Component
public class GraphCreator {

	/** The Constant PATH_DELIMETER. */
	private static final String PATH_DELIMETER = ".";

	/**
	 * Enrich.
	 *
	 * @param toProcess
	 *            the to process
	 * @param result
	 *            the result
	 * @param defaultStatus
	 *            the default status
	 * @param metaTypes
	 *            the meta types
	 */
	public void enrich(FullModelDTO toProcess, MetaGraph result, MetaExistence defaultStatus, MetaType... metaTypes) {
		Set<MetaType> typesToProcess = new HashSet<>();
		if (metaTypes != null) {
			typesToProcess.addAll(Arrays.asList(metaTypes));
		}
		if(MapUtils.isNotEmpty(toProcess.getCustomCfs()) && typesToProcess.contains(MetaType.CF)){
			toProcess.getCustomCfs().forEach((k,v)->{
				addVertex(result, new MetaVertex(k, k, MetaType.CUSTOM_CF,
						defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));
			});
		}
		if (toProcess.getCleanseFunctions() != null && typesToProcess.contains(MetaType.CF)) {
			List<CompositeCleanseFunctionDef> composite = new ArrayList<>();
			processCFs(toProcess.getCleanseFunctions(), "", result, defaultStatus, composite);
			processCompositeCFs(composite, result, defaultStatus);
		}
		if (CollectionUtils.isNotEmpty(toProcess.getClsfs()) && typesToProcess.contains(MetaType.CLASSIFIER)) {
			processClsfs(toProcess.getClsfs(), result, defaultStatus);
		}


		if (toProcess.getMeasurementValues() != null && typesToProcess.contains(MetaType.MEASURE)) {
			processMeasurements(toProcess.getMeasurementValues(), result, defaultStatus);
		}
		if (toProcess.getModel() != null) {
			enrichWithModel(toProcess.getModel(), result, defaultStatus, typesToProcess);
			checkDuplicates(result);
		}
        if (toProcess.getMatchingSettings() != null && typesToProcess.contains(MetaType.MATCH_RULE)) {
            processMatching(toProcess.getMatchingSettings(), result, defaultStatus);
        }
	}

	/**
	 * Process measurements.
	 *
	 * @param measurementValues
	 *            the measurement values
	 * @param result
	 *            the result
	 * @param defaultStatus
	 *            the default status
	 */
	private void processMeasurements(MeasurementValues measurementValues, MetaGraph result,
			MetaExistence defaultStatus) {
		List<MeasurementValueDef> values = measurementValues.getValue();
		for (MeasurementValueDef value : values) {
			addVertex(result, new MetaVertex(value.getId(), value.getDisplayName(), MetaType.MEASURE,
					defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));
		}

	}

	/**
	 * Process matching.
	 *
	 * @param matchingUserSettings
	 *            the matching user settings
	 * @param result
	 *            the result
	 * @param defaultStatus
	 *            the default status
	 */
	private void processMatching(MatchingUserSettings matchingUserSettings, MetaGraph result,
			MetaExistence defaultStatus) {
		if (matchingUserSettings == null || matchingUserSettings.getMatchingRules().isEmpty()) {
			return;
		}
		Set<Map.Entry<String, String>> pairs = new HashSet<>();

		matchingUserSettings.getMatchingRules().forEach(ur -> {
			if (ur.getEntityName() != null && ur.getName() != null) {
				pairs.add(new AbstractMap.SimpleEntry<>(ur.getName(), ur.getEntityName()));
				Optional<MatchingAlgorithm> notValid = ur.getMatchingAlgorithms().stream().filter(ma -> ma.getName() == null).findAny();
				MetaVertex sourceVertex;
				if(notValid.isPresent()) {
					String errorMessage = MessageUtils.getMessage(ExceptionId.EX_MATCHING_ALGO_DOESNT_PRESENT.getCode(),
							notValid.get().getId(), notValid.get().getName());
					sourceVertex = new MetaVertex(ur.getEntityName() + "_" + ur.getName(), ur.getName(), MetaType.MATCH_RULE,
							defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus, createErrorMessage(errorMessage));
				} else {
					sourceVertex = new MetaVertex(ur.getEntityName() + "_" + ur.getName(), ur.getName(), MetaType.MATCH_RULE,
							defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus);
				}

				addVertex(result, sourceVertex);
			}
		});
		if (pairs.isEmpty()) {
			return;
		}
		for (Map.Entry<String, String> pair : pairs) {
			if (StringUtils.isNotEmpty(pair.getKey()) && StringUtils.isNotEmpty(pair.getValue())) {
				MetaVertex sourceVertex = new MetaVertex(pair.getValue() + "_" + pair.getKey(), MetaType.MATCH_RULE);
				MetaVertex targetVertex = new MetaVertex(pair.getValue(), MetaType.ENTITY);
				if (!result.containsVertex(targetVertex) && !result.containsVertex(new MetaVertex(pair.getValue(), MetaType.LOOKUP))) {
					result.addVertex(new MetaVertex(pair.getValue(), pair.getValue(), MetaType.ENTITY, MetaAction.NONE,
							MetaExistence.NOT_FOUND,
							createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
									pair.getValue(), MetaType.ENTITY))));
				}

				if (!result.containsEdge(sourceVertex, targetVertex)) {
					result.addEdge(sourceVertex, targetVertex);
				}
			}

		}
	}

	/**
	 * Process clsfs.
	 *
	 * @param clsfs
	 *            the clsfs
	 * @param result
	 *            the result
	 * @param defaultStatus
	 *            the default status
	 */
	private void processClsfs(List<ClsfDTO> clsfs, MetaGraph result, MetaExistence defaultStatus) {
		if (clsfs == null) {
			return;
		}
		for (ClsfDTO value : clsfs) {
			addVertex(result, new MetaVertex(value.getName(), value.getDisplayName(), MetaType.CLASSIFIER,
					defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));
		}

	}

	/**
	 * Creates the.
	 *
	 * @param model
	 *            the model
	 * @param result
	 *            the result
	 * @param defaultStatus
	 *            the default status
	 * @param typesToProcess
	 *            the types to process
	 * @return the meta graph
	 */
	public void enrichWithModel(Model model, MetaGraph result, MetaExistence defaultStatus,
			Set<MetaType> typesToProcess) {
		List<LookupEntityDef> les = model.getLookupEntities();
		List<EntityDef> es = model.getEntities();
		List<EnumerationDataType> enums = model.getEnumerations();
		List<MeasurementValueDef> mvds = model.getMeasurementValues() == null ? new ArrayList<>()
				: model.getMeasurementValues().getValue();
		List<RelationDef> rels = model.getRelations();
		// ListOfCleanseFunctions locf = model.getCleanseFunctions();

		List<SourceSystemDef> ss = model.getSourceSystems();
		List<NestedEntityDef> ness = model.getNestedEntities();
		EntitiesGroupDef egs = model.getEntitiesGroup();

		if (typesToProcess.contains(MetaType.GROUPS) && model.getEntitiesGroup() != null) {
			addEntitiesGroups(egs, "", null, result, defaultStatus);
		}
		// List<CompositeCleanseFunctionDef> composite = new ArrayList<>();
		// if (typesToProcess.contains(MetaType.CF)) {
		// processCFs(locf.getGroup(), "", result, defaultStatus, composite);
		// processCompositeCFs(composite, result, defaultStatus);
		// }
		// process entities groups
		// nested entities
		if (typesToProcess.contains(MetaType.NESTED_ENTITY) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getNestedEntities())) {
			for (NestedEntityDef nes : ness) {
				addVertex(result, new MetaVertex(nes.getName(), nes.getDisplayName(), MetaType.NESTED_ENTITY,
						defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));
			}
		}
		// lookup entities
		if (typesToProcess.contains(MetaType.LOOKUP) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getLookupEntities())) {
			for (LookupEntityDef le : les) {
				addVertex(result, new MetaVertex(le.getName(), le.getDisplayName(), MetaType.LOOKUP,
						defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));

			}
		}
		if (typesToProcess.contains(MetaType.ENTITY) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getEntities())) {
			// entities
			for (EntityDef e : es) {
				addVertex(result, new MetaVertex(e.getName(), e.getDisplayName(), MetaType.ENTITY,
						defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));
			}
		}
		// enumerations
		if (typesToProcess.contains(MetaType.ENUM) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getEnumerations())) {
			for (EnumerationDataType enumerationDataType : enums) {
				addVertex(result,
						new MetaVertex(enumerationDataType.getName(), enumerationDataType.getDisplayName(),
								MetaType.ENUM, defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE,
								defaultStatus));
			}
		}
		// measurements
		if (typesToProcess.contains(MetaType.MEASURE)) {
			for (MeasurementValueDef mvd : mvds) {
				addVertex(result, new MetaVertex(mvd.getId(), mvd.getDisplayName(), MetaType.MEASURE,
						defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));
			}
		}
		// relations
		if (typesToProcess.contains(MetaType.RELATION) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getRelations())) {
			for (RelationDef rel : rels) {
				MetaVertex vertex = new MetaVertex(rel.getName(), rel.getDisplayName(), MetaType.RELATION,
						defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus);

				Map<MetaPropKey, String> customProps = new HashMap<>();
				customProps.put(MetaPropKey.FROM, rel.getFromEntity());
				customProps.put(MetaPropKey.TO, rel.getToEntity());
				customProps.put(MetaPropKey.REL_TYPE, rel.getRelType().value());
				vertex.setCustomProps(customProps);
				addVertex(result, vertex);
			}
		}
		// source systems
		if (typesToProcess.contains(MetaType.SOURCE_SYSTEM) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getSourceSystems())) {
			for (SourceSystemDef s : ss) {
				addVertex(result, new MetaVertex(s.getName(), s.getName(), MetaType.SOURCE_SYSTEM,
						defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));
			}
		}
		if (typesToProcess.contains(MetaType.NESTED_ENTITY) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getNestedEntities())) {
			processNestedEntities(ness, result, defaultStatus, typesToProcess);
		}
		if (typesToProcess.contains(MetaType.LOOKUP) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getLookupEntities())) {
			processLookupEntities(les, result, typesToProcess);
		}
		if (typesToProcess.contains(MetaType.ENTITY) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getEntities())) {
			processEntities(es, result, typesToProcess);
		}
		if (typesToProcess.contains(MetaType.RELATION) && !org.apache.commons.collections.CollectionUtils.isEmpty(model.getRelations())) {
			processRelations(rels, result, typesToProcess);
		}
	}

	/**
	 * Adds the entities groups.
	 *
	 * @param egs
	 *            the egs
	 * @param path
	 *            the path
	 * @param root
	 *            the root
	 * @param result
	 *            the result
	 * @param defaultStatus
	 *            the default status
	 */
	private void addEntitiesGroups(EntitiesGroupDef egs, String path, MetaVertex root, MetaGraph result,
			MetaExistence defaultStatus) {
		path = StringUtils.isEmpty(path) ? egs.getGroupName() : String.join(PATH_DELIMETER, path, egs.getGroupName());
		MetaVertex eVertex = new MetaVertex(path, egs.getTitle(), MetaType.GROUPS,
				defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus);
		addVertex(result, eVertex);
		if (root != null) {
			result.addEdge(eVertex, root);
		}
		List<EntitiesGroupDef> defs = egs.getInnerGroups();
		for (EntitiesGroupDef entitiesGroupDef : defs) {
			addEntitiesGroups(entitiesGroupDef, path, eVertex, result, defaultStatus);
		}

	}

	/**
	 * Process relations.
	 *
	 * @param rels
	 *            the rels
	 * @param result
	 *            the result
	 * @param typesToProcess
	 *            the types to process
	 */
	private void processRelations(List<RelationDef> rels, MetaGraph result, Set<MetaType> typesToProcess) {
		if (rels == null) {
			return;
		}
		for (RelationDef rel : rels) {
			processRelation(rel, result);
		}

	}

	/**
	 * Process relation.
	 *
	 * @param rel
	 *            the rel
	 * @param result
	 *            the result
	 */
	private void processRelation(RelationDef rel, MetaGraph result) {
		MetaVertex eVertex = new MetaVertex(rel.getName(), MetaType.RELATION);
		if (!result.containsVertex(new MetaVertex(rel.getFromEntity(), MetaType.ENTITY))) {
			result.addVertex(new MetaVertex(rel.getFromEntity(), rel.getFromEntity(), MetaType.ENTITY, MetaAction.NONE,
					MetaExistence.NOT_FOUND,
					createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
							rel.getFromEntity(), MetaType.ENTITY))));
		}
		result.addEdge(eVertex, new MetaVertex(rel.getFromEntity(), MetaType.ENTITY));
		if (!result.containsVertex(new MetaVertex(rel.getToEntity(), MetaType.ENTITY))) {
			result.addVertex(new MetaVertex(rel.getToEntity(), rel.getToEntity(), MetaType.ENTITY, MetaAction.NONE,
					MetaExistence.NOT_FOUND,
					createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
							rel.getToEntity(), MetaType.ENTITY))));
		}
		result.addEdge(eVertex, new MetaVertex(rel.getToEntity(), MetaType.ENTITY));

	}

	/**
	 * Process nested entities.
	 *
	 * @param ness
	 *            the ness
	 * @param result
	 *            the result
	 * @param defaultStatus
	 *            the default status
	 * @param typesToProcess
	 *            the types to process
	 */
	private void processNestedEntities(List<NestedEntityDef> ness, MetaGraph result, MetaExistence defaultStatus,
			Set<MetaType> typesToProcess) {
		if (ness == null) {
			return;
		}
		for (NestedEntityDef nes : ness) {
			processNestedEntitie(nes, result, defaultStatus, typesToProcess);
		}
	}

	/**
	 * Process nested entitie.
	 *
	 * @param nes
	 *            the nes
	 * @param result
	 *            the result
	 * @param defaultStatus
	 *            the default status
	 * @param typesToProcess
	 *            the types to process
	 */
	private void processNestedEntitie(NestedEntityDef nes, MetaGraph result, MetaExistence defaultStatus,
			Set<MetaType> typesToProcess) {
		MetaVertex eVertex = new MetaVertex(nes.getName(), nes.getDisplayName(), MetaType.NESTED_ENTITY,
				defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus);
		// addVertex(result, eVertex);
		processComplexAttributes(result, eVertex, nes.getComplexAttribute(), typesToProcess);
		if (typesToProcess.contains(MetaType.SOURCE_SYSTEM)) {
			processMergeSettings(result, eVertex, nes.getMergeSettings());
		}
		if (typesToProcess.contains(MetaType.CLASSIFIER)) {
			processClsfs(result, eVertex, nes.getClassifiers());
		}
		if (typesToProcess.contains(MetaType.CF)) {
			processDQs(result, eVertex, nes.getDataQualities());
		}

		processSAs(result, eVertex, nes.getSimpleAttribute(), typesToProcess);
		List<ArrayAttributeDef> aas = nes.getArrayAttribute();
		processAAs(result, eVertex, aas, typesToProcess);
	}

	/**
	 * Process composite C fs.
	 *
	 * @param composite
	 *            the composite
	 * @param result
	 *            the result
	 * @param defaultStatus
	 *            the default status
	 */
	private void processCompositeCFs(List<CompositeCleanseFunctionDef> composite, MetaGraph result,
			MetaExistence defaultStatus) {
		for (CompositeCleanseFunctionDef value : composite) {
			processCompositeCF(value, result);
		}

	}

	/**
	 * Process entities.
	 *
	 * @param es
	 *            the es
	 * @param result
	 *            the result
	 * @param typesToProcess
	 *            the types to process
	 */
	private void processEntities(List<EntityDef> es, MetaGraph result, Set<MetaType> typesToProcess) {
		for (EntityDef e : es) {
			MetaVertex eVertex = new MetaVertex(e.getName(), MetaType.ENTITY);
			if (typesToProcess.contains(MetaType.CLASSIFIER)) {
				List<String> clsfs = e.getClassifiers();
				processClsfs(result, eVertex, clsfs);
			}
			if (typesToProcess.contains(MetaType.CF)) {
				List<DQRuleDef> dqrs = e.getDataQualities();
				processDQs(result, eVertex, dqrs);
			}

			List<SimpleAttributeDef> sas = e.getSimpleAttribute();
			processSAs(result, eVertex, sas, typesToProcess);
			List<ArrayAttributeDef> aas = e.getArrayAttribute();
			processAAs(result, eVertex, aas, typesToProcess);
			if (typesToProcess.contains(MetaType.SOURCE_SYSTEM)) {
				MergeSettingsDef mergeSettings = e.getMergeSettings();
				processMergeSettings(result, eVertex, mergeSettings);
			}
			List<ComplexAttributeDef> complexAttributes = e.getComplexAttribute();
			processComplexAttributes(result, eVertex, complexAttributes, typesToProcess);
			if (typesToProcess.contains(MetaType.GROUPS)) {
				processGroupName(result, eVertex, e.getGroupName());
			}
		}
	}

	/**
	 * Process complex attributes.
	 *
	 * @param result
	 *            the result
	 * @param eVertex
	 *            the e vertex
	 * @param complexAttributes
	 *            the complex attributes
	 * @param typesToProcess
	 *            the types to process
	 */
	private void processComplexAttributes(MetaGraph result, MetaVertex eVertex,
			List<ComplexAttributeDef> complexAttributes, Set<MetaType> typesToProcess) {
		if (complexAttributes == null || !typesToProcess.contains(MetaType.NESTED_ENTITY)) {
			return;
		}
		for (ComplexAttributeDef ca : complexAttributes) {
			if (!result.containsVertex(new MetaVertex(ca.getNestedEntityName(), MetaType.NESTED_ENTITY))) {
				result.addVertex(new MetaVertex(ca.getNestedEntityName(), ca.getNestedEntityName(),
						MetaType.NESTED_ENTITY, MetaAction.NONE, MetaExistence.NOT_FOUND,
						createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
								ca.getNestedEntityName(), MetaType.NESTED_ENTITY))));
			}
			result.addEdge(eVertex, new MetaVertex(ca.getNestedEntityName(), MetaType.NESTED_ENTITY));

		}

	}

	/**
	 * Process merge settings.
	 *
	 * @param result
	 *            the result
	 * @param eVertex
	 *            the e vertex
	 * @param mergeSettings
	 *            the merge settings
	 */
	private void processMergeSettings(MetaGraph result, MetaVertex eVertex, MergeSettingsDef mergeSettings) {
		if (mergeSettings == null) {
			return;
		}
		Set<String> sourceSystems = new HashSet<>();
		if (mergeSettings.getBvrSettings() != null
				&& mergeSettings.getBvrSettings().getSourceSystemsConfigs() != null) {
			mergeSettings.getBvrSettings().getSourceSystemsConfigs().stream().forEach(ss -> {
				sourceSystems.add(ss.getName());
			});
		}
		if (mergeSettings.getBvtSettings() != null && mergeSettings.getBvtSettings().getAttributes() != null) {
			mergeSettings.getBvtSettings().getAttributes().stream().forEach(v -> {
				v.getSourceSystemsConfigs().stream().forEach(ss -> {
					sourceSystems.add(ss.getName());
				});
			});
		}
		sourceSystems.stream().forEach(ss -> {
			if (!StringUtils.isEmpty(ss)) {
				if (!result.containsVertex(new MetaVertex(ss, MetaType.SOURCE_SYSTEM))) {
					result.addVertex(
							new MetaVertex(ss, ss, MetaType.SOURCE_SYSTEM, MetaAction.NONE, MetaExistence.NOT_FOUND,
									createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
											ss, MetaType.SOURCE_SYSTEM))));
				}
				MetaVertex targetVertex = new MetaVertex(ss, MetaType.SOURCE_SYSTEM);
				if (!result.containsEdge(eVertex, targetVertex)) {
					result.addEdge(eVertex, targetVertex);
				}
			}
		});

	}

	/**
	 * Process S as.
	 *
	 * @param result
	 *            the result
	 * @param sourceVertex
	 *            the source vertex
	 * @param sas
	 *            the sas
	 * @param typesToProcess
	 *            the types to process
	 */
	private void processSAs(MetaGraph result, MetaVertex sourceVertex, List<SimpleAttributeDef> sas,
			Set<MetaType> typesToProcess) {
		if (sas == null) {
			return;
		}
		for (SimpleAttributeDef sa : sas) {
			if (typesToProcess.contains(MetaType.MEASURE)) {
				AttributeMeasurementSettingsDef amsd = sa.getMeasureSettings();
				if (amsd != null && !StringUtils.isEmpty(amsd.getValueId())) {
					if (!result.containsVertex(new MetaVertex(amsd.getValueId(), MetaType.MEASURE))) {
						result.addVertex(new MetaVertex(amsd.getValueId(), amsd.getValueId(), MetaType.MEASURE,
								MetaAction.NONE, MetaExistence.NOT_FOUND,
								createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
										amsd.getValueId(), MetaType.MEASURE))));
					}
					MetaVertex targetVertex = new MetaVertex(amsd.getValueId(), MetaType.MEASURE);
					if (!result.containsEdge(sourceVertex, targetVertex)) {
						result.addEdge(sourceVertex, targetVertex);
					}
				}
			}
			if (typesToProcess.contains(MetaType.ENUM)) {
				String edt = sa.getEnumDataType();
				if (!StringUtils.isEmpty(edt)) {
					if (!result.containsVertex(new MetaVertex(edt, MetaType.ENUM))) {
						result.addVertex(new MetaVertex(edt, edt, MetaType.ENUM, MetaAction.NONE,
								MetaExistence.NOT_FOUND, createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
										edt, MetaType.ENUM))));
					}
					MetaVertex targetVertex = new MetaVertex(edt, MetaType.ENUM);
					if (!result.containsEdge(sourceVertex, targetVertex)) {
						result.addEdge(sourceVertex, targetVertex);
					}
				}
			}
			if (typesToProcess.contains(MetaType.LOOKUP)) {
				String let = sa.getLookupEntityType();
				if (!StringUtils.isEmpty(let)) {
					if (!result.containsVertex(new MetaVertex(let, MetaType.LOOKUP))) {
						result.addVertex(
								new MetaVertex(let, let, MetaType.LOOKUP, MetaAction.NONE, MetaExistence.NOT_FOUND,
										createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
												let, MetaType.LOOKUP))));
					}
					MetaVertex targetVertex = new MetaVertex(let, MetaType.LOOKUP);
					if (!result.containsEdge(sourceVertex, targetVertex)) {
						result.addEdge(sourceVertex, targetVertex);
					}
				}
			}
		}
	}

	/**
	 * Process A as.
	 *
	 * @param result
	 *            the result
	 * @param sourceVertex
	 *            the source vertex
	 * @param aas
	 *            the aas
	 * @param typesToProcess
	 *            the types to process
	 */
	private void processAAs(MetaGraph result, MetaVertex sourceVertex, List<ArrayAttributeDef> aas,
			Set<MetaType> typesToProcess) {
		if (aas == null) {
			return;
		}
		for (ArrayAttributeDef aa : aas) {

			if (typesToProcess.contains(MetaType.LOOKUP)) {
				String let = aa.getLookupEntityType();
				if (!StringUtils.isEmpty(let)) {
					if (!result.containsVertex(new MetaVertex(let, MetaType.LOOKUP))) {
						result.addVertex(
								new MetaVertex(let, let, MetaType.LOOKUP, MetaAction.NONE, MetaExistence.NOT_FOUND,
										createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
												let, MetaType.LOOKUP))));
					}
					MetaVertex targetVertex = new MetaVertex(let, MetaType.LOOKUP);
					if (!result.containsEdge(sourceVertex, targetVertex)) {
						result.addEdge(sourceVertex, targetVertex);
					}
				}
			}
		}
	}

	/**
	 * Process clsfs.
	 *
	 * @param result
	 *            the result
	 * @param eVertex
	 *            the e vertex
	 * @param clsfs
	 *            the clsfs
	 */
	private void processClsfs(MetaGraph result, MetaVertex eVertex, List<String> clsfs) {
		for (String clsfName : clsfs) {
			if (!result.containsVertex(new MetaVertex(clsfName, MetaType.CLASSIFIER))) {
				result.addVertex(new MetaVertex(clsfName, clsfName, MetaType.CLASSIFIER, MetaAction.NONE,
						MetaExistence.NOT_FOUND,
						createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
								clsfName, MetaType.CLASSIFIER))));
			}
			result.addEdge(eVertex, new MetaVertex(clsfName, MetaType.CLASSIFIER));
		}
	}

	/**
	 * Process D qs.
	 *
	 * @param result
	 *            the result
	 * @param eVertex
	 *            the e vertex
	 * @param dqrs
	 *            the dqrs
	 */
	private void processDQs(MetaGraph result, MetaVertex eVertex, List<DQRuleDef> dqrs) {
		for (DQRuleDef dqr : dqrs) {
			String functionName = dqr.getCleanseFunctionName();
			if (!result.containsVertex(new MetaVertex(functionName, MetaType.CUSTOM_CF))
					&&!result.containsVertex(new MetaVertex(functionName, MetaType.COMPOSITE_CF))) {
				result.addVertex(new MetaVertex(functionName, functionName, MetaType.CUSTOM_CF, MetaAction.NONE,
						MetaExistence.NOT_FOUND,
						createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
								functionName, MetaType.CUSTOM_CF))));

			}
			result.addEdge(eVertex, new MetaVertex(functionName, MetaType.CUSTOM_CF));
			List<String> sourceSystems = new ArrayList<>();
			if (dqr.getEnrich() != null && !StringUtils.isEmpty(dqr.getEnrich().getSourceSystem())) {
				sourceSystems.add(dqr.getEnrich().getSourceSystem());

			}

			if (dqr.getOrigins() != null && dqr.getOrigins().getSourceSystem() != null
					&& dqr.getOrigins().getSourceSystem().size() != 0) {
				List<DQRSourceSystemRef> dqssrs = dqr.getOrigins().getSourceSystem();
				for (DQRSourceSystemRef dqssr : dqssrs) {
					if (!StringUtils.isEmpty(dqssr.getName())) {
						sourceSystems.add(dqssr.getName());
					}
				}
			}
			for (String sourceSystem : sourceSystems) {
				if (!result.containsVertex(new MetaVertex(sourceSystem, MetaType.SOURCE_SYSTEM))) {
					result.addVertex(new MetaVertex(sourceSystem, sourceSystem, MetaType.SOURCE_SYSTEM, MetaAction.NONE,
							MetaExistence.NOT_FOUND,
							createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
									sourceSystem, MetaType.SOURCE_SYSTEM))));

				}
				result.addEdge(eVertex, new MetaVertex(sourceSystem, MetaType.SOURCE_SYSTEM));
			}
		}
	}

	/**
	 * Validate.
	 *
	 * @param metaGraph
	 *            the meta graph
	 * @return true, if successful
	 */
	private static boolean validate(MetaGraph metaGraph) {
		DirectedSimpleCycles<MetaVertex, MetaEdge<MetaVertex>> cycleFinder = new SzwarcfiterLauerSimpleCycles<MetaVertex, MetaEdge<MetaVertex>>(
				metaGraph);
		List<List<MetaVertex>> cycles = cycleFinder.findSimpleCycles();
		if (cycles.size() != 0) {
			throw new SystemRuntimeException("Cycles in metamodel not allowed",
					ExceptionId.EX_META_CANNOT_ASSEMBLE_MODEL, "");
		}
		ConnectivityInspector<MetaVertex, MetaEdge<MetaVertex>> connectivityInspector = new ConnectivityInspector<>(
				metaGraph);
		if (!connectivityInspector.isGraphConnected()) {
			// throw new SystemRuntimeException("", id, args)
		}
		return false;
	}

	/**
	 * Check duplicates.
	 *
	 * @param metaGraph
	 *            the meta graph
	 */
	private void checkDuplicates(MetaGraph metaGraph) {

		Set<MetaVertex> vertexes = metaGraph.vertexSet();
		if (vertexes == null) {
			return;
		}
		Map<String, List<MetaVertex>> nameMap = new HashMap<>();
		vertexes.stream().forEach(v -> {
			if (!nameMap.containsKey(v.getId())) {
				nameMap.put(v.getId(), new ArrayList<>());
			}
			//disable default functions
			if (v.getType() == MetaType.CUSTOM_CF && StringUtils.contains(v.getId(), ".")) {
				v.setStatus(MetaExistence.EXIST);
				v.setAction(MetaAction.NONE);
			}
			nameMap.get(v.getId()).add(v);
		});
		nameMap.forEach((k, v) -> {
			if (v.size() > 1) {
				Optional<MetaVertex> alreadyExist = v.stream().filter(el -> el.getStatus() == MetaExistence.EXIST)
						.findAny();
				v.stream().forEach(el -> {
					if (el.getStatus() != MetaExistence.EXIST && alreadyExist.isPresent()) {
						el.getMessages().add(createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE.getCode(),
								el.getId(), el.getType(), alreadyExist.get().getDisplayName(),
								alreadyExist.get().getType())));
					}
				});
			}
		});
	}

	/**
	 * Process lookup entities.
	 *
	 * @param les
	 *            the les
	 * @param result
	 *            the result
	 * @param typesToProcess
	 *            the types to process
	 */
	private void processLookupEntities(List<LookupEntityDef> les, MetaGraph result, Set<MetaType> typesToProcess) {
		for (LookupEntityDef le : les) {
			MetaVertex eVertex = new MetaVertex(le.getName(), MetaType.LOOKUP);
			if (typesToProcess.contains(MetaType.CLASSIFIER)) {
				List<String> clsfs = le.getClassifiers();
				processClsfs(result, eVertex, clsfs);
			}
			if (typesToProcess.contains(MetaType.CF)) {
				List<DQRuleDef> dqrs = le.getDataQualities();
				processDQs(result, eVertex, dqrs);
			}
			List<SimpleAttributeDef> sas = le.getSimpleAttribute();
			processSAs(result, eVertex, sas, typesToProcess);
			List<ArrayAttributeDef> aas = le.getArrayAttribute();
			processAAs(result, eVertex, aas, typesToProcess);
			MergeSettingsDef mergeSettings = le.getMergeSettings();
			if (typesToProcess.contains(MetaType.SOURCE_SYSTEM)) {
				processMergeSettings(result, eVertex, mergeSettings);
			}
			if (typesToProcess.contains(MetaType.GROUPS)) {
				processGroupName(result, eVertex, le.getGroupName());
			}
		}
	}

	/**
	 * Process group name.
	 *
	 * @param result
	 *            the result
	 * @param eVertex
	 *            the e vertex
	 * @param groupName
	 *            the group name
	 */
	private void processGroupName(MetaGraph result, MetaVertex eVertex, String groupName) {
		if (!result.containsVertex(new MetaVertex(groupName, MetaType.GROUPS))) {
			result.addVertex(new MetaVertex(groupName, groupName, MetaType.GROUPS, MetaAction.NONE,
					MetaExistence.NOT_FOUND, createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
							groupName, MetaType.GROUPS))));

		}
		result.addEdge(eVertex, new MetaVertex(groupName, MetaType.GROUPS));

	}

	/**
	 * Parses list of cleanse functions.
	 *
	 * @param node
	 *            cf group
	 * @param path
	 *            full path to cf including groups
	 * @param result
	 *            the result graph
	 * @param defaultStatus
	 *            the default status
	 * @param composite
	 *            the composite
	 */
	private void processCFs(CleanseFunctionGroupDef node, String path, MetaGraph result, MetaExistence defaultStatus,
			List<CompositeCleanseFunctionDef> composite) {
		// first execution path will be null
		if (node == null) {
			return;
		}
		if (path == null) {
			path = "";
		}
		List<?> list = node.getGroupOrCleanseFunctionOrCompositeCleanseFunction();
		// iterate over tree nodes
		for (Object object : list) {
			// if cleanse function group found just add group name to path
			if (object instanceof CleanseFunctionGroupDef) {
				CleanseFunctionGroupDef group = (CleanseFunctionGroupDef) object;
				processCFs(group,
						StringUtils.isEmpty(path) ? group.getGroupName()
								: String.join(PATH_DELIMETER, path, group.getGroupName()),
						result, defaultStatus, composite);
			} else if (object instanceof CompositeCleanseFunctionDef) {
				CompositeCleanseFunctionDef def = (CompositeCleanseFunctionDef) object;
				composite.add(def);
				if (def.getLogic() != null) {
					String id = StringUtils.isEmpty(path) ? def.getFunctionName()
							: String.join(PATH_DELIMETER, path, def.getFunctionName());
					addVertex(result, new MetaVertex(id, def.getFunctionName(), MetaType.COMPOSITE_CF,
							defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));
				}
			} else if (object instanceof CleanseFunctionDef) {
				CleanseFunctionDef def = (CleanseFunctionDef) object;
				if (def.getJavaClass() != null) {
					String id = StringUtils.isEmpty(path) ? def.getFunctionName()
							: String.join(PATH_DELIMETER, path, def.getFunctionName());
					addVertex(result, new MetaVertex(id, def.getFunctionName(), MetaType.CUSTOM_CF,
							defaultStatus == MetaExistence.NEW ? MetaAction.UPSERT : MetaAction.NONE, defaultStatus));
				}
			}
		}
	}

	/**
	 * Adds the vertex.
	 *
	 * @param graph
	 *            the graph
	 * @param vertex
	 *            the vertex
	 */
	private void addVertex(MetaGraph graph, MetaVertex vertex) {
		// update already existing vertex
		if (graph.containsVertex(vertex)) {
			// not optimal
			MetaVertex toModify = graph.vertexSet().stream().filter(v -> v.equals(vertex)).findFirst().get();
			if (vertex.getType() != toModify.getType()&&toModify.getStatus()!=MetaExistence.NOT_FOUND) {
			    vertex.getMessages().add(createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_DUPLICATE.getCode(),
                        vertex.getId(), vertex.getType(), toModify.getDisplayName(),
                        toModify.getType())));
			}
			toModify.setAction(vertex.getAction());
			toModify.setMessages(vertex.getMessages());
			toModify.setStatus(
					(toModify.getStatus() == MetaExistence.EXIST || toModify.getStatus() == MetaExistence.UPDATE)
							? MetaExistence.UPDATE : vertex.getStatus());
			toModify.setDisplayName(vertex.getDisplayName());
		} else {
			graph.addVertex(vertex);
		}
	}

	/**
	 * Process composite CF.
	 *
	 * @param node
	 *            the node
	 * @param result
	 *            the result
	 */
	private void processCompositeCF(CompositeCleanseFunctionDef node, MetaGraph result) {
		List<Node> nodes = node.getLogic().getNodes();
		for (Node node2 : nodes) {
			if (node2.getNodeType() != CompositeCleanseFunctionNodeType.FUNCTION) {
				continue;
			}
			if (!result.containsVertex(new MetaVertex(node2.getFunctionName(), MetaType.CUSTOM_CF))) {
				result.addVertex(new MetaVertex(node2.getFunctionName(), node2.getFunctionName(), MetaType.CUSTOM_CF,
						MetaAction.NONE, MetaExistence.NOT_FOUND,
						createErrorMessage(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_EL_NOT_FOUND.getCode(),
								node2.getFunctionName(), MetaType.CUSTOM_CF))));
			}
			result.addEdge(new MetaVertex(node.getFunctionName(), MetaType.COMPOSITE_CF),
					new MetaVertex(node2.getFunctionName(), MetaType.CUSTOM_CF));
		}
	}

	/**
	 * Creates the error message.
	 *
	 * @param messages
	 *            the messages
	 * @return the meta message
	 */
	private static MetaMessage createErrorMessage(String... messages) {
		return new MetaMessage().withMessage(messages).withStatus(MetaStatus.ERROR);
	}
}
