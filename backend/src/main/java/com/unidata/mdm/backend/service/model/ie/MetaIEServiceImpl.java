package com.unidata.mdm.backend.service.model.ie;

import static com.unidata.mdm.backend.service.measurement.MeasurementValueXmlConverter.convert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jgrapht.EdgeFactory;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph.CycleFoundException;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.io.ByteStreams;
import com.unidata.mdm.backend.api.rest.dto.XmlClassifierWrapper;
import com.unidata.mdm.backend.api.rest.dto.cleanse.CFCustomUploaderResponse;
import com.unidata.mdm.backend.common.cleanse.CleanseFunction;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext;
import com.unidata.mdm.backend.common.context.SaveLargeObjectRequestContext.SaveLargeObjectRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpsertType;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext;
import com.unidata.mdm.backend.common.context.UpsertUserEventRequestContext.UpsertUserEventRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.security.UserEventDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.CleanseFunctionExecutionException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.SystemRuntimeException;
import com.unidata.mdm.backend.common.service.DataRecordsService;
import com.unidata.mdm.backend.common.service.MetaDraftService;
import com.unidata.mdm.backend.dao.SystemElementsDao;
import com.unidata.mdm.backend.po.initializer.ElementTypePO;
import com.unidata.mdm.backend.service.classifier.ClsfService;
import com.unidata.mdm.backend.service.cleanse.CleanseFunctionServiceExt;
import com.unidata.mdm.backend.service.configuration.ConfigurationHolder;
import com.unidata.mdm.backend.service.configuration.ConfigurationService;
import com.unidata.mdm.backend.service.maintenance.MaintenanceService;
import com.unidata.mdm.backend.service.maintenance.dto.SystemMode;
import com.unidata.mdm.backend.service.maintenance.dto.SystemMode.ModeEnum;
import com.unidata.mdm.backend.service.matching.MatchingGroupsService;
import com.unidata.mdm.backend.service.matching.MatchingMetaFacadeService;
import com.unidata.mdm.backend.service.matching.MatchingRulesService;
import com.unidata.mdm.backend.service.matching.data.MatchingGroup;
import com.unidata.mdm.backend.service.matching.data.MatchingKey;
import com.unidata.mdm.backend.service.matching.data.MatchingRule;
import com.unidata.mdm.backend.service.matching.data.MatchingUserSettings;
import com.unidata.mdm.backend.service.measurement.MeasurementValueXmlConverter;
import com.unidata.mdm.backend.service.measurement.MetaMeasurementService;
import com.unidata.mdm.backend.service.measurement.data.MeasurementValue;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.ie.dto.FullModelDTO;
import com.unidata.mdm.backend.service.model.ie.dto.MetaAction;
import com.unidata.mdm.backend.service.model.ie.dto.MetaEdgeFactory;
import com.unidata.mdm.backend.service.model.ie.dto.MetaExistence;
import com.unidata.mdm.backend.service.model.ie.dto.MetaGraph;
import com.unidata.mdm.backend.service.model.ie.dto.MetaType;
import com.unidata.mdm.backend.service.model.ie.dto.MetaVertex;
import com.unidata.mdm.backend.service.model.util.ModelUtils;
import com.unidata.mdm.backend.service.registration.RegistrationService;
import com.unidata.mdm.backend.service.security.UserService;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.JarUtils;
import com.unidata.mdm.backend.util.JaxbUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;
import com.unidata.mdm.classifier.ClassifierDef;
import com.unidata.mdm.classifier.FullClassifierDef;
import com.unidata.mdm.conf.WorkflowProcessDefinition;
import com.unidata.mdm.meta.CleanseFunctionDef;
import com.unidata.mdm.meta.CleanseFunctionExtendedDef;
import com.unidata.mdm.meta.CleanseFunctionGroupDef;
import com.unidata.mdm.meta.CompositeCleanseFunctionDef;
import com.unidata.mdm.meta.DefaultClassifier;
import com.unidata.mdm.meta.EntitiesGroupDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.EnumerationDataType;
import com.unidata.mdm.meta.ListOfCleanseFunctions;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.MeasurementValueDef;
import com.unidata.mdm.meta.MeasurementValues;
import com.unidata.mdm.meta.Model;
import com.unidata.mdm.meta.NestedEntityDef;
import com.unidata.mdm.meta.RelationDef;
import com.unidata.mdm.meta.SourceSystemDef;

/**
 * The Class MetaIEServiceImpl.
 */
@Component
public class MetaIEServiceImpl implements MetaIEService {

	/** The cleanse function service. */
	@Autowired
	private CleanseFunctionServiceExt cleanseFunctionService;

	/** The matching rules service. */
	@Autowired
	private MatchingRulesService matchingRulesService;

	/** The matching groups service. */
	@Autowired
	private MatchingGroupsService matchingGroupsService;

	/** The meta measurement service. */
	@Autowired
	private MetaMeasurementService metaMeasurementService;

	/** The conversion service. */
	@Autowired
	private ConversionService conversionService;

	/** The clsf service. */
	@Autowired
	private ClsfService clsfService;

	/** The user service. */
	@Autowired
	private UserService userService;

	/** The data record service. */
	@Autowired
	private DataRecordsService dataRecordService;
	/** The model service. */
	@Autowired
	private MetaModelServiceExt modelService;

	/** The maintenance service. */
	@Autowired
	private MaintenanceService maintenanceService;

	/** The graph creator. */
	@Autowired
	private GraphCreator graphCreator;

	/** The configuration service. */
	@Autowired
	private ConfigurationService configurationService;

	/** The matching meta facade service. */
	@Autowired
	private MatchingMetaFacadeService matchingMetaFacadeService;
	/** Meta draft service. */
	@Autowired
	private MetaDraftService metaDraftService;


	/**
	 * Registration service.
	 */
	@Autowired
	private RegistrationService registrationService;

	/** The system elements DAO. */
	@Autowired
	private SystemElementsDao systemElementsDAO;

	/** The tx manager. */
	// TODO: temporary
	@Autowired
	@Qualifier("transactionManager")
	private PlatformTransactionManager txManager;
	/** The classifiers. */
	private static final String CLASSIFIERS = "classifiers";

	/** The matching. */
	private static final String MATCHING = "matching";

	/** The cf folder. */
	private static final String CF_FOLDER = "custom_cf";

	/** The measure. */
	private static final String MEASURE = "measure";

	/** The Constant INTEGRATION. */
	private static final String INTEGRATION = "integration";

	/** The Constant BPM. */
	private static final String BPM = "bpm";

	/** The Constant CONFIG. */
	private static final String CONFIG = "config";

	/** The catalina base. */
	private static final String CATALINA_BASE = "catalina.base";

	/** The temp. */
	private static final String TEMP = "temp";

	/** The to export folder. */
	private static final String TO_EXPORT_FOLDER = "to_export";

	/** The to import folder. */
	private static final String TO_IMPORT_FOLDER = "to_import";

	/** The xml extension. */
	private static final String XML_EXTENSION = ".xml";

	/** The jar extension. */
	private static final String JAR_EXTENSION = ".jar";

	/** The model. */
	private static final String MODEL = "model";

	/** The Constant MEASUREMENT_VALUE_QNAME. */
	private static final QName MEASUREMENT_VALUE_QNAME = new QName("http://meta.mdm.unidata.com/", "MeasurementValues",
			"measurementValues");

	/** The to import. */
	private Path toImport = Paths.get(System.getProperty(CATALINA_BASE) + File.separator + TEMP + File.separator
			+ TO_IMPORT_FOLDER + File.separator);

