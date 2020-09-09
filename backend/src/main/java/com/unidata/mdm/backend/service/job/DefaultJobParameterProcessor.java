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

package com.unidata.mdm.backend.service.job;

import java.time.ZonedDateTime;
import java.util.Date;

import com.unidata.mdm.backend.service.job.batch.core.CustomJobParameter;
import org.springframework.batch.core.JobParametersBuilder;

import com.unidata.mdm.backend.common.dto.job.JobParameterDTO;

/**
 * @author Mikhail Mikhailov
 * Default implementation.
 */
public class DefaultJobParameterProcessor implements JobParameterProcessor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(JobParameterDTO jobParameter, JobParametersBuilder builder) {
        switch (jobParameter.getType()) {
            case STRING: {
                if (jobParameter.isMultiValue()) {
                    builder.addParameter(jobParameter.getName(), new CustomJobParameter(jobParameter.getStringArrayValue()));
                } else {
                    builder.addString(jobParameter.getName(), jobParameter.getStringValue());
                }
                break;
            }
            case DATE: {
                if (jobParameter.isMultiValue()) {
                    Date[] toValues;
                    ZonedDateTime[] fromValues = jobParameter.getDateArrayValue();

                    // Convert ZonedDateTime array to Date array.
                    if (fromValues != null) {
                        toValues = new Date[fromValues.length];

                        for (int i = 0; i < fromValues.length; i++) {
                            toValues[i] = Date.from(fromValues[i].toInstant());
                        }
                    } else {
                        toValues = new Date[0];
                    }
                    builder.addParameter(jobParameter.getName(), new CustomJobParameter(toValues));
                } else {
                    builder.addDate(jobParameter.getName(), Date.from(jobParameter.getDateValue().toInstant()));
                }
                break;
            }
            case LONG: {
                if (jobParameter.isMultiValue()) {
                    builder.addParameter(jobParameter.getName(), new CustomJobParameter(jobParameter.getLongArrayValue()));
                } else {
                    builder.addLong(jobParameter.getName(), jobParameter.getLongValue());
                }
                break;
            }
            case DOUBLE: {
                if (jobParameter.isMultiValue()) {
                    builder.addParameter(jobParameter.getName(), new CustomJobParameter(jobParameter.getDoubleArrayValue()));
                } else {
                    builder.addDouble(jobParameter.getName(), jobParameter.getDoubleValue());
                }
                break;
            }
            case BOOLEAN: {
                if (jobParameter.isMultiValue()) {
                    String[] toValues;
                    Boolean[] fromValues = jobParameter.getBooleanArrayValue();

                    if (fromValues != null) {
                        toValues = new String[fromValues.length];
                        for (int i = 0; i < fromValues.length; i++) {
                            toValues[i] = fromValues[i].toString();
                        }
                    } else {
                        toValues = new String[0];
                    }
                    builder.addParameter(jobParameter.getName(), new CustomJobParameter(toValues));
                } else {
                    final Boolean b = jobParameter.getBooleanValue();
                    builder.addString(jobParameter.getName(), b.toString());
                }
                break;
            }
        }
    }
}
