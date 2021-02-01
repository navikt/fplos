import React, { FunctionComponent, useMemo } from 'react';
import { FormattedMessage } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import useKodeverk from 'data/useKodeverk';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import behandlingType from 'kodeverk/behandlingType';
import { CheckboxField } from 'form/FinalFields';
import Kodeverk from 'kodeverk/kodeverkTsType';

const behandlingstypeOrder = Object.values(behandlingType);

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
  const alleBehandlingTyper = useKodeverk(kodeverkTyper.BEHANDLING_TYPE);
  const behandlingTyper = useMemo(() => behandlingstypeOrder.map((kode) => alleBehandlingTyper.find((bt) => bt.kode === kode)),
    []);
  return (
    <>
      <Undertekst>
        <FormattedMessage id="BehandlingstypeVelger.Behandlingstype" />
      </Undertekst>
      <VerticalSpacer eightPx />
      {behandlingTyper.map((bt) => (
        <React.Fragment key={bt.kode}>
          <VerticalSpacer fourPx />
          <CheckboxField
            name={bt.kode}
            label={bt.navn}
            onChange={(isChecked) => lagreSakslisteBehandlingstype({
              sakslisteId: valgtSakslisteId,
              avdelingEnhet: valgtAvdelingEnhet,
              behandlingType: bt,
              checked: isChecked,
            }).then(() => {
              hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
              hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
            })}
          />
        </React.Fragment>
      ))}
    </>
  );
};

export default BehandlingstypeVelger;
