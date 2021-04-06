package com.unidata.mdm.backend.service.notification.messages;

import javax.annotation.Nullable;

import com.unidata.mdm.api.UnidataMessageDef;

public class UnidataMessage implements NotificationMessage {

    @Nullable
    private final UnidataMessageDef unidataMessage;

    public UnidataMessage(@Nullable UnidataMessageDef unidataMessage) {
        this.unidataMessage = unidataMessage;
    }

    @Nullable
    public UnidataMessageDef getUnidataMessage() {
        return unidataMessage;
    }
}
