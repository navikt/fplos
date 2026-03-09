-- Testdata: avdelinger brukt i enhetstester
-- Denne filen kjøres kun under testmigrering og er ikke en del av produksjonsmigreringen.
INSERT INTO avdeling (avdeling_enhet, navn, krever_kode_6)
VALUES ('4806', 'NAV Familie- og pensjonsytelser Drammen', 'N')
ON CONFLICT DO NOTHING;

INSERT INTO avdeling (avdeling_enhet, navn, krever_kode_6)
VALUES ('4867', 'NAV Familie- og pensjonsytelser Nasjonal kø', 'N')
ON CONFLICT DO NOTHING;

INSERT INTO avdeling (avdeling_enhet, navn, krever_kode_6)
VALUES ('1111', 'dummy for slettetest', 'N')
    ON CONFLICT DO NOTHING;
