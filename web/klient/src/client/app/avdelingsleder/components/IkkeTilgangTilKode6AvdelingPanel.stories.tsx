import React from 'react';

import IkkeTilgangTilKode6AvdelingPanel from 'avdelingsleder/components/IkkeTilgangTilKode6AvdelingPanel';

import withIntl from 'storybookUtils/decorators/withIntl';

export default {
  title: 'avdelingsleder/IkkeTilgangTilKode6AvdelingPanel',
  component: IkkeTilgangTilKode6AvdelingPanel,
  decorators: [withIntl],
};

export const IkkeTilgangGrunnetKode6 = () => (
  <IkkeTilgangTilKode6AvdelingPanel />
);
