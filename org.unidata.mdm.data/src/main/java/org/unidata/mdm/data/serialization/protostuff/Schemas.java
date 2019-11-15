package org.unidata.mdm.data.serialization.protostuff;

import org.unidata.mdm.core.type.data.ArrayAttribute;
import org.unidata.mdm.core.type.data.CodeAttribute;
import org.unidata.mdm.core.type.data.ComplexAttribute;
import org.unidata.mdm.core.type.data.DataRecord;
import org.unidata.mdm.core.type.data.SimpleAttribute;

import io.protostuff.Schema;

/**
 * @author Mikhail Mikhailov
 * Just a static schemas holder.
 */
public final class Schemas {
    /**
     * Data record schema.
     */
    public static final Schema<DataRecord> DATA_RECORD_SCHEMA = new DataRecordSchema();
    /**
     * Simple attribute schema.
     */
    public static final Schema<SimpleAttribute<?>> SIMPLE_ATTRIBUTE_SCHEMA = new SimpleAttributeSchema();
    /**
     * Code attribute schema.
     */
    public static final Schema<CodeAttribute<?>> CODE_ATTRIBUTE_SCHEMA = new CodeAttributeSchema();
    /**
     * Array attribute schema.
     */
    public static final Schema<ArrayAttribute<?>> ARRAY_ATTRIBUTE_SCHEMA = new ArrayAttributeSchema();
    /**
     * Complex attribute schema.
     */
    public static final Schema<ComplexAttribute> COMPLEX_ATTRIBUTE_SCHEMA = new ComplexAttributeSchema();
    /**
     * Constructor.
     */
    private Schemas() {
        super();
    }
}
