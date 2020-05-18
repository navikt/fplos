import React, { FunctionComponent } from 'react';
import { connect } from 'react-redux';
import { FormattedMessage } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { getKodeverk } from 'kodeverk/duck';
import Kodeverk from 'kodeverk/kodeverkTsType';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import { RadioGroupField, RadioOption } from 'form/FinalFields';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper, valgtFagsakYtelseType) => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

interface OwnProps {
  fagsakYtelseTyper: Kodeverk[];
  valgtSakslisteId: number;
  lagreSakslisteFagsakYtelseType: (sakslisteId: number, fagsakYtelseType: string, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
}

/**
 * FagsakYtelseTypeVelger
 */
export const FagsakYtelseTypeVelger: FunctionComponent<OwnProps> = ({
  fagsakYtelseTyper,
  valgtSakslisteId,
  lagreSakslisteFagsakYtelseType,
  valgtAvdelingEnhet,
}) => (
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

const mapStateToProps = (state) => ({
  fagsakYtelseTyper: getKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE)(state).filter((k) => k.kode !== fagsakYtelseType.ENDRING_FORELDREPENGER),
});

export default connect(mapStateToProps)(FagsakYtelseTypeVelger);
