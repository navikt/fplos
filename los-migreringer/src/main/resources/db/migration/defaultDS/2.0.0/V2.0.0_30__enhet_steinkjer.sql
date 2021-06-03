INSERT INTO AVDELING (ID,AVDELING_ENHET,NAVN)
values (SEQ_AVDELING.nextval,'4817','NAV Familie- og pensjonsytelser Steinkjer');

insert into AVDELING_SAKSBEHANDLER(saksbehandler_id, avdeling_id)
SELECT avs.SAKSBEHANDLER_ID, a2.id
from AVDELING_SAKSBEHANDLER avs, AVDELING a1, AVDELING a2
where a1.AVDELING_ENHET='4847' and a2.AVDELING_ENHET = '4817'
  and a1.id=avs.avdeling_id;