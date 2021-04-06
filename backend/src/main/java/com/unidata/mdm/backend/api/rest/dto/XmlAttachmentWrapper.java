package com.unidata.mdm.backend.api.rest.dto;

import java.nio.file.Path;

import javax.annotation.Nonnull;

/**
 *
 */
public class XmlAttachmentWrapper {
    /**
     *
     */
    @Nonnull
    private final Path attachment;

    /**
     * @param attachment
     */
    public XmlAttachmentWrapper(@Nonnull Path attachment) {
        this.attachment = attachment;
    }

    @Nonnull
    public Path getAttachment() {
        return attachment;
    }
}
