--
-- amagdenko

-- Common sequence and function to generate ids
--

CREATE SEQUENCE common_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1
  CACHE 1;

CREATE OR REPLACE FUNCTION generate_ids(seqname text, count integer)
  RETURNS SETOF bigint AS
$BODY$
    DECLARE
        counter int = 0;
    BEGIN
        WHILE counter != count
        LOOP
          return next nextval(seqName);

          counter := counter + 1;
        END LOOP;

      END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
