INSERT INTO KODEVERK (KODE, KODEVERK_EIER, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, SAMMENSATT)
VALUES ('FAGSAK_STATUS', 'VL', 'FagsakStatus', 'Internt kodeverk for statuser på fagsaker.', 'VL', to_timestamp('29.11.2017', 'DD.MM.RRRR'), 'N');

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_STATUS', 'OPPR', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_STATUS', 'UBEH', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_STATUS', 'LOP', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_STATUS', 'AVSLU', to_date('2000-01-01', 'YYYY-MM-DD'));


INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_STATUS', 'OPPR', 'NB', 'Opprettet');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_STATUS', 'UBEH', 'NB', 'Under behandling');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_STATUS', 'LOP', 'NB', 'Løpende');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSAK_STATUS', 'AVSLU', 'NB', 'Avsluttet');


UPDATE KODEVERK set KODEVERK_EIER = 'VL' where kode = 'FAGSYSTEM';

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'ARENA', 'AO01', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'GRISEN', 'AO11', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'GOSYS', 'FS22', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'INFOTRYGD', 'IT01', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'HJE_HEL_ORT', 'OEBS', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'PESYS', 'PP01', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'VENTELONN', 'V2', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'UNNTAK', 'UFM', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'FPSAK', 'FS36', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', '-', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'UTBETALINGSMELDING', 'OB36', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'MELOSYS', 'FS38', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'JOARK', 'AS36', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'TPS', 'FS03', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'MEDL', 'FS18', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'INNTEKT', 'FS28', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'GSAK', 'FS19', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'BISYS', 'BID', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'SKANNING', 'MOT', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'ØKONOMI', 'OKO', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'BIDRAGINNKREVING', 'BII', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'ØVRIG', 'OVR', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'ENHETSREGISTERET', 'ER01', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, offisiell_kode, gyldig_fom)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'AAREGISTERET', 'AR01', to_date('2000-01-01', 'YYYY-MM-DD'));


INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'ARENA', 'NB', 'Arena');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'GRISEN', 'NB', 'Grisen');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'GOSYS', 'NB', 'Gosys');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'INFOTRYGD', 'NB', 'Infotrygd');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'HJE_HEL_ORT', 'NB', 'Hjelpemidler, Helsetjenester og Ort. Hjelpemidler');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'PESYS', 'NB', 'Pesys');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'VENTELONN', 'NB', 'Ventelønn');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'UNNTAK', 'NB', 'Unntak');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'FPSAK', 'NB', 'Vedtaksløsning Foreldrepenger');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', '-', 'NB', 'Ikke definert');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'UTBETALINGSMELDING', 'NB', 'Utbetalingsmelding');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'MELOSYS', 'NB', 'Melosys');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'JOARK', 'NB', 'Joark');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'TPS', 'NB', 'TPS');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'MEDL', 'NB', 'MEDL');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'INNTEKT', 'NB', 'INNTEKT');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'GSAK', 'NB', 'GSAK');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'BISYS', 'NB', 'BID');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'SKANNING', 'NB', 'MOT');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'ØKONOMI', 'NB', 'OKO');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'BIDRAGINNKREVING', 'NB', 'BII');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'ØVRIG', 'NB', 'ØVRIG');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'ENHETSREGISTERET', 'NB', 'Enhetsregisteret');

INSERT INTO KODELISTE_NAVN_I18N (id, kl_kodeverk, kl_kode, sprak, navn)
VALUES  (seq_kodeliste.nextval, 'FAGSYSTEM', 'AAREGISTERET', 'NB', 'AAregisteret');