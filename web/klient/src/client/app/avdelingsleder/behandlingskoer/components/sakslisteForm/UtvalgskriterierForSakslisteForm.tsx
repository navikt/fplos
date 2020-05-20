import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators, Dispatch } from 'redux';
import { Form } from 'react-final-form';
import { injectIntl, WrappedComponentProps, FormattedMessage } from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Undertittel, Element, Normaltekst } from 'nav-frontend-typografi';

import { getAlleKodeverk } from 'kodeverk/duck';
import { getValgtAvdelingEnhet } from 'app/duck';
import { Row, Column } from 'nav-frontend-grid';
import {
  required, minLength, maxLength, hasValidName,
} from 'utils/validation/validators';
import Kodeverk from 'kodeverk/kodeverkTsType';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { InputField } from 'form/FinalFields';

import KoSorteringType from '../../KoSorteringTsType';
import {
  getAntallOppgaverForSakslisteResultat,
  lagreSakslisteSortering as lagreSakslisteSorteringActionCreator,
  lagreSakslisteSorteringErDynamiskPeriode as lagreSakslisteSorteringErDynamiskPeriodeActionCreator,
  lagreSakslisteSorteringTidsintervallDato as lagreSakslisteSorteringTidsintervallDatoActionCreator,
  lagreSakslisteSorteringNumeriskIntervall as lagreSakslisteSorteringNumeriskIntervallActionCreator,
} from '../../duck';
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

interface OwnProps {
  valgtSaksliste: Saksliste;
  alleKodeverk: {[key: string]: Kodeverk[]};
  lagreSakslisteNavn: (saksliste: {sakslisteId: number; navn: string}, avdelingEnhet: string) => void;
  lagreSakslisteBehandlingstype: (sakslisteId: number, behandlingType: Kodeverk, isChecked: boolean, avdelingEnhet: string) => void;
  lagreSakslisteFagsakYtelseType: (sakslisteId: number, fagsakYtelseType: string, avdelingEnhet: string) => void;
  lagreSakslisteAndreKriterier: (sakslisteId: number, andreKriterierType: Kodeverk, isChecked: boolean, skalInkludere: boolean, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
  antallOppgaver?: number;
  hentAntallOppgaverForSaksliste: (sakslisteId: number, avdelingEnhet: string) => Promise<string>;
}

interface DispatchProps {
  lagreSakslisteSortering: (sakslisteId: number, sakslisteSorteringValg: KoSorteringType, avdelingEnhet: string) => void;
  lagreSakslisteSorteringErDynamiskPeriode: (sakslisteId: number, avdelingEnhet: string) => void;
  lagreSakslisteSorteringTidsintervallDato: (sakslisteId: number, fomDato: string, tomDato: string, avdelingEnhet: string) => void;
  lagreSakslisteSorteringNumeriskIntervall: (sakslisteId: number, fra: number, til: number, avdelingEnhet: string) => void;
}

/**
 * UtvalgskriterierForSakslisteForm
 */
export class UtvalgskriterierForSakslisteForm extends Component<OwnProps & DispatchProps & WrappedComponentProps> {
  componentDidMount = () => {
    const {
      valgtSaksliste, hentAntallOppgaverForSaksliste, valgtAvdelingEnhet,
    } = this.props;
    hentAntallOppgaverForSaksliste(valgtSaksliste.sakslisteId, valgtAvdelingEnhet);
  }

  componentDidUpdate = (prevProps: OwnProps) => {
    const {
      valgtSaksliste, hentAntallOppgaverForSaksliste, valgtAvdelingEnhet,
    } = this.props;
    if (prevProps.valgtSaksliste.sakslisteId !== valgtSaksliste.sakslisteId) {
      hentAntallOppgaverForSaksliste(valgtSaksliste.sakslisteId, valgtAvdelingEnhet);
    }
  }

  buildInitialValues = (intl: any) => {
    const {
      valgtSaksliste,
    } = this.props;

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
  }

  tranformValues = (values: {sakslisteId: number; navn: string}) => {
    const {
      lagreSakslisteNavn, valgtAvdelingEnhet,
    } = this.props;
    lagreSakslisteNavn({ sakslisteId: values.sakslisteId, navn: values.navn }, valgtAvdelingEnhet);
  }

  render = () => {
    const {
      intl,
      lagreSakslisteBehandlingstype,
      lagreSakslisteFagsakYtelseType,
      valgtSaksliste,
      valgtAvdelingEnhet,
      antallOppgaver,
      lagreSakslisteAndreKriterier,
      alleKodeverk,
      lagreSakslisteSortering,
      lagreSakslisteSorteringErDynamiskPeriode,
      lagreSakslisteSorteringTidsintervallDato,
      lagreSakslisteSorteringNumeriskIntervall,
    } = this.props;

    return (
      <Form
        onSubmit={() => undefined}
        initialValues={this.buildInitialValues(intl)}
        render={({ values }) => (
          <Panel className={styles.panel}>
            <AutoLagringVedBlur lagre={this.tranformValues} fieldNames={['navn']} />
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
                  lagreSakslisteFagsakYtelseType={lagreSakslisteFagsakYtelseType}
                  valgtSakslisteId={valgtSaksliste.sakslisteId}
                  valgtAvdelingEnhet={valgtAvdelingEnhet}
                  alleKodeverk={alleKodeverk}
                />
              </Column>
            </Row>
            <Row>
              <Column xs="3">
                <BehandlingstypeVelger
                  lagreSakslisteBehandlingstype={lagreSakslisteBehandlingstype}
                  valgtSakslisteId={valgtSaksliste.sakslisteId}
                  valgtAvdelingEnhet={valgtAvdelingEnhet}
                  alleKodeverk={alleKodeverk}
                />
              </Column>
              <Column xs="4">
                <AndreKriterierVelger
                  lagreSakslisteAndreKriterier={lagreSakslisteAndreKriterier}
                  valgtSakslisteId={valgtSaksliste.sakslisteId}
                  valgtAvdelingEnhet={valgtAvdelingEnhet}
                  values={values}
                  alleKodeverk={alleKodeverk}
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
                  alleKodeverk={alleKodeverk as {[key: string]: KoSorteringType[]}}
                  lagreSakslisteSortering={lagreSakslisteSortering}
                  lagreSakslisteSorteringErDynamiskPeriode={lagreSakslisteSorteringErDynamiskPeriode}
                  lagreSakslisteSorteringTidsintervallDato={lagreSakslisteSorteringTidsintervallDato}
                  lagreSakslisteSorteringNumeriskIntervall={lagreSakslisteSorteringNumeriskIntervall}
                />
              </Column>
            </Row>
          </Panel>
        )}
      />
    );
  }
}

const mapStateToProps = (state) => ({
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
  antallOppgaver: getAntallOppgaverForSakslisteResultat(state),
  alleKodeverk: getAlleKodeverk(state),
});

const mapDispatchToProps = (dispatch: Dispatch): DispatchProps => ({
  ...bindActionCreators({
    lagreSakslisteSortering: lagreSakslisteSorteringActionCreator,
    lagreSakslisteSorteringErDynamiskPeriode: lagreSakslisteSorteringErDynamiskPeriodeActionCreator,
    lagreSakslisteSorteringTidsintervallDato: lagreSakslisteSorteringTidsintervallDatoActionCreator,
    lagreSakslisteSorteringNumeriskIntervall: lagreSakslisteSorteringNumeriskIntervallActionCreator,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(UtvalgskriterierForSakslisteForm));
