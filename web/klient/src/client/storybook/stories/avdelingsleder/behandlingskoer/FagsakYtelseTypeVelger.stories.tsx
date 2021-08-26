import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';
import { useForm } from 'react-hook-form';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import FagsakYtelseTypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/FagsakYtelseTypeVelger';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import RestApiMock from '../../../utils/RestApiMock';
import Form from '../../../../app/formNew/Form';

export default {
  title: 'avdelingsleder/behandlingskoer/FagsakYtelseTypeVelger',
  component: FagsakYtelseTypeVelger,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ verdier: Record<string, string> }> = ({
  verdier,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE.name, data: undefined },
  ];

  const formMethods = useForm({
    defaultValues: verdier,
  });

  return (
    <RestApiMock data={data}>
      <Form formMethods={formMethods}>
        <FagsakYtelseTypeVelger
          valgtSakslisteId={1}
          valgtAvdelingEnhet="NAV Viken"
          hentAvdelingensSakslister={action('button-click')}
          hentAntallOppgaver={action('button-click')}
        />
      </Form>
    </RestApiMock>
  );
};

export const VelgFagsakYtelseTyper = Template.bind({});
VelgFagsakYtelseTyper.args = {
  verdier: {
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
  },
};
