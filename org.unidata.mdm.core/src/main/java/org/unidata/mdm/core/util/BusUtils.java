package org.unidata.mdm.core.util;

import org.unidata.mdm.core.dto.BusMessage;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class BusUtils {
    private BusUtils() { }

    public static BiConsumer<String, Object> senderWithType(@Nonnull final Consumer<BusMessage> sender) {
        return (type, body) -> sender.accept(BusMessage.withType(type, body));
    }
}
