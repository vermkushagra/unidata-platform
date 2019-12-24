package org.unidata.mdm.core.service;

import org.unidata.mdm.core.dto.BusMessage;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface BusService {
    void send(@Nonnull String target, @Nonnull BusMessage busMessage);
    Consumer<BusMessage> sender(@Nonnull String target);
}
