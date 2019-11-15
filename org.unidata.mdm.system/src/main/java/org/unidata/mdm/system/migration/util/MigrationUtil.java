package org.unidata.mdm.system.migration.util;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Alexander Malyshev
 */
public final class MigrationUtil {
    private MigrationUtil() { }


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
