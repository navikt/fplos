import React from 'react';
import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import ReservasjonerTabell from './ReservasjonerTabell';

interface TsProps {
  reservasjoner: Reservasjon[];
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
}
const ReservasjonerPanel = ({
  reservasjoner,
  opphevReservasjon,
  endreOppgaveReservasjon,
  flyttReservasjon,
}: TsProps) => (
  <>
    <ReservasjonerTabell
      reservasjoner={reservasjoner}
      opphevReservasjon={opphevReservasjon}
      endreOppgaveReservasjon={endreOppgaveReservasjon}
      flyttReservasjon={flyttReservasjon}
    />
  </>
);

export default ReservasjonerPanel;
