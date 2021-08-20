import React from 'react';
import { Form } from 'react-final-form';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import AndreKriterierVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/AndreKriterierVelger';
import andreKriterierType from 'kodeverk/andreKriterierType';

import withIntl from '../../../decorators/withIntl';
import alleKodeverk from '../../../mocks/alleKodeverk.json';
import withRestApiProvider from '../../../decorators/withRestApi';
import RestApiMock from '../../../utils/RestApiMock';

export default {
  title: 'avdelingsleder/behandlingskoer/AndreKriterierVelger',
  component: AndreKriterierVelger,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ verdier: Record<string, boolean> }> = ({
  verdier,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER.name, data: {} },
  ];

  return (
    <RestApiMock data={data}>
      <Form
        onSubmit={() => undefined}
        initialValues={verdier}
        render={({ values }) => (
          <AndreKriterierVelger
            valgtSakslisteId={1}
            valgtAvdelingEnhet="NAV Viken"
            values={values}
            hentAvdelingensSakslister={action('button-click')}
            hentAntallOppgaver={action('button-click')}
          />
        )}
      />
    </RestApiMock>
  );
};

export const VelgAndreKriterier = Template.bind({});
VelgAndreKriterier.args = {
  verdier: {
    [andreKriterierType.TIL_BESLUTTER]: true,
    [`${andreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
  },
};
