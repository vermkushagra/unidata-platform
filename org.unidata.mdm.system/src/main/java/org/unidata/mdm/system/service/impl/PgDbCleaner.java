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
