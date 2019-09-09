UPDATE AVDELING
SET AVDELING_ENHET = '4292',
    NAVN           = 'NAV Klageinstans Midt-Norge',
    VERSJON        = VERSJON + 1,
    ENDRET_AV      = 'VL',
    ENDRET_TID     = current_timestamp
WHERE AVDELING_ENHET = '4205'
  AND NAVN = 'Klageinstans Midt-Norge';
