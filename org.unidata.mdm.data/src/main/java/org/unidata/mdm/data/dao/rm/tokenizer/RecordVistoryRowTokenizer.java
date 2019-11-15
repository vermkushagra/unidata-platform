package org.unidata.mdm.data.dao.rm.tokenizer;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PGbytea;
import org.unidata.mdm.core.dao.tokenizer.CompositeValueIterator;
import org.unidata.mdm.core.dao.tokenizer.CompositeValueTokenizer;
import org.unidata.mdm.core.dao.tokenizer.RowTokenizerField;
import org.unidata.mdm.data.po.data.RecordVistoryPO;
import org.unidata.mdm.data.serialization.DataSerializer;

/**
 * @author Mikhail Mikhailov
 * Record vistory tokenizer.
 */
public class RecordVistoryRowTokenizer extends AbstractVistoryRowTokenizer<RecordVistoryPO> {
    /**
     * The default static tokenizer.
     */
    public static final RecordVistoryRowTokenizer DEFAULT_RECORD_VISTORY_TOKENIZER
        = new RecordVistoryRowTokenizer();
    /**
     * Jaxb Data.
     */
    private static final RowTokenizerField<RecordVistoryPO> DATA_A = (v, po) -> {

        if (StringUtils.isBlank(v)) {
            return;
        }

        po.setData(DataSerializer.restoreOriginRecordFromJaxb(CompositeValueTokenizer.stripText(v)));
    };
    /**
     * Protostuff data.
     */
    private static final RowTokenizerField<RecordVistoryPO> DATA_B = (v, po) -> {

        if (StringUtils.isBlank(v)) {
            return;
        }

        try {
            po.setData(DataSerializer.fromProtostuff(PGbytea.toBytes(CompositeValueTokenizer.stripBytea(v).trim().getBytes())));
        } catch(SQLException sqle) {
            // Just suppress
        }
    };

    private static final RowTokenizerField<RecordVistoryPO> MAJOR = (v, po) -> po.setMajor(Integer.parseInt(v));
    private static final RowTokenizerField<RecordVistoryPO> MINOR = (v, po) -> po.setMinor(Integer.parseInt(v));

    private static final RowTokenizerField<RecordVistoryPO>[] FIELDS = RowTokenizerField.fields(DATA_A, DATA_B, MAJOR, MINOR);

    /**
     * Constructor.
     */
    private RecordVistoryRowTokenizer() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected int size() {
        return super.size() + FIELDS.length;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    protected RecordVistoryPO process(CompositeValueTokenizer fields) {

        int sz = size();
        if (fields.getSize() != sz) {
            return null;
        }

        CompositeValueIterator rti = new CompositeValueIterator(fields);
        RecordVistoryPO po = new RecordVistoryPO();
        super.process(rti, po);
        super.process(rti, po, FIELDS);

        return po;
    }
}
