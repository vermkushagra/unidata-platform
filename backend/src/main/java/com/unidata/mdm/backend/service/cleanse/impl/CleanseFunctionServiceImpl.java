/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.cleanse.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.graph.DirectedMultigraph;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.ITopic;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFApply;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFCustomUploaderResponse;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFCustomUploaderResponse.CFFunction;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFSaveStatus;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFState;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFUploadAction;
import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionInputParam;
import com.unidata.mdm.backend.common.cleanse.CleanseFunctionOutputParam;
import com.unidata.mdm.backend.common.context.CleanseFunctionContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.runtime.MeasurementPoint;
import com.unidata.mdm.backend.common.service.MetaDraftServiceExt;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DateSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimeSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampSimpleAttributeImpl;
import com.unidata.mdm.backend.dao.SystemElementsDao;
import com.unidata.mdm.backend.po.initializer.ActionTypePO;
import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.po.initializer.SystemElementPO;
import com.unidata.mdm.backend.service.cleanse.CFListener;
import com.unidata.mdm.backend.service.cleanse.CleanseFunctionServiceExt;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionParam;
import com.unidata.mdm.backend.service.model.MetaDependencyService;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;
import com.unidata.mdm.backend.service.model.util.wrappers.CleanseFunctionWrapper;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.JarUtils;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;
import com.unidata.mdm.meta.CompositeCleanseFunctionNodeType;
import com.unidata.mdm.meta.ConstantValueDef;

/**
 * Cleanse functions service. Contains methods for cleanse functions
 * management(crud), validation, execute, etc
 *
 * @author ilya.bykov
 */
@Component
public class CleanseFunctionServiceImpl implements CleanseFunctionServiceExt {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(CleanseFunctionServiceImpl.class);
	/**
	 * The meta model dao.
	 */
	@Autowired
	private MetaModelServiceExt metaModelService;

	/** The functions cache. */
	private Map<String, Map<String, CleanseFunctionWrapper>> functionsCache = new HashMap<>();

	/** The system elements dao. */
	@Autowired(required = false)
	private SystemElementsDao systemElementsDao;

	/** The meta draft service. */
	@Autowired
	private MetaDraftServiceExt metaDraftService;

	/** The hazelcast instance. */
	@Autowired
	private HazelcastInstance hazelcastInstance;

	/** The dependency service. */
	@Autowired
	private MetaDependencyService dependencyService;
	/** The topic. */
	private ITopic<String> topic = null;

	/** The temp names. */
	private IMap<String, String> tempNames = null;

