package org.unidata.mdm.meta.migration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import nl.myndocs.database.migrator.MigrationScript;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * storage migrations to install security meta + admin login + resource
 *
 *
 * @author maria.chistyakova
 */
public final class InstallMetaSchemaMigrations {

    private static final MigrationScript[] MIGRATIONS = {
            new UN12317InitializationMetaSchema()

    };

    /**
     * Constructor.
     */
    private InstallMetaSchemaMigrations() {
        super();
    }

    /**
     * Makes SONAR happy.
     *
     * @return migrations
     */
    public static MigrationScript[] migrations() {
        return MIGRATIONS;
    }

    public static InputStream[] loadRawResources(ApplicationContext ctx, String... names) {

        Resource[] raw = Arrays.stream(names)
                .map(name -> ctx.getResource("classpath:/storage/migration/" + name + ".sql"))
                .toArray(Resource[]::new);

        if (raw.length > 0) {
            return Stream.of(raw)
                    .map(r -> { try { return r.getInputStream(); } catch (IOException ioe) { return null; } })
                    .filter(Objects::nonNull)
                    .toArray(InputStream[]::new);
        }

        return (InputStream[]) ArrayUtils.EMPTY_OBJECT_ARRAY;
    }


}
