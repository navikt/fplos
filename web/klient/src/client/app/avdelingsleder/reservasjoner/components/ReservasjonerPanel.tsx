import React from 'react';
import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import ReservasjonerTabell from './ReservasjonerTabell';

interface TsProps {
  reservasjoner: Reservasjon[];
  opphevReservasjon: (oppgaveId: number) => Promise<string>;
}
const ReservasjonerPanel = ({
  reservasjoner,
  opphevReservasjon,
}: TsProps) => (
  <>
    <ReservasjonerTabell reservasjoner={reservasjoner} opphevReservasjon={opphevReservasjon} />
  </>
);

export default ReservasjonerPanel;
