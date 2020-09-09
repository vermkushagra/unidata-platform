INSERT INTO S_RESOURCE(NAME, DISPLAY_NAME, R_TYPE)
SELECT 'BULK_OPERATIONS_OPERATOR', 'Групповые операции', 'USER_DEFINED'
WHERE
    NOT EXISTS (
        SELECT NAME FROM S_RESOURCE WHERE NAME = 'BULK_OPERATIONS_OPERATOR'
    );
UPDATE S_RESOURCE SET R_TYPE='SYSTEM' WHERE NAME='BULK_OPERATIONS_OPERATOR';