	/** The to export. */
	private Path toExport = Paths.get(System.getProperty(CATALINA_BASE) + File.separator + TEMP + File.separator
			+ TO_EXPORT_FOLDER + File.separator);

	/** The unidata integration. */
	private Path unidataIntegration = Paths
			.get(System.getProperty(CATALINA_BASE) + File.separator + "unidata-integration");

	/** The to load. */
	private Map<String, FullModelDTO> toLoad = new HashMap<>();

	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(MetaIEServiceImpl.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.unidata.mdm.backend.service.model.ie.MetaIEService#preloadMetaZip(
	 * java.nio.file.Path)
	 */
	@Override
	public MetaGraph preloadMetaZip(Path zipFile, boolean isOverride) {
		String id = IdUtils.v4String();
		String fileName = zipFile.getFileName().toString();
		MetaGraph result = new MetaGraph(new MetaEdgeFactory());
		result.setOverride(isOverride);
		result.setFileName(fileName);
		result.setId(id);
		// if old model shouldn't be preserved no need to collect dependences
		if (!isOverride) {
			FullModelDTO fromRuntime = null;
			try {
				fromRuntime = gatherFromRuntime(null);
			} catch (CleanseFunctionExecutionException e1) {
				// shouldn't occur
				LOGGER.error("Invalid cleanse function", e1);
			}
			graphCreator.enrich(fromRuntime, result, MetaExistence.EXIST, MetaType.values());
		}
		try {
			FullModelDTO fromZip = gatherFromZip(zipFile, isOverride);
			graphCreator.enrich(fromZip, result, MetaExistence.NEW, MetaType.values());
			toLoad.put(id, fromZip);
		} catch (BusinessException e) {
			throw e;
		} catch (SystemRuntimeException e) {
			throw e;
		} catch (ConversionFailedException e) {
			// hack for business exception throwed from spring converter.
			throw e.getCause() != null && e.getCause() instanceof BusinessException ? (BusinessException) e.getCause()
					: new SystemRuntimeException("Unable to parse zip file.", e,
							ExceptionId.EX_META_IMPORT_MODEL_UNABLE_TO_PARSE, fileName);
		} catch (Exception e) {
			throw new SystemRuntimeException("Unable to parse zip file.", e,
					ExceptionId.EX_META_IMPORT_MODEL_UNABLE_TO_PARSE, fileName);

		}

		return result;
	}

	/**
	 * Gather from runtime.
	 *
	 * @param storageId
	 *            the storage id
	 * @return the full model DTO
	 * @throws CleanseFunctionExecutionException
	 *             the cleanse function execution exception
	 */
	private FullModelDTO gatherFromRuntime(String storageId) throws CleanseFunctionExecutionException {
		FullModelDTO result = new FullModelDTO().withMatchingSettings(assembleMatching(storageId))
				.withClsfs(assembleClassifiers(storageId)).withMeasurementValues(assembleMeasurement(storageId))
				.withModel(assembleModel(storageId)).withCleanseFunctions(cleanseFunctionService.getAll());
		return result;
	}

