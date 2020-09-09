/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.job.batch.core;

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
