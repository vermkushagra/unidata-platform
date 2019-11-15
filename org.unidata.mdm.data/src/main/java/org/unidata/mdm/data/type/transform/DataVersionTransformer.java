package org.unidata.mdm.data.type.transform;

import org.unidata.mdm.data.type.data.OriginRecord;

/**
 * @author Mikhail Mikhailov
 * Interface for stale data transformations.
 */
public abstract class DataVersionTransformer {
    /**
     * This member's major.
     */
    private int major;
    /**
     * This member's minor.
     */
    private int minor;
    /**
     * Next member.
     */
    private DataVersionTransformer next;
    /**
     * Constructor.
     * @param major the major
     * @param minor the minor
     */
    public DataVersionTransformer(int major, int minor) {
        super();
        this.major = major;
        this.minor = minor;
    }
    /**
     * Transform re cord if needed.
     * @param record the record to transform
     */
    public void transform(OriginRecord record) {

        boolean apply = false;
        if (record.getInfoSection().getMajor() < this.major
        || (record.getInfoSection().getMajor() == this.major && record.getInfoSection().getMinor() < this.minor)) {
            apply = true;
        }

        if (apply) {
            apply(record);
        }

        if (next != null) {
            next.transform(record);
        }
    }
    /**
     * @param next the next to set
     */
    public void setNext(DataVersionTransformer next) {
        this.next = next;
    }
    /**
     * Transorm record if necessary and give it to the chain.
     * @param record the record to transform
     */
    public abstract void apply(OriginRecord record);
}
