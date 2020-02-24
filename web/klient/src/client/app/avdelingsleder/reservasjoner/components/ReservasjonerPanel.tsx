import React from 'react';
import { Reservasjon } from 'avdelingsleder/reservasjoner/reservasjonTsType';
import { ReservasjonerTabell } from './ReservasjonerTabell';

interface TsProps {
  reservasjoner: Reservasjon[];
}
const ReservasjonerPanel = ({
  reservasjoner,
 }: TsProps) => (
   <>
     <ReservasjonerTabell reservasjoner={reservasjoner} />
   </>
);

export default ReservasjonerPanel;
