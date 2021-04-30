import React, { useMemo, FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import Kodeverk from 'types/kodeverkTsType';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import KodeverkType from 'kodeverk/kodeverkTyper';
import { RadioGroupField, RadioOption } from 'form/FinalFields';
import useKodeverk from 'data/useKodeverk';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper: Kodeverk[], valgtFagsakYtelseType: string) => {
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
      <Undertekst>
        <FormattedMessage id="FagsakYtelseTypeVelger.Stonadstype" />
      </Undertekst>
      <VerticalSpacer eightPx />
      <RadioGroupField
        name="fagsakYtelseType"
        onChange={(fyt) => lagreSakslisteFagsakYtelseType(fyt !== ''
          ? { sakslisteId: valgtSakslisteId, avdelingEnhet: valgtAvdelingEnhet, fagsakYtelseType: fyt }
          : { sakslisteId: valgtSakslisteId, avdelingEnhet: valgtAvdelingEnhet })
          .then(() => {
            hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
            hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
          })}
      >
        <RadioOption
          value={FagsakYtelseType.FORELDREPRENGER}
          label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.FORELDREPRENGER)}
        />
        <RadioOption
          value={FagsakYtelseType.ENGANGSSTONAD}
          label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.ENGANGSSTONAD)}
        />
        <RadioOption
          value={FagsakYtelseType.SVANGERSKAPPENGER}
          label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, FagsakYtelseType.SVANGERSKAPPENGER)}
        />
        <RadioOption
          value=""
          label={<FormattedMessage id="FagsakYtelseTypeVelger.Alle" />}
        />
      </RadioGroupField>
    </>
  );
};

export default FagsakYtelseTypeVelger;
