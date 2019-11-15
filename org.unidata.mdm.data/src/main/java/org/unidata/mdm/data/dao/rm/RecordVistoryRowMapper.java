/**
 *
 */
package org.unidata.mdm.data.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.unidata.mdm.data.po.data.RecordVistoryPO;

/**
 * @author Mikhail Mikhailov
 * Origins vistory row mapper.
 */
public class RecordVistoryRowMapper extends AbstractVistoryRowMapper<RecordVistoryPO> implements RowMapper<RecordVistoryPO> {
    /**
     * Default 'JAXB data' row mapper.
     */
    public static final RecordVistoryRowMapper DEFAULT_JAXB_ROW_MAPPER
        = new RecordVistoryRowMapper(true, false);
    /**
     * Default 'binary protostuff data' row mapper.
     */
    public static final RecordVistoryRowMapper DEFAULT_PROTOSTUFF_ROW_MAPPER
        = new RecordVistoryRowMapper(false, true);
    /**
     * Default 'binary protostuff data' row mapper.
     */
    public static final RecordVistoryRowMapper RAW_PROTOSTUFF_ROW_MAPPER
        = new RecordVistoryRowMapper(false, true, true);
    /**
     * Default 'without data' row mapper.
     */
    public static final RecordVistoryRowMapper DEFAULT_NO_DATA_ROW_MAPPER
        = new RecordVistoryRowMapper(false, false);
    /**
     * Constructor.
     */
    protected RecordVistoryRowMapper(boolean jaxbData, boolean protostuffData) {
        super(jaxbData, protostuffData);
    }
    /**
     * Constructor.
     */
    protected RecordVistoryRowMapper(boolean jaxbData, boolean protostuffData, boolean rawData) {
        super(jaxbData, protostuffData, rawData);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RecordVistoryPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        RecordVistoryPO po = new RecordVistoryPO();
        super.mapRow(po, rs, rowNum);
        return po;
    }
}
