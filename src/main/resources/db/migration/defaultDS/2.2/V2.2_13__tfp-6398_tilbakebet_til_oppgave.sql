alter table OPPGAVE add FEILUTBETALING_BELOP NUMBER(19, 2);
alter table OPPGAVE add FEILUTBETALING_START TIMESTAMP(3);

comment on column OPPGAVE.FEILUTBETALING_BELOP is 'Feilutbetalt beløp fra fptilbake';
comment on column OPPGAVE.FEILUTBETALING_START is 'Startdato for feilutbetaling fra fptilbake';
