package com.unidata.mdm.backend.service.classifier;

import static com.unidata.mdm.backend.common.context.SearchRequestContext.forClassifierElements;
import static com.unidata.mdm.backend.common.search.FormField.strictString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.unidata.mdm.backend.common.context.SearchRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext;
import com.unidata.mdm.backend.common.context.UpdateModelRequestContext.UpdateModelRequestContextBuilder;
import com.unidata.mdm.backend.common.dto.SearchResultDTO;
import com.unidata.mdm.backend.common.dto.SearchResultHitFieldDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeAttrDTO;
import com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO;
import com.unidata.mdm.backend.common.dto.job.JobDTO;
import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;
import com.unidata.mdm.backend.common.exception.BusinessException;
import com.unidata.mdm.backend.common.exception.DataProcessingException;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.ValidationResult;
import com.unidata.mdm.backend.common.search.FormField;
import com.unidata.mdm.backend.common.search.FormFieldsGroup;
import com.unidata.mdm.backend.common.search.types.EntitySearchType;
import com.unidata.mdm.backend.common.types.SimpleAttribute.DataType;
import com.unidata.mdm.backend.configuration.application.ConfigurationUpdatesConsumer;
import com.unidata.mdm.backend.configuration.application.UnidataConfigurationProperty;
import com.unidata.mdm.backend.dao.ClsfDao;
import com.unidata.mdm.backend.service.classifier.converters.ClsfDTOToPOConverter;
import com.unidata.mdm.backend.service.classifier.converters.ClsfNodeAttrDTOToPOConverter;
import com.unidata.mdm.backend.service.classifier.converters.ClsfNodeAttrPOToDTOConverter;
import com.unidata.mdm.backend.service.classifier.converters.ClsfNodeDTOToPOConverter;
import com.unidata.mdm.backend.service.classifier.converters.ClsfNodePOToDTOConverter;
import com.unidata.mdm.backend.service.classifier.converters.ClsfPOToDTOConverter;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodeAttrPO;
import com.unidata.mdm.backend.service.classifier.po.ClsfNodePO;
import com.unidata.mdm.backend.service.classifier.po.ClsfPO;
import com.unidata.mdm.backend.service.job.JobServiceExt;
import com.unidata.mdm.backend.service.job.reindex.ReindexDataJobConstants;
import com.unidata.mdm.backend.service.job.reports.JobReportConstants;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.search.SearchServiceExt;
import com.unidata.mdm.backend.service.search.util.ClassifierDataHeaderField;
import com.unidata.mdm.backend.service.search.util.ClassifierHeaderField;
import com.unidata.mdm.backend.service.search.util.SearchUtils;
import com.unidata.mdm.backend.service.security.SecurityServiceExt;
import com.unidata.mdm.backend.service.security.utils.SecurityUtils;
import com.unidata.mdm.backend.util.IdUtils;
import com.unidata.mdm.backend.util.MessageUtils;
import com.unidata.mdm.backend.util.constants.UserMessageConstants;
import com.unidata.mdm.classifier.ClassifierNodeDef;
import com.unidata.mdm.classifier.ClassifierValueDef;
import com.unidata.mdm.classifier.FullClassifierDef;
import com.unidata.mdm.classifier.SimpleAttributeWithOptionalValueDef;
import com.unidata.mdm.meta.AbstractEntityDef;
import com.unidata.mdm.meta.EntityDef;
import com.unidata.mdm.meta.LookupEntityDef;
import com.unidata.mdm.meta.NestedEntityDef;

import reactor.core.publisher.Flux;

/**
 * The Class ClsfServiceImpl.
 */
