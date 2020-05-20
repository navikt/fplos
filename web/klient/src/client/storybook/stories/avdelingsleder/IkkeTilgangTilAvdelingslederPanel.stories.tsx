import React from 'react';

import IkkeTilgangTilAvdelingslederPanel from 'avdelingsleder/components/IkkeTilgangTilAvdelingslederPanel';

import withIntl from '../../decorators/withIntl';

export default {
  title: 'avdelingsleder/IkkeTilgangTilAvdelingslederPanel',
  component: IkkeTilgangTilAvdelingslederPanel,
  decorators: [withIntl],
};

export const skalViseIkkeTilgangTilAvdelingsleder = () => (
  <IkkeTilgangTilAvdelingslederPanel />
);
