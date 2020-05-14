import React, { FunctionComponent } from 'react';
import ReservasjonerTabell from './ReservasjonerTabell';

// TODO Slett denne komponenten. Gjer ingenting

interface OwnProps {
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
  endreOppgaveReservasjon: (oppgaveId: number, reserverTil: string) => Promise<string>;
  flyttReservasjon: (oppgaveId: number, brukerident: string, begrunnelse: string) => Promise<string>;
}
const ReservasjonerPanel: FunctionComponent<OwnProps> = ({
  opphevReservasjon,
  endreOppgaveReservasjon,
  flyttReservasjon,
}) => (
  <ReservasjonerTabell
    opphevReservasjon={opphevReservasjon}
    endreOppgaveReservasjon={endreOppgaveReservasjon}
    flyttReservasjon={flyttReservasjon}
  />
);

export default ReservasjonerPanel;
