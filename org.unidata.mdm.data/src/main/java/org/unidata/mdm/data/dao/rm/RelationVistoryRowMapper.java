package org.unidata.mdm.data.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.unidata.mdm.data.po.data.RelationVistoryPO;

/**
 * @author Mikhail Mikhailov
 * Relations vistory row mapper.
 */
public class RelationVistoryRowMapper extends AbstractVistoryRowMapper<RelationVistoryPO> implements RowMapper<RelationVistoryPO> {
    /**
     * Default 'with JAXB data' row mapper.
     */
    public static final RelationVistoryRowMapper DEFAULT_JAXB_ROW_MAPPER
        = new RelationVistoryRowMapper(true, false);
    /**
     * Default 'with Protostuff data' row mapper.
     */
    public static final RelationVistoryRowMapper DEFAULT_PROTOSTUFF_ROW_MAPPER
        = new RelationVistoryRowMapper(false, true);
    /**
     * Default 'without data' row mapper.
     */
    public static final RelationVistoryRowMapper DEFAULT_NO_DATA_ROW_MAPPER
        = new RelationVistoryRowMapper(false, false);
    /**
     * Default 'binary protostuff data' row mapper.
     */
    public static final RelationVistoryRowMapper RAW_PROTOSTUFF_ROW_MAPPER
        = new RelationVistoryRowMapper(false, true, true);
    /**
     * Constructor.
     */
    private RelationVistoryRowMapper(boolean jaxbData, boolean protostuffData) {
        super(jaxbData, protostuffData);
    }
    /**
     * Constructor.
     */
    private RelationVistoryRowMapper(boolean jaxbData, boolean protostuffData, boolean rawData) {
        super(jaxbData, protostuffData, rawData);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public RelationVistoryPO mapRow(ResultSet rs, int rowNum) throws SQLException {
        RelationVistoryPO po = new RelationVistoryPO();
        super.mapRow(po, rs, rowNum);
        return po;
    }

}
