import React, { FunctionComponent } from 'react';
import { Form } from 'react-final-form';
import {
  injectIntl, WrappedComponentProps, FormattedMessage, IntlShape,
} from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Undertittel, Element, Normaltekst } from 'nav-frontend-typografi';

import { Row, Column } from 'nav-frontend-grid';
import {
  required, minLength, maxLength, hasValidName,
} from 'utils/validation/validators';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { InputField } from 'form/FinalFields';
import { RestApiPathsKeys } from 'data/restApiPaths';
import useRestApi from 'data/rest-api-hooks/useRestApi';
import useRestApiRunner from 'data/rest-api-hooks/useRestApiRunner';
import Saksliste from '../../sakslisteTsType';
import AutoLagringVedBlur from './AutoLagringVedBlur';
import BehandlingstypeVelger from './BehandlingstypeVelger';
import AndreKriterierVelger from './AndreKriterierVelger';
import FagsakYtelseTypeVelger from './FagsakYtelseTypeVelger';
import SorteringVelger from './SorteringVelger';

import styles from './utvalgskriterierForSakslisteForm.less';

const minLength3 = minLength(3);
const maxLength100 = maxLength(100);

const finnDagerSomTall = (antallDager) => {
  const nr = Number.parseInt(antallDager, 10);
  return Number.isNaN(nr) ? undefined : nr;
};

const buildInitialValues = (intl: IntlShape, valgtSaksliste): InitialValues => {
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

/**
 * UtvalgskriterierForSakslisteForm
 */
const UtvalgskriterierForSakslisteForm: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtSaksliste,
  valgtAvdelingEnhet,
}) => {
  const { data: antallOppgaver } = useRestApi(RestApiPathsKeys.OPPGAVE_ANTALL,
    { sakslisteId: valgtSaksliste.sakslisteId, avdelingEnhet: valgtAvdelingEnhet }, [valgtSaksliste.sakslisteId]);

  const { startRequest: lagreSakslisteNavn } = useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN);

  const tranformValues = (values: {sakslisteId: number; navn: string}): void => {
    lagreSakslisteNavn({ sakslisteId: values.sakslisteId, navn: values.navn, avdelingEnhet: valgtAvdelingEnhet });
  };

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={buildInitialValues(intl, valgtSaksliste)}
      render={({ values }) => (
        <Panel className={styles.panel}>
          <AutoLagringVedBlur lagre={tranformValues} fieldNames={['navn']} />
          <Element>
            <FormattedMessage id="UtvalgskriterierForSakslisteForm.Utvalgskriterier" />
          </Element>
          <VerticalSpacer eightPx />
          <Row>
            <Column xs="9">
              <InputField
                name="navn"
                label={intl.formatMessage({ id: 'UtvalgskriterierForSakslisteForm.Navn' })}
                validate={[required, minLength3, maxLength100, hasValidName]}
                onBlurValidation
                bredde="L"
                autoFocus
              />
            </Column>
            <Column xs="3">
              <div className={styles.grayBox}>
                <Normaltekst><FormattedMessage id="UtvalgskriterierForSakslisteForm.AntallSaker" /></Normaltekst>
                <Undertittel>{antallOppgaver || '0'}</Undertittel>
              </div>
            </Column>
          </Row>
          <Row>
            <Column xs="6" className={styles.stonadstypeRadios}>
              <FagsakYtelseTypeVelger
                valgtSakslisteId={valgtSaksliste.sakslisteId}
                valgtAvdelingEnhet={valgtAvdelingEnhet}
              />
            </Column>
          </Row>
          <Row>
            <Column xs="3">
              <BehandlingstypeVelger
                valgtSakslisteId={valgtSaksliste.sakslisteId}
                valgtAvdelingEnhet={valgtAvdelingEnhet}
              />
            </Column>
            <Column xs="4">
              <AndreKriterierVelger
                valgtSakslisteId={valgtSaksliste.sakslisteId}
                valgtAvdelingEnhet={valgtAvdelingEnhet}
                values={values}
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
              />
            </Column>
          </Row>
        </Panel>
      )}
    />
  );
};

export default injectIntl(UtvalgskriterierForSakslisteForm);
