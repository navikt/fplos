ALTER SESSION SET CONTAINER=FREEPDB1;

DECLARE
    userexists INTEGER;
BEGIN
    SELECT count(*)
    INTO userexists
    FROM SYS.ALL_USERS
    WHERE USERNAME = 'FPLOS';
    IF (userexists = 0)
    THEN
        EXECUTE IMMEDIATE ('CREATE USER FPLOS IDENTIFIED BY fplos PROFILE DEFAULT ACCOUNT UNLOCK');
    END IF;
END;
/

GRANT
    CREATE SESSION,
    ALTER SESSION,
    CONNECT,
    RESOURCE,
    CREATE MATERIALIZED VIEW,
    CREATE JOB,
    CREATE TABLE,
    CREATE SYNONYM,
    CREATE VIEW,
    CREATE SEQUENCE,
    UNLIMITED TABLESPACE,
    SELECT ANY TABLE
TO FPLOS;

ALTER USER FPLOS QUOTA UNLIMITED ON SYSTEM;
