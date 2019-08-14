INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_YTELSE', 'SVP', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_YTELSE', 'SVP', 'NB', 'Svangerskapspenger');
