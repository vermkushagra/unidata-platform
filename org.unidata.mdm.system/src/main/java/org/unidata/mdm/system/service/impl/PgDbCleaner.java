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

package org.unidata.mdm.system.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unidata.mdm.system.service.DbCleaner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

@Service
public class PgDbCleaner implements DbCleaner {

    @Value("${unidata.db.clean.app.schemas}")
    private String schemas;

    private final DataSource systemDataSource;

    public PgDbCleaner(final DataSource systemDataSource) {
        this.systemDataSource = systemDataSource;
    }

    @Override
    public void clean() {
        if (schemas == null) {
            return;
        }
        try (Connection connection = systemDataSource.getConnection()) {
            Arrays.stream(schemas.split(";")).forEach(s -> {
                try {
                    final Statement statement = connection.createStatement();
                    statement.executeUpdate(String.format("drop schema if exists %s cascade", s));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
