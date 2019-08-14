ALTER TABLE AVDELING ADD krever_kode_6 VARCHAR2(1 CHAR) DEFAULT 'N';
COMMENT ON COLUMN AVDELING.krever_kode_6 IS 'Kreves kode 6 for å kunne velge denne avdelingen';