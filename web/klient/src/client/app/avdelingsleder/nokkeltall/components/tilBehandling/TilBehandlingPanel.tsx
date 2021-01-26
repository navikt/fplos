import React, { FunctionComponent } from 'react';
import moment from 'moment';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { Form } from 'react-final-form';
import { Element } from 'nav-frontend-typografi';
import { Row, Column } from 'nav-frontend-grid';

import StoreValuesInLocalStorage from 'form/StoreValuesInLocalStorage';
import { RadioGroupField, RadioOption, SelectField } from 'form/FinalFields';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import useKodeverk from 'data/useKodeverk';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import Kodeverk from 'kodeverk/kodeverkTsType';
import TilBehandlingGraf, { OppgaveForDatoGraf } from './TilBehandlingGraf';
import OppgaveForDato from './oppgaverForDatoTsType';

import styles from './tilBehandlingPanel.less';

export const ALLE_YTELSETYPER_VALGT = 'ALLE';
export const UKE_2 = '2';

const uker = [{
  kode: UKE_2,
  tekstKode: 'TilBehandlingPanel.ToSisteUker',
}, {
  kode: '4',
  tekstKode: 'TilBehandlingPanel.FireSisteUker',
}];

const erDatoInnenforPeriode = (oppgaveForAvdeling: OppgaveForDato, ukevalg: string): boolean => {
  if (ukevalg === uker[1].kode) {
    return true;
  }
  const toUkerSiden = moment().subtract(2, 'w');
  return moment(oppgaveForAvdeling.opprettetDato).isSameOrAfter(toUkerSiden);
};

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper: Kodeverk[], valgtFagsakYtelseType: string): string => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

const slaSammenLikeBehandlingstyperOgDatoer = (oppgaverForAvdeling: OppgaveForDato[]): OppgaveForDatoGraf[] => {
  const sammenslatte = [];

  oppgaverForAvdeling.forEach((o) => {
    const index = sammenslatte.findIndex((s) => s.behandlingType.kode === o.behandlingType.kode && s.opprettetDato === o.opprettetDato);
    if (index === -1) {
      sammenslatte.push(o);
    } else {
      sammenslatte[index] = {
        behandlingType: sammenslatte[index].behandlingType,
        opprettetDato: sammenslatte[index].opprettetDato,
        antall: sammenslatte[index].antall + o.antall,
      };
    }
  });

  return sammenslatte;
};

interface OwnProps {
  width: number;
  height: number;
  oppgaverPerDato: OppgaveForDato[];
  getValueFromLocalStorage: (key: string) => string;
}

const formName = 'tilBehandlingForm';
const formDefaultValues = { ytelseType: ALLE_YTELSETYPER_VALGT, ukevalg: UKE_2 };

/**
 * TilBehandlingPanel.
 */
export const TilBehandlingPanel: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  width,
  height,
  oppgaverPerDato,
  getValueFromLocalStorage,
}) => {
  const behandlingTyper = useKodeverk(kodeverkTyper.BEHANDLING_TYPE);
  const fagsakYtelseTyper = useKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE);
  const stringFromStorage = getValueFromLocalStorage(formName);
  const lagredeVerdier = stringFromStorage ? JSON.parse(stringFromStorage) : undefined;
  return (
    <Form
      onSubmit={() => undefined}
      initialValues={lagredeVerdier || formDefaultValues}
      render={({ values }) => (
        <>
          <StoreValuesInLocalStorage stateKey={formName} values={values} />
          <Element>
            <FormattedMessage id="TilBehandlingPanel.TilBehandling" />
          </Element>
          <VerticalSpacer eightPx />
          <Row>
            <Column xs="2">
              <SelectField
                name="ukevalg"
                label=""
                selectValues={uker.map((u) => <option key={u.kode} value={u.kode}>{intl.formatMessage({ id: u.tekstKode })}</option>)}
                bredde="l"
              />
            </Column>
            <Column xs="8">
              <div className={styles.radioPadding}>
                <RadioGroupField name="ytelseType">
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
                    value={ALLE_YTELSETYPER_VALGT}
                    label={<FormattedMessage id="FordelingAvBehandlingstypePanel.Alle" />}
                  />
                </RadioGroupField>
              </div>
            </Column>
          </Row>
          <TilBehandlingGraf
            width={width}
            height={height}
            isToUkerValgt={values.ukevalg === UKE_2}
            behandlingTyper={behandlingTyper}
            oppgaverPerDato={oppgaverPerDato ? slaSammenLikeBehandlingstyperOgDatoer(oppgaverPerDato
              .filter((ofa) => (values.ytelseType === ALLE_YTELSETYPER_VALGT ? true : values.ytelseType === ofa.fagsakYtelseType.kode))
              .filter((ofa) => erDatoInnenforPeriode(ofa, values.ukevalg))) : []}
          />
        </>
      )}
    />
  );
};

export default injectIntl(TilBehandlingPanel);
