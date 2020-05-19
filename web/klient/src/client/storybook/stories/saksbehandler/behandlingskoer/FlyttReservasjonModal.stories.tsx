import React, { useState } from 'react';
import { action } from '@storybook/addon-actions';

import { FlyttReservasjonModal } from 'saksbehandler/behandlingskoer/components/menu/FlyttReservasjonModal';
import SaksbehandlerForFlytting from 'saksbehandler/behandlingskoer/components/menu/saksbehandlerForFlyttingTsType';

import withIntl from '../../../decorators/withIntl';

export default {
  title: 'saksbehandler/behandlingskoer/FlyttReservasjonModal',
  component: FlyttReservasjonModal,
  decorators: [withIntl],
};

export const skalViseModalForFlyttingAvReservasjon = (intl) => {
  const [erStartet, setStartet] = useState(false);
  const [erFerdig, setFerdig] = useState(false);
  const [saksbehandler, setSaksbehandler] = useState<SaksbehandlerForFlytting>();
  const finnSaksbehandler = () => {
    setStartet(true);
    setTimeout(() => {
      setSaksbehandler({
        brukerIdent: 'R232323',
        navn: 'Espen Utvikler',
        avdelingsnavn: ['NAV Viken'],
      });
      setStartet(false);
      setFerdig(true);
    }, 1000);
  };
  return (
    <FlyttReservasjonModal
      intl={intl}
      showModal
      oppgaveId={1}
      closeModal={action('button-click')}
      submit={action('button-click')}
      finnSaksbehandler={finnSaksbehandler}
      resetSaksbehandler={action('button-click')}
      saksbehandler={saksbehandler}
      erSaksbehandlerSokStartet={erStartet}
      erSaksbehandlerSokFerdig={erFerdig}
    />
  );
};
