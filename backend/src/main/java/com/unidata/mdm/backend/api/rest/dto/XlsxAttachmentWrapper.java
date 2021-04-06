package com.unidata.mdm.backend.api.rest.dto;

import java.nio.file.Path;

import javax.annotation.Nonnull;

/**
 * Wrapper for xslattachment
 */
public class XlsxAttachmentWrapper {
    /**
     *
     */
    @Nonnull
    private final Path attachment;

    /**
     *
     * @param attachment
     */
    public XlsxAttachmentWrapper(@Nonnull Path attachment) {
        this.attachment = attachment;
    }

    @Nonnull
    public Path getAttachment() {
        return attachment;
    }
}
