package com.unidata.mdm.backend.service.data.util;

import static com.unidata.mdm.backend.common.types.TypeOfChange.ADDED;
import static com.unidata.mdm.backend.common.types.TypeOfChange.CHANGED;
import static com.unidata.mdm.backend.common.types.TypeOfChange.DELETED;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.unidata.mdm.backend.common.model.AttributeInfoHolder;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.Attribute.AttributeType;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.TypeOfChange;
import com.unidata.mdm.backend.service.model.MetaModelServiceExt;
import com.unidata.mdm.backend.service.model.util.wrappers.AttributesWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.EntityWrapper;
import com.unidata.mdm.backend.service.model.util.wrappers.LookupEntityWrapper;

/**
 * @author Ruslan Trachuk
 */
public class DataUtils {

    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataUtils.class);
    /**
     * Hack. Meta model service instance.
     */
    private static MetaModelServiceExt metaModelService;
    /**
     * Constructor.
     */
    private DataUtils() {
        super();
    }
    /**
     * Convenient init method.
     */
    public static void init(ApplicationContext ac) {
        try {
            metaModelService = ac.getBean(MetaModelServiceExt.class);
        } catch (Exception exc) {
            LOGGER.warn("Meta model service bean GET. Exception caught.", exc);
        }
    }

    /**
     * Create object from string.
     *
     * @param s string.
     * @return object.
     */
    public static Object fromString(String s) {
        byte[] data = Base64.getDecoder().decode(s);
        Object o = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
            o = ois.readObject();
            ois.close();
        } catch (ClassNotFoundException | IOException e) {

        }
        return o;
    }

    /**
     * Create base64 string from object.
     *
     * @param o object
     * @return base64 string
     */
    public static String toString(Serializable o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.close();
        } catch (IOException e) {
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * Collects sum of all simple attributes hash codes.
     *
     * @param nr nested record
     * @return sum
     */
    private static long collectDataHashcodesRecursive(DataRecord dr) {

        long result = 0L;
        if (dr != null) {

            for (Attribute attr : dr.getAllAttributes()) {
                switch (attr.getAttributeType()) {
                    case SIMPLE:
                    case ARRAY:
                    case CODE:
                        result += attr.hashCode();
                        break;
                    case COMPLEX: {
                        for (DataRecord child : ((ComplexAttribute) attr).getRecords()) {
                            result += collectDataHashcodesRecursive(child);
                        }
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * Compares data of all simple attributes of both records recursive.
     *
     * @param o1 object 1
     * @param o2 object 2
     * @return true if the nested records are "data equal", false otherwise
     */
    public static boolean dataEquals(DataRecord o1, DataRecord o2) {

        long sum1 = collectDataHashcodesRecursive(o1);
        long sum2 = collectDataHashcodesRecursive(o2);

        return sum1 == sum2;
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
    public static DataRecord simpleDataDiff(String entityName, DataRecord newRecord, DataRecord oldRecord, DataRecord prevMatch) {

        AttributesWrapper wrapper = metaModelService.isEntity(entityName)
                ? metaModelService.getValueById(entityName, EntityWrapper.class)
                : metaModelService.getValueById(entityName, LookupEntityWrapper.class);

        Map<TypeOfChange, Map<String, Attribute>> attrsDiff =  createDiffTable(newRecord, oldRecord, wrapper, false);

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
                 || attrsDiff.get(DELETED).containsKey(prev.getName())) {
                    continue;
                }

                target.addAttribute(prev);
            }
        }

        return target;
    }

    public static Map<String, Map<TypeOfChange, Attribute>> simpleDataDiffAsAttributesTable(
            String entityName, DataRecord newRecord, DataRecord oldRecord, boolean collectPrevious) {

        AttributesWrapper wrapper = metaModelService.isEntity(entityName)
                ? metaModelService.getValueById(entityName, EntityWrapper.class)
                : metaModelService.getValueById(entityName, LookupEntityWrapper.class);

        Map<TypeOfChange, Map<String, Attribute>> attrsDiff = createDiffTable(newRecord, oldRecord, wrapper, collectPrevious);

        boolean noChanges =
                   attrsDiff.get(CHANGED).isEmpty()
                && attrsDiff.get(ADDED).isEmpty()
                && attrsDiff.get(DELETED).isEmpty();

        if (noChanges) {
            return Collections.emptyMap();
        }

        return invertDiffTable(attrsDiff);
    }

    private static Map<TypeOfChange, Map<String, Attribute>> createDiffTable(
            DataRecord newAttrs, DataRecord oldAttrs, AttributesWrapper wrapper, boolean collectOld) {

        // 1. Create resulting table existing first. Change to simple fixed size array?
        Map<TypeOfChange, Map<String, Attribute>> result = new EnumMap<>(TypeOfChange.class);
        Map<String, Attribute> changed = new HashMap<>();
        Map<String, Attribute> added = new HashMap<>();
        Map<String, Attribute> deleted = new HashMap<>();

        // 2. Iterate over new
        for (Attribute update : newAttrs.getAllAttributes()) {

            AttributeInfoHolder holder = wrapper.getAttributes().get(update.getName());

            // 2.1 Skip main code alternative attrs, pass alternative code attrs through
            // Skip link templates. It doen't make sence to check 'em
            if ((update.getAttributeType() == AttributeType.CODE && !holder.isCodeAlternative())
             || holder.isLinkTemplate()) {
                continue;
            }

            // 2.2 Process complex
            if (update.getAttributeType() == AttributeType.COMPLEX) {
                ComplexAttribute current = oldAttrs.getComplexAttribute(update.getName());

                // 2.2.1. New
                if (current == null) {
                    added.put(update.getName(), collectOld ? null : update);
                    continue;
                }

                Long currentHash = RecursiveHashCalculator.traverse(current);
                Long updateHash = RecursiveHashCalculator.traverse((ComplexAttribute) update);

                // 2.2.2. Changed.
                if (!currentHash.equals(updateHash)) {
                    changed.put(update.getName(), collectOld ? current : update);
                    continue;
                }

                // 2.2.3. Same. Remove by skipping.
                continue;
            }

            // 2.3. Process array and simple attributes
            Attribute current = oldAttrs.getAttribute(update.getName());
            if (detectArrayOrSimpleAttributeValueDelete(update, current)) {
                deleted.put(update.getName(), collectOld ? current : update);
                continue;
            }

            int currentHash = current != null ? current.hashCode() : 0;
            int updateHash = update.hashCode();
            if (currentHash != 0 && currentHash == updateHash) {
                continue;
            }

            if (currentHash != 0 && currentHash != updateHash) {
                changed.put(update.getName(), collectOld ? current : update);
                continue;
            }

            added.put(update.getName(), collectOld ? current : update);
        }

        result.put(DELETED, deleted);
        result.put(CHANGED, changed);
        result.put(ADDED, added);

        return result;
    }

    /**
     * Inverts diff map.
     * @param map the map to invert
     * @return inverted map
     */
    private static Map<String, Map<TypeOfChange, Attribute>> invertDiffTable(Map<TypeOfChange, Map<String, Attribute>> map) {

        Map<String, Map<TypeOfChange, Attribute>> result = new HashMap<>();
        for (Entry<TypeOfChange, Map<String, Attribute>> row : map.entrySet()) {
            for (Entry<String, Attribute> column : row.getValue().entrySet()) {
                result.put(column.getKey(), Collections.singletonMap(row.getKey(), column.getValue()));
            }
        }

        return result;
    }

    private static boolean detectArrayOrSimpleAttributeValueDelete(Attribute update, Attribute current) {

        if (update != null && current != null) {
            if (update.getAttributeType() == AttributeType.ARRAY) {
                return ((ArrayAttribute<?>) update).isEmpty()
                    && !((ArrayAttribute<?>) current).isEmpty();
            } else if (current.getAttributeType() == AttributeType.SIMPLE) {
                return ((SimpleAttribute<?>) update).getValue() == null
                    && ((SimpleAttribute<?>) current).getValue() != null;
            }
        }

        return false;
    }
}
