package com.unidata.mdm.backend.api.rest.converter;

import com.unidata.mdm.backend.api.rest.dto.ArrayDataType;
import com.unidata.mdm.backend.api.rest.dto.data.ArrayAttributeRO;
import com.unidata.mdm.backend.api.rest.dto.data.ArrayObjectRO;
import com.unidata.mdm.backend.api.rest.dto.data.extended.ExtendedArrayAttributeRO;
import com.unidata.mdm.backend.common.keys.OriginKey;
import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.ArrayValue;
import com.unidata.mdm.backend.common.types.CodeLinkValue;
import com.unidata.mdm.backend.common.types.EtalonRecord;
import com.unidata.mdm.backend.common.types.extended.WinnerInformationArrayAttribute;
import com.unidata.mdm.backend.common.types.impl.DateArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.DateArrayValue;
import com.unidata.mdm.backend.common.types.impl.IntegerArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerArrayValue;
import com.unidata.mdm.backend.common.types.impl.NumberArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.NumberArrayValue;
import com.unidata.mdm.backend.common.types.impl.StringArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.StringArrayValue;
import com.unidata.mdm.backend.common.types.impl.TimeArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimeArrayValue;
import com.unidata.mdm.backend.common.types.impl.TimestampArrayAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.TimestampArrayValue;

import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Mikhail Mikhailov
 *
 */
public class ArrayAttributeConverter {

    /**
     * Constructor.
     */
    private ArrayAttributeConverter() {
        super();
    }

    public static ArrayAttribute<?> from(ArrayAttributeRO source) {

        if (Objects.isNull(source)) {
            return null;
        }

        ArrayAttribute<?> result;
        com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType type
            = com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType.valueOf(source.getType().name());

        switch (type) {
        case DATE:
            result = new DateArrayAttributeImpl(source.getName(), CollectionUtils.isEmpty(source.getValue())
                    ? null
                    : source.getValue().stream()
                        .map(ArrayObjectRO::getValue)
                        .map(v -> new DateArrayValue((LocalDate) v))
                        .collect(Collectors.toList()));
            break;
        case INTEGER:
            result = new IntegerArrayAttributeImpl(source.getName(), CollectionUtils.isEmpty(source.getValue())
                    ? null
                    : source.getValue().stream()
                        .map(ArrayObjectRO::getValue)
                        .map(v -> new IntegerArrayValue((Long) v))
                        .collect(Collectors.toList()));
            break;
        case NUMBER:
            result = new NumberArrayAttributeImpl(source.getName(), CollectionUtils.isEmpty(source.getValue())
                    ? null
                    : source.getValue().stream()
                        .map(ArrayObjectRO::getValue)
                        .map(v -> new NumberArrayValue((Double) v))
                        .collect(Collectors.toList()));
            break;
        case STRING:
            result = new StringArrayAttributeImpl(source.getName(), CollectionUtils.isEmpty(source.getValue())
                    ? null
                    : source.getValue().stream()
                        .map(ArrayObjectRO::getValue)
                        .map(v -> new StringArrayValue((String) v))
                        .collect(Collectors.toList()));
            break;
        case TIME:
            result = new TimeArrayAttributeImpl(source.getName(), CollectionUtils.isEmpty(source.getValue())
                    ? null
                    : source.getValue().stream()
                        .map(ArrayObjectRO::getValue)
                        .map(v -> new TimeArrayValue((LocalTime) v))
                        .collect(Collectors.toList()));
            break;
        case TIMESTAMP:
            result = new TimestampArrayAttributeImpl(source.getName(), CollectionUtils.isEmpty(source.getValue())
                    ? null
                    : source.getValue().stream()
                        .map(ArrayObjectRO::getValue)
                        .map(v -> new TimestampArrayValue((LocalDateTime) v))
                        .collect(Collectors.toList()));
            break;
        default:
            return null;
        }

        return result;
    }

    public static List<ArrayAttribute<?>> from(Collection<ArrayAttributeRO> source) {

        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<ArrayAttribute<?>> result = new ArrayList<>(source.size());
        for (ArrayAttributeRO val : source) {
            ArrayAttribute<?> converted = from(val);
            if (Objects.isNull(converted)) {
                continue;
            }

            result.add(converted);
        }

        return result;
    }

    public static ArrayAttributeRO to(ArrayAttribute<?> source) {
        if (Objects.isNull(source)) {
            return null;
        }

        ArrayAttributeRO target = new ArrayAttributeRO();
        populate(source, target);
        return target;
    }

    public static List<ArrayAttributeRO> to(Collection<ArrayAttribute<?>> source) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<ArrayAttributeRO> result = new ArrayList<>(source.size());
        for (ArrayAttribute<?> attr : source) {
            ArrayAttributeRO converted = to(attr);
            if (Objects.isNull(converted)) {
                continue;
            }

            result.add(converted);
        }
        return result;
    }

    public static List<ArrayAttributeRO> to(Collection<ArrayAttribute<?>> source, EtalonRecord etalonRecord, OriginKey originKey) {
        if (CollectionUtils.isEmpty(source)) {
            return Collections.emptyList();
        }

        List<ArrayAttributeRO> result = new ArrayList<>(source.size());
        for (ArrayAttribute<?> sourceAttribute : source) {
            if(sourceAttribute != null){
                ExtendedArrayAttributeRO targetAttribute = new ExtendedArrayAttributeRO();
                populate(sourceAttribute, targetAttribute);

                ArrayAttribute winnerAttribute = etalonRecord.getArrayAttribute(sourceAttribute.getName());
                targetAttribute.setWinner(winnerAttribute instanceof WinnerInformationArrayAttribute
                        && originKey.getExternalId().equals(((WinnerInformationArrayAttribute) winnerAttribute).getWinnerExternalId())
                        && originKey.getSourceSystem().equals(((WinnerInformationArrayAttribute) winnerAttribute).getWinnerSourceSystem()));

                result.add(targetAttribute);
            }
        }
        return result;
    }

    protected static void populate(ArrayAttribute<?> source, ArrayAttributeRO target) {
        List<ArrayObjectRO> value = null;

        if (!source.isEmpty()) {

            value = new ArrayList<>(source.getValue().size());
            for (int i = 0; i < source.getValue().size(); i++) {
                ArrayValue<?> entry = source.getValue().get(i);
                ArrayObjectRO obj = new ArrayObjectRO();

                obj.setValue(entry.getValue());
                obj.setDisplayValue(entry.getDisplayValue());

                if (source.getDataType() == com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType.STRING
                        || source.getDataType() == com.unidata.mdm.backend.common.types.ArrayAttribute.ArrayDataType.INTEGER) {
                    obj.setTargetEtalonId(((CodeLinkValue) entry).getLinkEtalonId());
                }

                value.add(obj);
            }
        }

        target.setName(source.getName());
        target.setType(ArrayDataType.valueOf(source.getDataType().name()));
        target.setValue(value);
    }
}
