import React, { useMemo, FunctionComponent } from 'react';
import { FormattedMessage } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { RadioGroupField, RadioOption } from 'form/FinalFields';
import useKodeverk from 'data/useKodeverk';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper, valgtFagsakYtelseType) => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

interface OwnProps {
  valgtSakslisteId: number;
  lagreSakslisteFagsakYtelseType: (sakslisteId: number, fagsakYtelseType: string, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
}

/**
 * FagsakYtelseTypeVelger
 */
const FagsakYtelseTypeVelger: FunctionComponent<OwnProps> = ({
  valgtSakslisteId,
  lagreSakslisteFagsakYtelseType,
  valgtAvdelingEnhet,
}) => {
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
        onChange={(fyt) => lagreSakslisteFagsakYtelseType(valgtSakslisteId, fyt, valgtAvdelingEnhet)}
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
