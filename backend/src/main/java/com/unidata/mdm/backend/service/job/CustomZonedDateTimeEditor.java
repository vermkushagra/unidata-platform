package com.unidata.mdm.backend.service.job;

import org.springframework.util.StringUtils;

import java.beans.PropertyEditorSupport;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Denis Kostovarov
 */
public class CustomZonedDateTimeEditor extends PropertyEditorSupport {

    public CustomZonedDateTimeEditor() {
        // no-op.
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(parseText(text));
    }

    @Override
    public String getAsText() {
        final ZonedDateTime value = parseText(String.valueOf(getValue()));
        return (value != null ? value.toString() : "");
    }

    private ZonedDateTime parseText(String text) {
        ZonedDateTime zdt;
        if (StringUtils.isEmpty(text)) {
            zdt = ZonedDateTime.now(ZoneId.systemDefault());
        } else {
            try {
                zdt = ZonedDateTime.parse(text);
            } catch(Exception ee) {
                zdt = null;
            }
        }

        return zdt;
    }
}