	private static final String CF_CALL_CATEGORY = "CF-CALL";
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CleanseFunctionGroupDef getAll() {
		return metaModelService.getCleanseFunctionRootGroup();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CleanseFunctionExtendedDef getFunctionInfoById(String pathID) {
		CleanseFunctionWrapper wrapper = metaModelService.getValueById(pathID, CleanseFunctionWrapper.class);
		return wrapper == null ? null : wrapper.getCleanseFunctionDef();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAvailable(String cleanseFunctionName) {
		CleanseFunctionWrapper wrapper = metaModelService.getValueById(cleanseFunctionName,
				CleanseFunctionWrapper.class);
		return Objects.nonNull(wrapper);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(CleanseFunctionContext cfc) {

	    // 1. Get fn
        CleanseFunctionWrapper wrapper = metaModelService.getValueById(cfc.getCleanseFunctionName(),
                CleanseFunctionWrapper.class);
        if (Objects.isNull(wrapper)) {
            final String message = "Cleanse function '{}' not found in the meta model!";
            LOGGER.warn(message, message, cfc.getCleanseFunctionName());
            throw new CleanseFunctionExecutionException(message,
                    ExceptionId.EX_DQ_CLEANSE_FUNCTION_NOT_FOUND,
                    cfc.getCleanseFunctionName());
        }

		MeasurementPoint.start();
		try {

			if (wrapper.isComposite()) {
				executeComposite(wrapper, cfc);
			} else {
				executeSingle(wrapper, cfc);
			}

		} catch (CleanseFunctionExecutionException cfe) {
			throw cfe;
		} catch (Exception e) {
			final String message = "Unknown exception caught while executing cleanse function [{}]";
			LOGGER.warn(message, cfc.getCleanseFunctionName(), e);
			throw new CleanseFunctionExecutionException(message, e, ExceptionId.EX_DQ_CLEANSE_FUNCTION_EXCEPTION_CAUGHT,
					cfc.getCleanseFunctionName());
		} finally {
		    MeasurementPoint.stop();
		}
	}

	/**
	 * Executes simple single function.
	 *
	 * @param wrapper            the function wrapper.
	 * @param cfc the cfc
	 * @throws Exception the exception
	 */
	@SuppressWarnings("deprecation")
	private void executeSingle(CleanseFunctionWrapper wrapper, CleanseFunctionContext cfc) throws Exception {

	    CleanseFunction function = wrapper.getCleanseFunction();
	    MeasurementPoint.start(CF_CALL_CATEGORY, StringUtils.substringAfterLast(function.getClass().getName(), "."));
	    try {

	        if (function.isContextAware()) {
    			function.execute(cfc);
    		} else {
    			convertFromLegacyOutput(cfc, function.execute(convertToLegacyInput(cfc)));
    		}

	    } finally {
            MeasurementPoint.stop();
        }
	}

	/**
	 * Executes composite function.
	 *
	 * @param wrapper            the function wrapper
	 * @param cfc the cfc
	 */
	private void executeComposite(CleanseFunctionWrapper wrapper, CleanseFunctionContext cfc) {

	    MeasurementPoint.start();
	    try {

    	    // We remember the last local context, being executed,
    	    // to check it for paths and errors by output processing in DQSI.
    	    // We do not collect intermediate paths and errors, because validation errors are created
    	    // in DQSI later according to output params in the context
    	    // so the connection between validation error and failed paths is lost already for intermediate.
    	    CleanseFunctionContext local = null;

    		DirectedMultigraph<Node, NodeLink> graph = wrapper.getMdagRep();
    		TopologicalOrderIterator<Node, NodeLink> iterator = new TopologicalOrderIterator<>(graph);
    		Map<NodeLink, CompositeFunctionParam> registers = new IdentityHashMap<>();
    		while (iterator.hasNext()) {


    			Node vertex = iterator.next();
    			if (CompositeCleanseFunctionNodeType.INPUT_PORTS == vertex.getNodeType()) {

    				// Put input values to interim result map.
    				Set<NodeLink> nodeLinks = graph.outgoingEdgesOf(vertex);
    				for (NodeLink nodeLink : nodeLinks) {
    					registers.put(nodeLink,
    							new CompositeFunctionParam(cfc.getInputParamByPortName(nodeLink.getFromPort())));
    				}

    			} else if (CompositeCleanseFunctionNodeType.OUTPUT_PORTS == vertex.getNodeType()) {

    				// Construct cleanse function output
    				Set<NodeLink> nodeLinks = graph.incomingEdgesOf(vertex);
    				for (NodeLink nodeLink : nodeLinks) {

    					CompositeFunctionParam cfp = registers.get(nodeLink);
    					if (cfp != null) {
    						cfc.putOutputParam(cfp.toOutputParam(nodeLink.getToPort()));
    					}
    				}

    				if (Objects.nonNull(local)) {
    				    cfc.failedValidations().addAll(local.failedValidations());
    				    cfc.errors().addAll(local.errors());
    				}

    			} else if (CompositeCleanseFunctionNodeType.FUNCTION == vertex.getNodeType()) {
    				boolean isEmpty = true;
    				// Execute cleanse function
    				List<CleanseFunctionInputParam> params = new ArrayList<>();
    				Set<NodeLink> incomingNodeLinks = graph.incomingEdgesOf(vertex);
    				for (NodeLink nodeLink : incomingNodeLinks) {

    					CompositeFunctionParam cfp = registers.get(nodeLink);
    					if (cfp != null) {
    						params.add(cfp.toInputParam(nodeLink.getToPort()));
    						isEmpty = false;
    					}
    				}
    				if (!isEmpty) {
    					local = CleanseFunctionContext.builder(cfc).cleanseFunctionName(vertex.getFunctionName())
    							.input(params).build();

    					execute(local);

    					Set<NodeLink> outgoingNodeLinks = graph.outgoingEdgesOf(vertex);
    					for (NodeLink nodeLink : outgoingNodeLinks) {
    						registers.put(nodeLink,
    								new CompositeFunctionParam(local.getOutputParamByPortName(nodeLink.getFromPort())));
    					}
    				}
    			} else if (CompositeCleanseFunctionNodeType.CONSTANT == vertex.getNodeType()) {
    				Set<NodeLink> nodeLinks = graph.outgoingEdgesOf(vertex);
    				for (NodeLink nodeLink : nodeLinks) {
    					registers.put(nodeLink,
    							new CompositeFunctionParam(ofConstantValue(vertex.getValue(), nodeLink.getToPort())));
    				}
    			} else if (CompositeCleanseFunctionNodeType.IFTHENELSE == vertex.getNodeType()) {
    				Set<NodeLink> incoming = graph.incomingEdgesOf(vertex);
    				Set<NodeLink> outgoing = graph.outgoingEdgesOf(vertex);
    				NodeLink condition = incoming.stream()
    						.filter(n -> n.getToPortType() == CompositeCleanseFunctionNodeType.CONDITION).findFirst().get();
    				Set<NodeLink> incomingParams = incoming.stream()
    						.filter(n -> n.getToPortType() == CompositeCleanseFunctionNodeType.INPUT)
    						.collect(Collectors.toSet());


    				if(((BooleanSimpleAttributeImpl)registers.get(condition).getAttributes().stream().findFirst().get()).getValue()) {
    					Set<NodeLink> outTrueParams = outgoing.stream()
    							.filter(n -> n.getFromPortType() == CompositeCleanseFunctionNodeType.OUTPUT_TRUE)
    							.collect(Collectors.toSet());
    					for (NodeLink nodeLink : outTrueParams) {
    						registers.put(nodeLink,
    								new CompositeFunctionParam(registers
    										.get(incomingParams.stream()
    												.filter(n -> StringUtils.equals(n.getToPort(),
    														nodeLink.getFromPort()))
    												.findFirst().get())
    										.toOutputParam(nodeLink.getFromPort())));
    					}
    				}else {
    					Set<NodeLink> outFalseParams = outgoing.stream()
    							.filter(n -> n.getFromPortType() == CompositeCleanseFunctionNodeType.OUTPUT_FALSE)
    							.collect(Collectors.toSet());
    					for (NodeLink nodeLink : outFalseParams) {
    						registers.put(nodeLink,
    								new CompositeFunctionParam(registers
    										.get(incomingParams.stream()
    												.filter(n -> StringUtils.equals(n.getToPort(),
    														nodeLink.getFromPort()))
    												.findFirst().get())
    										.toOutputParam(nodeLink.getFromPort())));
    					}
    				}
    			}
    		}

	    } finally {
            MeasurementPoint.stop();
        }
	}

	/**
	 * Of constant value.
	 *
	 * @param cvd the cvd
	 * @param attributeName the attribute name
	 * @return the cleanse function input param
	 */
	private CleanseFunctionInputParam ofConstantValue(ConstantValueDef cvd, String attributeName) {

		if (cvd.getType() == null) {
			return null;
		}

		SimpleAttribute<?> attribute = null;
		switch (cvd.getType()) {
		case BOOLEAN:
			attribute = new BooleanSimpleAttributeImpl(attributeName, cvd.isBoolValue());
			break;
		case DATE:
			attribute = new DateSimpleAttributeImpl(attributeName, cvd.getDateValue());
			break;
		case INTEGER:
			attribute = new IntegerSimpleAttributeImpl(attributeName, cvd.getIntValue());
			break;
		case NUMBER:
			attribute = new NumberSimpleAttributeImpl(attributeName, cvd.getNumberValue());
			break;
		case STRING:
			attribute = new StringSimpleAttributeImpl(attributeName, cvd.getStringValue());
			break;
		case TIME:
			attribute = new TimeSimpleAttributeImpl(attributeName, cvd.getTimeValue());
			break;
		case TIMESTAMP:
			attribute = new TimestampSimpleAttributeImpl(attributeName, cvd.getTimestampValue());
			break;
		default:
			break;
		}

		if (Objects.isNull(attribute)) {
			return null;
		}

		return CleanseFunctionInputParam.of(attributeName, Collections.singletonList(attribute));
	}

	/**
	 * Converts to legacy input map.
	 *
	 * @param cfc
	 *            the context
	 * @return map
	 */
	private Map<String, Object> convertToLegacyInput(CleanseFunctionContext cfc) {

		Map<String, Object> input = new HashMap<>();
		for (CleanseFunctionInputParam ip : cfc.input()) {

			Object value;
			if (ip.isSingleton()) {
				value = ip.getSingleton();
			} else {
				value = ip.isEmpty() ? null : ip.getAttributes().iterator().next();
			}

			input.put(ip.getPortName(), value);
		}

		return input;
	}

	/**
	 * Convert from legacy output.
	 *
	 * @param cfc the cfc
	 * @param legacy the legacy
	 */
	@SuppressWarnings("unchecked")
	private void convertFromLegacyOutput(CleanseFunctionContext cfc, Map<String, Object> legacy) {

		if (MapUtils.isEmpty(legacy)) {
			return;
		}

		for (Entry<String, Object> portValue : legacy.entrySet()) {

			Object value = portValue.getValue();
			if (value instanceof Collection) {

				Collection<?> cast = (Collection<?>) value;
				if (CollectionUtils.isNotEmpty(cast)) {
					Object inner = cast.iterator().next();
					if (inner instanceof Attribute) {
						cfc.putOutputParam(CleanseFunctionOutputParam.of(portValue.getKey(), (Attribute) inner));
					} else if (inner instanceof DataRecord) {
						cfc.putOutputParam(
								CleanseFunctionOutputParam.of(portValue.getKey(), (Collection<DataRecord>) inner));
					}
				}
			} else if (value instanceof Attribute) {
				cfc.putOutputParam(CleanseFunctionOutputParam.of(portValue.getKey(), (Attribute) value));
			}
			// TODO cover other simple types + array values
			// We don't have such CFs AFAIK
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void upsertCompositeCleanseFunction(String pathID, CompositeCleanseFunctionDef compositeCleanseFunctionDef) {

		removeFunctionById(pathID);
		// clean up as much as possible (since composite functions are stored on the
		// first level only, we check only this level)
		if (metaModelService.getValueById(pathID, CleanseFunctionWrapper.class) != null) {
			metaModelService.removeValueById(pathID, CleanseFunctionWrapper.class);
		}

		metaModelService.getCleanseFunctionRootGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction()
				.remove(new CompositeCleanseFunctionDef() {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean equals(Object obj) {
						if (obj instanceof CompositeCleanseFunctionDef) {
							return ((CompositeCleanseFunctionDef) obj).getFunctionName().equals(pathID);
						}
						return super.equals(obj);
					}
				});
		metaModelService.getCleanseFunctionRootGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction()
				.add(compositeCleanseFunctionDef);

		CleanseFunctionWrapper cleanseFunctionWrapper = new CleanseFunctionWrapper(compositeCleanseFunctionDef);
		metaModelService.putValue(pathID, cleanseFunctionWrapper, CleanseFunctionWrapper.class);
		// save model with new function
		UpdateModelRequestContext ctx = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
				.cleanseFunctionsUpdate(metaModelService.getCleanseFunctionRootGroup()).build();
		metaModelService.upsertModel(ctx);
//		metaDraftService.update(ctx);
	}

	/**
	 * Validate jar.
	 *
	 * @param temporaryId
	 *            the temporary id
	 * @param list
	 *            the list
	 */
	private void applyFunction(String temporaryId, List<CFApply> list) {
		for (CFApply entry : list) {
			CFUploadAction action = entry.getAction();
			String name = entry.getName();
			if (CFUploadAction.CREATE.equals(action)) {
				if (metaModelService.getValueById(name, CleanseFunctionWrapper.class) != null) {
					metaModelService.removeValueById(name, CleanseFunctionWrapper.class);
				}
				removeFunctionById(temporaryId);
				metaModelService.getCleanseFunctionRootGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction()
						.remove(new CleanseFunctionExtendedDef() {
							private static final long serialVersionUID = 1L;

							@Override
							public boolean equals(Object obj) {
								if (obj instanceof CleanseFunctionExtendedDef) {
									return ((CleanseFunctionExtendedDef) obj).getFunctionName().equals(name);
								}
								return super.equals(obj);
							}
						});
				CleanseFunctionWrapper cleanseFunctionWrapper = functionsCache.get(temporaryId).get(name);
				metaModelService.getCleanseFunctionRootGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction()
						.add(cleanseFunctionWrapper.getCleanseFunctionDef());

				metaModelService.putValue(name, cleanseFunctionWrapper, CleanseFunctionWrapper.class);
			} else if (CFUploadAction.OVERWRITE.equals(action)) {
				if (metaModelService.getValueById(name, CleanseFunctionWrapper.class) != null) {
					metaModelService.removeValueById(name, CleanseFunctionWrapper.class);
				}
				removeFunctionById(temporaryId);
				CleanseFunctionWrapper cleanseFunctionWrapper = functionsCache.get(temporaryId).get(name);
				metaModelService.getCleanseFunctionRootGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction()
						.add(cleanseFunctionWrapper.getCleanseFunctionDef());
				metaModelService.putValue(name, cleanseFunctionWrapper, CleanseFunctionWrapper.class);
			} else if (CFUploadAction.DO_NOTHING.equals(action)) {
				//
			}

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CFCustomUploaderResponse preloadAndValidateCustomFunction(Path jarFile, boolean saveToDb) {
		CFCustomUploaderResponse response = new CFCustomUploaderResponse();
		try {
			Entry<String, Map<String, CleanseFunctionWrapper>> cleanseFunctions = loadCustomFunctions(jarFile);		
			functionsCache.put(cleanseFunctions.getKey(), cleanseFunctions.getValue());
			tempNames.put(cleanseFunctions.getKey(), jarFile.toString(), 5, TimeUnit.MINUTES);
			findDuplicates(cleanseFunctions, response);
			if (saveToDb) {
				String functionClass = extractJavaClass(cleanseFunctions);
				systemElementsDao.deleteByClassName(functionClass);
				systemElementsDao.create(new SystemElementPO()
						.withAction(ActionTypePO.CREATE)
						.withType(ElementTypePO.CUSTOM_CF)
						.withChecksum("")
						.withClassName(functionClass)
						.withContent(Files.readAllBytes(jarFile))
						.withDescription("")
						.withFolder(jarFile.getParent().toString())
						.withName(jarFile.getFileName().toString())
						.withCreatedAt(new Date())
						.withCreatedBy(SecurityUtils.getCurrentUserName()));}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
			LOGGER.error("Error occured while trying to preload cleanse functions from jar file:", e);
			response.setStatus(CFSaveStatus.ERROR);
		}
		return response;
	}

	/**
	 * Extract java class.
	 *
	 * @param cleanseFunctions the cleanse functions
	 * @return  Cleanse function class.
	 */
	private String extractJavaClass(Entry<String, Map<String, CleanseFunctionWrapper>> cleanseFunctions) {
		String functionClass = cleanseFunctions
				.getValue().values()
					.stream()
					.findFirst()
					.orElse(null)
					.getCleanseFunction().getClass().getName();
		return functionClass;
	}

	/**
	 * Find duplicates.
	 *
	 * @param cleanseFunctions
	 *            the cleanse functions
	 * @param uploaderResponse
	 *            the uploader response
	 */
	private void findDuplicates(Entry<String, Map<String, CleanseFunctionWrapper>> cleanseFunctions,
			CFCustomUploaderResponse uploaderResponse) {
		CFFunction function = uploaderResponse.new CFFunction();
		cleanseFunctions.getValue().values().forEach(cleanse -> {

			function.setName(cleanse.getCleanseFunctionDef().getFunctionName());
			if (metaModelService.getValueById(cleanse.getCleanseFunctionDef().getFunctionName(),
					CleanseFunctionWrapper.class) != null) {
				function.setState(CFState.DUPLICATE);
			} else {
				function.setState(CFState.NEW);
			}
			uploaderResponse.addFunction(function);
		});
		uploaderResponse.setTemporaryId(cleanseFunctions.getKey());
		uploaderResponse.setStatus(CFSaveStatus.SUCCESS);
	}

	/**
	 * Load custom functions.
	 *
	 * @param pathToJar            the jarfile
	 * @return the entry
	 * @throws InstantiationException             the instantiation exception
	 * @throws IllegalAccessException             the illegal access exception
	 * @throws IOException             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException             the class not found exception
	 */
	private Entry<String, Map<String, CleanseFunctionWrapper>> loadCustomFunctions(Path pathToJar)
			throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {

		List<Class<CleanseFunction>> cleanseFunctions = JarUtils.findClassesInJar(CleanseFunction.class,
				pathToJar.toString());
		Map<String, CleanseFunctionWrapper> functions = new HashMap<>();
		cleanseFunctions.forEach(cf -> {
			CleanseFunctionWrapper cleanseFunctionWrapper = new CleanseFunctionWrapper(cf);
			functions.put(cleanseFunctionWrapper.getId(), cleanseFunctionWrapper);
		});

		return new AbstractMap.SimpleImmutableEntry<>(pathToJar.getFileName().toString(), functions);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeFunctionById(String pathID) {
		Map<String, CleanseFunctionWrapper> wrappers = functionsCache.get(pathID);
		if (wrappers == null) {
			return;
		}
		wrappers.forEach((name, wrapper) -> {
			if (metaModelService.getValueById(name, CleanseFunctionWrapper.class) != null) {
				metaModelService.removeValueById(name, CleanseFunctionWrapper.class);
				List<Serializable> list = metaModelService.getCleanseFunctionRootGroup()
						.getGroupOrCleanseFunctionOrCompositeCleanseFunction();
				for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
					Object object = iterator.next();
					if (object instanceof CleanseFunctionDef) {
						CleanseFunctionDef def = (CleanseFunctionDef) object;
						if (StringUtils.equals(def.getFunctionName(), name)) {
							iterator.remove();
						}
					}

				}
			}
		});

	}

	/**
	 * Init custom cleanse functions.
	 */
	@Override
	public void afterContextRefresh() {
		topic = hazelcastInstance.getTopic("cleanseTopic");
		topic.addMessageListener(new CFListener(this, hazelcastInstance.getCluster().getLocalMember().getUuid()));
		tempNames = hazelcastInstance.getMap("tempPaths");
		String cfFolder = System.getProperty("catalina.base") + File.separator + JarUtils.UNIDATA_INTEGRATION
				+ File.separator + "custom_cf" + File.separator;

		try {
			List<SystemElementPO> result = loadFiles(cfFolder);
			migrateOldFunctions(result);
		} catch (Exception e) {
			LOGGER.warn("Caught Exception: ", e);
		}

		File folder = new File(Paths.get(cfFolder).toUri());
		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (File file : files) {
			initCfFromFile(file);
		}
	}
	/**
	 * Migrate old cleanse functions and delete duplicates.
	 * TODO: this is temporary method and it must be removed!
	 * @param systemElements
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void migrateOldFunctions(List<SystemElementPO> systemElements)
			throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
		Map<Integer, String> toUpdate = new HashMap<>();
		boolean migrationNeeded = false;
		for (SystemElementPO systemElement : systemElements) {
			if (StringUtils.isEmpty(systemElement.getClassName())) {
				Entry<String, Map<String, CleanseFunctionWrapper>> cleanseFunctions = loadCustomFunctions(
						Paths.get(systemElement.getFolder() + File.separator + systemElement.getName()));
				toUpdate.put(systemElement.getId(), extractJavaClass(cleanseFunctions));
				migrationNeeded = true;
			}
		}
		if (migrationNeeded) {
			systemElementsDao.removeOldFunctions(toUpdate);
			afterContextRefresh();
		}
	}

	/**
	 * Load custom cf from file.
	 *
	 * @param file
	 *            the file to load
	 */
	private void initCfFromFile(File file) {
		if (StringUtils.endsWith(file.getName(), ".jar") && !StringUtils.startsWith(file.getName(), "unidata-")) {
			CFCustomUploaderResponse preloadResponse = preloadAndValidateCustomFunction(file.toPath(), false);
			if (preloadResponse.getStatus() == CFSaveStatus.SUCCESS) {
				List<CFFunction> functions = preloadResponse.getFunctions();
				if (functions == null) {
					return;
				}
				functions.stream().forEach(cf -> {
					CFApply apply = new CFApply();
					apply.setAction(CFUploadAction.CREATE);
					apply.setName(cf.getName());
					try {
						applyFunction(preloadResponse.getTemporaryId(), Collections.singletonList(apply));
					} catch (Exception e) {
						LOGGER.error("Unable to load cleanse functions from jar file: {} Error: {}",
								file.getAbsolutePath(), e);
					}
				});
			} else {
				LOGGER.error("Unable to load cleanse functions from jar file: {} ", file.getAbsolutePath());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendInitSignal(String tempId) {
		topic.publish(tempId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadAndInit(String tempId) throws IOException {
		SystemElementPO elementPO = systemElementsDao.getByNameAndPath(
				Paths.get(tempNames.get(tempId)).getFileName().toString(), Paths.get(System.getProperty("catalina.base")
						+ File.separator + JarUtils.UNIDATA_INTEGRATION + File.separator + "custom_cf").toString());

		try (FileOutputStream fileOuputStream = new FileOutputStream(
				elementPO.getFolder() + File.separator + elementPO.getName())) {
			fileOuputStream.write(elementPO.getContent());
		}

		initCfFromFile(new File(elementPO.getFolder() + File.separator + elementPO.getName()));
	}

	/**
	 * Load files.
	 *
	 * @param cfFolder            the cf folder
	 * @return the list
	 * @throws IOException             Signals that an I/O exception has occurred.
	 */
	private List<SystemElementPO> loadFiles(String cfFolder) throws IOException {
		final Path dir = Paths.get(cfFolder);
		if (Files.exists(dir) && Files.isDirectory(dir)) {

			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					if (exc == null) {
						Files.delete(dir);
						return FileVisitResult.CONTINUE;
					} else {
						throw exc;
					}
				}

			});
		}
		Files.createDirectories(dir);
		final List<SystemElementPO> elementPOs = systemElementsDao.getByPathAndTypes(dir.toString(),
				ElementTypePO.CUSTOM_CF);
		if (elementPOs == null || elementPOs.size() == 0) {
			return Collections.emptyList();
		}
		List<SystemElementPO> result = new ArrayList<>();
		for (final SystemElementPO el : elementPOs) {
			try (final FileOutputStream fileOuputStream = new FileOutputStream(
					cfFolder + File.separator + el.getName())) {
				fileOuputStream.write(el.getContent());
				result.add(el);
			}
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see com.unidata.mdm.backend.common.service.CleanseFunctionService#deleteFunction(java.lang.String)
	 */
	@Override
	public void deleteFunction(String name) {
		if (metaModelService.getValueById(name, CleanseFunctionWrapper.class) != null) {
			preValidateDelete(name);
			metaModelService.removeValueById(name, CleanseFunctionWrapper.class);
			functionsCache.forEach((k, v) -> {
				if (v.get(name) != null) {
					systemElementsDao.deleteByNameAndTypes(k, ElementTypePO.CUSTOM_CF);
				}
			});

			List<Serializable> list = metaModelService.getCleanseFunctionRootGroup()
					.getGroupOrCleanseFunctionOrCompositeCleanseFunction();
			for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
				Object object = iterator.next();
				if (object instanceof CleanseFunctionDef) {
					CleanseFunctionDef def = (CleanseFunctionDef) object;
					if (StringUtils.equals(def.getFunctionName(), name)) {
						iterator.remove();
					}
				}

			}
		}
		UpdateModelRequestContext ctx = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
				.cleanseFunctionsUpdate(metaModelService.getCleanseFunctionRootGroup()).build();
		metaModelService.upsertModel(ctx);

	}


	/**
	 * Pre validate delete.
	 *
	 * @param name the name
	 */
	private void preValidateDelete(String name) {
		Set<MetaType> toCheck = new HashSet<>();
		toCheck.add(MetaType.CF);
		toCheck.add(MetaType.COMPOSITE_CF);
		toCheck.add(MetaType.CUSTOM_CF);
		toCheck.add(MetaType.ENTITY);
		toCheck.add(MetaType.LOOKUP);
		toCheck.add(MetaType.NESTED_ENTITY);
		Set<MetaType> toSkip = new HashSet<>();
		MetaGraph metaGraph = dependencyService.calculateDependencies(null, toCheck, toSkip);
		Set<MetaVertex> metaVertexs = metaGraph.vertexSet();
		for (MetaVertex metaVertex : metaVertexs) {
			if (StringUtils.equals(name, metaVertex.getId()) && (metaVertex.getType() == MetaType.CF
					|| metaVertex.getType() == MetaType.CUSTOM_CF || metaVertex.getType() == MetaType.COMPOSITE_CF)) {
				Set<MetaEdge<MetaVertex>> incoming = metaGraph.incomingEdgesOf(metaVertex);
				if (incoming != null && incoming.size() != 0) {
					String deps = "";
					for (MetaEdge<MetaVertex> in : incoming) {
						deps = deps + "\n name: " + in.getFrom().getId() + " type: " + in.getFrom().getType().name();
					}
					throw new BusinessException(
							"Unable to delete cleanse function. Dependencies must be resolved first {}",
							ExceptionId.EX_SYSTEM_CLEANSE_DELETE_FAILED, deps);
				}
			}
		}
	}

	@Override
	public void importCompositeCleanseFunctions(List<CompositeCleanseFunctionDef> ccfs) {
		if (CollectionUtils.isEmpty(ccfs)) {
			return;
		}
		for (CompositeCleanseFunctionDef ccf : ccfs) {
			removeFunctionById(ccf.getFunctionName());
			metaModelService.getCleanseFunctionRootGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction()
					.add(ccf);

			CleanseFunctionWrapper cleanseFunctionWrapper = new CleanseFunctionWrapper(ccf);
			metaModelService.putValue(ccf.getFunctionName(), cleanseFunctionWrapper, CleanseFunctionWrapper.class);
		}
		// save model with new function
		UpdateModelRequestContext ctx = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
				.cleanseFunctionsUpdate(metaModelService.getCleanseFunctionRootGroup()).build();
		metaModelService.upsertModel(ctx);
	}


}
