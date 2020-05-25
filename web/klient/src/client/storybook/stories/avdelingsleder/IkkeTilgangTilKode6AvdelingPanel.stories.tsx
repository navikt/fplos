import React from 'react';

import IkkeTilgangTilKode6AvdelingPanel from 'avdelingsleder/components/IkkeTilgangTilKode6AvdelingPanel';

import withIntl from '../../decorators/withIntl';

export default {
  title: 'avdelingsleder/IkkeTilgangTilKode6AvdelingPanel',
  component: IkkeTilgangTilKode6AvdelingPanel,
  decorators: [withIntl],
};

export const skalViseIkkeTilgangGrunnetKode6 = () => (
  <IkkeTilgangTilKode6AvdelingPanel />
);
