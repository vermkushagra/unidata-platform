/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.po.CharacterLargeObjectPO;

/**
 * @author Mikhail Mikhailov
 *
 */
public class CharacterLargeObjectRowMapper extends LargeObjectAbstractRowMapper
    implements RowMapper<CharacterLargeObjectPO> {

    /**
     * Default reusable row mapper.
     */
    public static final CharacterLargeObjectRowMapper DEFAULT_ROW_MAPPER = new CharacterLargeObjectRowMapper();

    /**
     * Constructor.
     */
    public CharacterLargeObjectRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharacterLargeObjectPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        CharacterLargeObjectPO po = new CharacterLargeObjectPO();
        super.mapRow(po, rs, rowNum);

        return po;
    }

}
