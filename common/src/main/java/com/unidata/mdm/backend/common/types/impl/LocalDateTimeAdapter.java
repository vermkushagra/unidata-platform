package com.unidata.mdm.backend.common.types.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Mikhail Mikhailov
 *
 */
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    /**
     * Constructor.
     */
    public LocalDateTimeAdapter() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
     */
    @Override
    public LocalDateTime unmarshal(String v) throws Exception {

        if (Objects.isNull(v)) {
            return null;
        }

        return LocalDateTime.parse(v, DateTimeFormatter.ISO_DATE_TIME);
    }

    /* (non-Javadoc)
     * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
     */
    @Override
    public String marshal(LocalDateTime v) throws Exception {

        if (Objects.isNull(v)) {
            return null;
        }

        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(v);
    }

}
