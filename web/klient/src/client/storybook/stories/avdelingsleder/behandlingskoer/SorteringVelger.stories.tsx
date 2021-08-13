import React from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import SorteringVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/SorteringVelger';
import behandlingType from 'kodeverk/behandlingType';
import koSortering from 'kodeverk/KoSortering';
import Kodeverk from 'types/kodeverkTsType';

import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withIntl from '../../../decorators/withIntl';
import withRestApiProvider from '../../../decorators/withRestApi';
import RestApiMock from '../../../utils/RestApiMock';

export default {
  title: 'avdelingsleder/behandlingskoer/SorteringVelger',
  component: SorteringVelger,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ valgteBehandlingtyper: Kodeverk[] }> = ({
  valgteBehandlingtyper,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
  ];

  const verdier = {
    sortering: koSortering.BEHANDLINGSFRIST,
    fra: 2,
    til: 3,
    fomDato: '2020.01.10',
    tomDato: '2020.10.01',
    erDynamiskPeriode: true,
  };

  return (
    <RestApiMock data={data}>
      <Form
        onSubmit={() => undefined}
        initialValues={verdier}
        render={() => (
          <SorteringVelger
            valgtSakslisteId={1}
            valgteBehandlingtyper={valgteBehandlingtyper}
            valgtAvdelingEnhet="NAV Viken"
            erDynamiskPeriode={verdier.erDynamiskPeriode}
            fra={verdier.fra}
            til={verdier.til}
            fomDato={verdier.fomDato}
            tomDato={verdier.tomDato}
            hentAvdelingensSakslister={action('button-click')}
            hentAntallOppgaver={action('button-click')}
          />
        )}
      />
    </RestApiMock>
  );
};

export const SorteringsvelgerNårMangeBehandlingstyperErValgt = Template.bind({});
SorteringsvelgerNårMangeBehandlingstyperErValgt.args = {
  valgteBehandlingtyper: [{
    kode: behandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegang',
  }, {
    kode: behandlingType.DOKUMENTINNSYN,
    navn: 'Innsyn',
  }],
};

export const SorteringsvelgerNårKunTilbakekrevingErValgt = Template.bind({});
SorteringsvelgerNårKunTilbakekrevingErValgt.args = {
  valgteBehandlingtyper: [{
    kode: behandlingType.TILBAKEBETALING,
    navn: 'Tilbakekreving',
  }],
};
