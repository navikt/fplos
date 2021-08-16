import React from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import FagsakYtelseTypeVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/FagsakYtelseTypeVelger';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import RestApiMock from '../../../utils/RestApiMock';

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
  ];

  return (
    <RestApiMock data={data}>
      <Form
        onSubmit={() => undefined}
        initialValues={verdier}
        render={() => (
          <FagsakYtelseTypeVelger
            valgtSakslisteId={1}
            valgtAvdelingEnhet="NAV Viken"
            hentAvdelingensSakslister={action('button-click')}
            hentAntallOppgaver={action('button-click')}
          />
        )}
      />
    </RestApiMock>
  );
};

export const VelgFagsakYtelseTyper = Template.bind({});
VelgFagsakYtelseTyper.args = {
  verdier: {
    fagsakYtelseType: fagsakYtelseType.FORELDREPRENGER,
  },
};
