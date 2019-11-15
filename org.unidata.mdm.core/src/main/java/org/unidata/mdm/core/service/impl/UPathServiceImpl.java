package org.unidata.mdm.core.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.unidata.mdm.core.exception.CoreExceptionIds;
import org.unidata.mdm.core.service.UPathService;
import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.ArrayAttribute.ArrayDataType;
import org.unidata.mdm.core.type.data.ArrayValue;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.Attribute.AttributeType;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.SimpleAttribute.DataType;
import org.unidata.mdm.core.type.data.impl.ComplexAttributeImpl;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.AttributeModelElement.AttributeValueType;
import org.unidata.mdm.core.type.upath.UPath;
import org.unidata.mdm.core.type.upath.UPathApplicationMode;
import org.unidata.mdm.core.type.upath.UPathConstants;
import org.unidata.mdm.core.type.upath.UPathElement;
import org.unidata.mdm.core.type.upath.UPathElementType;
import org.unidata.mdm.core.type.upath.UPathExecutionContext;
import org.unidata.mdm.core.type.upath.UPathIncompletePath;
import org.unidata.mdm.core.type.upath.UPathResult;
import org.unidata.mdm.system.exception.PlatformFailureException;

/**
 * @author Mikhail Mikhailov
 * UPath bits implementation.
 */
