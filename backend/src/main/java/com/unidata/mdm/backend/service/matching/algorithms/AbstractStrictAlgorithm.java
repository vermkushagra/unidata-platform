package com.unidata.mdm.backend.service.matching.algorithms;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.Attribute;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.util.ByteUtils;
import com.unidata.mdm.backend.util.CryptUtils;

/**
 * @author Mikhail Mikhailov
 *
 */
public abstract class AbstractStrictAlgorithm extends AbstractAlgorithm {

    /**
     * Constructor.
     */
    public AbstractStrictAlgorithm() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExact() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object construct(Attribute attr) {

        if (Objects.nonNull(attr)) {

            switch (attr.getAttributeType()) {
            case SIMPLE:
                return encodeSimpleAttribute((SimpleAttribute<?>) attr);
            case ARRAY:
                return encodeArrayAttribute((ArrayAttribute<?>) attr);
            default:
                break;
            }
        }

        return null;
    }

    /**
     * Encodes attribute value as string.
     * Attribute types BLOB, CLOB and LINK are not supported.
     *
     * @param attr simple attribute
     * @return strig
     */
    private String encodeSimpleAttribute(SimpleAttribute<?> attr) {

        Object val = attr.getValue();
        if (Objects.nonNull(val)) {

            switch (attr.getDataType()) {
            case BOOLEAN:
                return CryptUtils.toMurmurString((Boolean) val ? 1L : 0L);
            case DATE:
                return CryptUtils.toMurmurString(ByteUtils.packLocalDate((LocalDate) val));
            case TIME:
                return CryptUtils.toMurmurString(ByteUtils.packLocalTime((LocalTime) val));
            case TIMESTAMP:
                return CryptUtils.toMurmurString(ByteUtils.packLocalDateTime((LocalDateTime) val));
            case ENUM:
            case STRING:
                return CryptUtils.toMurmurString((String) val);
            case INTEGER:
                return CryptUtils.toMurmurString((Long) val);
            case NUMBER:
            case MEASURED:
                return CryptUtils.toMurmurString(Double.doubleToLongBits((Double) val));
            default:
                break;
            }
        }

        return null;
    }

    // TODO implement
    private String encodeArrayAttribute(ArrayAttribute<?> attr) {

        Object[] vals = attr.toArray();
        if (Objects.nonNull(vals)) {
            /*
            switch (attr.getDataType()) {
            case DATE:
                return CryptUtils.toMurmurString(ByteUtils.packLocalDate((LocalDate[]) vals));
            case TIME:
                return CryptUtils.toMurmurString(ByteUtils.packLocalTime((LocalTime) val));
            case TIMESTAMP:
                return CryptUtils.toMurmurString(ByteUtils.packLocalDateTime((LocalDateTime) val));
            case STRING:
                return CryptUtils.toMurmurString((String) val);
            case INTEGER:
                return CryptUtils.toMurmurString((Long) val);
            case NUMBER:
                return CryptUtils.toMurmurString(Double.doubleToLongBits((Double) val));
            default:
                break;
            }
            */
        }

        return null;
    }
}
