import React from 'react';
import { action } from '@storybook/addon-actions';
import { Story } from '@storybook/react';
import { useForm } from 'react-hook-form';

import { RestApiGlobalStatePathsKeys, RestApiPathsKeys } from 'data/fplosRestApi';
import AndreKriterierVelger from 'avdelingsleder/behandlingskoer/components/sakslisteForm/AndreKriterierVelger';
import andreKriterierType from 'kodeverk/andreKriterierType';
import { Form } from 'form/formIndex';

import RestApiMock from 'storybookUtils/RestApiMock';
import withIntl from 'storybookUtils/decorators/withIntl';
import withRestApiProvider from 'storybookUtils/decorators/withRestApi';
import alleKodeverk from 'storybookUtils/mocks/alleKodeverk.json';

export default {
  title: 'avdelingsleder/behandlingskoer/AndreKriterierVelger',
  component: AndreKriterierVelger,
  decorators: [withIntl, withRestApiProvider],
};

const Template: Story<{ defaultVerdier: Record<string, boolean> }> = ({
  defaultVerdier,
}) => {
  const data = [
    { key: RestApiGlobalStatePathsKeys.KODEVERK.name, data: alleKodeverk },
    { key: RestApiPathsKeys.LAGRE_SAKSLISTE_ANDRE_KRITERIER.name, data: {} },
  ];

  const formMethods = useForm({
    defaultValues: defaultVerdier,
  });

  const verdier = formMethods.watch();

  return (
    <RestApiMock data={data}>
      <Form formMethods={formMethods}>
        <AndreKriterierVelger
          valgtSakslisteId={1}
          valgtAvdelingEnhet="NAV Viken"
          values={verdier}
          hentAvdelingensSakslister={action('button-click')}
          hentAntallOppgaver={action('button-click')}
        />
      </Form>
    </RestApiMock>
  );
};

export const Default = Template.bind({});
Default.args = {
  defaultVerdier: {
    [andreKriterierType.TIL_BESLUTTER]: true,
    [`${andreKriterierType.TIL_BESLUTTER}_inkluder`]: true,
  },
};
