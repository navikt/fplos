--NYTT KODEVERK RETTEN TIL
INSERT INTO KODEVERK (kode, navn, beskrivelse, kodeverk_eier) VALUES ('KO_SORTERING', 'Valgt sortering for kø', 'Mulige sorteringer for saksbehandleres køer','FPLOS');

INSERT INTO KODELISTE (id, kodeverk, kode, beskrivelse, gyldig_fom)
VALUES(seq_kodeliste.nextval, 'KO_SORTERING', 'OPPRBEH', 'Dato for opprettelse av behandling', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE_NAVN_I18N (ID, KL_KODEVERK, KL_KODE, SPRAK, NAVN)
VALUES (SEQ_KODELISTE_NAVN_I18N.NEXTVAL, 'KO_SORTERING', 'OPPRBEH', 'NB', 'Dato for opprettelse av behandling');

INSERT INTO KODELISTE (id, kodeverk, kode, beskrivelse, gyldig_fom)
VALUES(seq_kodeliste.nextval, 'KO_SORTERING', 'BEHFRIST', 'Dato for behandlingsfrist', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE_NAVN_I18N (ID, KL_KODEVERK, KL_KODE, SPRAK, NAVN)
VALUES (SEQ_KODELISTE_NAVN_I18N.NEXTVAL, 'KO_SORTERING', 'BEHFRIST', 'NB', 'Dato for behandlingsfrist');

INSERT INTO KODELISTE (id, kodeverk, kode, beskrivelse, gyldig_fom)
VALUES(seq_kodeliste.nextval, 'KO_SORTERING', '-', 'udefinert', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE_NAVN_I18N (ID, KL_KODEVERK, KL_KODE, SPRAK, NAVN)
VALUES (SEQ_KODELISTE_NAVN_I18N.NEXTVAL, 'KO_SORTERING', '-', 'NB', 'Udefinert');



