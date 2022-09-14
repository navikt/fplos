import React, { FunctionComponent } from 'react';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import { useForm } from 'react-hook-form';
import dayjs from 'dayjs';
import { Label } from '@navikt/ds-react';
import { Row, Column } from 'nav-frontend-grid';

import StoreValuesInLocalStorage from 'data/StoreValuesInLocalStorage';
import { Form, RadioGroupPanel, SelectField } from '@navikt/ft-form-hooks';
import useKodeverk from 'data/useKodeverk';
import { VerticalSpacer } from '@navikt/ft-ui-komponenter';
import FagsakYtelseType from 'kodeverk/fagsakYtelseType';
import KodeverkType from 'kodeverk/kodeverkTyper';
import { KodeverkMedNavn } from '@navikt/ft-types';
import OppgaverManueltPaVent from 'types/avdelingsleder/oppgaverManueltPaVentTsType';
import ManueltPaVentGraf from './ManueltPaVentGraf';

const finnFagsakYtelseTypeNavn = (fagsakYtelseTyper: KodeverkMedNavn[], valgtFagsakYtelseType: string): string => {
  const type = fagsakYtelseTyper.find((fyt) => fyt.kode === valgtFagsakYtelseType);
  return type ? type.navn : '';
};

export const ALLE_YTELSETYPER_VALGT = 'ALLE';
export const UKE_4 = '4';

const uker = [{
  kode: UKE_4,
  tekstKode: 'ManueltPaVentPanel.FireSisteUker',
}, {
  kode: '8',
  tekstKode: 'ManueltPaVentPanel.AtteSisteUker',
}];

const erDatoInnenforPeriode = (behandlingFrist: string, ukevalg: string): boolean => {
  if (ukevalg === uker[1].kode) {
    return true;
  }

  const dataOmFireUker = dayjs().add(4, 'w');
  return dayjs(behandlingFrist).isSameOrBefore(dataOmFireUker);
};

interface OwnProps {
  intl: any;
  height: number;
  oppgaverManueltPaVent: OppgaverManueltPaVent[];
  getValueFromLocalStorage: (key: string) => string | undefined;
}

const formName = 'manueltPaVentForm';
const formDefaultValues = { valgtYtelsetype: ALLE_YTELSETYPER_VALGT, ukevalg: UKE_4 };

type FormValues = {
  ukevalg: string;
  valgtYtelsetype: string;
}

/**
 * ManueltPaVentPanel.
 */
const ManueltPaVentPanel: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  height,
  oppgaverManueltPaVent,
  getValueFromLocalStorage,
}) => {
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
      <Label size="small">
        <FormattedMessage id="ManueltPaVentPanel.SattPaVent" />
      </Label>
      <VerticalSpacer sixteenPx />
      <Row>
        <Column xs="2">
          <SelectField
            name="ukevalg"
            label=""
            selectValues={uker.map((u) => <option key={u.kode} value={u.kode}>{intl.formatMessage({ id: u.tekstKode })}</option>)}
          />
        </Column>
        <Column xs="8">
          <RadioGroupPanel
            name="valgtYtelsetype"
            isHorizontal
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
              value: ALLE_YTELSETYPER_VALGT,
              label: <FormattedMessage id="ManueltPaVentPanel.Alle" />,
            }]}
          />
        </Column>
      </Row>
      <VerticalSpacer sixteenPx />
      <ManueltPaVentGraf
        height={height}
        isFireUkerValgt={values.ukevalg === UKE_4}
        oppgaverManueltPaVent={oppgaverManueltPaVent && oppgaverManueltPaVent
          .filter((ompv) => (values.valgtYtelsetype === ALLE_YTELSETYPER_VALGT ? true : values.valgtYtelsetype === ompv.fagsakYtelseType))
          .filter((ompv) => erDatoInnenforPeriode(ompv.behandlingFrist, values.ukevalg))}
      />
    </Form>
  );
};

export default injectIntl(ManueltPaVentPanel);
