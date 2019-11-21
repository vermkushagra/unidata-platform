package org.unidata.mdm.data.util;

import static org.unidata.mdm.core.type.data.TypeOfChange.ADDED;
import static org.unidata.mdm.core.type.data.TypeOfChange.CHANGED;
import static org.unidata.mdm.core.type.data.TypeOfChange.DELETED;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.Attribute;
import org.unidata.mdm.core.type.data.Attribute.AttributeType;
import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;
import org.unidata.mdm.core.type.data.TypeOfChange;
import org.unidata.mdm.core.type.data.impl.SerializableDataRecord;
import org.unidata.mdm.core.type.data.impl.SimpleAttributesDiff;
import org.unidata.mdm.core.type.model.AttributeModelElement;
import org.unidata.mdm.core.type.model.AttributedModelElement;
import org.unidata.mdm.core.type.model.EntityModelElement;
import org.unidata.mdm.data.configuration.DataConfiguration;
import org.unidata.mdm.meta.service.MetaModelService;
import org.unidata.mdm.meta.type.info.impl.EntityInfoHolder;
import org.unidata.mdm.meta.type.info.impl.LookupInfoHolder;

/**
 * A very simple data diff calculator.
 * @author Mikhail Mikhailov on Nov 7, 2019
 */
public class DataDiffUtils {
    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataDiffUtils.class);
    /**
     * Hack. Meta model service instance.
     */
    private static MetaModelService metaModelService;
    /**
     * Constructor.
     */
    private DataDiffUtils() {
        super();
    }
    /**
     * Convenient init method.
     */
    public static void init() {
        try {
            metaModelService = DataConfiguration.getBean(MetaModelService.class);
        } catch (Exception exc) {
            LOGGER.warn("Meta model service bean GET. Exception caught.", exc);
        }
    }
    /**
     * Trivial naive first level records difference. Tells, whether there is at
     * least one difference. TODO re-write this crap asap.
     *
     * @param entityName
     *            the entity name
     * @param newRecord
     *            new record
     * @param oldRecord
     *            old record
     * @return true for difference existance, false otherwise
     */
    public static boolean hasDiff(String entityName, DataRecord newRecord, DataRecord oldRecord) {
        EntityModelElement wrapper = metaModelService.getEntityModelElementById(entityName);
        Map<TypeOfChange, Map<String, Attribute>> attrsDiff = createDiffTable(newRecord, oldRecord, wrapper, false, true, false);
        return !attrsDiff.get(CHANGED).isEmpty() || !attrsDiff.get(ADDED).isEmpty() || !attrsDiff.get(DELETED).isEmpty();
    }

    /**
     * Trivial naive first level records difference. TODO re-write this crap
     * asap.
     *
     * @param entityName
     *            the name of the entity
     * @param newRecord
     *            the update
     * @param oldRecord
     *            old record
     * @param prevMatch
     *            previous version from admin source system
     *
     * @return origin
     */
    public static DataRecord diffAsRecord(String entityName, DataRecord newRecord, DataRecord oldRecord, DataRecord prevMatch) {
       return diffAsRecord(entityName, newRecord, oldRecord, prevMatch, false);
    }

    /**
     * Trivial naive first level records difference.
     * TODO re-write this crap asap.
     * @param entityName the name of the entity
     * @param newRecord the update
     * @param oldRecord old record
     * @param prevMatch previous version from admin source system
     *
     * @return origin
     */
    public static DataRecord diffAsRecord(String entityName, DataRecord newRecord, DataRecord oldRecord, DataRecord prevMatch, boolean forceCheckCode) {

        AttributedModelElement wrapper = metaModelService.isEntity(entityName)
                ? metaModelService.getValueById(entityName, EntityInfoHolder.class)
                : metaModelService.getValueById(entityName, LookupInfoHolder.class);

        Map<TypeOfChange, Map<String, Attribute>> attrsDiff =  createDiffTable(newRecord, oldRecord, wrapper, false, false, forceCheckCode);

        boolean noChanges =
                attrsDiff.get(CHANGED).isEmpty()
                        && attrsDiff.get(ADDED).isEmpty()
                        && attrsDiff.get(DELETED).isEmpty();

        if (noChanges) {
            return null;
        }

        SerializableDataRecord target = new SerializableDataRecord(attrsDiff.size() + 1);

        target.addAll(attrsDiff.get(CHANGED).values());
        target.addAll(attrsDiff.get(ADDED).values());
        target.addAll(attrsDiff.get(DELETED).values());

        if (prevMatch != null) {
            for (Attribute prev : prevMatch.getAllAttributes()) {
                if (attrsDiff.get(CHANGED).containsKey(prev.getName())
                 || attrsDiff.get(DELETED).containsKey(prev.getName())
                 || previousValueOverridden(prev, oldRecord)) {
                    continue;
                }

                target.addAttribute(prev);
            }
        }

        return target;
    }

    public static SimpleAttributesDiff diffAsAttributesTable(String entityName,
            DataRecord newRecord, DataRecord oldRecord, boolean collectPrevious) {

        AttributedModelElement wrapper = metaModelService.isEntity(entityName)
                ? metaModelService.getValueById(entityName, EntityInfoHolder.class)
                : metaModelService.getValueById(entityName, LookupInfoHolder.class);

        Map<TypeOfChange, Map<String, Attribute>> attrsDiff
            = createDiffTable(newRecord, oldRecord, wrapper, collectPrevious, false, false);

        boolean noChanges = attrsDiff.get(CHANGED).isEmpty()
                && attrsDiff.get(ADDED).isEmpty()
                && attrsDiff.get(DELETED).isEmpty();

        if (noChanges) {
            return new SimpleAttributesDiff(null);
        }

        return new SimpleAttributesDiff(attrsDiff);
    }

    /**
     * Check for previous value overridden, return true if overridden.
     * This condition arises, for example, if a contributing enrichment overrides admin SS in resulting record.
     */
    private static boolean previousValueOverridden(Attribute prev, DataRecord oldRecord) {

        Attribute oldAttribute = oldRecord.getAttribute(prev.getName());
        if (oldAttribute == null || oldAttribute.getAttributeType() != prev.getAttributeType()) {
            return false;
        }

        //not equals if previous value overridden, return true
        return !oldAttribute.equals(prev);
    }

    private static Map<TypeOfChange, Map<String, Attribute>> createDiffTable(
            DataRecord newAttrs, DataRecord oldAttrs, AttributedModelElement wrapper, boolean collectOld, boolean stopOnFirstHit, boolean forceCheckCode) {

        // 1. Create resulting table existing first. Change to simple fixed size
        // array?
        Map<TypeOfChange, Map<String, Attribute>> result = new EnumMap<>(TypeOfChange.class);
        Map<String, Attribute> changed = new HashMap<>();
        Map<String, Attribute> added = new HashMap<>();
        Map<String, Attribute> deleted = new HashMap<>();

        // 2. Iterate over new
        for (Attribute update : newAttrs.getAllAttributes()) {

            //when create diff for classifiers, wrapper is NULL
            if (wrapper != null) {
                AttributeModelElement holder = wrapper.getAttributes().get(update.getName());

                // 2.1 Skip main code alternative attrs, pass alternative code attrs
                // through
                // Skip link templates. It doen't make sence to check 'em
                if ((update.getAttributeType() == AttributeType.CODE && !holder.isCodeAlternative() && !forceCheckCode)
                        || holder.isLinkTemplate()) {
                    continue;
                }
            }

            // 2.2 Process complex
            if (update.getAttributeType() == AttributeType.COMPLEX) {
                ComplexAttribute current = oldAttrs.getComplexAttribute(update.getName());

                // 2.2.1. New
                if (current == null) {
                    added.put(update.getName(), collectOld ? null : update);
                    if (stopOnFirstHit) {
                        break;
                    } else {
                        continue;
                    }
                }

                Long currentHash = RecursiveHashCalculator.traverse(current);
                Long updateHash = RecursiveHashCalculator.traverse((ComplexAttribute) update);

                // 2.2.2. Changed.
                if (!currentHash.equals(updateHash)) {
                    changed.put(update.getName(), collectOld ? current : update);
                    if (stopOnFirstHit) {
                        break;
                    }
                }

                // 2.2.3. Same. Remove by skipping.
                continue;
            }

            // 2.3. Process array and simple attributes
            Attribute current = oldAttrs.getAttribute(update.getName());
            if (detectArrayOrSimpleAttributeValueDelete(update, current)) {
                deleted.put(update.getName(), collectOld ? current : update);
                if (stopOnFirstHit) {
                    break;
                } else {
                    continue;
                }
            }

            int currentHash = current != null ? current.hashCode() : 0;
            int updateHash = update.hashCode();
            if (currentHash != 0 && currentHash == updateHash) {
                continue;
            }

            if (currentHash != 0 && currentHash != updateHash) {
                changed.put(update.getName(), collectOld ? current : update);
                if (stopOnFirstHit) {
                    break;
                } else {
                    continue;
                }
            }

            added.put(update.getName(), collectOld ? current : update);
            if (stopOnFirstHit) {
                break;
            }
        }

        result.put(DELETED, deleted);
        result.put(CHANGED, changed);
        result.put(ADDED, added);

        return result;
    }

    private static boolean detectArrayOrSimpleAttributeValueDelete(Attribute update, Attribute current) {

        if (update != null && current != null) {
            if (update.getAttributeType() == AttributeType.ARRAY) {
                return ((ArrayAttribute<?>) update).isEmpty() && !((ArrayAttribute<?>) current).isEmpty();
            } else if (current.getAttributeType() == AttributeType.SIMPLE) {
                return ((SimpleAttribute<?>) update).getValue() == null
                        && ((SimpleAttribute<?>) current).getValue() != null;
            }
        }

        return false;
    }

    /**
     * @author Mikhail Mikhailov
     * Calculates sub-tree hashes.
     */
    private static final class RecursiveHashCalculator {

        /**
         * Constructor.
         */
        private RecursiveHashCalculator() {
            super();
        }

        /**
         * Traverses a nested record and collectes hash codes.
         * @param record the record
         * @return sum of hash codes
         */
        public static long traverse(DataRecord record) {

            long result = 0L;
            if (Objects.nonNull(record)) {
                Collection<Attribute> attrs = record.getAllAttributes();
                for (Attribute attr : attrs) {
                    switch (attr.getAttributeType()) {
                        case SIMPLE:
                        case ARRAY:
                            result += attr.hashCode();
                            break;
                        case COMPLEX:
                            result += traverse((ComplexAttribute) attr);
                            break;
                        case CODE:
                            // TODO add code
                            break;
                    }
                }
            }

            return result;
        }

        /**
         * Traverses hierarchy of a complex attribute and collects hash codes.
         * @param attribute the attribute
         * @return sum of hash codes
         */
        public static long traverse(ComplexAttribute attribute) {

            long result = 0L;
            for (int i = 0; attribute != null && !attribute.isEmpty() && i < attribute.size(); i++) {
                DataRecord record = attribute.get(i);
                result += traverse(record);
            }

            return result;
        }
    }
}
