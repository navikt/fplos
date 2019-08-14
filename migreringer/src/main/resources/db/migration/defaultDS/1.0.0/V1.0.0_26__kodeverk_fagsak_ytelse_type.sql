INSERT INTO KODEVERK (KODE, KODEVERK_EIER, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, SAMMENSATT)
VALUES ('FAGSAK_YTELSE', 'VL', 'FagsakYtelseType', 'Internt kodeverk for ytelsestype.', 'VL', to_timestamp('29.11.2017', 'DD.MM.RRRR'), 'N');

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_YTELSE', 'ES', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_YTELSE', 'FP', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_YTELSE', '-', to_date('2000-01-01', 'YYYY-MM-DD'));


INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_YTELSE', 'ES', 'NB', 'Engangsstønad');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_YTELSE', 'FP', 'NB', 'Foreldrepenger');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_YTELSE', '-', 'NB', 'Ikke definert');