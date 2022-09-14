import React, { FunctionComponent, useMemo } from 'react';
import { FormattedMessage } from 'react-intl';
import { Label } from '@navikt/ds-react';

import { VerticalSpacer } from '@navikt/ft-ui-komponenter';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import useKodeverk from 'data/useKodeverk';
import KodeverkType from 'kodeverk/kodeverkTyper';
import BehandlingType from 'kodeverk/behandlingType';
import { CheckboxField } from '@navikt/ft-form-hooks';

const behandlingstypeOrder = Object.values(BehandlingType);

interface OwnProps {
  valgtSakslisteId: number;
  valgtAvdelingEnhet: string;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void;
}

/**
 * BehandlingstypeVelger
 */
const BehandlingstypeVelger: FunctionComponent<OwnProps> = ({
  valgtSakslisteId,
  valgtAvdelingEnhet,
  hentAvdelingensSakslister,
  hentAntallOppgaver,
}) => {
  const { startRequest: lagreSakslisteBehandlingstype } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_BEHANDLINGSTYPE);
  const alleBehandlingTyper = useKodeverk(KodeverkType.BEHANDLING_TYPE);
  const behandlingTyper = useMemo(() => behandlingstypeOrder.map((kode) => alleBehandlingTyper.find((bt) => bt.kode === kode)),
    []);
  return (
    <>
      <Label size="small">
        <FormattedMessage id="BehandlingstypeVelger.Behandlingstype" />
      </Label>
      <VerticalSpacer eightPx />
      {behandlingTyper.map((bt) => {
        if (!bt) {
          return null;
        }
        return (
          <React.Fragment key={bt.kode}>
            <VerticalSpacer fourPx />
            <CheckboxField
              name={bt.kode}
              label={bt.navn}
              onChange={(isChecked) => lagreSakslisteBehandlingstype({
                sakslisteId: valgtSakslisteId,
                avdelingEnhet: valgtAvdelingEnhet,
                behandlingType: bt.kode,
                checked: isChecked,
              }).then(() => {
                hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
                hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
              })}
            />
          </React.Fragment>
        );
      }).filter((bt) => !!bt)}
    </>
  );
};

export default BehandlingstypeVelger;
