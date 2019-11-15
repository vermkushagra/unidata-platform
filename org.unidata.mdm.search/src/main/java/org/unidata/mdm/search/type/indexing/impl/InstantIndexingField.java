package org.unidata.mdm.search.type.indexing.impl;

import java.time.Instant;
import java.time.ZoneId;

import org.unidata.mdm.search.type.FieldType;

/**
 * @author Mikhail Mikhailov on Oct 15, 2019
 */
public class InstantIndexingField extends AbstractValueIndexingField<Instant, InstantIndexingField> {
    /**
     * Specified zone id or system default.
     */
    private ZoneId zoneId;

    /**
     * Constructor.
     * @param name
     */
    public InstantIndexingField(String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldType getFieldType() {
        return FieldType.INSTANT;
    }

    /**
     * @return the zoneId
     */
    public ZoneId getZoneId() {
        return zoneId == null ? ZoneId.systemDefault() : zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     */
    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }
    /**
     * Sets zone id.
     * @param id the zone id
     * @return self
     */
    public InstantIndexingField withZoneId(ZoneId id) {
        setZoneId(id);
        return self();
    }
}
