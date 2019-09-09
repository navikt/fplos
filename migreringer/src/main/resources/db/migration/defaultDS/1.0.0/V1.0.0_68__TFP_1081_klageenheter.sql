UPDATE AVDELING
SET AVDELING_ENHET   = '4292',
    NAVN             = 'NAV Klageinstans Midt-Norge',
    VERSJON          = VERSJON + 1,
    ENDRET_AV        = 'VL',
    ENDRET_TID = current_timestamp
WHERE AVDELING_ENHET = '4205'
  AND NAVN = 'Klageinstans Midt-Norge';

INSERT INTO AVDELING (ID, AVDELING_ENHET, NAVN, VERSJON, OPPRETTET_AV, OPPRETTET_TID, KREVER_KODE_6)
VALUES (seq_avdeling.nextval, '4291', 'NAV Klageinstans Oslo/Akershus', 0, 'VL', current_timestamp, 'N');

INSERT INTO AVDELING (ID, AVDELING_ENHET, NAVN, VERSJON, OPPRETTET_AV, OPPRETTET_TID, KREVER_KODE_6)
VALUES (seq_avdeling.nextval, '4293', 'NAV Klageinstans Øst', 0, 'VL', current_timestamp, 'N');

INSERT INTO AVDELING (ID, AVDELING_ENHET, NAVN, VERSJON, OPPRETTET_AV, OPPRETTET_TID, KREVER_KODE_6)
VALUES (seq_avdeling.nextval, '4294', 'NAV Klageinstans Vest', 0, 'VL', current_timestamp, 'N');

INSERT INTO AVDELING (ID, AVDELING_ENHET, NAVN, VERSJON, OPPRETTET_AV, OPPRETTET_TID, KREVER_KODE_6)
VALUES (seq_avdeling.nextval, '4295', 'NAV Klageinstans Nord', 0, 'VL', current_timestamp, 'N');
