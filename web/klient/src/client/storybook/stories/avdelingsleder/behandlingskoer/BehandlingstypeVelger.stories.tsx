import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';
import { useForm } from 'react-hook-form';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import BehandlingstypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/BehandlingstypeVelger';
import behandlingType from 'kodeverk/behandlingType';

import Form from '../../../../app/formNew/Form';
import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import RestApiMock from '../../../utils/RestApiMock';

export default {
  title: 'avdelingsleder/behandlingskoer/BehandlingstypeVelger',
  component: BehandlingstypeVelger,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ verdier: Record<string, boolean> }> = ({
  verdier,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE.name, data: undefined },
  ];

  const formMethods = useForm({
    defaultValues: verdier,
  });

  return (
    <RestApiMock data={data}>
      <Form formMethods={formMethods}>
        <BehandlingstypeVelger
          valgtSakslisteId={1}
          valgtAvdelingEnhet="NAV Viken"
          hentAvdelingensSakslister={action('button-click')}
          hentAntallOppgaver={action('button-click')}
        />
      </Form>
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  verdier: {
    [behandlingType.FORSTEGANGSSOKNAD]: true,
  },
};
