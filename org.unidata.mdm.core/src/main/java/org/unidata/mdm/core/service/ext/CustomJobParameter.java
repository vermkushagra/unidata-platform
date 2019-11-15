package org.unidata.mdm.core.service.ext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;

import org.springframework.batch.core.JobParameter;

/**
 * Custom job parameter used only as wrapper on base class {@link JobParameter} to add arrays.
 * All work based on reflection mechanism.
 *
 * @author Aleksandr Magdenko
 */
public class CustomJobParameter extends JobParameter {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -8116683696740383807L;

    public CustomJobParameter(String[] parameter) {
        super((String)null);
        setParameterValue(parameter);
    }

    public CustomJobParameter(Long[] parameter) {
        super((Long)null);
        setParameterValue(parameter);
    }

    public CustomJobParameter(Date[] parameter) {
        super((Date)null);
        setParameterValue(parameter);
    }

    public CustomJobParameter(Double[] parameter) {
        super((Double)null);
        setParameterValue(parameter);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JobParameter)) {
            return false;
        } else if (this == obj) {
            return true;
        } else {
            JobParameter rhs = (JobParameter)obj;
            if (getValue() == null) {
                return rhs.getValue() == null && this.getType() == rhs.getType();
            } else {
                if (getValue().getClass().isArray()) {
                    if (rhs.getValue() != null && rhs.getValue().getClass().isArray()) {
                        // Note, that we can't get primitive arrays here. Hence no need to check for primitive arrays.
                        return Arrays.equals((Object[])getValue(), (Object[])rhs.getValue());
                    } else {
                        return false;
                    }
                } else {
                    return this.getValue().equals(rhs.getValue());
                }
            }
        }
    }

    @Override
    public String toString() {
        if (this.getValue() == null) {
            return null;
        }

        if (this.getValue().getClass().isArray()) {
            return Arrays.asList(this.getValue()).toString();
        } else {
            return super.toString();
        }
    }

    @Override
    public int hashCode() {
        return 7 + 21 * (this.getValue() == null ? this.getType().hashCode() :
                (getValue().getClass().isArray() ?
                        // Note, that we can't get primitive arrays here. Hence no need to check for primitive arrays.
                        Arrays.hashCode((Object[]) getValue()) : getValue().hashCode()));
    }

    /**
     * This is hack method to set array directly in parent class.
     *
     * @param object Object value can be array.
     */
    private void setParameterValue(Object object) {
        try {
            Field parameterField = JobParameter.class.getDeclaredField("parameter");
            parameterField.setAccessible(true);

            parameterField.set(this, object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to instantiate job parameter", e);
        }
    }
}
