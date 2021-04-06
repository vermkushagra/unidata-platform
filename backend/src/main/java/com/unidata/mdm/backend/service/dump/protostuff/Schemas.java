package com.unidata.mdm.backend.service.dump.protostuff;

import com.unidata.mdm.backend.common.types.ArrayAttribute;
import com.unidata.mdm.backend.common.types.CodeAttribute;
import com.unidata.mdm.backend.common.types.ComplexAttribute;
import com.unidata.mdm.backend.common.types.DataRecord;
import com.unidata.mdm.backend.common.types.SimpleAttribute;

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
