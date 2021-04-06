package com.unidata.mdm.backend.service.cleanse.impl;

import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.engine.DocumentMissingException;
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
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.service.MetaDraftService;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.dao.SystemElementsDao;
import com.unidata.mdm.backend.po.initializer.ActionTypePO;
import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.po.initializer.SystemElementPO;
import com.unidata.mdm.backend.service.cleanse.CFListener;
import com.unidata.mdm.backend.service.cleanse.CFUtils;
import com.unidata.mdm.backend.service.cleanse.CleanseFunctionServiceExt;
import com.unidata.mdm.backend.service.cleanse.composite.CompositeFunctionMDAGRep;
import com.unidata.mdm.backend.service.model.MetaDependencyService;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdge;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.JarUtils;
import com.unidata.mdm.cleanse.common.CleanseFunctionWrapper;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.Node;
import com.unidata.mdm.meta.CompositeCleanseFunctionLogic.NodeLink;
import com.unidata.mdm.meta.CompositeCleanseFunctionNodeType;

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
	/** The meta model dao. */
	@Autowired(required=false)
	private MetaModelServiceExt metaModelService;

	/** The functions cache. */
	private Map<String, Map<String, CleanseFunctionWrapper>> functionsCache = new HashMap<String, Map<String, CleanseFunctionWrapper>>();

	/** The system elements dao. */
	@Autowired(required=false)
	private SystemElementsDao systemElementsDao;
	@Autowired
	private MetaDraftService metaDraftService;

	/** The hazelcast instance. */
	@Autowired
	private HazelcastInstance hazelcastInstance;
	@Autowired
	private MetaDependencyService dependencyService;

	/** The topic. */
	private ITopic<String> topic = null;

	/** The temp names. */
	private IMap<String, String> tempNames = null;

	/**
     * {@inheritDoc}
     */
	@Override
    public Map<String, Object> executeSingle(Map<String, Object> input, String pathID)
			throws CleanseFunctionExecutionException {
		if (metaModelService.getValueById(pathID, CleanseFunctionWrapper.class) == null) {
			throw new CleanseFunctionExecutionException(pathID, "Cleanse function " + pathID + " not defined!");
		}

		Map<String, Object> result = null;
		try {
			CleanseFunctionWrapper wrapper = metaModelService.getValueById(pathID, CleanseFunctionWrapper.class);
			CleanseFunctionExtendedDef def = wrapper.getCleanseFunctionDef();
			if (def instanceof CompositeCleanseFunctionDef) {
				result = executeComposite(input, wrapper.getMdagRep());
			} else {
				result = wrapper.getCleanseFunction().execute(input);
			}
		} catch (CleanseFunctionExecutionException cleanseFunctionException) {
			throw cleanseFunctionException;
		} catch (Exception e) {
			// TODO hack! Remove this.
			if (!(e instanceof DocumentMissingException)) {
				LOGGER.error("Error while execute cleanse function", e);
				throw new CleanseFunctionExecutionException(pathID, e, "Exception while execute cleanse function.");
			}
		}
		return result;
	}

	/**
	 * Execute composite cleanse function.
	 *
	 *
	 * @param input
	 *            the input
	 * @param mdagRep
	 *            the mdag rep
	 * @return the map
	 * @throws CleanseFunctionExecutionException
	 *             the cleanse function exception
	 */
	private Map<String, Object> executeComposite(Map<String, Object> input, CompositeFunctionMDAGRep mdagRep)
			throws CleanseFunctionExecutionException {
		Map<String, Object> result = new HashMap<>();
		// Topologically sort composite cleanse function graph
		TopologicalOrderIterator<Node, NodeLink> iterator = CFUtils.topologicalIterator(mdagRep);
		Map<NodeLink, SimpleAttribute<?>> interimResult = new HashMap<>();
		while (iterator.hasNext()) {
			Node vertex = iterator.next();
			if (CompositeCleanseFunctionNodeType.INPUT_PORTS.equals(vertex.getNodeType())) {
				// Put input values to interim result map.
				Set<NodeLink> nodeLinks = mdagRep.outgoingEdgesOf(vertex);
				for (NodeLink nodeLink : nodeLinks) {
					interimResult.put(nodeLink, (SimpleAttribute<?>) input.get(nodeLink.getFromPort()));
				}

			} else if (CompositeCleanseFunctionNodeType.OUTPUT_PORTS.equals(vertex.getNodeType())) {
				// Construct cleanse function output
				Set<NodeLink> nodeLinks = mdagRep.incomingEdgesOf(vertex);
				for (NodeLink nodeLink : nodeLinks) {
					result.put(nodeLink.getToPort(), interimResult.get(nodeLink));
				}
			} else if (CompositeCleanseFunctionNodeType.FUNCTION.equals(vertex.getNodeType())) {
				// Execute cleanse function
				Map<String, Object> toExecute = new HashMap<String, Object>();
				Set<NodeLink> incomingNodeLinks = mdagRep.incomingEdgesOf(vertex);
				for (NodeLink nodeLink : incomingNodeLinks) {
					toExecute.put(nodeLink.getToPort(), interimResult.get(nodeLink));
				}
				Map<String, Object> fromExecute = executeSingle(toExecute, vertex.getFunctionName());
				Set<NodeLink> outgoingNodeLinks = mdagRep.outgoingEdgesOf(vertex);
				for (NodeLink nodeLink : outgoingNodeLinks) {
					interimResult.put(nodeLink, (SimpleAttribute<?>) fromExecute.get(nodeLink.getFromPort()));
				}
			}

		}
		return result;

	}

	/**
     * {@inheritDoc}
     */
	@Override
    public CleanseFunctionGroupDef getAll() throws CleanseFunctionExecutionException {
		return metaModelService.getCleanseFunctionRootGroup();
	}

	/**
     * {@inheritDoc}
     */
	@Override
    public CleanseFunctionExtendedDef getByID(String pathID) {
		CleanseFunctionWrapper wrapper = metaModelService.getValueById(pathID, CleanseFunctionWrapper.class);
		return wrapper == null ? null : wrapper.getCleanseFunctionDef();
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public void upsertCompositeCleanseFunction(String pathID, CompositeCleanseFunctionDef compositeCleanseFunctionDef)
            throws CleanseFunctionExecutionException {
        removeFunctionById(pathID);
        // clean up as much as possible (since composite functions are stored on the first level only, we check only this level)
        if (metaModelService.getValueById(pathID, CleanseFunctionWrapper.class) != null) {
            metaModelService.removeValueById(pathID, CleanseFunctionWrapper.class);
        }

        metaModelService.getCleanseFunctionRootGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction().remove(
                new CompositeCleanseFunctionDef() {
					private static final long serialVersionUID = 1L;

					@Override
                    public boolean equals(Object obj) {
                        if (obj instanceof  CompositeCleanseFunctionDef) {
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
        metaDraftService.update(ctx);
    }

	/**
	 * Validate jar.
	 *
	 * @param temporaryId
	 *            the temporary id
	 * @param list
	 *            the list
	 * @return true, if successful
	 * @throws CleanseFunctionExecutionException
	 *             the cleanse function exception
	 */
    private void applyFunction(String temporaryId, List<CFApply> list) throws CleanseFunctionExecutionException {
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
			    UpdateModelRequestContext ctx = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
			                .cleanseFunctionsUpdate(metaModelService.getCleanseFunctionRootGroup()).build();
			    metaDraftService.update(ctx);
			} else if (CFUploadAction.OVERWRITE.equals(action)) {
				if (metaModelService.getValueById(name, CleanseFunctionWrapper.class) != null) {
					metaModelService.removeValueById(name, CleanseFunctionWrapper.class);
				}
				removeFunctionById(temporaryId);
				CleanseFunctionWrapper cleanseFunctionWrapper = functionsCache.get(temporaryId).get(name);
				metaModelService.getCleanseFunctionRootGroup().getGroupOrCleanseFunctionOrCompositeCleanseFunction()
						.add(cleanseFunctionWrapper.getCleanseFunctionDef());
				metaModelService.putValue(name, cleanseFunctionWrapper, CleanseFunctionWrapper.class);
			    UpdateModelRequestContext ctx = new UpdateModelRequestContext.UpdateModelRequestContextBuilder()
			                .cleanseFunctionsUpdate(metaModelService.getCleanseFunctionRootGroup()).build();
			    metaDraftService.update(ctx);
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
				systemElementsDao.deleteByNameAndPath(jarFile.getFileName().toString(), jarFile.getParent().toString());
				systemElementsDao.create(new SystemElementPO().withAction(ActionTypePO.CREATE)
						.withType(ElementTypePO.CUSTOM_CF).withChecksum("").withContent(Files.readAllBytes(jarFile))
						.withCreatedAt(new Date()).withDescription("").withFolder(jarFile.getParent().toString())
						.withName(jarFile.getFileName().toString()).withCreatedBy(SecurityUtils.getCurrentUserName()));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
			LOGGER.error("Error occured while trying to preload cleanse functions from jar file:", e);
			response.setStatus(CFSaveStatus.ERROR);
		}
		return response;
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
	 * @param pathToJar
	 *            the jarfile
	 * @return the entry
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	private Entry<String, Map<String, CleanseFunctionWrapper>> loadCustomFunctions(Path pathToJar)
			throws FileNotFoundException, InstantiationException, IllegalAccessException, IOException,
			ClassNotFoundException {

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
    public void removeFunctionById(String pathID) throws CleanseFunctionExecutionException {
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
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Override
	public void afterContextRefresh() {

		topic = hazelcastInstance.getTopic("cleanseTopic");
		
		topic.addMessageListener(new CFListener(this, hazelcastInstance.getCluster().getLocalMember().getUuid()));
		tempNames = hazelcastInstance.getMap("tempPaths");
		String cfFolder = System.getProperty("catalina.base") + File.separator + JarUtils.UNIDATA_INTEGRATION
				+ File.separator + "custom_cf" + File.separator;

		try {
		    loadFiles(cfFolder);
		} catch (IOException ioe) {
		    LOGGER.warn("Caught IOException: ", ioe);
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
		SystemElementPO elementPO = systemElementsDao.getByNameAndPath(Paths.get(tempNames.get(tempId)).getFileName().toString(),
				Paths.get(System.getProperty("catalina.base") + File.separator + JarUtils.UNIDATA_INTEGRATION
						+ File.separator + "custom_cf").toString());

		FileOutputStream fileOuputStream = null;
		try {
			fileOuputStream = new FileOutputStream(elementPO.getFolder() + File.separator + elementPO.getName());
			fileOuputStream.write(elementPO.getContent());
		} finally {
			fileOuputStream.close();
		}
		initCfFromFile(new File(elementPO.getFolder() + File.separator + elementPO.getName()));
	}

	/**
	 * Load files.
	 *
	 * @param cfFolder
	 *            the cf folder
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
    private void loadFiles(String cfFolder) throws IOException {
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
            return;
        }
        for (final SystemElementPO el : elementPOs) {
            try (final FileOutputStream fileOuputStream = new FileOutputStream(cfFolder + File.separator + el.getName())) {
                fileOuputStream.write(el.getContent());
            }
        }
    }
    @Override
    public void deleteFunction(String name) {
    	if (metaModelService.getValueById(name, CleanseFunctionWrapper.class) != null) {
    		preValidateDelete(name);
			metaModelService.removeValueById(name, CleanseFunctionWrapper.class);
			functionsCache.forEach((k,v)->{
				if(v.get(name)!=null) {
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
						deps = deps+"\n name: "+in.getFrom().getId()+" type: "+in.getFrom().getType().name();
					}
					throw new BusinessException("Unable to delete cleanse function. Dependencies must be resolved first {}", ExceptionId.EX_SYSTEM_CLEANSE_DELETE_FAILED, deps) ;
				}
			}
		}
	}
}
