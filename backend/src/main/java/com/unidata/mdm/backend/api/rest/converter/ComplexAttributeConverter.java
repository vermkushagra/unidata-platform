/**
 *
 */
package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.data.ComplexAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.NestedRecordRO;
import com.unidata.mdm.backend.api.rest.dto.data.extended.ExtendedComplexAttributeRO;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.record.SerializableDataRecord;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.extended.WinnerInformationComplexAttribute;
import com.unidata.mdm.backend.common.types.impl.ComplexAttributeImpl;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Mikhail Mikhailov
 * REST complex attribute to complex attribute.
 */
public class ComplexAttributeConverter {

    /**
     * Constructor.
     */
    private ComplexAttributeConverter() {
        super();
    }

    /**
     * Copy a list of complex attributes.
     * @param source the source
     * @return collection
     */
    public static Collection<Attribute> from(List<ComplexAttributeRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<Attribute> destination = new ArrayList<>(source.size());
        for (ComplexAttributeRO a : source) {
            destination.add(from(a));
        }

        return destination;
    }

    /**
     * Copy a single complex attribute.
     * @param source the source
     * @return target the target
     */
    public static ComplexAttribute from(ComplexAttributeRO source) {

        if (source == null) {
            return null;
        }

        ComplexAttribute target = new ComplexAttributeImpl(source.getName());
        for (NestedRecordRO record : source.getNestedRecords()) {

            SerializableDataRecord nested = new SerializableDataRecord(
                    record.getSimpleAttributes().size() +
                    record.getComplexAttributes().size() +
                    record.getArrayAttributes().size() + 1);

            nested.addAll(SimpleAttributeConverter.from(record.getSimpleAttributes()));
            nested.addAll(from(record.getComplexAttributes()));
            nested.addAll(ArrayAttributeConverter.from(record.getArrayAttributes()));

            target.getRecords().add(nested);
        }

        return target;
    }

    /**
     * Copy a list of complex attributes.
     * @param source the source
     * @param target the target
     */
    public static void to(Collection<ComplexAttribute> source, List<ComplexAttributeRO> target) {
        if (CollectionUtils.isEmpty(source)) {
            return;
        }

        for (ComplexAttribute sourceAttribute : source) {
            target.add(to(sourceAttribute));
        }
    }

    /**
     * Copy a list of complex attributes.
     * @param source the source
     * @param target the target
     */
    public static void to(Collection<ComplexAttribute> source, List<ComplexAttributeRO> target, EtalonRecord etalonRecord, OriginKey originKey) {
        if (CollectionUtils.isEmpty(source)) {
            return;
        }

        for (ComplexAttribute sourceAttribute : source) {
            target.add(to(sourceAttribute, etalonRecord, originKey));
        }
    }

    /**
     * Copy a single complex attribute.
     * @param source the source
     * @param target the target
     */
    public static ComplexAttributeRO to(ComplexAttribute source) {

        if (source == null) {
            return null;
        }

        ComplexAttributeRO target = new ComplexAttributeRO();
        target.setName(source.getName());
        for (DataRecord record : source.getRecords()) {
            NestedRecordRO attr = new NestedRecordRO();

            SimpleAttributeConverter.to(record.getSimpleAttributes(), attr.getSimpleAttributes());
            to(record.getComplexAttributes(), attr.getComplexAttributes());
            attr.getArrayAttributes().addAll(ArrayAttributeConverter.to(record.getArrayAttributes()));

            target.getNestedRecords().add(attr);
        }

        return target;
    }

    /**
     * Copy a single complex attribute.
     * @param source the source
     * @param etalonRecord etalon record
     * @param originKey origin Key
     */
    public static ComplexAttributeRO to(ComplexAttribute source, EtalonRecord etalonRecord, OriginKey originKey) {

        if (source == null) {
            return null;
        }

        ExtendedComplexAttributeRO target = new ExtendedComplexAttributeRO();
        target.setName(source.getName());

        ComplexAttribute winnerAttribute = etalonRecord.getComplexAttribute(source.getName());
        target.setWinner(winnerAttribute instanceof WinnerInformationComplexAttribute
                && originKey.getExternalId().equals(((WinnerInformationComplexAttribute) winnerAttribute).getWinnerExternalId())
                && originKey.getSourceSystem().equals(((WinnerInformationComplexAttribute) winnerAttribute).getWinnerSourceSystem()));


        for (DataRecord record : source.getRecords()) {
            NestedRecordRO attr = new NestedRecordRO();

            SimpleAttributeConverter.to(record.getSimpleAttributes(), attr.getSimpleAttributes());
            to(record.getComplexAttributes(), attr.getComplexAttributes());
            attr.getArrayAttributes().addAll(ArrayAttributeConverter.to(record.getArrayAttributes()));

            target.getNestedRecords().add(attr);
        }

        return target;
    }

}
