package org.unidata.mdm.meta.service;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.unidata.mdm.core.type.measurement.MeasurementUnit;
import org.unidata.mdm.core.type.measurement.MeasurementValue;
import org.unidata.mdm.system.service.AfterContextRefresh;

/**
 * Provide base methods working with measurement;
 */
public interface MetaMeasurementService extends AfterContextRefresh {
    /**
     * conversion function.
     */
    String BASE_CONVERSION = "value";
    /**
     * Id regexp
     */
    Pattern ID_PATTERN = Pattern.compile("[\\w]*");
    /**
     * Max length for short names
     */
    int SHORT_STRING_LENGTH = 31;
    /**
     * Max length for ids
     */
    int BASE_STRING_LENGTH = 63;
    /**
     * Max length for names.
     */
    int LONG_STRING_LENGTH = 127;
    /**
     * Conversion function max length
     */
    int MAX_STRING_LENGTH = 255;

    /**
     * @return collection of values
     */
    @Nonnull
    Collection<MeasurementValue> getAllValues();

    /**
     * @param valueId - value id
     * @return value if exist
     */
    @Nullable
    MeasurementValue getValueById(@Nonnull String valueId);

    /**
     * @param valueId - value id
     * @return unit - unit
     */
    @Nullable
    MeasurementUnit getUnitById(@Nonnull String valueId, @Nonnull String unitId);

    /**
     * @param measurementValue - value
     */
    void saveValue(@Nonnull MeasurementValue measurementValue);

    /**
     * remove value
     *
     * @param measureValueId - value id
     * @return true if everything ok, otherwise false
     */
    boolean removeValue(@Nonnull String measureValueId);

    /**
     * @param measureValueIds - value ids
     * @return true if everything ok, otherwise false
     */
    boolean batchRemove(@Nonnull Collection<String> measureValueIds, boolean dropRefs, boolean override);
    /**
     * Save values.
     * @param values measurement values.
     */

	void saveValues(List<MeasurementValue> values);

    /**
     * Validate measured value
     * @param measurementValue
     */
    void validateValue(MeasurementValue measurementValue);
}
