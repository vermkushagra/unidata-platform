package org.unidata.mdm.data.type.keys;

import java.io.Serializable;

import org.unidata.mdm.core.type.keys.EtalonKey;

/**
 * Etalon record id.
 */
public class RecordEtalonKey extends EtalonKey implements Serializable {
    /**
     * SVUID.
     */
    private static final long serialVersionUID = -6495921977514956243L;
    /**
     * Constructor.
     * @param b builder.
     */
    private RecordEtalonKey(RecordEtalonKeyBuilder b) {
        super(b);
    }
    /**
     * Builder.
     * @return
     */
    public static RecordEtalonKeyBuilder builder() {
        return new RecordEtalonKeyBuilder();
    }
    /**
     * Copy.
     * @return
     */
    public static RecordEtalonKeyBuilder builder(RecordEtalonKey other) {
        return new RecordEtalonKeyBuilder(other);
    }
    /**
     * @author Mikhail Mikhailov
     * Builder class.
     */
    public static class RecordEtalonKeyBuilder extends EtalonKeyBuilder<RecordEtalonKeyBuilder> {
        /**
         * Constructor.
         */
        private RecordEtalonKeyBuilder() {
            super();
        }
        /**
         * Copy constructor.
         * @param other
         */
        private RecordEtalonKeyBuilder(RecordEtalonKey other) {
            super(other);
        }
        /**
         * Build.
         * @return key
         */
        @Override
        public RecordEtalonKey build() {
            return new RecordEtalonKey(this);
        }
    }
}
