import React from 'react';
import { ReservasjonerTabell } from './ReservasjonerTabell';

interface TsProps {

}
const ReservasjonerPanel = ({
  reservasjoner,
 }: TsProps) => (
                             <>
                               <ReservasjonerTabell />
                             </>
);

export default ReservasjonerPanel;
