import React, { FunctionComponent, useEffect, useCallback } from 'react';
import {
  injectIntl, WrappedComponentProps, FormattedMessage, IntlShape,
} from 'react-intl';
import { useForm } from 'react-hook-form';
import Panel from 'nav-frontend-paneler';
import { Undertittel, Element, Normaltekst } from 'nav-frontend-typografi';

import { Row, Column } from 'nav-frontend-grid';
import {
  required, minLength, maxLength, hasValidName,
} from 'utils/validation/validators';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import { Form, InputField } from 'form/formIndex';
import Saksliste from 'types/avdelingsleder/sakslisteAvdelingTsType';
import BehandlingstypeVelger from './BehandlingstypeVelger';
import AndreKriterierVelger from './AndreKriterierVelger';
import FagsakYtelseTypeVelger from './FagsakYtelseTypeVelger';
import SorteringVelger from './SorteringVelger';

import styles from './utvalgskriterierForSakslisteForm.less';

const minLength3 = minLength(3);
const maxLength100 = maxLength(100);

const finnDagerSomTall = (antallDager: string): undefined | number => {
  const nr = Number.parseInt(antallDager, 10);
  return Number.isNaN(nr) ? undefined : nr;
};

const buildInitialValues = (intl: IntlShape, valgtSaksliste: Saksliste): InitialValues => {
  const behandlingTypes = valgtSaksliste.behandlingTyper ? valgtSaksliste.behandlingTyper.reduce((acc, bt) => ({ ...acc, [bt.kode]: true }), {}) : {};
  const fagsakYtelseType = valgtSaksliste.fagsakYtelseTyper && valgtSaksliste.fagsakYtelseTyper.length > 0
    ? valgtSaksliste.fagsakYtelseTyper[0].kode : '';

  const andreKriterierTyper = valgtSaksliste.andreKriterier
    ? valgtSaksliste.andreKriterier.reduce((acc, ak) => ({ ...acc, [ak.andreKriterierType.kode]: true }), {}) : {};
  const andreKriterierInkluder = valgtSaksliste.andreKriterier
    ? valgtSaksliste.andreKriterier.reduce((acc, ak) => ({ ...acc, [`${ak.andreKriterierType.kode}_inkluder`]: ak.inkluder }), {}) : {};

  return {
    sakslisteId: valgtSaksliste.sakslisteId,
    navn: valgtSaksliste.navn ? valgtSaksliste.navn : intl.formatMessage({ id: 'UtvalgskriterierForSakslisteForm.NyListe' }),
    sortering: valgtSaksliste.sortering ? valgtSaksliste.sortering.sorteringType.kode : undefined,
    fomDato: valgtSaksliste.sortering ? valgtSaksliste.sortering.fomDato : undefined,
    tomDato: valgtSaksliste.sortering ? valgtSaksliste.sortering.tomDato : undefined,
    fra: valgtSaksliste.sortering ? valgtSaksliste.sortering.fra : undefined,
    til: valgtSaksliste.sortering ? valgtSaksliste.sortering.til : undefined,
    erDynamiskPeriode: valgtSaksliste.sortering ? valgtSaksliste.sortering.erDynamiskPeriode : undefined,
    fagsakYtelseType,
    ...andreKriterierTyper,
    ...andreKriterierInkluder,
    ...behandlingTypes,
  };
};

interface OwnProps {
  valgtSaksliste: Saksliste;
  valgtAvdelingEnhet: string;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
  hentOppgaverForAvdelingAntall: (params: {avdelingEnhet: string}) => void;
}

interface InitialValues {
  sakslisteId: number;
  navn: string;
  sortering?: string;
  fomDato?: string;
  tomDato?: string;
  fra?: number;
  til?: number;
  erDynamiskPeriode?: boolean;
  fagsakYtelseType: string;
}

type FormValues = {
  sakslisteId: number
  navn: string;
  fagsakYtelseType: string;
  sortering: string;
  erDynamiskPeriode: boolean;
  fra: string;
  til: string;
  fomDato: string;
  tomDato: string;
}

/**
 * UtvalgskriterierForSakslisteForm
 */
