import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';

import { Form } from 'react-final-form';
import { injectIntl, intlShape, FormattedMessage } from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Undertittel, Element, Normaltekst } from 'nav-frontend-typografi';

import { getValgtAvdelingEnhet } from 'app/duck';
import { Row, Column } from 'nav-frontend-grid';
import {
  required, minLength, maxLength, hasValidName,
} from 'utils/validation/validators';
import { Kodeverk } from 'kodeverk/kodeverkTsType';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { InputField } from 'form/FinalFields';
import { Saksliste } from '../../sakslisteTsType';
import sakslistePropType from '../../sakslistePropType';
import { getAntallOppgaverForSakslisteResultat } from '../../duck';
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

interface TsProps {
  intl: any;
  valgtSaksliste: Saksliste;
  lagreSakslisteNavn: (saksliste: {sakslisteId: number; navn: string}, avdelingEnhet: string) => void;
  lagreSakslisteBehandlingstype: (sakslisteId: number, behandlingType: Kodeverk, isChecked: boolean, avdelingEnhet: string) => void;
  lagreSakslisteFagsakYtelseType: (sakslisteId: number, fagsakYtelseType: string, avdelingEnhet: string) => void;
  lagreSakslisteAndreKriterier: (sakslisteId: number, andreKriterierType: Kodeverk, isChecked: boolean, skalInkludere: boolean, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
  antallOppgaver?: number;
  hentAntallOppgaverForSaksliste: (sakslisteId: number, avdelingEnhet: string) => Promise<string>;
}

/**
 * UtvalgskriterierForSakslisteForm
 */
export class UtvalgskriterierForSakslisteForm extends Component<TsProps> {
  static propTypes = {
    intl: intlShape.isRequired,
    valgtSaksliste: sakslistePropType.isRequired,
    lagreSakslisteNavn: PropTypes.func.isRequired,
    lagreSakslisteBehandlingstype: PropTypes.func.isRequired,
    lagreSakslisteFagsakYtelseType: PropTypes.func.isRequired,
    lagreSakslisteAndreKriterier: PropTypes.func.isRequired,
    valgtAvdelingEnhet: PropTypes.string.isRequired,
    antallOppgaver: PropTypes.number,
    hentAntallOppgaverForSaksliste: PropTypes.func.isRequired,
  };

  componentDidMount = () => {
    const {
      valgtSaksliste, hentAntallOppgaverForSaksliste, valgtAvdelingEnhet,
    } = this.props;
    hentAntallOppgaverForSaksliste(valgtSaksliste.sakslisteId, valgtAvdelingEnhet);
  }

  componentDidUpdate = (prevProps: TsProps) => {
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
      intl, lagreSakslisteBehandlingstype, lagreSakslisteFagsakYtelseType, valgtSaksliste, valgtAvdelingEnhet, antallOppgaver,
      lagreSakslisteAndreKriterier,
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
                  valgtFagsakYtelseType={values ? values.fagsakYtelseType : ''}
                />
              </Column>
            </Row>
            <Row>
              <Column xs="3">
                <BehandlingstypeVelger
                  lagreSakslisteBehandlingstype={lagreSakslisteBehandlingstype}
                  valgtSakslisteId={valgtSaksliste.sakslisteId}
                  valgtAvdelingEnhet={valgtAvdelingEnhet}
                />
              </Column>
              <Column xs="4">
                <AndreKriterierVelger
                  lagreSakslisteAndreKriterier={lagreSakslisteAndreKriterier}
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
  }
}

const mapStateToProps = state => ({
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
  antallOppgaver: getAntallOppgaverForSakslisteResultat(state),
});

export default connect(mapStateToProps)(injectIntl(UtvalgskriterierForSakslisteForm));
