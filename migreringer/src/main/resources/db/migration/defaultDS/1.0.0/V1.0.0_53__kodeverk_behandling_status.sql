INSERT INTO KODEVERK (KODE, KODEVERK_EIER, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, SAMMENSATT)
VALUES ('BEHANDLING_STATUS', 'VL', 'BehandlingStatus', 'Internt kodeverk for behandling status.', 'VL', to_timestamp('29.11.2017', 'DD.MM.RRRR'), 'N');

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'AVSLU', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'FVED', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'IVED', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'OPPRE', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'UTRED', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', '-', to_date('2000-01-01', 'YYYY-MM-DD'));


INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'AVSLU', 'NB', 'Avsluttet');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'FVED', 'NB', 'Fatter vedtak');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'IVED', 'NB', 'Iverksetter vedtak');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'OPPRE', 'NB', 'Opprettet');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', 'UTRED', 'NB', 'Behandling utredes');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'BEHANDLING_STATUS', '-', 'NB', 'Ikke definert');