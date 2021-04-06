package com.unidata.mdm.backend.service.data.util;

import java.util.Collection;
import java.util.Objects;

import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;

/**
 * @author Mikhail Mikhailov
 * Calculates sub-tree hashes.
 */
public final class RecursiveHashCalculator {

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
        for (int i = 0;
             attribute != null && !CollectionUtils.isEmpty(attribute.getRecords()) && i < attribute.getRecords().size();
             i++) {
            DataRecord record = attribute.getRecords().get(i);
            result += traverse(record);
        }

        return result;
    }
}
