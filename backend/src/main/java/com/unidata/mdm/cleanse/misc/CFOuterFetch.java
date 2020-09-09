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

package com.unidata.mdm.cleanse.misc;

import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT2;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT3;
import static com.unidata.mdm.cleanse.common.CleanseConstants.INPUT4;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT1;
import static com.unidata.mdm.cleanse.common.CleanseConstants.OUTPUT2;
import static java.sql.ResultSet.CONCUR_READ_ONLY;
import static java.sql.ResultSet.TYPE_FORWARD_ONLY;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;
import com.unidata.mdm.backend.common.types.SimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.AbstractSimpleAttribute;
import com.unidata.mdm.backend.common.types.impl.BooleanSimpleAttributeImpl;
import com.unidata.mdm.backend.common.types.impl.IntegerSimpleAttributeImpl;
import com.unidata.mdm.backend.service.data.util.AttributeUtils;
import com.unidata.mdm.cleanse.common.BasicCleanseFunctionAbstract;

/**
 * Fetch data from external
 */
public class CFOuterFetch extends BasicCleanseFunctionAbstract {

    /**
     * Index of the first column
     */
    private static final int FIRST_COLUMN = 1;

    /**
     * Single result count
     */
    private static final long SINGLE_RESULT_COUNT = 1;

    /**
     * Instantiates a new cleanse function abstract.
     */
    public CFOuterFetch() {
        super(CFOuterFetch.class);
    }

    @Override
    public void execute(Map<String, Object> input, Map<String, Object> result) throws Exception {
        String jdbcUrl = (String) super.getValueByPort(INPUT1, input);
        String query = (String) super.getValueByPort(INPUT2, input);
        Long fetchType = (Long) super.getValueByPort(INPUT3, input);
        String outputType = (String) super.getValueByPort(INPUT4, input);
        SimpleAttribute.DataType dataType = SimpleAttribute.DataType.valueOf(outputType.toUpperCase());

        //TODO cache it in wrapper! (as a variant)
        DataSource outerDataSource = DataSources.unpooledDataSource(jdbcUrl);
        Object fetchedObject = null;
        long count = 0;
        try (Connection connection = outerDataSource.getConnection();
                Statement statement = connection.createStatement(TYPE_FORWARD_ONLY, CONCUR_READ_ONLY);
                ResultSet resultSet = statement.executeQuery(query)) {
            Object lastResult = null;
            Object firstResult = null;
            while (resultSet != null && resultSet.next()) {
                count++;
                lastResult = resultSet.getObject(FIRST_COLUMN);
                firstResult = count == SINGLE_RESULT_COUNT ? lastResult : firstResult;
            }
            fetchedObject = fetchType.intValue() == 0 ? firstResult : lastResult;
        }
        boolean justOne = count == SINGLE_RESULT_COUNT;
        SimpleAttribute<?> simpleAttribute = AbstractSimpleAttribute.of(dataType, OUTPUT1);
        AttributeUtils.processSimpleAttributeValue(simpleAttribute, fetchedObject);
        result.put(OUTPUT1, simpleAttribute);
        result.put(OUTPUT2, new BooleanSimpleAttributeImpl(OUTPUT2).withValue(justOne));
        result.put("port3", new IntegerSimpleAttributeImpl("port3").withValue(count));
    }
}
