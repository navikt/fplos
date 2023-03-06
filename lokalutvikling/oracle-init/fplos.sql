-- ###############################
-- ### Opplegg for lokal jetty ###
-- ###############################
DECLARE userexists INTEGER;
BEGIN
SELECT count(*)
INTO userexists
FROM SYS.ALL_USERS
WHERE USERNAME = 'FPLOS';
IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER FPLOS IDENTIFIED BY fplos');
END IF;
END;
/

GRANT CONNECT, RESOURCE, CREATE JOB, CREATE TABLE, CREATE SYNONYM, CREATE VIEW, CREATE MATERIALIZED VIEW TO FPLOS;

-- ##################################################
-- ### Opplegg for enhetstester (lokal + jenkins) ###
-- ##################################################
DECLARE userexists INTEGER;
BEGIN
SELECT count(*)
INTO userexists
FROM SYS.ALL_USERS
WHERE USERNAME = upper('fplos_unit');
IF (userexists = 0)
  THEN
    EXECUTE IMMEDIATE ('CREATE USER fplos_unit IDENTIFIED BY fplos_unit');
END IF;
END;
/

GRANT CONNECT, RESOURCE, CREATE JOB, CREATE TABLE, CREATE SYNONYM, CREATE VIEW, CREATE MATERIALIZED VIEW TO fplos_unit;
