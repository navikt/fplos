DELETE from FILTRERING_SAKSBEHANDLER;
DELETE from AVDELING_SAKSBEHANDLER;
DELETE from FILTRERING_BEHANDLING_TYPE;
DELETE from FILTRERING_YTELSE_TYPE;
DELETE from FILTRERING_ANDRE_KRITERIER;
DELETE FROM OPPGAVE_FILTRERING;
DELETE from AVDELING;

INSERT INTO AVDELING(ID, AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval, '4806','NAV Familie- og pensjonsytelser Drammen');
INSERT INTO AVDELING(ID, AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval, '4842','NAV Familie- og pensjonsytelser Stord');

INSERT INTO AVDELING(ID, AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval, '4833','NAV Familie- og pensjonsytelser Oslo 1');
INSERT INTO AVDELING(ID, AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval, '4812','NAV Familie- og pensjonsytelser Bergen');

INSERT INTO AVDELING(ID, AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval, '4847','NAV Familie- og pensjonsytelser Levanger');
INSERT INTO AVDELING(ID, AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval, '4849','NAV Familie- og pensjonsytelser Tromsø');

INSERT INTO AVDELING(ID, AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval, '4205','Klageinstans Midt-Norge');
INSERT INTO AVDELING(ID, AVDELING_ENHET,NAVN) values (SEQ_AVDELING.nextval, '4802','NAV Familie- og pensjonsytelser Bærum');
