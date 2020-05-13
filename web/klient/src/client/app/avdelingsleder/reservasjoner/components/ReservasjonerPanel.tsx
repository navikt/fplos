import React, { FunctionComponent } from 'react';
import Reservasjon from 'avdelingsleder/reservasjoner/reservasjonTsType';
import ReservasjonerTabell from './ReservasjonerTabell';

// TODO Slett denne komponenten. Gjer ingenting

interface OwnProps {
  reservasjoner: Reservasjon[];
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
}
const ReservasjonerPanel: FunctionComponent<OwnProps> = ({
  reservasjoner,
  opphevReservasjon,
  endreOppgaveReservasjon,
  flyttReservasjon,
}) => (
  <ReservasjonerTabell
    reservasjoner={reservasjoner}
    opphevReservasjon={opphevReservasjon}
    endreOppgaveReservasjon={endreOppgaveReservasjon}
    flyttReservasjon={flyttReservasjon}
  />
);

export default ReservasjonerPanel;
