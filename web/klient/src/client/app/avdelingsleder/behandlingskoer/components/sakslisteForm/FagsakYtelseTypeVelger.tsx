import React, { useMemo, FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';

import { KodeverkMedNavn } from '@navikt/ft-types';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import { VerticalSpacer } from '@navikt/ft-ui-komponenter';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import KodeverkType from 'kodeverk/kodeverkTyper';
import useKodeverk from 'data/useKodeverk';
import { RadioGroupPanel } from '@navikt/ft-form-hooks';

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
  const fagsakYtelseTyper = useMemo(() => alleFagsakYtelseTyper.filter((k) => k.kode !== FagsakYtelseType.ENDRING_FORELDREPENGER),
    []);
  return (
    <>
      <VerticalSpacer eightPx />
      <RadioGroupPanel
        name="fagsakYtelseType"
        label={<FormattedMessage id="FagsakYtelseTypeVelger.Stonadstype" />}
        isHorizontal
        onChange={(fyt) => lagreSakslisteFagsakYtelseType(fyt !== ''
          ? { sakslisteId: valgtSakslisteId, avdelingEnhet: valgtAvdelingEnhet, fagsakYtelseType: fyt }
          : { sakslisteId: valgtSakslisteId, avdelingEnhet: valgtAvdelingEnhet })
          .then(() => {
            hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
            hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
          })}
        radios={[{
          value: FagsakYtelseType.FORELDREPRENGER,
          label: finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.FORELDREPRENGER),
        }, {
          value: FagsakYtelseType.ENGANGSSTONAD,
          label: finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.ENGANGSSTONAD),
        }, {
          value: FagsakYtelseType.SVANGERSKAPPENGER,
          label: finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.SVANGERSKAPPENGER),
        }, {
          value: '',
          label: <FormattedMessage id="FagsakYtelseTypeVelger.Alle" />,
        }]}
      />
    </>
  );
};

export default FagsakYtelseTypeVelger;
