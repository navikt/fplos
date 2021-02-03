import React, { useMemo, FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { RadioGroupField, RadioOption } from 'form/FinalFields';
import useKodeverk from 'data/useKodeverk';
import Kodeverk from 'kodeverk/kodeverkTsType';

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
  const alleFagsakYtelseTyper = useKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE);
  const fagsakYtelseTyper = useMemo(() => alleFagsakYtelseTyper.filter((k) => k.kode !== fagsakYtelseType.ENDRING_FORELDREPENGER),
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
          value={fagsakYtelseType.FORELDREPRENGER}
          label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, fagsakYtelseType.FORELDREPRENGER)}
        />
        <RadioOption
          value={fagsakYtelseType.ENGANGSSTONAD}
          label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, fagsakYtelseType.ENGANGSSTONAD)}
        />
        <RadioOption
          value={fagsakYtelseType.SVANGERSKAPPENGER}
          label={finnFagsakYtelseTypeNavn(fagsakYtelseTyper, fagsakYtelseType.SVANGERSKAPPENGER)}
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
