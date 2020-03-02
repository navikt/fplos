import React from 'react';
import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import ReservasjonerTabell from './ReservasjonerTabell';

interface TsProps {
  reservasjoner: Reservasjon[];
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
}
const ReservasjonerPanel = ({
  reservasjoner,
  opphevReservasjon,
  endreOppgaveReservasjon,
}: TsProps) => (
  <>
    <ReservasjonerTabell
      reservasjoner={reservasjoner}
      opphevReservasjon={opphevReservasjon}
      endreOppgaveReservasjon={endreOppgaveReservasjon}
    />
  </>
);

export default ReservasjonerPanel;