export const UtvalgskriterierForSakslisteForm: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtSaksliste,
  valgtAvdelingEnhet,
  hentAvdelingensSakslister,
  hentOppgaverForAvdelingAntall,
}) => {
  const { data: antallOppgaver, startRequest: hentAntallOppgaverForSaksliste } = restApiHooks
    .useRestApiRunner(RestApiPathsKeys.OPPGAVE_ANTALL);
  useEffect(() => {
    hentAntallOppgaverForSaksliste({ sakslisteId: valgtSaksliste.sakslisteId, avdelingEnhet: valgtAvdelingEnhet });
  }, [valgtSaksliste.sakslisteId]);

  const hentAntallOppgaver = useCallback((sakslisteId, avdelingEnhet) => {
    hentAntallOppgaverForSaksliste({ sakslisteId, avdelingEnhet });
    hentOppgaverForAvdelingAntall({ avdelingEnhet });
  }, []);

  const { startRequest: lagreSakslisteNavn } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN);

  const tranformValues = useCallback((values: FormValues): void => {
    lagreSakslisteNavn({ sakslisteId: values.sakslisteId, navn: values.navn, avdelingEnhet: valgtAvdelingEnhet })
      .then(() => hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet }));
  }, [valgtAvdelingEnhet]);

  const formMethods = useForm<FormValues>({
    defaultValues: buildInitialValues(intl, valgtSaksliste),
  });

  const values = formMethods.watch();

  return (
    <Form<FormValues> formMethods={formMethods}>
      <Panel className={styles.panel}>
        <Element>
          <FormattedMessage id="UtvalgskriterierForSakslisteForm.Utvalgskriterier" />
        </Element>
        <VerticalSpacer eightPx />
        <Row>
          <Column xs="9">
            <InputField
              name="navn"
              label={intl.formatMessage({ id: 'UtvalgskriterierForSakslisteForm.Navn' })}
              validate={[required(intl), minLength3(intl), maxLength100(intl), hasValidName(intl)]}
              bredde="L"
              onBlur={tranformValues}
              shouldValidateOnBlur
            />
          </Column>
          <Column xs="3">
            <div className={styles.grayBox}>
              <Normaltekst><FormattedMessage id="UtvalgskriterierForSakslisteForm.AntallSaker" /></Normaltekst>
              <Undertittel>{antallOppgaver ? `${antallOppgaver}` : '0'}</Undertittel>
            </div>
          </Column>
        </Row>
        <Row>
          <Column xs="6" className={styles.stonadstypeRadios}>
            <FagsakYtelseTypeVelger
              valgtSakslisteId={valgtSaksliste.sakslisteId}
              valgtAvdelingEnhet={valgtAvdelingEnhet}
              hentAvdelingensSakslister={hentAvdelingensSakslister}
              hentAntallOppgaver={hentAntallOppgaver}
            />
          </Column>
        </Row>
        <Row>
          <Column xs="3">
            <BehandlingstypeVelger
              valgtSakslisteId={valgtSaksliste.sakslisteId}
              valgtAvdelingEnhet={valgtAvdelingEnhet}
              hentAvdelingensSakslister={hentAvdelingensSakslister}
              hentAntallOppgaver={hentAntallOppgaver}
            />
          </Column>
          <Column xs="4">
            <AndreKriterierVelger
              valgtSakslisteId={valgtSaksliste.sakslisteId}
              valgtAvdelingEnhet={valgtAvdelingEnhet}
              values={values}
              hentAvdelingensSakslister={hentAvdelingensSakslister}
              hentAntallOppgaver={hentAntallOppgaver}
            />
          </Column>
          <Column xs="4">
            <SorteringVelger
              valgtSakslisteId={valgtSaksliste.sakslisteId}
              valgteBehandlingtyper={valgtSaksliste.behandlingTyper}
              valgtAvdelingEnhet={valgtAvdelingEnhet}
              erDynamiskPeriode={values.erDynamiskPeriode}
              fra={finnDagerSomTall(values.fra)}
              til={finnDagerSomTall(values.til)}
              fomDato={values.fomDato}
              tomDato={values.tomDato}
              hentAvdelingensSakslister={hentAvdelingensSakslister}
              hentAntallOppgaver={hentAntallOppgaver}
            />
          </Column>
        </Row>
      </Panel>
    </Form>
  );
};

export default injectIntl(UtvalgskriterierForSakslisteForm);
