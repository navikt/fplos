import React, { FunctionComponent } from 'react';
import dayjs from 'dayjs';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { useForm } from 'react-hook-form';
import { Element } from 'nav-frontend-typografi';
import { Row, Column } from 'nav-frontend-grid';
import isSameOrAfter from 'dayjs/plugin/isSameOrAfter';
import isSameOrBefore from 'dayjs/plugin/isSameOrBefore';

import { VerticalSpacer } from '@navikt/ft-ui-komponenter';
import useKodeverk from 'data/useKodeverk';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import KodeverkType from 'kodeverk/kodeverkTyper';
import OppgaveForDato from 'types/avdelingsleder/oppgaverForDatoTsType';
import StoreValuesInLocalStorage from 'data/StoreValuesInLocalStorage';
import {
  Form, RadioGroupField, RadioOption, SelectField,
} from '@navikt/ft-form-hooks';
import { KodeverkMedNavn } from '@navikt/ft-types';
import TilBehandlingGraf, { OppgaveForDatoGraf } from './TilBehandlingGraf';

import styles from './tilBehandlingPanel.less';

dayjs.extend(isSameOrAfter);
dayjs.extend(isSameOrBefore);

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
  const toUkerSiden = dayjs().subtract(2, 'w');
  return dayjs(oppgaveForAvdeling.opprettetDato).isSameOrAfter(toUkerSiden);
};

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper: KodeverkMedNavn[], valgtFagsakYtelseType: string): string => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

const slaSammenLikeBehandlingstyperOgDatoer = (oppgaverForAvdeling: OppgaveForDato[]): OppgaveForDatoGraf[] => {
  const sammenslatte: OppgaveForDatoGraf[] = [];

  oppgaverForAvdeling.forEach((o) => {
    const index = sammenslatte.findIndex((s) => s.behandlingType === o.behandlingType && s.opprettetDato === o.opprettetDato);
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
  height: number;
  oppgaverPerDato: OppgaveForDato[];
  getValueFromLocalStorage: (key: string) => string | undefined;
}

type FormValues = {
  ukevalg: string;
  ytelseType: string;
}

const formName = 'tilBehandlingForm';
const formDefaultValues = { ytelseType: ALLE_YTELSETYPER_VALGT, ukevalg: UKE_2 };

/**
 * TilBehandlingPanel.
 */
export const TilBehandlingPanel: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  height,
  oppgaverPerDato,
  getValueFromLocalStorage,
}) => {
  const behandlingTyper = useKodeverk(KodeverkType.BEHANDLING_TYPE);
  const fagsakYtelseTyper = useKodeverk(KodeverkType.FAGSAK_YTELSE_TYPE);
  const stringFromStorage = getValueFromLocalStorage(formName);
  const lagredeVerdier = stringFromStorage ? JSON.parse(stringFromStorage) : undefined;

  const formMethods = useForm<FormValues>({
    defaultValues: lagredeVerdier || formDefaultValues,
  });

  const values = formMethods.watch();

  return (
    <Form<FormValues> formMethods={formMethods}>
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
                value={ALLE_YTELSETYPER_VALGT}
                label={<FormattedMessage id="FordelingAvBehandlingstypePanel.Alle" />}
              />
            </RadioGroupField>
          </div>
        </Column>
      </Row>
      <TilBehandlingGraf
        height={height}
        isToUkerValgt={values.ukevalg === UKE_2}
        behandlingTyper={behandlingTyper}
        oppgaverPerDato={oppgaverPerDato ? slaSammenLikeBehandlingstyperOgDatoer(oppgaverPerDato
          .filter((ofa) => (values.ytelseType === ALLE_YTELSETYPER_VALGT ? true : values.ytelseType === ofa.fagsakYtelseType))
          .filter((ofa) => erDatoInnenforPeriode(ofa, values.ukevalg))) : []}
      />
    </Form>
  );
};

export default injectIntl(TilBehandlingPanel);
