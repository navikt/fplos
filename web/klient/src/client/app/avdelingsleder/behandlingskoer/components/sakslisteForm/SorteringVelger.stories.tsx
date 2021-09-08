import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';
import { useForm } from 'react-hook-form';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import SorteringVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/SorteringVelger';
import behandlingType from 'kodeverk/behandlingType';
import koSortering from 'kodeverk/KoSortering';
import Kodeverk from 'types/kodeverkTsType';
import { Form } from 'form/formIndex';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

export default {
  title: 'avdelingsleder/behandlingskoer/SorteringVelger',
  component: SorteringVelger,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ valgteBehandlingtyper: Kodeverk[], erDynamiskPeriode: boolean }> = ({
  valgteBehandlingtyper,
  erDynamiskPeriode,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_INTERVALL.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE.name, data: undefined },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO.name, data: undefined },
  ];

  const verdier = {
    sortering: koSortering.BEHANDLINGSFRIST,
    fra: 2,
    til: 3,
    fomDato: '2020-01-10',
    tomDato: '2020-10-01',
    erDynamiskPeriode,
  };

  const formMethods = useForm({
    defaultValues: verdier,
  });

  return (
    <RestApiMock data={data}>
      <Form formMethods={formMethods}>
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
      </Form>
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
  erDynamiskPeriode: false,
};

export const SorteringsvelgerNårDynamiskPeriodeErValgt = Template.bind({});
SorteringsvelgerNårDynamiskPeriodeErValgt.args = {
  valgteBehandlingtyper: [{
    kode: behandlingType.FORSTEGANGSSOKNAD,
    navn: 'Førstegang',
  }, {
    kode: behandlingType.DOKUMENTINNSYN,
    navn: 'Innsyn',
  }],
  erDynamiskPeriode: true,
};

export const SorteringsvelgerNårKunTilbakekrevingErValgt = Template.bind({});
SorteringsvelgerNårKunTilbakekrevingErValgt.args = {
  valgteBehandlingtyper: [{
    kode: behandlingType.TILBAKEBETALING,
    navn: 'Tilbakekreving',
  }],
  erDynamiskPeriode: false,
};
