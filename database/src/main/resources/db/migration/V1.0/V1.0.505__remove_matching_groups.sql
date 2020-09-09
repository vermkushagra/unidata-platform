DO $outher$
BEGIN
  IF NOT EXISTS(SELECT 1
                FROM schema_version
                WHERE script ILIKE '% remove_matching_group %')
  THEN

    DROP TABLE IF EXISTS matching_group_matching_rule;

    DROP INDEX IF EXISTS ix_clusters_search;
    CREATE UNIQUE INDEX ix_clusters_search
      ON clusters USING BTREE (entity_name, rule_id, cluster_identifier);

    ALTER TABLE clusters
      DROP COLUMN IF EXISTS group_id;

    DROP INDEX IF EXISTS ix_blocked_records_search;
    CREATE UNIQUE INDEX ix_blocked_records_search
      ON blocked_matched_records USING BTREE (entity_name, rule_id, cluster_identifier);

    ALTER TABLE blocked_matched_records
      DROP COLUMN IF EXISTS group_id;

    DROP TABLE IF EXISTS matching_groups;

    ALTER TABLE matching_rules
      ADD COLUMN auto_merge BOOLEAN DEFAULT FALSE;



    DROP FUNCTION IF EXISTS ud_calc_records_etalon_batch_blocks(_start BIGINT, _block INT, _names TEXT [], _operation_id TEXT, _upd_mode TEXT );

    CREATE OR REPLACE FUNCTION ud_calc_records_etalon_batch_blocks(_start        BIGINT, _block INT, _names TEXT [],
                                                                   _operation_id TEXT, _upd_mode TEXT,
                                                                   _statuses     TEXT [])
      RETURNS TABLE(block_num INT, start_id CHARACTER(36), start_gsn BIGINT, end_id CHARACTER(36), end_gsn BIGINT, name TEXT)
    AS $inner$
DECLARE
exec_sql TEXT;
cur_gsn BIGINT := COALESCE (_start, -9223372036854775808);
cur_block INT := 0;
cur_name TEXT;
block_sz INT := COALESCE (_block, 5000);
statuses TEXT [] := _statuses:: TEXT [];
BEGIN
-- 1. Result table
CREATE TEMPORARY TABLE __result (
  block_num INT NOT NULL PRIMARY KEY,
  start_id  CHARACTER(36),
  start_gsn BIGINT,
  end_id    CHARACTER(36),
  end_gsn   BIGINT,
  name      TEXT
) ON COMMIT DROP;
-- 2. Exec stmt
exec_sql := ' WITH _block AS (SELECT
                                 id,
                                 gsn,
                                 name
                               FROM etalons
                               WHERE gsn >= $1 ';
IF (array_length(_names, 1) > 0) THEN
exec_sql := exec_sql || ' AND NAME = $6';
END IF;

IF (_operation_id IS NOT NULL ) THEN
IF (_upd_mode = 'RELATIONS') THEN
exec_sql := exec_sql || ' AND ( EXISTS ( SELECT TRUE FROM etalons_relations e WHERE e.etalon_id_from = etalons.id AND e.operation_id = $2) OR EXISTS '
|| ' ( SELECT TRUE FROM etalons_relations e, origins_relations o, origins_relations_vistory v WHERE e.etalon_id_from = etalons.id AND o.etalon_id = e.id AND o.id = v.origin_id AND v.operation_id = $2))';
ELSE
exec_sql := exec_sql || ' AND ((etalons.operation_id = $2 OR EXISTS ( SELECT TRUE FROM origins o, origins_vistory v WHERE o.etalon_id = etalons.id AND o.id = v.origin_id AND v.operation_id = $2))'
|| ' OR ( EXISTS ( SELECT TRUE FROM etalons_classifiers e WHERE e.etalon_id_record = etalons.id AND e.operation_id = $2) OR EXISTS '
|| ' ( SELECT TRUE FROM etalons_classifiers e, origins_classifiers o, origins_classifiers_vistory v WHERE e.etalon_id_record = etalons.id AND o.etalon_id = e.id AND o.id = v.origin_id AND v.operation_id = $2)))';
END IF;
END IF;

IF (_statuses IS NOT NULL ) THEN
exec_sql := exec_sql || ' AND status = ANY ( ARRAY [$5]::record_status[])';
END IF;

exec_sql := exec_sql || ' ORDER BY gsn LIMIT $3), '
|| ' _block_start AS ( SELECT id, gsn, NAME FROM _block ORDER BY gsn ASC LIMIT 1), _block_end AS ( SELECT id, gsn, NAME FROM _block ORDER BY gsn DESC LIMIT 1)'
|| ' INSERT INTO __result (block_num, start_id, start_gsn, end_id, end_gsn, name)'
|| ' SELECT
        $4,
        _block_start.id,
        _block_start.gsn,
        _block_end.id,
        _block_end.gsn,
        ';

IF (array_length(_names, 1) > 0) THEN
exec_sql := exec_sql || ' _block_end.name';
ELSE
exec_sql := exec_sql || ' NULL ';
END IF;

exec_sql := exec_sql || ' FROM _block_start, _block_end';

-- RAISE NOTICE 'sql [%]', exec_sql;

IF (array_length(_names, 1) > 0) THEN
FOREACH cur_name IN ARRAY _names LOOP

cur_gsn := ( SELECT MIN (gsn) FROM etalons WHERE etalons.name = cur_name);
WHILE TRUE LOOP
EXECUTE exec_sql USING  cur_gsn, _operation_id, block_sz, cur_block, statuses, cur_name;

cur_gsn := ( SELECT __result.end_gsn FROM __result WHERE __result.block_num = cur_block);
IF cur_gsn IS NULL THEN
EXIT;
END IF;

cur_gsn := cur_gsn + 1;
cur_block := cur_block + 1;

END LOOP;
END LOOP;
ELSE
WHILE TRUE LOOP
EXECUTE exec_sql USING  cur_gsn, _operation_id, block_sz, cur_block, statuses;

cur_gsn := ( SELECT __result.end_gsn FROM __result WHERE __result.block_num = cur_block);
IF cur_gsn IS NULL THEN
EXIT;
END IF;

cur_gsn := cur_gsn + 1;
cur_block := cur_block + 1;

END LOOP;
END IF;
RETURN QUERY SELECT
               __result.block_num,
               __result.start_id,
               __result.start_gsn,
               __result.end_id,
               __result.end_gsn,
               __result.name
             FROM __result;

end $inner$ LANGUAGE plpgsql;
END IF;
END $outher$;
