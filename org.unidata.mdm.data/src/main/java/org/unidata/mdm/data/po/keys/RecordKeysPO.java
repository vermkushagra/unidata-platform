package org.unidata.mdm.data.po.keys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.unidata.mdm.core.type.calculables.ModificationBoxKey;

/**
 * @author Mikhail Mikhailov
 * PO for keys digest.
 * The DB type is:
 *
 * create type record_key as (
 * id uuid,
 * status record_status,
 * state approval_state,
 * lsn bigint,
 * name varchar(256),
 * approved boolean,
 * create_date timestamptz,
 * created_by varchar(256),
 * update_date timestamptz,
 * updated_by varchar(256),
 * origin_keys record_origin_key[]);
 */
public class RecordKeysPO extends AbstractKeysPO {
    /**
     * Origin keys.
     */
    public static final String FIELD_ORIGIN_KEYS = "origin_keys";
    /**
     * Collection of origin keys.
     */
    private Map<String, RecordOriginKeyPO> originKeys;
    /**
     * Constructor.
     */
    public RecordKeysPO() {
        super();
    }
    /**
     * @return the originKeys
     */
    public List<RecordOriginKeyPO> getOriginKeys() {
        return Objects.isNull(originKeys) ? Collections.emptyList() : new ArrayList<>(originKeys.values());
    }
    /**
     * @param originKeys the originKeys to set
     */
    public void setOriginKeys(List<RecordOriginKeyPO> originKeys) {
        if (CollectionUtils.isNotEmpty(originKeys)) {
            this.originKeys = originKeys.stream()
                    .collect(Collectors.toMap(
                            ok -> ModificationBoxKey.toBoxKey(ok.getSourceSystem(), ok.getExternalId()),
                            Function.identity()));
        }
    }

    public RecordOriginKeyPO findByBoxKey(String boxKey) {
        return originKeys.get(boxKey);
    }

    public RecordOriginKeyPO findByExternalId(String externalId, String sourceSystem) {
        return originKeys.get(ModificationBoxKey.toBoxKey(sourceSystem, externalId));
    }
 }