	/**
	 * Gather from zip.
	 *
	 * @param zipFile
	 *            the zip file
	 * @param isOverride
	 *            the is override
	 * @return the full model DTO
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws JAXBException
	 *             the JAXB exception
	 */
	private FullModelDTO gatherFromZip(Path zipFile, boolean isOverride) throws IOException, JAXBException {
		FullModelDTO result = new FullModelDTO();
		Path rootFolder = ZipUtils.unzipDir(zipFile, Paths.get(toImport.toString(), MODEL));

		try {
			validateArchiveStructure(rootFolder, zipFile.getFileName().toString());
			Path modelFolder = Paths.get(rootFolder.toString(), MODEL);
			if (Files.exists(modelFolder)) {
				try (Stream<Path> stream = Files.list(modelFolder)) {
					Optional<Path> modelFile = stream.findAny();

					if (modelFile.isPresent()) {
						try (InputStream is = new FileInputStream(modelFile.get().toFile())) {
							Model model = JaxbUtils.createModelFromInputStream(is);
							result.withModel(model);
							result.withCleanseFunctions(enrichDefaultFunctions(model.getCleanseFunctions()).getGroup());
						}
					} else if (isOverride) {
						Model model = createDefaultModel();
						result.withModel(model);
						result.withCleanseFunctions(enrichDefaultFunctions(model.getCleanseFunctions()).getGroup());
					}
				}

			} else if (isOverride) {
				Model model = createDefaultModel();
				result.withModel(model);
				result.withCleanseFunctions(enrichDefaultFunctions(model.getCleanseFunctions()).getGroup());
			}
			Path measurementFolder = Paths.get(rootFolder.toString(), MEASURE);
			if (Files.exists(measurementFolder)) {
				try (Stream<Path> stream = Files.list(measurementFolder)) {
					Optional<Path> measurementFile = stream.findAny();
					if (measurementFile.isPresent()) {
						try (InputStream is = new FileInputStream(measurementFile.get().toFile())) {
							MeasurementValues measurementValues = JaxbUtils.createMeasurementValuesFromInputStream(is);
							result.withMeasurementValues(measurementValues);
						}
					}
				}
			}
			Path matchingFolder = Paths.get(rootFolder.toString(), MATCHING);
			if (Files.exists(matchingFolder)) {
				try (Stream<Path> stream = Files.list(matchingFolder)) {
					Optional<Path> matchingFile = stream.findAny();
                    if (matchingFile.isPresent()) {
                        MatchingUserSettings matchingUserSettings = conversionService.convert(matchingFile.get(),
                                MatchingUserSettings.class);
					result.withMatchingSettings(matchingUserSettings);

					}
				}
			}
			if (Paths.get(rootFolder.toString(), INTEGRATION).toFile().exists()) {
				if (Paths.get(rootFolder.toString(), INTEGRATION, CF_FOLDER).toFile().exists()) {
					try (Stream<Path> paths = Files.walk(Paths.get(rootFolder.toString(), INTEGRATION, CF_FOLDER))) {
						Map<String, ByteBuffer> map = new HashMap<>();
						result.withCustomCfs(map);
						paths.forEach(filePath -> {
							if (Files.isRegularFile(filePath)
									&& (StringUtils.endsWithIgnoreCase(filePath.toString(), JAR_EXTENSION))) {
								try (InputStream is = new FileInputStream(filePath.toFile())) {
									List<Class<CleanseFunction>> funcs = JarUtils
											.findClassesInJar(CleanseFunction.class, filePath.toString());
									if (funcs != null) {
										for (Class<CleanseFunction> func : funcs) {
											CleanseFunction cleanseFunction = func.newInstance();
											map.put(cleanseFunction.getDefinition().getFunctionName(),
													ByteBuffer.wrap(ByteStreams.toByteArray(is)));
										}
									}
								} catch (BusinessException e) {
									throw e;
								} catch (SystemRuntimeException e) {
									throw e;
								} catch (Throwable e) {
									throw new SystemRuntimeException("Unable to parse zip file.", e,
											ExceptionId.EX_META_IMPORT_MODEL_UNABLE_TO_PARSE,
											zipFile.getFileName().toString());
								}
							}
						});

					}
				}
			}
			if (Paths.get(rootFolder.toString(), CLASSIFIERS).toFile().exists()) {
				List<ClsfDTO> classifierDefs = new ArrayList<>();
				result.withClsfs(classifierDefs);
				List<FullClassifierDef> clsfsToImport = new ArrayList<>();
				result.withClsfToImport(clsfsToImport);
				try (Stream<Path> paths = Files.walk(Paths.get(rootFolder.toString(), CLASSIFIERS))) {
					paths.forEach(filePath -> {
						if (Files.isRegularFile(filePath)
								&& (StringUtils.endsWithIgnoreCase(filePath.toString(), XML_EXTENSION))) {
							try (InputStream is = new FileInputStream(filePath.toFile())) {
								FullClassifierDef toSave = JaxbUtils.createClassifierFromInputStream(is);
								clsfsToImport.add(toSave);
								ClsfDTO clsf = new ClsfDTO();
								clsf.setCodePattern(toSave.getClassifier().getCodePattern());
								clsf.setCreatedAt(new Date());
								clsf.setCreatedBy(SecurityUtils.getCurrentUserName());
								clsf.setDescription(toSave.getClassifier().getDescription());
								clsf.setDisplayName(toSave.getClassifier().getDisplayName());
								clsf.setName(toSave.getClassifier().getName());
								classifierDefs.add(clsf);
							} catch (FileNotFoundException e) {
								throw new SystemRuntimeException("Unable to parse zip file.", e,
										ExceptionId.EX_META_IMPORT_MODEL_UNABLE_TO_PARSE,
										zipFile.getFileName().toString());
							} catch (IOException e) {
								throw new SystemRuntimeException("Unable to parse zip file.", e,
										ExceptionId.EX_META_IMPORT_MODEL_UNABLE_TO_PARSE,
										zipFile.getFileName().toString());
							} catch (JAXBException e) {
								throw new SystemRuntimeException("Unable to parse zip file.", e,
										ExceptionId.EX_META_IMPORT_MODEL_UNABLE_TO_PARSE,
										zipFile.getFileName().toString());
							}
						}
					});
				}

			}
			if (result.getModel() != null) {
				List<DefaultClassifier> clResult = result.getModel().getDefaultClassifiers().stream()
						.filter(e -> !result.getClsfsToImport().stream().map(FullClassifierDef::getClassifier)
								.map(ClassifierDef::getName).collect(Collectors.toSet()).contains(e.getName()))
						.collect(Collectors.toList());
				for (DefaultClassifier defaultClassifier : clResult) {
					FullClassifierDef fcls = new FullClassifierDef();
					ClassifierDef cls = new ClassifierDef();
					cls.setName(defaultClassifier.getName());
					cls.setDisplayName(defaultClassifier.getDisplayName());
					cls.setDescription(defaultClassifier.getDescription());
					cls.setCodePattern(defaultClassifier.getCodePattern());
					fcls.setClassifier(cls);
					result.getClsfsToImport().add(fcls);
					ClsfDTO e = new ClsfDTO();
					e.setName(defaultClassifier.getName());
					e.setDisplayName(defaultClassifier.getDisplayName());
					e.setDescription(defaultClassifier.getDescription());
					e.setCodePattern(defaultClassifier.getCodePattern());
					result.getClsfs().add(e);
				}
			}
		} finally {
			try {
				Files.walkFileTree(rootFolder, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.deleteIfExists(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.deleteIfExists(dir);
						return FileVisitResult.CONTINUE;
					}
				});
				Files.deleteIfExists(zipFile);
			} catch (IOException e) {
				LOGGER.error("Unable to delete temporary metamodel export files", e);

			}
		}
		// TODO: CF pre validation(custom)
		return result;
	}

	/**
	 * Creates the default model.
	 *
	 * @return the model
	 */
	private Model createDefaultModel() {
		return new Model()
				.withCleanseFunctions(
						new ListOfCleanseFunctions().withGroup(ModelUtils.createDefaultCleanseFunctionGroup()))
				.withSourceSystems(Collections.singletonList(ModelUtils.createDefaultSourceSystem()))
				.withEntitiesGroup(ModelUtils.createDefaultEntitiesGroup());

	}

	/**
	 * Enrich list with default functions.
	 *
	 * @param listOfCleanseFunctions
	 *            list with default functions.
	 * @return list with default functions.
	 */
	private ListOfCleanseFunctions enrichDefaultFunctions(ListOfCleanseFunctions listOfCleanseFunctions) {
		CleanseFunctionGroupDef result = ModelUtils.createDefaultCleanseFunctionGroup();
		if (listOfCleanseFunctions == null || listOfCleanseFunctions.getGroup() == null) {
			return new ListOfCleanseFunctions().withGroup(result);
		}
		List<Serializable> list = listOfCleanseFunctions.getGroup()
				.getGroupOrCleanseFunctionOrCompositeCleanseFunction();
		for (Serializable obj : list) {
			if (obj instanceof CompositeCleanseFunctionDef) {
				result.getGroupOrCleanseFunctionOrCompositeCleanseFunction().add(obj);
			} else if (obj instanceof CleanseFunctionDef) {
				result.getGroupOrCleanseFunctionOrCompositeCleanseFunction().add(obj);
			} else if (obj instanceof CleanseFunctionExtendedDef) {
				result.getGroupOrCleanseFunctionOrCompositeCleanseFunction().add(obj);
			}
		}

		return new ListOfCleanseFunctions().withGroup(result);
	}

	/**
	 * Validate archive structure.
	 *
	 * @param root
	 *            the root
	 * @param fileName
	 *            the file name
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void validateArchiveStructure(Path root, String fileName) throws IOException {
		Set<Path> toCheck = new HashSet<>();
		List<String> messages = new ArrayList<>();
		Path modelFolder = Paths.get(root.toString(), MODEL);
		toCheck.add(modelFolder);
		Path classifiersFolder = Paths.get(root.toString(), CLASSIFIERS);
		toCheck.add(classifiersFolder);
		Path cfFolder = Paths.get(root.toString(), INTEGRATION, CF_FOLDER);
		toCheck.add(cfFolder);
		Path measureFolder = Paths.get(root.toString(), MEASURE);
		toCheck.add(measureFolder);
		Path matchingFolder = Paths.get(root.toString(), MATCHING);
		toCheck.add(matchingFolder);
		Path integrationFolder = Paths.get(root.toString(), INTEGRATION);
		toCheck.add(integrationFolder);
		Path bpmFolder = Paths.get(root.toString(), BPM);
		toCheck.add(bpmFolder);
		Path configFolder = Paths.get(root.toString(), CONFIG);
		toCheck.add(configFolder);
		// root
		try (Stream<Path> stream = Files.list(root)) {
			stream.forEach(el -> {
				if (!toCheck.contains(el)) {
					messages.add(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_FILE_UNKNOWN_ONLY_DIRS.getCode(),
							StringUtils.substringAfter(el.toString(), root.toString()),
							""));
				}
			});
		}
		// model
		checkFolder(root, messages, modelFolder);
		// measure
		checkFolder(root, messages, measureFolder);
		// matching
		checkFolder(root, messages, matchingFolder);
		// classifiers
		if (Files.exists(classifiersFolder)) {
			checkExtension(root, messages, classifiersFolder, XML_EXTENSION);

		}
		// custom cfs
		if (Files.exists(cfFolder)) {
			checkExtension(root, messages, cfFolder, JAR_EXTENSION);
		}
		if (messages.size() > 0) {
			throw new BusinessException("File structure invalid.",
					ExceptionId.EX_META_IMPORT_MODEL_FILE_STRUCTURE_INVALID, fileName,
					"\n" + messages.stream().collect(Collectors.joining("\n")));
		}
	}

	/**
	 * Check folder.
	 *
	 * @param root
	 *            the root
	 * @param messages
	 *            the messages
	 * @param toCheck
	 *            the to check
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void checkFolder(Path root, List<String> messages, Path toCheck) throws IOException {
		if (Files.exists(toCheck)) {
			checkExtension(root, messages, toCheck, XML_EXTENSION);
			try (Stream<Path> stream = Files.list(toCheck)) {
				List<Path> list = stream.collect(Collectors.toList());
				if (list.size() > 1) {
					messages.add(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_FILE_DUPL_NOT_ALLOWED.getCode(),
							StringUtils.substringAfter(toCheck.toString(), root.toString()),
							list.size()));
				}
			}
		}
	}

	/**
	 * Check extension.
	 *
	 * @param root
	 *            the root
	 * @param messages
	 *            the messages
	 * @param toCheck
	 *            the to check
	 * @param extension
	 *            the extension
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void checkExtension(Path root, List<String> messages, Path toCheck, String extension) throws IOException {
		try (Stream<Path> stream = Files.list(toCheck)) {
			stream.forEach(me -> {
				if (!StringUtils.endsWith(me.toString(), extension)) {
					messages.add(MessageUtils.getMessage(ExceptionId.EX_META_IMPORT_MODEL_FILE_UNKNOWN.getCode(),
							StringUtils.substringAfter(me.toString(), root.toString()),
							StringUtils.substringAfter(toCheck.toString(), root.toString()),
							extension));
				}

			});
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.unidata.mdm.backend.service.model.ie.MetaIEService#importMetaZip(com.
	 * unidata.mdm.backend.service.model.ie.dto.MetaGraph)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public MetaGraph importMetaZip(MetaGraph graph) {
		// transfer unidata to maintenance mode
		maintenanceService.transferTo(
				new SystemMode().withModeEnum(ModeEnum.MAINTENANCE).withMessage(MessageUtils.getMessage(ExceptionId.EX_MAINTENANCE_IMPORT_MODEL.getCode(),
						SecurityUtils.getCurrentUserName())));
		boolean isExc = false;
		try {
			FullModelDTO toProcess = toLoad.get(graph.getId());
			UpdateModelRequestContextBuilder builder = new UpdateModelRequestContextBuilder();
			if (toProcess.getModel() != null) {

				List<EntityDef> entitiesToUpdate = entitiesToUpdate(graph, toProcess);

				if (entitiesToUpdate != null) {
					builder.entityUpdate(entitiesToUpdate);
				}
				List<LookupEntityDef> lookupsToUpdate = lookupsToUpdate(graph, toProcess);
				if (lookupsToUpdate != null) {
					builder.lookupEntityUpdate(lookupsToUpdate);
				}
				List<NestedEntityDef> nestedToUpdate = nestedToUpdate(graph, toProcess);
				if (nestedToUpdate != null) {
					builder.nestedEntityUpdate(nestedToUpdate);
				}
				List<RelationDef> relationsToUpdate = relationsToUpdate(graph, toProcess);
				if (relationsToUpdate != null) {
					builder.relationsUpdate(relationsToUpdate);
				}
				List<EnumerationDataType> enumerationsToUpdate = enumerationsToUpdate(graph, toProcess);
				if (enumerationsToUpdate != null) {
					builder.enumerationsUpdate(enumerationsToUpdate);
				}
				List<SourceSystemDef> sourceSystemsToUpdate = sourceSystemsToUpdate(graph, toProcess);
				if (sourceSystemsToUpdate != null) {
					builder.sourceSystemsUpdate(sourceSystemsToUpdate);
				}
				EntitiesGroupDef groupsToUpdate = groupsToUpdate(graph, toProcess);
				if (sourceSystemsToUpdate != null) {
					builder.entitiesGroupsUpdate(groupsToUpdate);
				}
				CleanseFunctionGroupDef cleanseFunctionsUpdate = cleanseFunctionsUpdate(graph, toProcess);
				builder.cleanseFunctionsUpdate(cleanseFunctionsUpdate);
				builder.isForceRecreate(graph.isOverride() ? UpsertType.FULLY_NEW : UpsertType.PARTIAL_UPDATE);
			}
			if (graph.isOverride()) {
				systemElementsDAO.deleteByPathAndTypes(Paths.get(unidataIntegration.toString(), CF_FOLDER).toString(),
						ElementTypePO.CUSTOM_CF);
				List<ClsfDTO> toRemove = clsfService.getAllClassifiersWithoutDescendants();
				if (toRemove != null) {
					for (ClsfDTO clsfDTO : toRemove) {
						clsfService.removeClassifier(clsfDTO.getName(), false);
					}

				}
			}
			List<FullClassifierDef> clsfsToUpdate = clsfsToUpdate(graph, toProcess);

			if (clsfsToUpdate != null) {
				for (FullClassifierDef clsf : clsfsToUpdate) {
					if (clsfService.getClassifierByName(clsf.getClassifier().getName()) != null) {
						clsfService.removeClassifier(clsf.getClassifier().getName(), false);
					}
					clsfService.addFullFilledClassifierByIds(clsf);
				}
			}

			Map<String, ByteBuffer> customCfsToUpdate = customCfsToUpdate(graph, toProcess);
			if (customCfsToUpdate != null) {
				customCfsToUpdate.forEach((k, v) -> {

					try {
						cleanseFunctionService.removeFunctionById(k);
						Path func = Files.write(Paths.get(unidataIntegration.toString(), CF_FOLDER, k + ".jar"),
								v.array(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
						CFCustomUploaderResponse resp = cleanseFunctionService.preloadAndValidateCustomFunction(func,
								true);
						cleanseFunctionService.loadAndInit(resp.getTemporaryId());
						cleanseFunctionService.sendInitSignal(resp.getTemporaryId());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
			MeasurementValues measurementValues = measurementsToUpdate(graph, toProcess);
			if (graph.isOverride()) {
				metaMeasurementService.batchRemove(metaMeasurementService.getAllValues().stream()
						.map(MeasurementValue::getId).collect(Collectors.toSet()), false, true);
			}
			if (measurementValues != null) {
				List<MeasurementValueDef> valueDefs = measurementValues.getValue();
				List<MeasurementValue> values = new ArrayList<>();
				for (MeasurementValueDef value : valueDefs) {
					values.add(convert(value));
				}
				metaMeasurementService.saveValues(values);
			}
			// ----------------------------------- Update model --------------------------------------------------------
			if (toProcess.getModel() != null) {
				modelService.upsertModel(builder.build());
			}
			//----------------------------------------------------------------------------------------------------------
			if (customCfsToUpdate != null) {
				customCfsToUpdate.forEach((k, v) -> {

					try {
						cleanseFunctionService.removeFunctionById(k);
						Path func = Files.write(Paths.get(unidataIntegration.toString(), CF_FOLDER, k + ".jar"),
								v.array(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
						CFCustomUploaderResponse resp = cleanseFunctionService.preloadAndValidateCustomFunction(func,
								true);
						cleanseFunctionService.sendInitSignal(resp.getTemporaryId());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
			// matching must be updated after model because it depends on
			// entities
			MatchingUserSettings matchingsToUpdate = matchingsToUpdate(graph, toProcess);

			if (matchingsToUpdate != null) {
				matchingMetaFacadeService.importUserSettings(matchingsToUpdate);
			}
		} catch (BusinessException e) {
			isExc = true;
			throw e;
		} catch (SystemRuntimeException e) {
			isExc = true;
			throw e;
		} catch (Exception e) {
			isExc = true;
			throw new SystemRuntimeException("Unable to parse zip file.", e,
					ExceptionId.EX_META_IMPORT_MODEL_UNABLE_TO_PARSE, graph.getFileName());

		} finally {
			// end of maintenance mode
			try {
				refresh(isExc);
				deleteDirectoryContent(toImport);
				
			} catch (IOException e) {
				isExc = true;
				throw new SystemRuntimeException("Unable to parse zip file.", e,
						ExceptionId.EX_META_IMPORT_MODEL_UNABLE_TO_PARSE, graph.getFileName());
			} finally {
				metaDraftService.removeDraft();
				maintenanceService.transferTo(new SystemMode().withModeEnum(ModeEnum.NORMAL));
				toLoad.remove(graph.getId());
			}

		}
		
		return graph;

	}
	/**
	 * Delete directory  content.
	 * @param directoryName -directory name.
	 * @throws IOException if exception occurs.
	 */
	private static void deleteDirectoryContent(Path toImport) throws IOException {
		Files.walkFileTree(toImport, new SimpleFileVisitor<Path>() {
 
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
 
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				if (!dir.equals(toImport)) {
					Files.delete(dir);
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}
	/**
	 * Cleanse functions update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the cleanse function group def
	 */
	private CleanseFunctionGroupDef cleanseFunctionsUpdate(MetaGraph graph, FullModelDTO toProcess) {
		if (toProcess == null || toProcess.getModel() == null || toProcess.getModel().getCleanseFunctions() == null) {
			return null;
		}
		CleanseFunctionGroupDef cfUpdate = toProcess.getModel().getCleanseFunctions().getGroup();
		Map<String, CleanseFunctionExtendedDef> funcs = new HashMap<>();
		List<Serializable> list = cfUpdate.getGroupOrCleanseFunctionOrCompositeCleanseFunction();
		CleanseFunctionGroupDef old = null;
		try {
			old = cleanseFunctionService.getAll();
		} catch (CleanseFunctionExecutionException e) {
			LOGGER.error("Unable to load cleanse functions", e);
			throw new SystemRuntimeException("Unable to load cleanse functions.", e,
					ExceptionId.EX_META_IMPORT_MODEL_UNABLE_TO_PARSE, graph.getFileName().toString());
		}
		List<Serializable> oldList = old.getGroupOrCleanseFunctionOrCompositeCleanseFunction();
		if (graph.isOverride()) {
			oldList.removeIf(el -> (!(el instanceof CleanseFunctionGroupDef)));
		}
		for (Object object : oldList) {
			if (object instanceof CompositeCleanseFunctionDef) {
				CleanseFunctionExtendedDef def = (CleanseFunctionExtendedDef) object;
				funcs.put(def.getFunctionName(), def);
			}
		}
		Set<String> ccfs = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.COMPOSITE_CF))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		for (Serializable object : list) {
			if (object instanceof CompositeCleanseFunctionDef) {
				CleanseFunctionExtendedDef def = (CleanseFunctionExtendedDef) object;
				if (ccfs.contains(def.getFunctionName())) {
					funcs.put(def.getFunctionName(), def);
				}
			}
		}

		for (int i = 0; i < oldList.size(); i++) {
			Serializable object = oldList.get(i);
			if (oldList.get(i) instanceof CompositeCleanseFunctionDef) {
				CleanseFunctionExtendedDef def = (CleanseFunctionExtendedDef) object;
				if (funcs.containsKey(def.getFunctionName())) {
					oldList.set(i, object);
					funcs.remove(def.getFunctionName());
				}
			}

		}
		oldList.addAll(funcs.values());
		return old;
	}

	/**
	 * Groups to update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the entities group def
	 * @throws CycleFoundException
	 *             the cycle found exception
	 */
	private EntitiesGroupDef groupsToUpdate(MetaGraph graph, FullModelDTO toProcess) throws CycleFoundException {
		if (graph.isOverride()) {
			return toProcess.getModel().getEntitiesGroup();
		}
		Set<String> gs = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.GROUPS))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		EntitiesGroupDef newGroup = toProcess.getModel().getEntitiesGroup();
		EntitiesGroupDef oldGroup = graph.isOverride() ? ModelUtils.createDefaultEntitiesGroup()
				: modelService.exportModel(null).getEntitiesGroup();
		GroupGraph newGroupGraph = new GroupGraph();
		fillGroupGraph("", newGroup, newGroup.getInnerGroups(), newGroupGraph);
		GroupGraph oldGroupGraph = new GroupGraph();
		fillGroupGraph("", oldGroup, oldGroup.getInnerGroups(), oldGroupGraph);
		// intersect two graphs
		TopologicalOrderIterator<GroupVertex, GroupEdge> topologicalOrderIterator = new TopologicalOrderIterator<>(
				newGroupGraph);
		while (topologicalOrderIterator.hasNext()) {
			GroupVertex newVertex = topologicalOrderIterator.next();
			if (!gs.contains(newVertex.getId())) {
				continue;
			}
			if (oldGroupGraph.containsVertex(newVertex)) {
				GroupVertex oldVertex = oldGroupGraph.getVertexById(newVertex.getId());
				oldVertex.getValue().setTitle(newVertex.getValue().getTitle());
				oldVertex.getValue().setVersion(newVertex.getValue().getVersion());

			} else {
				intersectSubTree(newVertex, newGroupGraph, oldGroupGraph);
			}
		}
		return oldGroup;
	}

	private void intersectSubTree(GroupVertex toAdd, GroupGraph from, GroupGraph to) {
		String idToEnrich = StringUtils.substringBeforeLast(toAdd.getId(), ".");
		GroupVertex oldVertex = to.getVertexById(idToEnrich);
		if (oldVertex != null) {
			oldVertex.getValue().getInnerGroups().add(toAdd.getValue());
		} else {
			intersectSubTree(from.getVertexById(idToEnrich), from, to);
		}
	}

	/**
	 * Fill group graph.
	 *
	 * @param path
	 *            the path
	 * @param top
	 *            the top
	 * @param inner
	 *            the inner
	 * @param groupGraph
	 *            the group graph
	 * @throws CycleFoundException
	 *             the cycle found exception
	 */
	private void fillGroupGraph(String path, EntitiesGroupDef top, List<EntitiesGroupDef> inner, GroupGraph groupGraph)
			throws CycleFoundException {
		GroupVertex topV = new GroupVertex(top, path);
		groupGraph.addVertex(topV);
		path = StringUtils.isEmpty(path) ? top.getGroupName() : String.join(".", path, top.getGroupName());
		if (inner != null && inner.size() != 0) {
			for (EntitiesGroupDef inn : inner) {
				GroupVertex innV = new GroupVertex(top, path);
				groupGraph.addVertex(innV);
				groupGraph.addDagEdge(topV, innV);
				fillGroupGraph(path, inn, inn.getInnerGroups(), groupGraph);
			}
		}
	}

	/**
	 * Refresh.
	 *
	 * @param isExc
	 *            the is exc
	 */
	// TODO: temporary
	public void refresh(boolean isExc) {
		TransactionTemplate txTemplate = new TransactionTemplate(txManager);
		txTemplate.setPropagationBehavior(
				isExc ? TransactionDefinition.PROPAGATION_REQUIRES_NEW : TransactionDefinition.PROPAGATION_REQUIRED);
		txTemplate.execute(new TransactionCallback<Object>() {

			@Override
			public Object doInTransaction(TransactionStatus status) {
				registrationService.cleanup();
				metaMeasurementService.afterContextRefresh();
				modelService.afterContextRefresh();
				matchingRulesService.afterContextRefresh();
				matchingGroupsService.afterContextRefresh();
				cleanseFunctionService.afterContextRefresh();
				return null;
			}
		});

	}

	/**
	 * Custom cfs to update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the map
	 */
	private Map<String, ByteBuffer> customCfsToUpdate(MetaGraph graph, FullModelDTO toProcess) {
		Set<String> ccfs = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.CUSTOM_CF))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		Map<String, ByteBuffer> result = new HashMap<>();
		if (toProcess.getCustomCfs() != null && toProcess.getCustomCfs().size() != 0) {

			toProcess.getCustomCfs().forEach((k, v) -> {
				if (ccfs.contains(k)) {
					result.put(k, v);
				}
			});
		}
		return result;
	}

	/**
	 * Measurements to update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the measurement values
	 */
	private MeasurementValues measurementsToUpdate(MetaGraph graph, FullModelDTO toProcess) {
		if (toProcess == null || toProcess.getMeasurementValues() == null) {
			return null;
		}

		Set<String> mvs = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.MEASURE))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		List<MeasurementValueDef> toUpdate = toProcess.getMeasurementValues().getValue().stream()
				.filter(v -> mvs.contains(v.getId())).collect(Collectors.toList());
		toProcess.getMeasurementValues().getValue().clear();
		toProcess.getMeasurementValues().withValue(toUpdate);
		return toProcess.getMeasurementValues();
	}

	/**
	 * Matchings to update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the matching user settings
	 */
	private MatchingUserSettings matchingsToUpdate(MetaGraph graph, FullModelDTO toProcess) {
		if (toProcess == null || toProcess.getMatchingSettings() == null) {
			return null;
		}
		Set<String> mrs = graph.vertexSet().stream()
				.filter(v -> v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.MATCH_RULE)
				.map(MetaVertex::getId)
				.collect(Collectors.toSet());

		Set<String> mgs = new HashSet<>();
		// reduce keys
		if (toProcess.getMatchingSettings().getMatchingKeys() != null) {
			Map<String, Collection<MatchingKey>> keysOld = toProcess.getMatchingSettings().getMatchingKeys();
			Map<String, Collection<MatchingKey>> keysNew = new HashMap<>();
			keysOld.forEach((k, v) -> {
				Set<MatchingKey> keys = v.stream()
						.filter(el -> mrs.contains(el.getEntityName() + "_" + el.getMatchingRuleName()))
						.collect(Collectors.toSet());
				keys.forEach(el -> mgs.add(el.getEntityName() + "_" + el.getMatchingGroupName()));

				if (!keys.isEmpty()) {
					keysNew.put(k, keys);
				}
			});
			toProcess.getMatchingSettings().setMatchingKeys(keysNew);
		}
		// reduce groups
		if (toProcess.getMatchingSettings().getMatchingGroups() != null) {
			Collection<MatchingGroup> groups = toProcess.getMatchingSettings().getMatchingGroups();
			toProcess.getMatchingSettings().setMatchingGroups(
					groups.stream().filter(g -> mgs.contains(g.getEntityName() + "_" + g.getName())).collect(Collectors.toList()));

		}
		// reduce rules
		if (toProcess.getMatchingSettings().getMatchingRules() != null) {
			List<MatchingRule> rules = toProcess.getMatchingSettings().getMatchingRules().stream()
					.filter(el -> mrs.contains(el.getEntityName() + "_" + el.getName()))
					.collect(Collectors.toList());
			toProcess.getMatchingSettings().setMatchingRules(rules);
		}
		return toProcess.getMatchingSettings();
	}

	/**
	 * Clsfs to update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the list
	 */
	private List<FullClassifierDef> clsfsToUpdate(MetaGraph graph, FullModelDTO toProcess) {
		Set<String> clss = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.CLASSIFIER))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		List<FullClassifierDef> result = null;
		if (toProcess.getClsfsToImport() != null) {
			result = toProcess.getClsfsToImport().stream().filter(e -> clss.contains(e.getClassifier().getName()))
					.collect(Collectors.toList());

		}
		return result;
	}

	/**
	 * Source systems to update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the list
	 */
	private List<SourceSystemDef> sourceSystemsToUpdate(MetaGraph graph, FullModelDTO toProcess) {
		Set<String> ss = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.SOURCE_SYSTEM))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		List<SourceSystemDef> result = null;
		if (toProcess.getModel().getEntities() != null) {
			result = toProcess.getModel().getSourceSystems().stream().filter(e -> ss.contains(e.getName()))
					.collect(Collectors.toList());

		}
		return result;
	}

	/**
	 * Enumerations to update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the list
	 */
	private List<EnumerationDataType> enumerationsToUpdate(MetaGraph graph, FullModelDTO toProcess) {
		Set<String> enums = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.ENUM))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		List<EnumerationDataType> result = null;
		if (toProcess.getModel() != null && toProcess.getModel().getEnumerations() != null) {
			result = toProcess.getModel().getEnumerations().stream().filter(e -> enums.contains(e.getName()))
					.collect(Collectors.toList());

		}
		return result;
	}

	/**
	 * Relations to update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the list
	 */
	private List<RelationDef> relationsToUpdate(MetaGraph graph, FullModelDTO toProcess) {
		Set<String> rels = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.RELATION))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		List<RelationDef> result = null;
		if (toProcess.getModel() != null && toProcess.getModel().getEntities() != null) {
			result = toProcess.getModel().getRelations().stream().filter(e -> rels.contains(e.getName()))
					.collect(Collectors.toList());

		}
		return result;
	}

	/**
	 * Nested to update.
	 *
	 * @param graph
	 *            the graph
	 * @param toProcess
	 *            the to process
	 * @return the list
	 */
	private List<NestedEntityDef> nestedToUpdate(MetaGraph graph, FullModelDTO toProcess) {
		Set<String> nents = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT && v.getType() == MetaType.NESTED_ENTITY))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		List<NestedEntityDef> result = null;
		if (toProcess.getModel() != null && toProcess.getModel().getEntities() != null) {
			result = toProcess.getModel().getNestedEntities().stream().filter(e -> nents.contains(e.getName()))
					.collect(Collectors.toList());

		}
		return result;
	}

	/**
	 * Entities to update.
	 *
	 * @param graph
	 *            the graph
	 * @param fullModel
	 *            the full model
	 * @return the list
	 */
	private List<EntityDef> entitiesToUpdate(MetaGraph graph, FullModelDTO fullModel) {
		Set<String> ents = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT
						&& (v.getType() == MetaType.LOOKUP || v.getType() == MetaType.ENTITY)))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		List<EntityDef> result = null;
		if (fullModel.getModel() != null && fullModel.getModel().getEntities() != null) {
			result = fullModel.getModel().getEntities().stream().filter(e -> ents.contains(e.getName()))
					.collect(Collectors.toList());

		}
		return result;
	}

	/**
	 * Lookups to update.
	 *
	 * @param graph
	 *            the graph
	 * @param fullModel
	 *            the full model
	 * @return the list
	 */
	private List<LookupEntityDef> lookupsToUpdate(MetaGraph graph, FullModelDTO fullModel) {
		Set<String> lents = graph.vertexSet().stream()
				.filter(v -> (v.getAction() == MetaAction.UPSERT
						&& (v.getType() == MetaType.LOOKUP || v.getType() == MetaType.ENTITY)))
				.map(MetaVertex::getId).collect(Collectors.toSet());
		List<LookupEntityDef> result = null;
		if (fullModel.getModel() != null && fullModel.getModel().getLookupEntities() != null) {
			result = fullModel.getModel().getLookupEntities().stream()

					.filter(e -> lents.contains(e.getName())).collect(Collectors.toList());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.unidata.mdm.backend.service.model.ie.MetaIEService#exportMeta(java.
	 * lang.String)
	 */
	@Override
	public void exportMeta(String storageId) {
		if (storageId == null) {
			storageId = SecurityUtils.getCurrentUserStorageId();
		}
		Path rootPath = Paths.get(toExport.toString(),
				storageId + DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd_HH-mm-ss"));
		try {

			Files.createDirectories(rootPath);
			// export cleanse functions
			// exportCleanse(storageId, rootPath);
			// export meta model
			exportModel(storageId, rootPath);
			// export matching rules
			exportMatching(storageId, rootPath);
			// export classifiers
			exportClassifiers(storageId, rootPath);
			// export measure values
			exportMeasure(storageId, rootPath);
			// export unidata-conf and external routing
			exportConfig(storageId, rootPath);
			// export bpm files
			exportBPM(storageId, rootPath);
			// export integration folder
			exportIntegration(storageId, rootPath);
			// zip early created results
			Path zipModel = ZipUtils.zipDir(rootPath);
			try (InputStream is = new FileInputStream(zipModel.toFile());) {
				UpsertUserEventRequestContext uueCtx = new UpsertUserEventRequestContextBuilder()
						.login(SecurityUtils.getCurrentUserName())
						.type("META_FULL_EXPORT")
						.content(MessageUtils.getMessage(UserMessageConstants.DATA_EXPORT_METADATA_SUCCESS))
						.build();
				UserEventDTO userEventDTO = userService.upsert(uueCtx);
				// save result and attach it to the early created user event
				SaveLargeObjectRequestContext slorCTX = new SaveLargeObjectRequestContextBuilder()
						.eventKey(userEventDTO.getId()).mimeType("application/zip").binary(true).inputStream(is)
						.filename(zipModel.getFileName().toString()).build();
				dataRecordService.saveLargeObject(slorCTX);
			}
		} catch (IOException e) {
			throw new SystemRuntimeException("Unable to create zip file for metamodel. Exception occured.",
					ExceptionId.EX_META_CANNOT_ASSEMBLE_MODEL);
		} finally {
			try {
				Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.deleteIfExists(file);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						Files.deleteIfExists(dir);
						return FileVisitResult.CONTINUE;
					}
				});
				Files.deleteIfExists(Paths.get(rootPath.toString() + ".zip"));
			} catch (IOException e) {
				LOGGER.error("Unable to delete temporary metamodel export files", e);
			}
		}

	}

	/**
	 * Export integration.
	 *
	 * @param storageId
	 *            the storage id
	 * @param rootPath
	 *            the root path
	 * @return the path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Path exportIntegration(String storageId, Path rootPath) throws IOException {
		Path from = Paths.get(unidataIntegration.toString());
		Files.createDirectories(Paths.get(rootPath.toString(), INTEGRATION));
		Path to = Paths.get(rootPath.toString(), INTEGRATION);
		copyFolder(from, to);
		return to;

	}

	/**
	 * Export BPM.
	 *
	 * @param storageId
	 *            the storage id
	 * @param rootPath
	 *            the root path
	 * @return the path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Path exportBPM(String storageId, Path rootPath) throws IOException {
		Path result = Files.createDirectories(Paths.get(rootPath.toString(), BPM));
		Files.createDirectories(Paths.get(rootPath.toString(), BPM));
		List<WorkflowProcessDefinition> list = configurationService.getDefinedProcessTypes();
		if (list != null) {
			for (WorkflowProcessDefinition wpd : list) {
				if (!StringUtils.isEmpty(wpd.getPath())) {
					Path from = Paths.get(wpd.getPath().replace("\\", "/"));
					if (!from.isAbsolute()) {
						from = Paths.get(System.getProperty(CATALINA_BASE), "conf", "unidata",
								wpd.getPath().replace("\\", "/"));
					}

					if (Files.exists(from)) {
						Path to = Paths.get(rootPath.toString(), BPM, from.getFileName().toString());
						Files.copy(from, to);
					}

				}
			}
		}
		return result;

	}

	/**
	 * Export config.
	 *
	 * @param storageId
	 *            the storage id
	 * @param rootPath
	 *            the root path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void exportConfig(String storageId, Path rootPath) throws IOException {
		Path unidataConf = Paths.get(System.getProperty(ConfigurationHolder.CONFIGURATION_PATH_PROPERTY), "unidata-conf.xml");
		Path externalRouting = Paths.get(System.getProperty(ConfigurationHolder.CONFIGURATION_PATH_PROPERTY), "external-routing.xml");
		if (Files.exists(unidataConf)) {
			Files.createDirectories(Paths.get(rootPath.toString(), CONFIG));
			Path to = Paths.get(rootPath.toString(), CONFIG, unidataConf.getFileName().toString());
			Files.copy(unidataConf, to, StandardCopyOption.REPLACE_EXISTING);
		}
		if (Files.exists(externalRouting)) {
			Files.createDirectories(Paths.get(rootPath.toString(), CONFIG));
			Path to = Paths.get(rootPath.toString(), CONFIG, externalRouting.getFileName().toString());
			Files.copy(externalRouting, to, StandardCopyOption.REPLACE_EXISTING);
		}
	}

	/**
	 * Export classifiers.
	 *
	 * @param storageId
	 *            the storage id
	 * @param rootPath
	 *            the root path
	 * @return the path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Path exportClassifiers(String storageId, Path rootPath) throws IOException {

		Path clsfFolder = Files.createDirectories(Paths.get(rootPath.toString(), CLASSIFIERS));
		List<ClsfDTO> clsfs = assembleClassifiers(storageId);
		if (clsfs == null || clsfs.size() == 0) {
			return clsfFolder;
		}
		for (ClsfDTO clsf : clsfs) {
			XmlClassifierWrapper xmlClassifierWrapper = new XmlClassifierWrapper(clsf);
			StreamingOutput output = conversionService.convert(xmlClassifierWrapper, StreamingOutput.class);
			try (FileOutputStream fao = new FileOutputStream(
					Paths.get(clsfFolder.toString(), clsf.getName() + XML_EXTENSION).toFile())) {
				output.write(fao);
			}
		}
		return clsfFolder;
	}

	/**
	 * Copy folder.
	 *
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void copyFolder(Path from, Path to) throws IOException {
		try (Stream<Path> stream = Files.walk(from)) {
			stream.forEach(path -> {

				try {
					Files.copy(path, Paths.get(path.toString().replace(from.toString(), to.toString())),StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					LOGGER.error("Unable to copy file", e);
				}

			});
		}
	}

	/**
	 * Assemble classifiers.
	 *
	 * @param storageId
	 *            the storage id
	 * @return the list
	 */
	private List<ClsfDTO> assembleClassifiers(String storageId) {
		List<ClsfDTO> clsfs = clsfService.getAllClassifiersWithoutDescendants();
		if (clsfs == null) {
			return null;
		}

		List<ClsfDTO> result = new ArrayList<>();
		for (ClsfDTO clsf : clsfs) {
			result.add(clsfService.getClassifierByNameWithAllNodes(clsf.getName()));
		}
		return result;
	}

	/**
	 * Export model.
	 *
	 * @param storageId
	 *            the storage id
	 * @param rootPath
	 *            the root path
	 * @return the path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Path exportModel(String storageId, Path rootPath) throws IOException {
		Model model = assembleModel(storageId);
		Path modelFolder = Files.createDirectories(Paths.get(rootPath.toString(), MODEL));
		String result = JaxbUtils.marshalMetaModel(model);
		Path file = Files.createFile(Paths.get(modelFolder.toString(), MODEL + XML_EXTENSION));
		write(result, file);
		return file;
	}

	/**
	 * Assemble model.
	 *
	 * @param storageId
	 *            the storage id
	 * @return the model
	 */
	private Model assembleModel(String storageId) {
		return modelService.exportModel(null);
	}

	/**
	 * Export measure.
	 *
	 * @param storageId
	 *            the storage id
	 * @param rootPath
	 *            the root path
	 * @return the path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Path exportMeasure(String storageId, Path rootPath) throws IOException {
		MeasurementValues measurementValueDef = assembleMeasurement(storageId);
		Path measurementFolder = Files.createDirectories(Paths.get(rootPath.toString(), MEASURE));
		String result = null;
		try {
			result = convertMeasure(measurementValueDef);
		} catch (JAXBException e) {
			LOGGER.error("Unable to convert measure values to string.", e);
		}
		Path file = Files.createFile(Paths.get(measurementFolder.toString(), MEASURE + XML_EXTENSION));
		write(result, file);
		return file;
	}

	/**
	 * Assemble measurement.
	 *
	 * @param storageId
	 *            the storage id
	 * @return the measurement values
	 */
	private MeasurementValues assembleMeasurement(String storageId) {
		Collection<MeasurementValueDef> measurementValue = metaMeasurementService.getAllValues().stream()
				.map(MeasurementValueXmlConverter::convert).collect(Collectors.toList());
		MeasurementValues result = new MeasurementValues().withValue(measurementValue);
		return result;
	}

	/**
	 * Export cleanse.
	 *
	 * @param storageId
	 *            the storage id
	 * @param rootPath
	 *            the root path
	 * @return the path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Path exportCleanse(String storageId, Path rootPath) throws IOException {
		Path from = Paths.get(unidataIntegration.toString(), CF_FOLDER);
		Path to = Paths.get(rootPath.toString(), CF_FOLDER);
		copyFolder(from, to);
		return to;
	}

	/**
	 * Export matching.
	 *
	 * @param storageId
	 *            the storage id
	 * @param rootPath
	 *            the root path
	 * @return the path
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private Path exportMatching(String storageId, Path rootPath) throws IOException {

		Path matchingFolder = Files.createDirectories(Paths.get(rootPath.toString(), MATCHING));
		MatchingUserSettings matchingUserSettings = assembleMatching(storageId);
		final String result = conversionService.convert(matchingUserSettings, String.class);
		Path file = Files.createFile(Paths.get(matchingFolder.toString(), MATCHING + XML_EXTENSION));
		write(result, file);
		return file;
	}

	/**
	 * Assemble matching.
	 *
	 * @param storageId
	 *            the storage id
	 * @return the matching user settings
	 */
	private MatchingUserSettings assembleMatching(String storageId) {
		MatchingUserSettings result = new MatchingUserSettings();
		result.setMatchingRules(matchingRulesService.getAllRules());
		result.setMatchingGroups(matchingGroupsService.getAllGroups());
		return result;
	}

	/**
	 * Write.
	 *
	 * @param string
	 *            the string
	 * @param file
	 *            the file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void write(String string, Path file) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file.toString()), "UTF-8"))) {
			bw.write(string);
		}
	}

	/**
	 * Convert measure.
	 *
	 * @param valueDef
	 *            the value def
	 * @return the string
	 * @throws JAXBException
	 *             the JAXB exception
	 */
	public static String convertMeasure(@Nonnull MeasurementValues valueDef) throws JAXBException {
		JAXBElement<MeasurementValues> jaxb = new JAXBElement<>(MEASUREMENT_VALUE_QNAME, MeasurementValues.class, null,
				valueDef);
		StringWriter sw = new StringWriter();
		Marshaller marshaller = JaxbUtils.getMetaContext().createMarshaller();
		marshaller.marshal(jaxb, sw);
		return sw.toString();
	}

	/**
	 * The Class GroupGraph.
	 */
	class GroupGraph extends DirectedAcyclicGraph<GroupVertex, GroupEdge> {

		/** The vertex map. */
		private Map<String, GroupVertex> vertexMap = new HashMap<>();

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/**
		 * Instantiates a new group graph.
		 */
		public GroupGraph() {
			super(new EdgeFactory<GroupVertex, GroupEdge>() {

				@Override
				public GroupEdge createEdge(GroupVertex sourceVertex, GroupVertex targetVertex) {
					GroupEdge edge = new GroupEdge();
					edge.setFrom(sourceVertex);
					edge.setTo(targetVertex);
					return edge;
				}
			});
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * org.jgrapht.experimental.dag.DirectedAcyclicGraph#addVertex(java.lang
		 * .Object)
		 */
		@Override
		public boolean addVertex(GroupVertex v) {
			vertexMap.put(v.getId(), v);
			return super.addVertex(v);
		}

		/**
		 * Gets the vertex by id.
		 *
		 * @param id
		 *            the id
		 * @return the vertex by id
		 */
		public GroupVertex getVertexById(String id) {
			return vertexMap.get(id);
		}
	}

	/**
	 * The Class GroupEdge.
	 */
	class GroupEdge {

		/** The from. */
		private GroupVertex from;

		/** The to. */
		private GroupVertex to;

		/**
		 * Gets the from.
		 *
		 * @return the from
		 */
		public GroupVertex getFrom() {
			return from;
		}

		/**
		 * Sets the from.
		 *
		 * @param from
		 *            the new from
		 */
		public void setFrom(GroupVertex from) {
			this.from = from;
		}

		/**
		 * Gets the to.
		 *
		 * @return the to
		 */
		public GroupVertex getTo() {
			return to;
		}

		/**
		 * Sets the to.
		 *
		 * @param to
		 *            the new to
		 */
		public void setTo(GroupVertex to) {
			this.to = to;
		}
	}

	/**
	 * The Class GroupVertex.
	 */
	class GroupVertex {

		/** The id. */
		private String id;

		/** The value. */
		private EntitiesGroupDef value;

		/**
		 * Instantiates a new group vertex.
		 *
		 * @param value
		 *            the value
		 * @param path
		 *            the path
		 */
		public GroupVertex(EntitiesGroupDef value, String path) {
			this.value = value;
			this.id = StringUtils.isEmpty(path) ? value.getGroupName() : String.join(".", path, value.getGroupName());
		}

		/**
		 * Gets the id.
		 *
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * Sets the id.
		 *
		 * @param id
		 *            the new id
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public EntitiesGroupDef getValue() {
			return value;
		}

		/**
		 * Sets the value.
		 *
		 * @param value
		 *            the new value
		 */
		public void setValue(EntitiesGroupDef value) {
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GroupVertex other = (GroupVertex) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		/**
		 * Gets the outer type.
		 *
		 * @return the outer type
		 */
		private MetaIEServiceImpl getOuterType() {
			return MetaIEServiceImpl.this;
		}
	}

}
