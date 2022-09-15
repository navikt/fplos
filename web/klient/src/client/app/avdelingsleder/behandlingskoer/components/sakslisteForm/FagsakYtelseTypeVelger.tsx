import React, { useMemo, FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Element } from 'nav-frontend-typografi';

import { KodeverkMedNavn } from '@navikt/ft-types';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import { VerticalSpacer } from '@navikt/ft-ui-komponenter';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import KodeverkType from 'kodeverk/kodeverkTyper';
import useKodeverk from 'data/useKodeverk';
import { CheckboxField } from '@navikt/ft-form-hooks';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper: KodeverkMedNavn[], valgtFagsakYtelseType: string) => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

interface OwnProps {
  valgtSakslisteId: number;
  valgtAvdelingEnhet: string;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void;
}

/**
 * FagsakYtelseTypeVelger
 */
const FagsakYtelseTypeVelger: FunctionComponent<OwnProps> = ({
  valgtSakslisteId,
  valgtAvdelingEnhet,
  hentAvdelingensSakslister,
  hentAntallOppgaver,
}) => {
  const { startRequest: lagreSakslisteFagsakYtelseType } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_FAGSAK_YTELSE_TYPE);
  const alleFagsakYtelseTyper = useKodeverk(KodeverkType.FAGSAK_YTELSE_TYPE);
  const fagsakYtelseTyper = useMemo(() => alleFagsakYtelseTyper.filter((fyt) => fyt.kode !== FagsakYtelseType.ENDRING_FORELDREPENGER),
    []);
  return (
    <>
      <Element>
        <FormattedMessage id="FagsakYtelseTypeVelger.Stonadstype" />
      </Element>
      <VerticalSpacer eightPx />
      {fagsakYtelseTyper.map((fyt) => (
        <React.Fragment key={fyt.kode}>
          <VerticalSpacer fourPx />
          <CheckboxField
            name={fyt.kode}
            label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, fyt.kode)}
            onChange={(isChecked) => lagreSakslisteFagsakYtelseType({
              sakslisteId: valgtSakslisteId,
              avdelingEnhet: valgtAvdelingEnhet,
              fagsakYtelseType: fyt.kode,
              checked: isChecked,
            }).then(() => {
              hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
              hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
            })}
          />
        </React.Fragment>
      ))}
      <VerticalSpacer eightPx />
    </>
  );
};

export default FagsakYtelseTypeVelger;