@Component
public class ClsfServiceImpl implements ClsfService, ConfigurationUpdatesConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClsfServiceImpl.class);
    /** Max number of search results. */
    @Value("${classifier.max.number.search.result:100}")
    private Integer maxNumberOfSearchResults;
    /**
     * The clsf dao.
     */
    @Autowired
    private ClsfDao clsfDao;

    /**
     * The search service.
     */
    @Autowired
    private SearchServiceExt searchService;

    /**
     * The security service.
     */
    @Autowired
    private SecurityServiceExt securityService;

    /**
     * The metamodel service.
     */
    @Autowired
    private MetaModelServiceExt metaModelService;

    /**
     * The job service.
     */
    @Autowired
    private JobServiceExt jobServiceExt;

    /**
     * The Constant ROOT_NODE_POSTFIX.
     */
    private static final String ROOT_NODE_POSTFIX = "root";

    /**
     * The Constant DOT.
     */
    private static final String DOT = ".";

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-z][a-z0-9_-]*$", Pattern.CASE_INSENSITIVE);

    private AtomicInteger batchSize = new AtomicInteger(
            (Integer) UnidataConfigurationProperty.CLASSIFIER_IMPORT_BATCH_SIZE.getDefaultValue().get()
    );

    private final ObjectMapper objectMapper = new ObjectMapper();

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#createClassifier(com
     * .unidata.mdm.backend.service.classifier.dto.ClsfDTO)
     */
    @Override
    @Transactional
    public void createClassifier(ClsfDTO toSave, boolean createRoot) {
        if (clsfDao.isClsfExists(toSave.getName())) {
            throw new BusinessException("Classifier already exist!", ExceptionId.EX_CLASSIFIER_ALREADY_CREATED,
                    toSave.getName(), toSave.getDisplayName());
        }
        validateClassifierCodePattern(toSave.getCodePattern());
        toSave.setCreatedAt(new Date());
        toSave.setCreatedBy(SecurityUtils.getCurrentUserName());
        ClsfPO clsfPO = ClsfDTOToPOConverter.convert(toSave);
        clsfDao.create(clsfPO);
        if (!searchService.classifierIndexExist(SecurityUtils.getCurrentUserStorageId())) {
            searchService.createClassifierIndex(SecurityUtils.getCurrentUserStorageId());
        }
        if (createRoot) {
            ClsfNodeDTO rootNode = new ClsfNodeDTO();
            rootNode.setName(toSave.getDisplayName());
            rootNode.setClsfName(toSave.getName());
            rootNode.setNodeId(String.join(DOT, toSave.getName(), ROOT_NODE_POSTFIX));
            rootNode.setDescription(toSave.getDescription());
            rootNode.setCode("");
            addNewNodeToClassifier(toSave.getName(), rootNode, false);
        }

        securityService.createResourceForClassifier(toSave.getName(), toSave.getDisplayName());
    }

    private void validateClassifierCodePattern(final String codePattern) {
        if (StringUtils.isBlank(codePattern)) {
            return;
        }
        final Collection<Pair<Integer, InvalidSymbolReason>> errors = CodeParser.validateCodePatternContent(codePattern);
        if (!errors.isEmpty()) {
            throw new CodePatternValidationException(
                    "Code pattern is incorrect: " + codePattern,
                    ExceptionId.EX_CLASSIFIER_CODE_PATTERN_INCORRECT,
                    errors.stream().map(r -> {
                        switch (r.getRight()) {
                            case UNKNOWN_SYMBOL:
                                return new ValidationResult(
                                        "Unknown symbol in position " + r.getLeft(),
                                        "app.classifier.code.pattern.unknown.symbol",
                                        r.getLeft()
                                );
                            case MUST_BE_PLACEHOLDER:
                                return new ValidationResult(
                                        "Placeholder must be in position " + r.getLeft(),
                                        "app.classifier.code.pattern.must.be.placeholder",
                                        r.getLeft()
                                );
                            case WRONG_ZERO_FILLER_POSITION:
                                return new ValidationResult(
                                        "Wrong position of zeros filler " + r.getLeft(),
                                        "app.classifier.code.pattern.wrong.position.of.zeros.filler",
                                        r.getLeft()
                                );
                        }
                        throw new RuntimeException("Unhandled validation symbol reason");
                    }).collect(Collectors.toList()),
                    codePattern,
                    highlightErrors(codePattern, errors)
            );
        }
    }

    private String highlightErrors(final String codePattern, Collection<Pair<Integer, InvalidSymbolReason>> errors) {
        final StringBuilder codePatternWith = new StringBuilder();
        final int[] currentSymbol = {0};
        errors.stream().map(p -> p.getLeft() - 1).forEach(errorPos -> {
            if (errorPos != 0) {
                codePatternWith.append(codePattern.substring(currentSymbol[0], errorPos));
            }
            codePatternWith.append('[')
                    .append(errorPos + 1)
                    .append(':')
                    .append(codePattern.charAt(errorPos))
                    .append(']');
            currentSymbol[0] = errorPos + 1;
        });
        return codePatternWith.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * getAllClassifiersWithoutDescendants()
     */
    @Override
    @Transactional
    public List<ClsfDTO> getAllClassifiersWithoutDescendants() {
        List<ClsfPO> clsfPOs = clsfDao.getAllClassifiers();
        return ClsfPOToDTOConverter.convert(clsfPOs);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * getClassifierByName(java.lang.String)
     */
    @Override
    @Transactional
    public ClsfDTO getClassifierByName(String classifierName) {
        ClsfPO clsfPO = clsfDao.getClassifierByName(classifierName);
        return ClsfPOToDTOConverter.convert(clsfPO);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#findNodes(java.
     * lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ClsfDTO findNodes(String classifierName, String text) {
        ClsfDTO clsf = getClassifierByName(classifierName);
        FormField classifierField = strictString(ClassifierHeaderField.CLASSIFIER_NAME.getField(), classifierName);
        FormField textField = FormField.startWithString(ClassifierHeaderField.NODE_SEARCH_ELEMENT.getField(), text);
        SearchRequestContext scb = SearchRequestContext.forClassifierElements().text(text)
                .form(FormFieldsGroup.createAndGroup(classifierField, textField))
                .returnFields(Collections.singletonList(ClassifierHeaderField.NODE_UNIQUE_ID.getField())).count(10)
                .page(0).totalCount(true).source(false).build();
        SearchResultDTO result = searchService.search(scb);
        if (result.getTotalCount() > maxNumberOfSearchResults) {
            throw new BusinessException("Too many results found",
                    ExceptionId.EX_SEARCH_CLASSIFIERS_META_RESULT_TOO_MUCH, maxNumberOfSearchResults);
        }
        if (result.getHits().isEmpty()) {
            return clsf;
        }

        List<String> nodes = result.getHits().stream()
                .map(hit -> hit.getFieldValue(ClassifierHeaderField.NODE_UNIQUE_ID.getField())).filter(Objects::nonNull)
                .filter(SearchResultHitFieldDTO::isNonNullField).map(SearchResultHitFieldDTO::getValues)
                .flatMap(Collection::stream).filter(Objects::nonNull).map(Object::toString).distinct()
                .collect(Collectors.toList());

        ClsfNodeDTO node = buildBranchToRoot(nodes, classifierName);

        clsf.setRootNode(node);
        return clsf;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#updateClassifier(
     * com.unidata.mdm.backend.common.dto.classifier.model.ClsfDTO)
     */
    @Override
    @Transactional
    public void updateClassifier(ClsfDTO clsfDTO) {
        if (!clsfDao.isClsfExists(clsfDTO.getName())) {
            throw new BusinessException(
                    "Classifier does not exist!", ExceptionId.EX_CLASSIFIER_NOT_EXIST_FOR_UPDATE, clsfDTO.getName()
            );
        }
        clsfDTO.setUpdatedAt(new Date());
        clsfDTO.setUpdatedBy(SecurityUtils.getCurrentUserName());
        ClsfPO toUpdate = ClsfDTOToPOConverter.convert(clsfDTO);
        clsfDao.update(toUpdate);
        final ClsfNodePO rootNode = clsfDao.getRootNode(clsfDTO.getName());
        if (rootNode != null && !rootNode.getName().equals(clsfDTO.getDisplayName())) {
            rootNode.setName(clsfDTO.getDisplayName());
            clsfDao.update(rootNode, clsfDTO.getName());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#removeClassifier(
     * java.lang.String)
     */
    @Override
    @Transactional
    public void removeClassifier(String classifierName, boolean dropRefs) {
        if (!clsfDao.isClsfExists(classifierName)) {
            throw new BusinessException(
                    "Classifier does not exist!",
                    ExceptionId.EX_CLASSIFIER_NOT_EXIST_FOR_UPDATE,
                    classifierName
            );
        }
        clsfDao.remove(classifierName);
        afterRemoveClassifierParts(classifierName, dropRefs);
    }

    private void afterRemoveClassifierParts(
            final String classifierName,
            final boolean dropRefs
    ) {
        FormField clsfNameField = strictString(ClassifierHeaderField.CLASSIFIER_NAME.getField(), classifierName);
        SearchRequestContext searchContext = forClassifierElements()
                .form(FormFieldsGroup.createAndGroup(clsfNameField))
                .storageId(SecurityUtils.getCurrentUserStorageId())
                .build();
        searchService.deleteFoundResult(searchContext);
        securityService.deleteResources(Collections.singletonList(classifierName));
        if (dropRefs) {
            updateRefsToClsf(classifierName, true);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * addNewNodeToClassifier(java.lang.String,
     * com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO)
     */
    @Override
    @Transactional
    public ClsfNodeDTO addNewNodeToClassifier(
            final String classifierName,
            final ClsfNodeDTO classifierNode,
            final boolean updateRefs
    ) {
        if (!clsfDao.isClsfExists(classifierName)) {
            throw new BusinessException("Classifier does not exist!", ExceptionId.EX_CLASSIFIER_NOT_EXIST_FOR_UPDATE,
                    classifierName);
        }

        final ClsfDTO classifier = getClassifierByName(classifierName);

        if (classifierNode.getParentId() == null) {
            classifierNode.setName(classifier.getDisplayName());
        }

        if (classifierNode.getNodeId() == null) {
            if (StringUtils.isNotBlank(classifierNode.getCode())
                    && StringUtils.isNotBlank(classifier.getCodePattern())
                    && classifier.isValidateCodeByLevel()) {
                classifierNode.setNodeId(CodeParser.toNodeId(classifierNode.getCode()));
            } else {
                classifierNode.setNodeId(IdUtils.v1String());
            }
        }

        validate(classifier, classifierNode, classifier.isValidateCodeByLevel());
        validateExistNodeNameAndCode(classifier, classifierNode);
        validateAttributeName(classifier, classifierNode);

        updateIds(classifierName, classifierNode);

        classifierNode.setCreatedAt(new Date());
        classifierNode.setCreatedBy(SecurityUtils.getCurrentUserName());

        ClsfNodePO toCreate = ClsfNodeDTOToPOConverter.convert(classifierNode);
        clsfDao.create(classifierName, toCreate);

        classifierNode.setClsfName(classifierName);
        searchService.indexClassifierNode(SecurityUtils.getCurrentUserStorageId(), classifierNode);
        if (updateRefs) {
            updateRefsToClsf(classifierName, false);
        }
        return getNodeWithAttrs(classifierNode.getNodeId(), classifierName, false);
    }

    private void validateAttributeName(final ClsfDTO classifier, final ClsfNodeDTO classifierNode) {
        final Map<String, List<String>> nodesWithPresentAttributesInClassifier = clsfDao.findNodesWithPresentAttributesInClassifier(
                classifier.getName(),
                classifierNode.getNodeId(),
                classifierNode.getNodeAttrs().stream().map(ClsfNodeAttrDTO::getAttrName).collect(Collectors.toList())
        );
        if (nodesWithPresentAttributesInClassifier.isEmpty()) {
            return;
        }
        throw new BusinessException(
                "Attributes names in not unique in classifier",
                ExceptionId.EX_CLASSIFIER_ATTRIBUTES_IS_NOT_UNIQUE,
                nodesWithPresentAttributesInClassifier.entrySet().stream()
                        .sorted(Comparator.comparing(Map.Entry::getKey))
                        .map(node -> String.format("%s: %s", node.getKey(), String.join(", ", node.getValue())))
                        .collect(Collectors.joining("; "))
        );
    }

    private void validateExistNodeNameAndCode(ClsfDTO clsf, ClsfNodeDTO classifierNode) {
        if (clsfDao.findNodeByCodeAndNameAndParentId(clsf.getName(), classifierNode.getCode(), classifierNode.getName(), classifierNode.getParentId()) != null) {
            throw new BusinessException(
                    "Node with same code and name on this level exists.",
                    ExceptionId.EX_CLASSIFIER_SAME_CODE_AND_NAME_ON_ONE_LEVEL,
                    clsf.getName(),
                    classifierNode.getCode(),
                    classifierNode.getName()
            );
        }
    }

    private void validate(ClsfDTO clsf, ClsfNodeDTO classifierNode, boolean validateCodeByLevel) {
        final String classifierName = clsf.getName();
        if (StringUtils.isEmpty(classifierNode.getName())) {
            throw new BusinessException(
                    "Conversion to classifier node failed. No value for field: name.",
                    ExceptionId.EX_CLASSIFIER_NODE_FAILED_MISSING_FIELD,
                    "name"
            );
        }
        if (CollectionUtils.isNotEmpty(classifierNode.getNodeAttrs())) {
            Set<String> attrNames = new HashSet<>();
            classifierNode.getNodeAttrs().forEach(attr -> {
                if (attrNames.contains(attr.getAttrName())) {
                    throw new BusinessException("Classifier does not exist!", ExceptionId.EX_CLASSIFIER_NODE_ATTR_DUPL,
                            attr.getAttrName(), classifierName, classifierNode.getName());
                }
                if (!NAME_PATTERN.matcher(attr.getAttrName()).matches()) {
                    throw new BusinessException(
                            "Classifier attribute name incorrect!",
                            ExceptionId.EX_CLASSIFIER_NODE_ATTR_NAME_INCORRECT,
                            attr.getAttrName(),
                            classifierName,
                            classifierNode.getName(),
                            attr.getDataType().name()
                    );
                }
                attrNames.add(attr.getAttrName());
            });
        }
        if (StringUtils.isNotEmpty(clsf.getCodePattern()) && StringUtils.isNotEmpty(classifierNode.getParentId())) {
            if (!CodeParser.isValidCodeForPattern(classifierNode.getCode(), clsf.getCodePattern())) {
                throw new BusinessException(
                        "Pattern are not matching",
                        ExceptionId.EX_CLASSIFIER_NODE_CODE_INCORRECT,
                        classifierNode.getCode(),
                        clsf.getCodePattern(),
                        clsf.getName()
                );
            }
            final String[] codes = CodeParser.extractGroups(classifierNode.getCode(), clsf.getCodePattern());
            final List<String> toRoot = clsfDao.getNodesToRoot(classifierNode.getParentId(), classifierName);
            if (validateCodeByLevel && codes.length != toRoot.size()) {
                throw new BusinessException("Code is incorrect!",
                        ExceptionId.EX_CLASSIFIER_NODE_CODE_DOESNT_MATCH_PARENT,
                        classifierNode.getCode());
            } else if (clsfDao.isClsfNodeCodeExists(classifierName, classifierNode.getCode())
                    && !clsfDao.isClsfNodeExists(classifierName, classifierNode.getNodeId())) {
                throw new BusinessException("Code is not unique!", ExceptionId.EX_CLASSIFIER_NODE_CODE_NOT_UNIQUE,
                        classifierName, classifierNode.getName(), classifierNode.getCode());
            } else if (toRoot.size() > 1) {
                ClsfNodeDTO parent = getNodeByNodeId(classifierNode.getParentId(), classifierName);
                final String[] parentCodes = Arrays.copyOfRange(codes, 0, codes.length - 1);
                for (int i = 0; i < codes.length - 1; i++) {
                    if (!parentCodes[i].equals(codes[i])) {
                        throw new BusinessException("Code is incorrect!",
                                ExceptionId.EX_CLASSIFIER_NODE_CODE_DOESNT_MATCH_PARENT_CODE,
                                classifierNode.getCode(), parent.getCode());
                    }

                }
            }
        }
    }

    private void updateIds(String classifierName, ClsfNodeDTO classifierNode) {
        if (classifierNode.getNodeId() == null) {
            classifierNode.setNodeId(IdUtils.v1String());
        }
        if (!StringUtils.equals(classifierNode.getNodeId(), String.join(DOT, classifierName, ROOT_NODE_POSTFIX))
                && classifierNode.getParentId() == null) {
            classifierNode.setParentId(String.join(DOT, classifierName, ROOT_NODE_POSTFIX));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * updateClassifierNode(java.lang.String,
     * com.unidata.mdm.backend.common.dto.classifier.model.ClsfNodeDTO)
     */
    @Override
    @Transactional
    public void updateClassifierNode(
            final String classifierName,
            final ClsfNodeDTO classifierNode,
            final boolean updateRefs,
            final boolean hasData
    ) {
        if (!clsfDao.isClsfExists(classifierName)) {
            throw new BusinessException("Classifier does not exist!", ExceptionId.EX_CLASSIFIER_NOT_EXIST_FOR_UPDATE,
                    classifierName);
        }
        ClsfDTO clsf = getClassifierByName(classifierName);

        if (classifierNode.getParentId() == null) {
            classifierNode.setName(clsf.getDisplayName());
        }

        validate(clsf, classifierNode, clsf.isValidateCodeByLevel());
        validateAttributeName(clsf, classifierNode);
        if (hasData) {
            validateAttributesModification(clsf, classifierNode);
        }
        classifierNode.setUpdatedAt(new Date());
        classifierNode.setUpdatedBy(SecurityUtils.getCurrentUserName());
        if (classifierNode.getNodeId() == null) {
            if (classifierNode.getCode() != null && clsf.getCodePattern() != null && clsf.isValidateCodeByLevel()) {
                classifierNode.setNodeId(CodeParser.toNodeId(classifierNode.getCode()));
            }
            else {
                classifierNode.setNodeId(IdUtils.v1String());
            }
        }
        ClsfNodePO toUpdate = ClsfNodeDTOToPOConverter.convert(classifierNode);
        FormField nodeIdField = strictString(ClassifierHeaderField.NODE_UNIQUE_ID.getField(),
                classifierNode.getNodeId());
        SearchRequestContext searchContext = forClassifierElements().form(FormFieldsGroup.createAndGroup(nodeIdField))
                .storageId(SecurityUtils.getCurrentUserStorageId()).build();
        searchService.deleteFoundResult(searchContext);
        classifierNode.setClsfName(classifierName);
        searchService.indexClassifierNode(SecurityUtils.getCurrentUserStorageId(), classifierNode);
        clsfDao.update(toUpdate, classifierName);
        if (updateRefs) {
            updateRefsToClsf(classifierName, false);
        }

    }

    private void validateAttributesModification(ClsfDTO clsf, ClsfNodeDTO classifierNode) {
        final Map<String, ClsfNodeAttrPO> currentAttrs =
                clsfDao.getOnlyNodeAttrs(clsf.getName(), classifierNode.getNodeId())
                        .stream()
                        .collect(Collectors.toMap(ClsfNodeAttrPO::getAttrName, a -> a)
        );
        classifierNode.getNodeAttrs().forEach(attr -> {
            final String attrName = attr.getAttrName();
            if (currentAttrs.containsKey(attrName)
                    && !Objects.equals(currentAttrs.get(attrName).getDataType(), attr.getDataType().name())) {
                throw new BusinessException(
                        "Modification attribute with data",
                        ExceptionId.EX_CLASSIFIER_MODIFICATION_ATTRIBUTE_WITH_DATA,
                        classifierNode.getName()
                );
            }
        });
        final Set<String> attrs = currentAttrs.keySet();
        final Set<String> newAttrs = classifierNode.getNodeAttrs().stream()
                .map(ClsfNodeAttrDTO::getAttrName)
                .collect(Collectors.toSet());
        attrs.removeAll(newAttrs);
        if (!attrs.isEmpty()) {
            throw new BusinessException(
                    "Modification attribute with data",
                    ExceptionId.EX_CLASSIFIER_MODIFICATION_ATTRIBUTE_WITH_DATA,
                    classifierNode.getName()
            );
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#removeNode(java.
     * lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void removeNode(String classifierName, String ownNodeId, boolean updateRefs) {

        // 1. Remove from metamodel
        clsfDao.remove(classifierName, ownNodeId);

        // 2. Remove from metaindex
        FormField nodeIdField = strictString(ClassifierHeaderField.NODE_UNIQUE_ID.getField(), ownNodeId);
        SearchRequestContext searchContext = forClassifierElements()
                .form(FormFieldsGroup.createAndGroup(nodeIdField))
                .storageId(SecurityUtils.getCurrentUserStorageId())
                .build();

        searchService.deleteAll(searchContext);

        // 3. Remove from data index.
        // Data will be removed by separate job, as potentially big piece of work.
        List<AbstractEntityDef> classifiedEntities = metaModelService.getClassifiedEntities(classifierName);
        if (CollectionUtils.isNotEmpty(classifiedEntities)) {

            String classifierNodeField = String.join(".", classifierName,
                    ClassifierDataHeaderField.FIELD_NODES.getField(),
                    ClassifierDataHeaderField.FIELD_NODE_ID.getField());

            FormField classifierField = strictString(ClassifierDataHeaderField.FIELD_NAME.getField(), classifierName);
            FormField textField = strictString(classifierNodeField, ownNodeId);
            for (AbstractEntityDef entityDef : classifiedEntities) {

                searchContext = SearchRequestContext.builder(EntitySearchType.CLASSIFIER, entityDef.getName())
                        .form(FormFieldsGroup.createAndGroup(classifierField, textField))
                        .storageId(SecurityUtils.getCurrentUserStorageId())
                        .build();

                searchService.deleteAll(searchContext);
            }
        }

        // 4. Update model
        if (updateRefs) {
            updateRefsToClsf(classifierName, false);
        }

    }

    /*
 * (non-Javadoc)
 *
 * @see
 * com.unidata.mdm.backend.service.classifier.ClsfService#getNodeByNodeId(
 * java.lang.String, java.lang.String)
 */
    @Override
    @Transactional
    public boolean isNodeExist(String ownNodeId, String classifierName) {
        if (StringUtils.equals(ownNodeId, "root")) {
            ownNodeId = classifierName + "." + "root";
        }
        return clsfDao.getNodeById(classifierName, ownNodeId) != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#getNodeByNodeId(
     * java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ClsfNodeDTO getNodeByNodeId(String ownNodeId, String classifierName) {
        if (StringUtils.equals(ownNodeId, "root")) {
            ownNodeId = classifierName + "." + "root";
        }
        ClsfNodePO clsfNodePO = clsfDao.getNodeById(classifierName, ownNodeId);
        ClsfNodeDTO result = ClsfNodePOToDTOConverter.convert(clsfNodePO);
        result.setChildren(ClsfNodePOToDTOConverter.convert(clsfDao.getNodesByParentId(classifierName, ownNodeId)));
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#getNodeWithAttrs(
     * java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ClsfNodeDTO getNodeWithAttrs(final String ownNodeId, final String classifierName, final boolean reduce) {
        ClsfNodePO clsfNodePO = StringUtils.equals(ownNodeId, "root") ?
                clsfDao.getRootNode(classifierName) : clsfDao.getNodeById(classifierName, ownNodeId);
        ClsfNodeDTO result = null;
        if (clsfNodePO != null) {
            clsfNodePO.setNodeAttrs(clsfDao.getNodeAttrs(classifierName, clsfNodePO.getNodeId()));
            result = ClsfNodePOToDTOConverter.convert(clsfNodePO);
            result.setHasOwnAttrs(clsfDao.isOwnAttrs(clsfNodePO.getNodeId(), classifierName));
            result.setChildren(
                    clsfDao.findNodesByParentIdWithChildCountAndHasAttrs(classifierName, clsfNodePO.getNodeId()).stream()
                            .map(triple -> {
                                final ClsfNodeDTO clsfNodeDTO = ClsfNodePOToDTOConverter.convert(triple.getLeft());
                                clsfNodeDTO.setChildCount(triple.getMiddle());
                                clsfNodeDTO.setHasOwnAttrs(triple.getRight());
                                return clsfNodeDTO;
                            })
                            .collect(Collectors.toList())
            );
            result.setChildCount(clsfDao.countChilds(result.getNodeId(), classifierName));
        }
        if (reduce && result != null) {
            reduceClsfNodeAttrs(result);
        }
        return result;
    }

    /**
     * Reduce classifier node attributes. List may contains duplicates inherited
     * from parents. It must not be returned for 'DATA'
     *
     * @param result result
     */
    private void reduceClsfNodeAttrs(ClsfNodeDTO result) {

        List<ClsfNodeAttrDTO> nodeAttrs = result.getNodeAttrs();
        if (nodeAttrs != null && nodeAttrs.size() != 0) {
            Map<String, ClsfNodeAttrDTO> nodeAttrsM = new HashMap<>();
            for (ClsfNodeAttrDTO nodeAttr : nodeAttrs) {
                if (!nodeAttrsM.containsKey(nodeAttr.getAttrName())) {
                    nodeAttrsM.put(nodeAttr.getAttrName(), nodeAttr);
                } else if (nodeAttrsM.containsKey(nodeAttr.getAttrName()) && !nodeAttr.isInherited()) {
                    nodeAttrsM.put(nodeAttr.getAttrName(), nodeAttr);
                }
            }
            result.setNodeAttrs(new ArrayList<>(nodeAttrsM.values()));
            result.getNodeAttrs().forEach(na -> na.setInherited(false));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#buildBranchToRoot(
     * java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ClsfNodeDTO buildBranchToRoot(List<String> nodeIds, String classifierName) {
        Set<String> distinctIds = new HashSet<>();
        for (String nodeId : nodeIds) {
            List<String> nodesToRoot = clsfDao.getNodesToRoot(nodeId, classifierName);
            distinctIds.addAll(nodesToRoot);
        }

        Map<String, ClsfNodePO> nodesPO = new HashMap<>();
        for (String nodeId : distinctIds) {
            ClsfNodePO clsfNodePo = clsfDao.getNodeById(classifierName, nodeId);
            clsfNodePo.setChildCount(clsfDao.countChilds(nodeId, classifierName));
            clsfNodePo.setHasOwnAttrs(clsfDao.isOwnAttrs(clsfNodePo.getNodeId(), classifierName));
            nodesPO.put(nodeId, clsfNodePo);
        }
        nodesPO.forEach((k, v) -> {
            if (v.getParentId() != null) {
                nodesPO.get(v.getParentId()).getChildren().add(v);
            }
        });
        return ClsfNodePOToDTOConverter.convert(nodesPO.get(String.join(DOT, classifierName, ROOT_NODE_POSTFIX)));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#getAllClsfAttr(
     * java.lang.String)
     */
    @Override
    @Transactional
    public List<ClsfNodeAttrDTO> getAllClsfAttr(String classifierName) {
        List<ClsfNodeAttrPO> attrPOs = clsfDao.getAllClassifierAttrs(classifierName);
        return ClsfNodeAttrPOToDTOConverter.convert(attrPOs, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#getIdsToRoot(java.
     * lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public List<String> getIdsToRoot(String classifierNodeId, String classifierName) {
        return clsfDao.getNodesToRoot(classifierNodeId, classifierName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * addFullFilledClassifierByCode(com.unidata.mdm.backend.service.classifier.
     * dto.ClsfDTO)
     */
    @Override
    @Transactional
    public boolean addFullFilledClassifierByCode(FullClassifierDef toSave, String importFromUser) {
        List<ClassifierNodeDef> nodes = toSave.getClassifierNodes();
        final String codePattern = toSave.getClassifier().getCodePattern();
        for (ClassifierNodeDef node : nodes) {
            if (node.getParentId() != null && node.getCode() != null && !CodeParser.isValidCodeForPattern(node.getCode(), codePattern)) {
                throw new BusinessException(
                        "Pattern are not matching",
                        ExceptionId.EX_CLASSIFIER_NODE_CODE_INCORRECT,
                        node.getCode(),
                        codePattern,
                        toSave.getClassifier().getName()
                );
            }
            if (!StringUtils.isEmpty(node.getCode())) {
                final String[] codes = CodeParser.extractGroups(node.getCode(), codePattern);
                if (codes.length == 1) {
                    node.setParentId(rootNodeId(toSave.getClassifier().getName()));
                    node.setId(CodeParser.toNodeId(node.getCode()));
                } else {
                    node.setParentId(CodeParser.extractParentId(node.getCode(), codePattern));
                    node.setId(CodeParser.toNodeId(node.getCode()));
                }
            } else {
                node.setId(rootNodeId(toSave.getClassifier().getName()));
                node.setParentId(null);
            }
        }
        return addFullFilledClassifierByIds(toSave, importFromUser);
    }

    private String rootNodeId(final String classifierName) {
        return classifierName + ".root";
    }

    @Override
    @Transactional
    public boolean addFullFilledClassifierByIds(final FullClassifierDef toSave) {
        return addFullFilledClassifierByIds(toSave, SecurityUtils.getCurrentUserName());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * addFullFilledClassifierByIds(com.unidata.mdm.backend.service.classifier.
     * dto.ClsfDTO)
     */
    @Override
    @Transactional
    public boolean addFullFilledClassifierByIds(final FullClassifierDef toSave, final String importFromUser) {
        validate(toSave);
        final ClsfPO classifier = clsfDao.getClassifierByName(toSave.getClassifier().getName());
        final boolean isUpdate = classifier != null;
        final ClsfDTO clsf = new ClsfDTO();
        if (!isUpdate) {
            clsf.setCreatedAt(new Date());
            clsf.setCreatedBy(SecurityUtils.getCurrentUserName());
        }
        clsf.setCodePattern(toSave.getClassifier().getCodePattern());
        clsf.setName(toSave.getClassifier().getName());
        clsf.setDisplayName(toSave.getClassifier().getDisplayName());
        clsf.setDescription(toSave.getClassifier().getDescription());
        clsf.setValidateCodeByLevel(toSave.getClassifier().isValidateCodeByLevel());

        List<ClassifierNodeDef> nodeDefs = toSave.getClassifierNodes();
        final Collection<ClassifierNodeDef> classifierNodeDefs = orderTree(clsf, nodeDefs);

        if (isUpdate) {
            updateClassifier(clsf);
            clsfDao.removeAllNodesByClassifierId(classifier.getId());
        } else {
            createClassifier(clsf, false);
        }

        try {
            final List<List<ClsfNodeDTO>> nodes = saveClassifierNodes(clsf, classifierNodeDefs);
            nodes.forEach(batch ->
                    searchService.indexClassifierNodes(SecurityUtils.getCurrentUserStorageId(), batch)
            );
            if (isUpdate) {
                removeAllNodesForClassifierFromIndex(classifier);
            }
        } finally {
            if (isUpdate) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        runClassifierReindexJob(clsf, importFromUser);
                    }
                });
            }
        }
        return isUpdate;
    }

    private void runClassifierReindexJob(final ClsfDTO clsf, String importFromUser) {
        final String typesForReindex = metaModelService.getClassifiedEntities(clsf.getName()).stream()
                .map(AbstractEntityDef::getName)
                .collect(Collectors.joining(SearchUtils.COMMA_SEPARATOR));
        final JobDTO reindexJob = new JobDTO();
        reindexJob.setDescription("Reindex classification");
        reindexJob.setName("Reindex classification Job");
        reindexJob.setEnabled(true);
        reindexJob.setJobNameReference(ReindexDataJobConstants.JOB_NAME);

        final Map<String, String> userReport = new HashMap<>();
        userReport.put(
                ReindexDataJobConstants.USER_REPORT_MESSAGE_PARAM,
                MessageUtils.getMessage(UserMessageConstants.CLASSIFIER_IMPORT_UPDATE_STEP2_SUCCESS)
        );
        userReport.put(
                ReindexDataJobConstants.USER_REPORT_FAIL_MESSAGE_PARAM,
                MessageUtils.getMessage(UserMessageConstants.CLASSIFIER_IMPORT_UPDATE_STEP2_FAIL)
        );

        String value = "";
        try {
            value = objectMapper.writeValueAsString(userReport);
        } catch (JsonProcessingException e) {
            LOGGER.error("Can't serialize user report data.", e);
        }
        reindexJob.setParameters(
                Arrays.asList(
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_TYPES, typesForReindex),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_UPDATE_MAPPINGS, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_CLEAN_INDEXES, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_RECORDS, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_MATCHING, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_CLASSIFIERS, Boolean.TRUE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_REINDEX_RELATIONS, Boolean.FALSE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_SKIP_DQ, Boolean.TRUE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_SUPPRESS_DEFAULT_REPORT, Boolean.TRUE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_SUPPRESS_CONSISTENCY_CHECK, Boolean.TRUE),
                        new JobParameterDTO(ReindexDataJobConstants.PARAM_BLOCK_SIZE, batchSize.longValue()),
                        new JobParameterDTO(JobReportConstants.USER_NAME_PARAM, importFromUser),
                        new JobParameterDTO(ReindexDataJobConstants.USER_REPORT_PARAM, value)
                )
        );
        jobServiceExt.startSystemJob(reindexJob);
    }

    private void validate(FullClassifierDef classifierDef) {
        final String classifierName = classifierDef.getClassifier().getName();
        if (StringUtils.isBlank(classifierName)) {
            throw new DataProcessingException(
                    "Missing name of classifier.",
                    ExceptionId.EX_CLASSIFIER_FAILED_MISSING_FIELD,
                    "name"
            );
        }
        if (!NAME_PATTERN.matcher(classifierName).matches()) {
            throw new BusinessException(
                    "Incorrect name of classifier " + classifierName + ".",
                    ExceptionId.EX_CLASSIFIER_NAME_INCORRECT,
                    classifierName
            );
        }
        if (StringUtils.isBlank(classifierDef.getClassifier().getDisplayName())) {
            throw new DataProcessingException(
                    "Missing displayName of classifier " + classifierName + ".",
                    ExceptionId.EX_CLASSIFIER_FAILED_MISSING_FIELD,
                    "displayName"
            );
        }
        if (CollectionUtils.isNotEmpty(classifierDef.getClassifierNodes())) {
            classifierDef.getClassifierNodes().forEach(node -> {
                if (StringUtils.isBlank(node.getName())) {
                    throw new DataProcessingException(
                            "Missing name of node of classifier " + classifierName + ".",
                            ExceptionId.EX_CLASSIFIER_NODE_FAILED_MISSING_FIELD,
                            "name", classifierName
                    );
                }
                if (StringUtils.isBlank(node.getId())) {
                    throw new DataProcessingException(
                            "Conversion to classifier failed. Missing nodeId of node " + node.getName() + " of classifier " + classifierName + ".",
                            ExceptionId.EX_CLASSIFIER_NODE_FAILED_MISSING_FIELD,
                            "nodeId", classifierName
                    );
                }
                final List<SimpleAttributeWithOptionalValueDef> attributes = node.getAttributes();
                if (CollectionUtils.isNotEmpty(attributes)) {
                    for (int i = 0; i < attributes.size(); i++) {
                        final SimpleAttributeWithOptionalValueDef attribute = attributes.get(i);
                        if (StringUtils.isBlank(attribute.getName())) {
                            throw new DataProcessingException(
                                    "Conversion to classifier failed. Missing name of attr of node " + node.getName(),
                                    ExceptionId.EX_CLASSIFIER_NODE_ATTR_FAILED_MISSING_FIELD,
                                    "name", i, node.getName(), classifierName
                            );
                        }
                        if (StringUtils.isBlank(attribute.getDisplayName())) {
                            throw new DataProcessingException(
                                    "Conversion to classifier failed. Missing displayName of attr of node " + node.getName(),
                                    ExceptionId.EX_CLASSIFIER_NODE_ATTR_FAILED_MISSING_FIELD,
                                    "displayName", i, node.getName(), classifierName
                            );
                        }
                        if (attribute.getValueType() == null) {
                            throw new DataProcessingException(
                                    "Conversion to classifier failed. Missing valueType of attr of node " + node.getName(),
                                    ExceptionId.EX_CLASSIFIER_NODE_ATTR_FAILED_MISSING_FIELD,
                                    "valueType", i, node.getName(), classifierName
                            );
                        }
                    }
                }
            });
        }
    }

    private List<List<ClsfNodeDTO>> saveClassifierNodes(
            final ClsfDTO clsf,
            final Collection<ClassifierNodeDef> nodeDefs
    ) {
        final String clsfName = clsf.getName();
        final int clsfId = clsfDao.getClassifierByName(clsfName).getId();

        final List<List<ClsfNodeDTO>> nodes = nodeDefs.stream()
                .map(converterFromClassifierNodeDefToClsfNodeDTO(clsf, clsfName))
                .collect(new BatchCollector<>(batchSize.get()));

        nodes.forEach(batch -> {
            batchSaveClsfNodes(clsfId, batch);
            batchSaveAttrs(clsfId, batch);
        });
        return nodes;
    }

    private Collection<ClassifierNodeDef> orderTree(
            final ClsfDTO classifier,
            final List<ClassifierNodeDef> nodeDefs
    ) {
        final String classifierName = classifier.getName();
        final List<ClassifierNodeDef> orderedTree = new ArrayList<>();
        final Set<String> nodesIdsInTree = new HashSet<>();
        final Set<Triple<String, String, String>> nodesKeys = new HashSet<>();
        final Set<String> nodesIds = new HashSet<>();
        final Map<String, List<ClassifierNodeDef>> nodesNotInTree = new TreeMap<>();
        final List<String> rootNodesNames = new ArrayList<>();
        final List<String> duplicatedNodes = new ArrayList<>();
        final List<String> duplicatedIds = new ArrayList<>();
        nodeDefs.forEach(
                (classifierNodeDef) -> {
                    final Triple<String, String, String> nodeKey = Triple.of(
                            classifierNodeDef.getParentId(), classifierNodeDef.getCode(), classifierNodeDef.getName()
                    );
                    if (!nodesKeys.add(nodeKey)) {
                        duplicatedNodes.add(classifierNodeDef.getName());
                    }
                    if (!nodesIds.add(classifierNodeDef.getId())) {
                        duplicatedIds.add(classifierNodeDef.getId());
                    }
                    if (StringUtils.isEmpty(classifierNodeDef.getParentId())) {
                        rootNodesNames.add(classifierNodeDef.getName());
                        classifierNodeDef.setName(classifier.getDisplayName());
                    }
                    if (StringUtils.isEmpty(classifierNodeDef.getParentId()) || nodesIdsInTree.contains(classifierNodeDef.getParentId())) {
                        nodesIdsInTree.add(classifierNodeDef.getId());
                        orderedTree.add(classifierNodeDef);
                        if (nodesNotInTree.containsKey(classifierNodeDef.getId())) {
                            orderedTree.addAll(nodesNotInTree.remove(classifierNodeDef.getId()));
                        }
                    } else {
                        if (!nodesNotInTree.containsKey(classifierNodeDef.getParentId())) {
                            nodesNotInTree.put(classifierNodeDef.getParentId(), new ArrayList<>());
                        }
                        nodesNotInTree.get(classifierNodeDef.getParentId()).add(classifierNodeDef);
                    }
                }
        );
        if (StringUtils.isNotEmpty(classifier.getCodePattern()) && !classifier.isValidateCodeByLevel() && !nodesNotInTree.isEmpty()) {
            orderedTree.addAll(resolveToClosestParent(classifier, nodesNotInTree, nodesIdsInTree));
            nodesNotInTree.clear();
        }
        validateClsfOrderedTree(classifierName, nodesNotInTree, rootNodesNames, duplicatedNodes, duplicatedIds);
        return orderedTree;
    }

    private List<ClassifierNodeDef> resolveToClosestParent(
            final ClsfDTO classifier,
            final Map<String, List<ClassifierNodeDef>> nodesNotInTree,
            final Set<String> nodesIdsInTree
    ) {
        return nodesNotInTree.entrySet().stream().flatMap(entry -> {
            final List<ClassifierNodeDef> nodes = entry.getValue();
            if (nodesIdsInTree.contains(entry.getKey())) {
                return nodes.stream();
            }
            final ClassifierNodeDef classifierNodeDef = nodes.get(0);
            final String parentId = findExsitParentIdForNode(classifier, classifierNodeDef, nodesIdsInTree);
            return nodes.stream().peek(node -> node.setParentId(parentId));
        }).collect(Collectors.toList());
    }

    private String findExsitParentIdForNode(
            final ClsfDTO classifier,
            final ClassifierNodeDef classifierNodeDef,
            final Set<String> nodesIdsInTree
    ) {
        final List<String> ids = CodeParser.extractParentIds(classifierNodeDef.getCode(), classifier.getCodePattern());
        return ids.stream()
                .filter(nodesIdsInTree::contains)
                .findFirst()
                .orElseGet(() -> rootNodeId(classifier.getName()));
    }

    private void validateClsfOrderedTree(
            final String classifierName,
            final Map<String, List<ClassifierNodeDef>> nodesNotInTree,
            final List<String> rootNodesNames,
            final List<String> duplicatedNodes,
            final List<String> duplicatedIds
    ) {
        if (duplicatedNodes.size() > 0) {
            throw new BusinessException(
                    "Found duplicate nodes " + duplicatedNodes + " in classifier " + classifierName,
                    ExceptionId.EX_CLASSIFIER_DUPLICATE_NODES,
                    classifierName,
                    duplicatedNodes
            );
        }
        if (duplicatedIds.size() > 0) {
            throw new BusinessException(
                    "Found duplicate ids " + duplicatedIds + " in classifier " + classifierName,
                    ExceptionId.EX_CLASSIFIER_DUPLICATE_IDS,
                    classifierName,
                    duplicatedIds
            );
        }
        if (rootNodesNames.size() > 1) {
            throw new BusinessException(
                    "More then one root nodes " + rootNodesNames + " in classifier " + classifierName,
                    ExceptionId.EX_CLASSIFIER_MORE_THEN_ONE_ROOT_NODES,
                    classifierName,
                    rootNodesNames
            );
        }
        if (!nodesNotInTree.isEmpty()) {
            final List<String> detachedNodes =
                    nodesNotInTree.values().stream()
                            .flatMap(Collection::stream)
                            .map(ClassifierNodeDef::getId)
                            .collect(Collectors.toList());
            throw new BusinessException(
                    "Nodes didn't attached to root " + detachedNodes + " in classifier " + classifierName,
                    ExceptionId.EX_CLASSIFIER_NOT_ATTACHED_NODES_TO_ROOT,
                    classifierName,
                    detachedNodes
            );
        }
    }

    private Function<ClassifierNodeDef, ClsfNodeDTO> converterFromClassifierNodeDefToClsfNodeDTO(
            final ClsfDTO clsf,
            final String clsfName
    ) {
        return classifierNodeDef -> {
            final ClsfNodeDTO clsfNodeDTO = convertToDTO(classifierNodeDef);
            validate(clsf, clsfNodeDTO, false);
            clsfNodeDTO.setClsfName(clsfName);
            return clsfNodeDTO;
        };
    }

    private ClsfNodeDTO convertToDTO(final ClassifierNodeDef classifierNodeDef) {
        return new ClsfNodeDTO(
                classifierNodeDef.getName(),
                classifierNodeDef.getId(),
                classifierNodeDef.getParentId(),
                classifierNodeDef.getCode(),
                classifierNodeDef.getDescription(),
                SecurityUtils.getCurrentUserName(),
                new Date(),
                classifierNodeDef.getAttributes().stream()
                        .map(simpleAttributeDef -> new ClsfNodeAttrDTO(
                                simpleAttributeDef.getName(),
                                DataType.valueOf(simpleAttributeDef.getValueType().name()),
                                convertToObj(simpleAttributeDef.getValue()),
                                simpleAttributeDef.getDisplayName(),
                                simpleAttributeDef.getDescription(),
                                simpleAttributeDef.isHidden(),
                                simpleAttributeDef.isNullable(),
                                simpleAttributeDef.isReadOnly(),
                                simpleAttributeDef.isSearchable(),
                                simpleAttributeDef.isUnique(),
                                SecurityUtils.getCurrentUserName(),
                                new Date()
                        ))
                        .collect(Collectors.toList())
        );
    }

    private void batchSaveClsfNodes(final int classifierId, final List<ClsfNodeDTO> nodes) {
        clsfDao.create(
                classifierId,
                ClsfNodeDTOToPOConverter.convert(nodes)
        );
    }

    private void batchSaveAttrs(final int clsfId, final List<ClsfNodeDTO> batch) {
        batch.stream()
                .flatMap(node ->
                        ClsfNodeAttrDTOToPOConverter.convert(node.getNodeAttrs()).stream()
                                .map(attr -> Pair.of(node, attr))
                )
                .collect(new BatchCollector<>(batchSize.get()))
                .forEach(attrsBatch -> clsfDao.insertNodeAttrs(attrsBatch, clsfId));
    }

    private void removeAllNodesForClassifierFromIndex(ClsfPO classifier) {
        final FormField clsfNameField = strictString(ClassifierHeaderField.CLASSIFIER_NAME.getField(), classifier.getName());
        final SearchRequestContext searchContext = forClassifierElements()
                .form(FormFieldsGroup.createAndGroup(clsfNameField))
                .storageId(SecurityUtils.getCurrentUserStorageId())
                .build();
        searchService.deleteAll(searchContext);

        final List<AbstractEntityDef> classifiedEntities = metaModelService.getClassifiedEntities(classifier.getName());
        for (AbstractEntityDef classifiedEntity : classifiedEntities) {
            final SearchRequestContext sc = SearchRequestContext.builder(EntitySearchType.CLASSIFIER, classifiedEntity.getName())
                    .form(FormFieldsGroup.createAndGroup(clsfNameField))
                    .storageId(SecurityUtils.getCurrentUserStorageId())
                    .build();

            searchService.deleteAll(sc);
        }
    }

    /**
     * Convert to obj.
     *
     * @param value the value
     * @return the object
     */
    private Object convertToObj(ClassifierValueDef value) {
        if (value == null || value.getType() == null) {
            return null;
        }
        Object result = null;
        switch (value.getType()) {
            case BLOB:
            case CLOB:
                result = null;
                break;
            case BOOLEAN:
                result = value.isBoolValue();
                break;
            case DATE:
                result = value.getDateValue() != null
                        ? Date.from(value.getDateValue().atStartOfDay(ZoneId.systemDefault()).toInstant()) : null;
                break;
            case TIME:
                result = value.getTimeValue() != null ? Date.from(
                        value.getTimeValue().atDate(LocalDate.of(1970, 1, 1)).atZone(ZoneId.systemDefault()).toInstant())
                        : null;
                break;
            case TIMESTAMP:
                result = value.getTimestampValue() != null
                        ? Date.from(value.getTimestampValue().atZone(ZoneId.systemDefault()).toInstant()) : null;
                break;
            case INTEGER:
                result = value.getIntValue();
                break;
            case NUMBER:
                result = value.getNumberValue();
                break;
            case STRING:
                result = value.getStringValue();
                break;
            default:
                break;
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.unidata.mdm.backend.service.classifier.ClsfService#
     * getClassifierByNameWithAllNodes(java.lang.String)
     */
    @Override
    public ClsfDTO getClassifierByNameWithAllNodes(String classifierName) {
        ClsfDTO result = getClassifierByName(classifierName);
        List<ClsfNodeDTO> nodes = ClsfNodePOToDTOConverter.convert(clsfDao.getAllNodes(classifierName));
        if (nodes == null || nodes.size() == 0) {
            return result;
        }
        Map<String, ClsfNodeDTO> interim = new HashMap<>();
        for (ClsfNodeDTO node : nodes) {
            interim.put(node.getNodeId(), node);
        }
        interim.forEach((k, v) -> {
            if (StringUtils.isEmpty(v.getParentId())) {
                result.setRootNode(v);
            } else {
                interim.get(v.getParentId()).getChildren().add(v);
            }
        });
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#findNodeByFullPath
     * (java.lang.String, java.lang.String)
     */
    @Override
    public ClsfNodeDTO findNodeByFullPath(String clsfName, String classifierPointer) {
        ClsfNodePO nodePO = clsfDao.getNodeByPath(clsfName, classifierPointer);
        return ClsfNodePOToDTOConverter.convert(nodePO);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.unidata.mdm.backend.service.classifier.ClsfService#findNodeByCode(
     * java.lang.String, java.lang.String)
     */
    @Override
    public ClsfNodeDTO findNodeByCode(String clsfName, String classifierPointer) {
        ClsfNodePO nodePO = clsfDao.getNodeByCode(clsfName, classifierPointer);
        return ClsfNodePOToDTOConverter.convert(nodePO);
    }

    /**
     * Removes the refs to clsf.
     *
     * @param clsfName the clsf name
     */
    private void updateRefsToClsf(String clsfName, boolean remove) {
        List<EntityDef> entityDefs = metaModelService.getEntitiesList();
        if (entityDefs.size() != 0) {
            for (EntityDef entityDef : entityDefs) {
                updateRefsToClsf(clsfName, entityDef, remove);
            }
        }
        List<LookupEntityDef> lookupEntityDefs = metaModelService.getLookupEntitiesList();
        if (lookupEntityDefs.size() != 0) {
            for (LookupEntityDef lookupEntityDef : lookupEntityDefs) {
                updateRefsToClsf(clsfName, lookupEntityDef, remove);
            }
        }
    }

    /**
     * Removes the refs to clsf.
     *
     * @param clsfName the clsf name
     * @param eDef the e def
     */
    private void updateRefsToClsf(String clsfName, LookupEntityDef eDef, boolean remove) {
        if (eDef == null || eDef.getClassifiers() == null || eDef.getClassifiers().size() == 0
                || !eDef.getClassifiers().contains(clsfName)) {
            return;
        }
        if (remove) {
            eDef.getClassifiers().remove(clsfName);
        }
        UpdateModelRequestContext ctx = new UpdateModelRequestContextBuilder()
                .lookupEntityUpdate(Collections.singletonList(eDef))
                .relationsUpdate(metaModelService.getRelationsByFromEntityName(eDef.getName()))
                .storageId(SecurityUtils.getCurrentUserStorageId()).build();
        metaModelService.upsertModel(ctx);
    }

    /**
     * Removes the refs to clsf.
     *
     * @param clsfName the clsf name
     * @param eDef the e def
     */
    private void updateRefsToClsf(String clsfName, EntityDef eDef, boolean remove) {
        if (eDef == null || eDef.getClassifiers() == null || eDef.getClassifiers().size() == 0
                || !eDef.getClassifiers().contains(clsfName)) {
            return;
        }
        if (remove) {
            eDef.getClassifiers().remove(clsfName);
        }
        List<NestedEntityDef> nestedEntityUpdate = metaModelService.getNestedEntitiesByTopLevelId(eDef.getName());
        UpdateModelRequestContext ctx = new UpdateModelRequestContextBuilder()
                .entityUpdate(Collections.singletonList(eDef)).nestedEntityUpdate(nestedEntityUpdate)
                .relationsUpdate(metaModelService.getRelationsByFromEntityName(eDef.getName()))
                .storageId(SecurityUtils.getCurrentUserStorageId()).build();
        metaModelService.upsertModel(ctx);
    }

    @Override
    public void subscribe(Flux<Map<String, Optional<? extends Serializable>>> updates) {
        final String batchSizePropertyKey = UnidataConfigurationProperty.CLASSIFIER_IMPORT_BATCH_SIZE.getKey();
        updates
                .filter(values ->
                        values.containsKey(batchSizePropertyKey) && values.get(batchSizePropertyKey).isPresent()
                )
                .map(values -> (Integer) values.get(batchSizePropertyKey).get())
                .subscribe(batchSize::set);
    }

    /**
     * Collect stream into fixed size batches.
     * !Non-thread safe.! Don't use in parallel streams.
     *
     * @param <T> Type of elements in batches.
     */
    public static class BatchCollector<T> implements Collector<T, List<List<T>>, List<List<T>>> {

        private final int batchSize;

        private final int[] counterHolder = {0};

        public BatchCollector(int batchSize) {
            this.batchSize = batchSize;
        }

        @Override
        public Supplier<List<List<T>>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<List<T>>, T> accumulator() {
            return (lists, value) -> {
                final int currentPosition = counterHolder[0]++;
                final int currentBatchIndex = currentPosition / batchSize;
                if (currentBatchIndex == lists.size()) {
                    lists.add(new ArrayList<>(batchSize));
                }
                lists.get(currentBatchIndex).add(value);
            };
        }

        @Override
        public BinaryOperator<List<List<T>>> combiner() {
            return (l1, l2) -> {
                l1.addAll(l2);
                return l1;
            };
        }

        @Override
        public Function<List<List<T>>, List<List<T>>> finisher() {
            return (e) -> e;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(Collector.Characteristics.IDENTITY_FINISH);
        }
    }
}
