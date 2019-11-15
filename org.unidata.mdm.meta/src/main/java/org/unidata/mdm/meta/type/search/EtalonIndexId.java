package org.unidata.mdm.meta.type.search;

import org.unidata.mdm.search.type.IndexType;
import org.unidata.mdm.search.type.id.AbstractManagedIndexId;

/**
 * @author Dmitry Kopin on 26.10.2018.
 */
public class EtalonIndexId extends AbstractManagedIndexId {
    /**
     * Constructor.
     */
    private EtalonIndexId() {
        super();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public IndexType getSearchType() {
        return EntityIndexType.ETALON;
    }
    /**
     * Creates a new record index id.
     * @param entityName the entity name
     * @param etalonId the to etalon id
     * @return index id
     */
    public static EtalonIndexId of(String entityName, String etalonId) {

        EtalonIndexId id = new EtalonIndexId();

        id.entityName = entityName;
        id.indexId = etalonId;
        id.routing = etalonId;

        return id;
    }
}
