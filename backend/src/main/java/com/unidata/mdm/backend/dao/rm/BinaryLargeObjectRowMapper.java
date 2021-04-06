/**
 *
 */
package com.unidata.mdm.backend.dao.rm;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.unidata.mdm.backend.po.BinaryLargeObjectPO;

/**
 * @author Mikhail Mikhailov
 * Binary data.
 */
public class BinaryLargeObjectRowMapper extends LargeObjectAbstractRowMapper
    implements RowMapper<BinaryLargeObjectPO> {

    /**
     * Default reusable row mapper.
     */
    public static final BinaryLargeObjectRowMapper DEFAULT_ROW_MAPPER = new BinaryLargeObjectRowMapper();

    /**
     * Constructor.
     */
    public BinaryLargeObjectRowMapper() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinaryLargeObjectPO mapRow(ResultSet rs, int rowNum) throws SQLException {

        BinaryLargeObjectPO po = new BinaryLargeObjectPO();
        super.mapRow(po, rs, rowNum);

        return po;
    }

}