@Service
public class UPathServiceImpl implements UPathService {
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UPathService.class);
    /**
     * Constructor.
     */
    public UPathServiceImpl() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UPath upathCreate(String entity, String path, Map<String, AttributeModelElement> info) {

        if (StringUtils.isBlank(entity) || StringUtils.isBlank(path)) {
            final String message = "Invalid input. Entity name [{}] or path [{}] blank.";
            LOGGER.warn(message, entity, path);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_INPUT_ENTITY_OR_PATH_BLANK, entity, path);
        }

        if (MapUtils.isEmpty(info)) {
            final String message = "Entity not found by name [{}].";
            LOGGER.warn(message, entity);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_ENTITY_NOT_FOUND_BY_NAME, entity);
        }

        // Check for whole record, being requested.
        String[] pathTokens = splitPath(path);
        if (pathTokens.length == 0) {
            final String message = "Invalid input. Path [{}] was split to zero elements.";
            LOGGER.warn(message, path);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_INPUT_SPLIT_TO_ZERO_ELEMENTS, path);
        }

        UPath upath = new UPath(entity);
        for (int i = 0; i < pathTokens.length; i++) {

            String token = pathTokens[i];

            // 1. Subscript or expression.
            if ((i == 0 && checkRootRecord(upath, token, info))
              || checkSubscriptFilter(upath, token, info)
              || checkExpressionFilter(upath, token, info)) {
                continue;
            }

            AttributeModelElement check = checkPathComponent(upath, info, token);

            // 2. Simple collecting
            upath.getElements().add(new UPathElement(token, UPathElementType.COLLECTING, dr -> true, check));
        }

        return upath;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UPathResult upathResult(String entity, String path, Map<String, AttributeModelElement> info, DataRecord record) {
        UPath upath = upathCreate(entity, path, info);
        return upathGet(upath, record, UPathApplicationMode.MODE_ALL);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UPathResult upathGet(UPath upath, DataRecord record, UPathExecutionContext context, UPathApplicationMode mode) {

        if (context == UPathExecutionContext.FULL_TREE) {
            return upathFullTreeGetImpl(upath, record, mode);
        } else if (context == UPathExecutionContext.SUB_TREE) {
            return upathSubTreeGetImpl(upath, record, mode);
        }

        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UPathResult upathGet(UPath upath, DataRecord record, UPathApplicationMode mode) {
        return upathGet(upath, record, UPathExecutionContext.FULL_TREE, mode);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public UPathResult upathGet(UPath upath, DataRecord record) {
        return upathGet(upath, record, UPathApplicationMode.MODE_ALL);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean upathSet(UPath upath, DataRecord record, Attribute target) {
        return upathSet(upath, record, target, UPathApplicationMode.MODE_ALL);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean upathSet(UPath upath, DataRecord record, Attribute target, UPathApplicationMode mode) {
        return upathSet(upath, record, target, UPathExecutionContext.FULL_TREE, mode);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean upathSet(UPath upath, DataRecord record, Attribute target, UPathExecutionContext context,
            UPathApplicationMode mode) {

        if (context == UPathExecutionContext.FULL_TREE) {
            return upathFullTreeSetImpl(upath, record, target, mode);
        } else if (context == UPathExecutionContext.SUB_TREE) {
            return upathSubTreeSetImpl(upath, record, target, mode);
        }

        return false;
    }

    private UPathResult upathSubTreeGetImpl(UPath upath, DataRecord record, UPathApplicationMode mode) {

        UPathResult result = new UPathResult(mode);

        // 1. Return immediately if this upath is empty or no input exist
        if (Objects.isNull(record) || upath.getElements().isEmpty()) {
            return result;
        }

        List<Pair<String, DataRecord>> segmentsChain = toSegmentsChain(record);

        // 2. Check root path and return immediately
        if (upath.isRoot()) {
            result.getAttributes().add(ComplexAttributeImpl.ofUnattended(UPathConstants.UPATH_ROOT_NAME, segmentsChain.get(0).getRight()));
            return result;
        }

        // 3. Check number of segments and proceed with diff
        int segmentsDiff = upath.getNumberOfSegments() - (segmentsChain.size() - 1);
        if (segmentsDiff > 0) {

            // 3.1. Childern, possibly filtered
            UPath subPath = upath.getSubSegmentsUPath(segmentsChain.size() - 1);
            return upathFullTreeGetImpl(subPath, record, mode);
        } else {

            // 3.2. Ancestors path processing
            List<UPathElement> upathSegments = upath.getSegments();
            for (int i = 0; i < upathSegments.size(); i++) {

                UPathElement element = upathSegments.get(i);
                Pair<String, DataRecord> source = segmentsChain.get(i);

                // 3.2.1 Check attribute exists to ensure path validity
                if (i < (upathSegments.size() - 1)) {

                    Attribute selection = source.getRight().getAttribute(element.getElement());
                    if (Objects.isNull(selection) || selection.getAttributeType() != AttributeType.COMPLEX) {

                        // Path lost. Return.
                        return result;
                    }

                    continue;
                }

                // 3.2.2 Last segment
                Attribute selection = source.getRight().getAttribute(element.getElement());
                if (Objects.nonNull(selection)) {

                    // 3.2.1.1 Complex attribute
                    if (selection.getAttributeType() == AttributeType.COMPLEX && segmentsChain.size() > (i + 1)) {
                        source = segmentsChain.get(i + 1);
                        result.getAttributes().add(ComplexAttributeImpl.ofUnattended(source.getLeft(), source.getRight()));
                    // 3.2.1.2 Simple, code, array
                    } else {
                        result.getAttributes().add(selection);
                    }
                }
            }
        }

        return result;
    }

    private UPathResult upathFullTreeGetImpl(UPath upath, DataRecord record, UPathApplicationMode mode) {

        UPathResult result = new UPathResult(mode);

        // Return immediatly if this upath is empty or no input exist
        if (Objects.isNull(record) || upath.getElements().isEmpty()) {
            return result;
        }

        List<Attribute> collected = new ArrayList<>();
        List<Attribute> packaged = new ArrayList<>();

        // Add the first one for iteration.
        packaged.add(ComplexAttributeImpl.ofUnattended(UPathConstants.UPATH_ROOT_NAME, record));

        for (int i = 0; i < upath.getElements().size(); i++) {

            // Nothing filtered.
            if (packaged.isEmpty()) {
                break;
            }

            UPathElement element = upath.getElements().get(i);
            boolean isComplex = false;
            boolean isTerminating = i == (upath.getElements().size() - 1);

            // For each complex attribute
            for (ListIterator<Attribute> ci = packaged.listIterator(); ci.hasNext(); ) {

                ComplexAttribute holder = (ComplexAttribute) ci.next();
                for (Iterator<DataRecord> li = holder.iterator(); li.hasNext(); ) {

                    // Filtering
                    DataRecord dr = li.next();
                    if (element.isFiltering()) {

                        if (!element.getPredicate().test(dr)) {
                            li.remove();
                        }

                        continue;
                    }

                    // Collecting
                    Attribute attr = dr.getAttribute(element.getElement());
                    if (Objects.nonNull(attr)) {

                        isComplex = attr.getAttributeType() == AttributeType.COMPLEX;
                        if (!isTerminating && !isComplex) {
                            final String message = "Attribute selected for an intermediate path element [{}] is not a complex attribute.";
                            LOGGER.warn(message, element.getElement());
                            throw new PlatformFailureException(message,
                                    CoreExceptionIds.EX_UPATH_NOT_A_COMPLEX_ATTRIBUTE_FOR_INTERMEDIATE_PATH_ELEMENT,
                                    element.getElement());
                        }

                        // UN-9738 handle complex attributes with no records
                        if (isComplex && !isTerminating) {
                            ComplexAttribute ca = attr.narrow();
                            if (ca.isEmpty() && mode == UPathApplicationMode.MODE_ALL_WITH_INCOMPLETE) {
                                result.getIncomplete().add(new UPathIncompletePath(dr, element));
                                // Prevent collecting of empty attribute
                                continue;
                            }
                        }

                        collected.add(attr);
                    } else {
                        // Collect incomplete element, if requested
                        if (mode == UPathApplicationMode.MODE_ALL_WITH_INCOMPLETE) {
                            result.getIncomplete().add(new UPathIncompletePath(dr, element));
                        }
                    }
                }

                // Remove attribute, if empty
                if (holder.isEmpty()) {
                    ci.remove();
                }
            }

            if (element.isCollecting()) {

                packaged.clear();
                if (isComplex) {
                    for (Attribute attr : collected) {
                        packaged.add(ComplexAttributeImpl.ofUnattended(attr.getName(), ((ComplexAttribute) attr).toCollection()));
                    }

                    collected.clear();
                }
            }
        }

        // Return only one value.
        // Wait until this is reached, because of the case with filtered and of path, denoting complex attributes
        if (mode == UPathApplicationMode.MODE_ONCE) {

            if (CollectionUtils.isNotEmpty(collected)) {
                result.getAttributes().add(collected.get(0));
            }

            if (CollectionUtils.isNotEmpty(packaged)) {
                result.getAttributes().add(packaged.get(0));
            }
        } else {

            result.getAttributes().addAll(collected);
            result.getAttributes().addAll(packaged);
        }

        return result;
    }
    /**
     * Sets the attribute to target record.
     * @param upath UPath to process
     * @param data the record to manipulate
     * @param target target attribute to set
     * @param mode application mode
     * @return modified record
     */
    private boolean upathSubTreeSetImpl(UPath upath, DataRecord data, Attribute target, UPathApplicationMode mode) {

        // 1. Return immediately if this upath is empty, denotes root (what is not allowed) or record is null
        if (upath.getElements().isEmpty() || upath.isRoot() || Objects.isNull(data)) {
            return false;
        }

        // 2. Collect segments
        List<Pair<String, DataRecord>> segmentsChain = toSegmentsChain(data);

        // 3. Check number of segments and proceed with diff
        int segmentsDiff = upath.getNumberOfSegments() - (segmentsChain.size() - 1);
        if (segmentsDiff > 0) {

            // 3.1. Childern, possibly filtered
            UPath subPath = upath.getSubSegmentsUPath(segmentsChain.size() - 1);
            return upathFullTreeSetImpl(subPath, data, target, mode);
        } else {

            // 3.2. Check, we're generally able to set with params
            checkGeneralSetAbility(upath.getElements().get(upath.getElements().size() - 1), target);

            // 3.3. Ancestors path processing
            List<UPathElement> upathSegments = upath.getSegments();
            for (int i = 0; i < upathSegments.size(); i++) {

                UPathElement element = upathSegments.get(i);
                Pair<String, DataRecord> source = segmentsChain.get(i);

                // 3.2.1 Check attribute exists to ensure path validity
                if (i < (upathSegments.size() - 1)) {

                    Attribute selection = source.getRight().getAttribute(element.getElement());
                    if (Objects.isNull(selection) || selection.getAttributeType() != AttributeType.COMPLEX) {

                        // Path lost. Return.
                        return false;
                    }

                    continue;
                }

                // 3.2.2 Last segment. Put.
                source.getRight().addAttribute(target);
                return true;
            }
        }

        return false;
    }
    /**
     * Sets the attribute to target record.
     * @param upath UPath to process
     * @param source the record to manipulate
     * @param target target attribute to set
     * @param mode application mode
     * @return modified record
     */
    private boolean upathFullTreeSetImpl(UPath upath, DataRecord source, Attribute target, UPathApplicationMode mode) {

        // Return immediately if this upath is empty
        if (upath.getElements().isEmpty() || Objects.isNull(source)) {
            return false;
        }

        // Check, we're generally able to set with params
        checkGeneralSetAbility(upath.getElements().get(upath.getElements().size() - 1), target);

        // Think about whether such behaviour is really desired.
        DataRecord record = Objects.isNull(source) ? new SerializableDataRecord() : source;

        List<Attribute> collected = new ArrayList<>(16);
        List<Attribute> packaged = new ArrayList<>(8);

        // Add the first one for iteration.
        packaged.add(ComplexAttributeImpl.ofUnattended("ROOT", record));

        boolean hadApplications = false;
        for (int i = 0; i < upath.getElements().size(); i++) {

            // Nothing filtered.
            if (packaged.isEmpty()) {
                break;
            }

            UPathElement element = upath.getElements().get(i);
            boolean isComplex = false;
            boolean isTerminating = i == (upath.getElements().size() - 1);

            // For each complex attribute
            for (ListIterator<Attribute> ci = packaged.listIterator(); ci.hasNext(); ) {

                ComplexAttribute holder = (ComplexAttribute) ci.next();
                for (Iterator<DataRecord> li = holder.iterator(); li.hasNext(); ) {

                    // Filtering
                    DataRecord dr = li.next();
                    if (element.isFiltering()) {

                        if (!element.getPredicate().test(dr)) {
                            li.remove();
                        }

                        continue;
                    }

                    // Set
                    if (isTerminating) {

                        dr.addAttribute(target);

                        // And finish
                        if (mode == UPathApplicationMode.MODE_ALL || mode == UPathApplicationMode.MODE_ALL_WITH_INCOMPLETE) {
                            hadApplications = true;
                            continue;
                        } else if (mode == UPathApplicationMode.MODE_ONCE) {
                            return true;
                        }
                    }

                    // Collecting
                    Attribute attr = dr.getAttribute(element.getElement());
                    if (Objects.nonNull(attr)) {

                        isComplex = attr.getAttributeType() == AttributeType.COMPLEX;
                        if (!isComplex) {
                            final String message = "Attribute selected for an intermediate path element [{}] is not a complex attribute.";
                            LOGGER.warn(message, element.getElement());
                            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_SET_NOT_A_COMPLEX_FOR_INTERMEDIATE, element.getElement());
                        }

                        collected.add(attr);
                    }
                }

                // Remove attribute, if empty
                if (holder.isEmpty()) {
                    ci.remove();
                }
            }

            if (element.isCollecting()) {

                packaged.clear();
                if (isComplex) {
                    for (Attribute attr : collected) {
                        packaged.add(ComplexAttributeImpl.ofUnattended(attr.getName(), ((ComplexAttribute) attr).toCollection()));
                    }

                    collected.clear();
                }
            }
        }

        return hadApplications;
    }
    /**
     * Builds segments chain from bottom to top.
     * @param last the lowest end of the hierarhie
     * @return chain
     */
    private List<Pair<String, DataRecord>> toSegmentsChain(DataRecord last) {

        if (Objects.isNull(last)) {
            return Collections.emptyList();
        }

        List<Pair<String, DataRecord>> chain = new ArrayList<>();
        DataRecord backpointer = last;
        while (!backpointer.isTopLevel()) {
            chain.add(0, new ImmutablePair<>(backpointer.getHolderAttribute().getName(), backpointer));
            backpointer = backpointer.getParentRecord();
        }

        chain.add(0, new ImmutablePair<String, DataRecord>(UPathConstants.UPATH_ROOT_NAME, backpointer));
        return chain;
    }
    /**
     * MMI: Taken from apache-commons StringUtils and modified for our needs.
     *
     * Performs the logic for the {@code split} and
     * {@code splitPreserveAllTokens} methods that do not return a
     * maximum array length.
     *
     * @param str  the String to parse, may not be {@code null}
     * @param separatorChar the separate character
     * @param preserveAllTokens if {@code true}, adjacent separators are
     * treated as empty token separators; if {@code false}, adjacent
     * separators are treated as one separator.
     * @return an array of parsed Strings, {@code null} if null String input
     */
    private String[] splitPath(@Nonnull final String str) {

        final int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        final List<String> list = new ArrayList<>();

        int i = 0;
        int start = 0;
        boolean hasChars = false;

        while (i < len) {

            if (str.charAt(i) == UPathConstants.UPATH_SEPARATOR_CHAR) {
                if (hasChars) {

                    // UD, honor escape sym followed by path separator,
                    // which may be part of expression
                    if (str.charAt(i - 1) == UPathConstants.UPATH_ESCAPE_CHAR) {
                        hasChars = true;
                        i++;
                        continue;
                    }

                    list.add(str.substring(start, i));
                    hasChars = false;
                }

                start = ++i;
                continue;
            }

            hasChars = true;
            i++;
        }

        if (hasChars) {
            list.add(str.substring(start, i));
        }

        return list.toArray(new String[list.size()]);
    }
    /**
     * Checks the general ability to set the target attribute to this UPath.
     * @param last last UPath element
     * @param target the target attribute
     */
    private void checkGeneralSetAbility(UPathElement last, Attribute target) {

        if (last.getType() != UPathElementType.COLLECTING) {
            final String message = "Invalid input. UPath for set operations must end with collecting element. Element '{}' is not a collecting one.";
            LOGGER.warn(message, last.getElement());
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_SET_WRONG_END_ELEMENT, last.getElement());
        }

        if (!isSetOperationTypeConform(target, last.getInfo())) {
            final String message = "Invalid input. Last element of this UPath '{}' and target attribute have different value types.";
            LOGGER.warn(message, last.getElement());
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_SET_WRONG_TARGET_ATTRIBUTE_TYPE, last.getElement());
        }

        if (!StringUtils.equals(last.getElement(), target.getName())) {
            final String message = "Invalid input. Attribute '{}' and last UPath element '{}' have different names.";
            LOGGER.warn(message, target.getName(), last.getElement());
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_SET_WRONG_ATTRIBUTE_NAME, target.getName(), last.getElement());
        }
    }
    /**
     * Check root record special notation.
     * @param upath the {@link UPath} currently being built
     * @param element the element being processed
     * @return true, if element has root special notation, false otherwise
     */
    private boolean checkRootRecord(UPath upath, String element, Map<String, AttributeModelElement> info) {

        int start = element.indexOf(UPathConstants.UPATH_EXPRESSION_START);
        if (start == -1) {
            return false;
        }

        int end = element.indexOf(UPathConstants.UPATH_EXPRESSION_END, start);
        if (end == -1) {
            final String message = "Invalid input. Root record expression incorrect [{}].";
            LOGGER.warn(message, element);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_ROOT_EXPRESSION, element);
        }

        boolean isUnfilteredRoot = end - start == 1;
        if (isUnfilteredRoot) {
            upath.getElements().add(new UPathElement(UPathConstants.UPATH_ROOT_NAME, UPathElementType.EXPRESSION, dr -> true, null));
            return true;
        } else {
            return checkExpressionFilter(upath, element, info);
        }
    }
    /**
     * Check subscript (record ordinal).
     * @param upath the {@link UPath} currently being built
     * @param element the element being processed
     * @return true, if element has subscript filtering, false otherwise
     */
    private boolean checkSubscriptFilter(UPath upath, String element, Map<String, AttributeModelElement> info) {

        int start = element.indexOf(UPathConstants.UPATH_SUBSCRIPT_START);
        if (start == -1) {
            return false;
        }

        int end = element.indexOf(UPathConstants.UPATH_SUBSCRIPT_END, start);
        if (end == -1) {
            final String message = "Invalid input. Subscript expression incorrect [{}].";
            LOGGER.warn(message, element);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_SUBSCRIPT_EXPRESSION, element);
        }

        final String subscriptAsString = element.substring(start + 1, end);
        final int ordinal = Integer.parseUnsignedInt(subscriptAsString);
        final String subpath = element.substring(0, start);

        AttributeModelElement checked = checkPathComponent(upath, info, subpath);

        upath.getElements().add(new UPathElement(subpath, UPathElementType.COLLECTING, dr -> true, checked));
        upath.getElements().add(new UPathElement(element.substring(start, (end + 1)), UPathElementType.SUBSCRIPT, dr -> dr.getOrdinal() == ordinal, null));

        return true;
    }
    /**
     * Checks the supplied expression.
     * @param upath current upath
     * @param element the element name
     * @param info entity metadata
     * @return true, if the element represents an expression, false otherwise
     */
    private boolean checkExpressionFilter(UPath upath, String element, Map<String, AttributeModelElement> info) {

        int start = element.indexOf(UPathConstants.UPATH_EXPRESSION_START);
        if (start == -1) {
            return false;
        }

        int mid = -1;
        int end = -1;
        int len = element.length();
        int i = start + 1;
        boolean hasChars = false;
        boolean hasKey = false;

        String name = element.substring(0, start);
        String key = null;
        String value = null;

        while (i < len) {

            // Expression key
            if (element.charAt(i) == UPathConstants.UPATH_EXPRESSION_MID && hasChars && !hasKey) {

                // UD, honor escape sym, which may be part of expression
                if (element.charAt(i - 1) == UPathConstants.UPATH_ESCAPE_CHAR) {
                    i++;
                    continue;
                }

                hasChars = false;
                hasKey = true;
                key = element.substring(start + 1, i);
                mid = i;
                i++;
                continue;
            }

            // Expression value
            if (element.charAt(i) == UPathConstants.UPATH_EXPRESSION_END && hasChars && hasKey) {

                // UD, honor escape sym, which may be part of expression
                if (element.charAt(i - 1) == UPathConstants.UPATH_ESCAPE_CHAR) {
                    i++;
                    continue;
                }

                // Read values only if separator and key were seen
                // and some characters were read
                if (mid > -1) {
                    value = element.substring(mid + 1, i);
                    end = i;
                }

                break;
            }

            hasChars = true;
            i++;
        }

        if (mid == -1 || end == -1) {
            return false;
        }

        // name == "", but the rest is ok - this is a root expression
        // omit complex attribute part
        if (StringUtils.isNotBlank(name)) {
            AttributeModelElement check = checkPathComponent(upath, info, name);
            upath.getElements().add(new UPathElement(name, UPathElementType.COLLECTING, dr -> true, check));
        }

        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            upath.getElements().add(createExpressionFilter(info, upath, key, value));
            return true;
        }

        return false;
    }

    /**
     * Expand the expression.
     * @param info metadata info
     * @param upath current upath
     * @param key expression key
     * @param value expression value
     * @return element
     */
    private UPathElement createExpressionFilter(Map<String, AttributeModelElement> info, UPath upath, String key, String value) {

        String path = joinNonBlank(".", upath.toPath(), key);
        AttributeModelElement aih = info.get(path);
        if (Objects.isNull(aih)) {
            final String message = "Invalid input. Filtering expression denotes attribute not found in model [{}].";
            LOGGER.warn(message, path);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_FILTERING_EXPRESSION_MISSING_ATTRIBUTE, path);
        }

        Predicate<DataRecord> predicate = null;
        AttributeValueType type = aih.getValueType();

        if (Objects.nonNull(type)) {
            switch (type) {
                case STRING:
                    predicate = createStringExpressionPredicate(key, value);
                    break;
                case MEASURED:
                case INTEGER:
                case NUMBER:
                    predicate = createNumericExpressionPredicate(key, value);
                    break;
                case DATE:
                    predicate = createDateExpressionPredicate(key, value);
                    break;
                case TIME:
                    predicate = createTimeExpressionPredicate(key, value);
                    break;
                case TIMESTAMP:
                    predicate = createTimestampExpressionPredicate(key, value);
                    break;
                case NONE:
                    final String message = "Invalid input. Filtering expression denotes complex attribute as filter attribute [{}]. "
                            + "Filter attribute may be either simple, code or array.";
                    LOGGER.warn(message, aih.getPath());
                    throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_FILTERING_EXPRESSION_COMPLEX_ATTRIBUTE, aih.getPath());
                default:
                    break;
            }
        }

        if (Objects.isNull(predicate)) {
            final String message = "Invalid input. Filtering expression addresses invalid attribute type in [{}]. Strings, numeric types and temporal types only are supported.";
            LOGGER.warn(message, key);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_FILTERING_EXPRESSION_ATTRIBUTE_TYPE, key);
        }

        return new UPathElement(new StringBuilder()
                .append("{")
                .append(key)
                .append(":")
                .append(value)
                .append("}")
                .toString(), UPathElementType.EXPRESSION, predicate, aih);
    }
    /**
     * Creates date expression predicate.
     * @param key attribute key
     * @param value match value
     * @return predicate
     */
    private Predicate<DataRecord> createDateExpressionPredicate(String key, String value) {

        final LocalDate match;
        try {
            match = LocalDate.parse(StringUtils.remove(value, '\\'));
        } catch (Exception e) {
            final String message = "Invalid input. Filtering expression denotes date value in wrong format [{}]. ISO date is expected.";
            LOGGER.warn(message, value);
            throw new PlatformFailureException(e, CoreExceptionIds.EX_UPATH_INVALID_FILTERING_EXPRESSION_DATE_FORMAT, value);
        }

        return dr -> {

            Attribute a = dr.getAttribute(key);
            if (Objects.isNull(a)) {
                return false;
            }

            if (a.getAttributeType() == AttributeType.SIMPLE) {

                SimpleAttribute<?> sa = a.narrow();
                if (sa.getDataType() == DataType.DATE) {
                    LocalDate ld = sa.castValue();
                    return match.equals(ld);
                }
            } else if (a.getAttributeType() == AttributeType.ARRAY) {

                ArrayAttribute<?> aa = a.narrow();
                if (aa.getDataType() != ArrayDataType.DATE) {
                    return false;
                }

                for (ArrayValue<?> av : aa) {
                    LocalDate ld = av.castValue();
                    if (match.equals(ld)) {
                        return true;
                    }
                }
            }

            return false;
        };
    }
    /**
     * Creates time expression predicate.
     * @param key attribute key
     * @param value match value
     * @return predicate
     */
    private Predicate<DataRecord> createTimeExpressionPredicate(String key, String value) {

        final LocalTime match;
        try {
            match = LocalTime.parse(StringUtils.remove(value, '\\'));
        } catch (Exception e) {
            final String message = "Invalid input. Filtering expression denotes time value in wrong format [{}]. ISO time is expected.";
            LOGGER.warn(message, value);
            throw new PlatformFailureException(e, CoreExceptionIds.EX_UPATH_INVALID_FILTERING_EXPRESSION_TIME_FORMAT, value);
        }

        return dr -> {

            Attribute a = dr.getAttribute(key);
            if (Objects.isNull(a)) {
                return false;
            }

            if (a.getAttributeType() == AttributeType.SIMPLE) {

                SimpleAttribute<?> sa = a.narrow();
                if (sa.getDataType() == DataType.TIME) {
                    LocalTime ld = sa.castValue();
                    return match.equals(ld);
                }
            } else if (a.getAttributeType() == AttributeType.ARRAY) {

                ArrayAttribute<?> aa = a.narrow();
                if (aa.getDataType() != ArrayDataType.TIME) {
                    return false;
                }

                for (ArrayValue<?> av : aa) {
                    LocalTime ld = av.castValue();
                    if (match.equals(ld)) {
                        return true;
                    }
                }
            }

            return false;
        };
    }
    /**
     * Creates timestamp expression predicate.
     * @param key attribute key
     * @param value match value
     * @return predicate
     */
    private Predicate<DataRecord> createTimestampExpressionPredicate(String key, String value) {

        final LocalDateTime match;
        try {
            match = LocalDateTime.parse(StringUtils.remove(value, '\\'));
        } catch (Exception e) {
            final String message = "Invalid input. Filtering expression denotes timestamp value in wrong format [{}]. ISO timestamp is expected.";
            LOGGER.warn(message, value);
            throw new PlatformFailureException(e, CoreExceptionIds.EX_UPATH_INVALID_FILTERING_EXPRESSION_TIMESTAMP_FORMAT, value);
        }

        return dr -> {

            Attribute a = dr.getAttribute(key);
            if (Objects.isNull(a)) {
                return false;
            }

            if (a.getAttributeType() == AttributeType.SIMPLE) {

                SimpleAttribute<?> sa = a.narrow();
                if (sa.getDataType() == DataType.TIMESTAMP) {
                    LocalDateTime ld = sa.castValue();
                    return match.equals(ld);
                }
            } else if (a.getAttributeType() == AttributeType.ARRAY) {

                ArrayAttribute<?> aa = a.narrow();
                if (aa.getDataType() != ArrayDataType.TIMESTAMP) {
                    return false;
                }

                for (ArrayValue<?> av : aa) {
                    LocalDateTime ld = av.castValue();
                    if (match.equals(ld)) {
                        return true;
                    }
                }
            }

            return false;
        };
    }
    /**
     * Creates numeric expression predicate.
     * @param key attribute key
     * @param value match value
     * @return predicate
     */
    private Predicate<DataRecord> createNumericExpressionPredicate(String key, String value) {

        // NU will throw if something is wrong
        final Number match;
        try {
            match = NumberUtils.createNumber(StringUtils.remove(value, '\\'));
        } catch (Exception e) {
            final String message = "Invalid input. Filtering expression denotes number value in wrong format [{}]. "
                    + "Unquoted numeric value in octal, hexadecimal, decimal possibly with type modifyer is expected.";
            LOGGER.warn(message, value);
            throw new PlatformFailureException(e, CoreExceptionIds.EX_UPATH_INVALID_FILTERING_EXPRESSION_NUMBER_FORMAT, value);
        }

        return dr -> {

            Attribute a = dr.getAttribute(key);
            if (Objects.isNull(a)) {
                return false;
            }

            if (a.getAttributeType() == AttributeType.SIMPLE) {

                SimpleAttribute<?> sa = a.narrow();
                if (sa.getDataType() == DataType.INTEGER) {
                    Long l = sa.castValue();
                    return l != null && match.longValue() == l.longValue();
                } else if (sa.getDataType() == DataType.NUMBER) {
                    Double d = sa.castValue();
                    return d != null && match.doubleValue() == d.doubleValue();
                }
            } else if (a.getAttributeType() == AttributeType.CODE) {
                CodeAttribute<Long> ca = a.narrow();
                return ca.getValue() != null && match.longValue() == ca.getValue().longValue();
            } else if (a.getAttributeType() == AttributeType.ARRAY) {

                ArrayAttribute<?> aa = a.narrow();
                for (ArrayValue<?> av : aa) {

                    if (aa.getDataType() == ArrayDataType.NUMBER) {
                        Long l = av.castValue();
                        if (l != null && match.longValue() == l.longValue()) {
                            return true;
                        }
                    } else if (aa.getDataType() == ArrayDataType.INTEGER) {
                        Double d = av.castValue();
                        if (d != null && match.doubleValue() == d.doubleValue()) {
                            return true;
                        }
                    }
                }
            }

            return false;
        };
    }
    /**
     * Creates string expression predicate.
     * @param key attribute key
     * @param value match value
     * @return predicate
     */
    private Predicate<DataRecord> createStringExpressionPredicate(String key, String value) {

        if (value.length() < 2
         || value.charAt(0) != '\''
         || value.charAt(value.length() - 1) != '\'') {
            final String message = "Invalid input. Filtering expression denotes string value in wrong format. Quoted 'value' is expected [{}].";
            LOGGER.warn(message, value);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_FILTERING_EXPRESSION_STRING_FORMAT, value);
        }

        final String match = value.length() == 2
               ? StringUtils.EMPTY
               : StringUtils.remove(value.substring(1, value.length() - 1), '\\');

        return dr -> {

            Attribute a = dr.getAttribute(key);
            if (Objects.isNull(a)) {
                return false;
            }

            if (a.getAttributeType() == AttributeType.SIMPLE) {
                SimpleAttribute<String> sa = a.narrow();
                return match.equals(sa.castValue());
            } else if (a.getAttributeType() == AttributeType.CODE) {
                CodeAttribute<String> ca = a.narrow();
                return match.equals(ca.castValue());
            } else if (a.getAttributeType() == AttributeType.ARRAY) {
                ArrayAttribute<String> aa = a.narrow();
                for (Iterator<ArrayValue<String>> i = aa.iterator(); i.hasNext(); ) {

                    ArrayValue<String> av = i.next();
                    if (match.equals(av.getValue())) {
                        return true;
                    }
                }
            }

            return false;
        };
    }

    private static AttributeModelElement checkPathComponent(UPath upath, Map<String, AttributeModelElement> info, String token) {

        String currentPath = joinNonBlank(".", upath.toPath(), token);
        AttributeModelElement check = info.get(currentPath);
        if (Objects.isNull(check)) {
            final String message = "Invalid input. Attribute not found by path [{}].";
            LOGGER.warn(message, currentPath);
            throw new PlatformFailureException(message, CoreExceptionIds.EX_UPATH_INVALID_INPUT_ATTRIBUTE_NOT_FOUND_BY_PATH, currentPath);
        }

        return check;
    }

    private static String joinNonBlank(String delimiter, String... strings) {

        StringJoiner joiner = new StringJoiner(delimiter);
        for (CharSequence cs: strings) {

            if (StringUtils.isBlank(cs)) {
                continue;
            }

            joiner.add(cs);
        }

        return joiner.toString();
    }
    /**
     * Checks the type of the attribute for set operation.
     * @param check the attribute to check
     * @param info attribute info
     * @return true, if matches, false otherwise
     */
    private boolean isSetOperationTypeConform(Attribute check, AttributeModelElement info) {

        if (check.getAttributeType() == AttributeType.ARRAY && info.isArray()) {

            ArrayAttribute<?> aa = (ArrayAttribute<?>) check;

            switch (aa.getDataType()) {
            case DATE:
                return info.getValueType() == AttributeValueType.DATE;
            case INTEGER:
                return info.getValueType() == AttributeValueType.INTEGER;
            case NUMBER:
                return info.getValueType() == AttributeValueType.NUMBER;
            case STRING:
                return info.getValueType() == AttributeValueType.STRING;
            case TIME:
                return info.getValueType() == AttributeValueType.TIME;
            case TIMESTAMP:
                return info.getValueType() == AttributeValueType.TIMESTAMP;
            default:
                break;
            }

        } else if (check.getAttributeType() == AttributeType.CODE && info.isCode()) {

            CodeAttribute<?> ca = (CodeAttribute<?>) check;

            switch (ca.getDataType()) {
            case INTEGER:
                return info.getValueType() == AttributeValueType.INTEGER;
            case STRING:
                return info.getValueType() == AttributeValueType.STRING;
            default:
                break;
            }

        } else if (check.getAttributeType() == AttributeType.SIMPLE && info.isSimple()) {

            SimpleAttribute<?> sa = (SimpleAttribute<?>) check;

            // ENUM and LINK are still unassigned so far
            switch (sa.getDataType()) {
            case BLOB:
                return info.isBlob();
            case BOOLEAN:
                return info.getValueType() == AttributeValueType.BOOLEAN;
            case CLOB:
                return info.isClob();
            case DATE:
                return info.getValueType() == AttributeValueType.DATE;
            case INTEGER:
                return info.getValueType() == AttributeValueType.INTEGER;
            case MEASURED:
                return info.getValueType() == AttributeValueType.MEASURED;
            case NUMBER:
                return info.getValueType() == AttributeValueType.NUMBER;
            case STRING:
                return info.getValueType() == AttributeValueType.STRING;
            case TIME:
                return info.getValueType() == AttributeValueType.TIME;
            case TIMESTAMP:
                return info.getValueType() == AttributeValueType.TIMESTAMP;
            default:
                break;
            }

        } else if (check.getAttributeType() == AttributeType.COMPLEX) {
            return info.isComplex();
        }

        return false;
    }
}
