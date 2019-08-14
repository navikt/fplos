
INSERT INTO KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE, KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, SAMMENSATT)
VALUES ('BEHANDLING_TYPE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Arkivfiltyper', '5', 'Behandlingstyper', 'N',
                         'N', 'Behandlingstyper', 'Internt kodeverk for behandlingstyper.', 'VL', to_timestamp('29.11.2017', 'DD.MM.RRRR'), 'N');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, NAVN, BESKRIVELSE, SPRAK, GYLDIG_FOM, GYLDIG_TOM)
VALUES (seq_kodeliste.nextval, 'BEHANDLING_TYPE', 'BT-006', 'ae0042', 'Dokumentinnsyn', 'Dokumentinnsyn', 'NB',
        to_date('01.07.2006', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));

INSERT INTO KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, NAVN, BESKRIVELSE, SPRAK, GYLDIG_FOM, GYLDIG_TOM)
VALUES (seq_kodeliste.nextval, 'BEHANDLING_TYPE', 'BT-002', 'ae0043', 'Førstegangsbehandling', 'Førstegangsbehandling', 'NB',
        to_date('01.07.2006', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));

INSERT INTO KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, NAVN, BESKRIVELSE, SPRAK, GYLDIG_FOM, GYLDIG_TOM)
VALUES (seq_kodeliste.nextval, 'BEHANDLING_TYPE', 'BT-003', 'ae0058', 'Klage', 'Klage', 'NB',
        to_date('01.07.2006', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));

INSERT INTO KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, NAVN, BESKRIVELSE, SPRAK, GYLDIG_FOM, GYLDIG_TOM)
VALUES (seq_kodeliste.nextval, 'BEHANDLING_TYPE', 'BT-004', 'ae0028', 'Revurdering', 'Revurdering', 'NB',
        to_date('01.07.2006', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));

INSERT INTO KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, NAVN, BESKRIVELSE, SPRAK, GYLDIG_FOM, GYLDIG_TOM)
VALUES (seq_kodeliste.nextval, 'BEHANDLING_TYPE', 'BT-005', 'ae0042', 'Søknad', 'Søknad', 'NB',
        to_date('01.07.2006', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));

INSERT INTO KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, NAVN, BESKRIVELSE, SPRAK, GYLDIG_FOM, GYLDIG_TOM)
VALUES (seq_kodeliste.nextval, 'BEHANDLING_TYPE', '-', null, 'Ukjent', 'Ukjent', 'NB',
        to_date('01.07.2006', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));
