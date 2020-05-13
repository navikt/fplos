import React, { Component, ReactNode } from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { Form, FormSpy } from 'react-final-form';
import {
  injectIntl, WrappedComponentProps, FormattedMessage,
} from 'react-intl';
import { bindActionCreators, Dispatch } from 'redux';
import { Element, Normaltekst } from 'nav-frontend-typografi';

import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import Image from 'sharedComponents/Image';
import { getValueFromLocalStorage, setValueInLocalStorage, removeValueFromLocalStorage } from 'utils/localStorageHelper';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import LabelWithHeader from 'sharedComponents/LabelWithHeader';
import { Saksliste } from 'saksbehandler/behandlingskoer/sakslisteTsType';
import { SelectField } from 'form/FinalFields';
import gruppeHoverUrl from 'images/gruppe_hover.svg';
import gruppeUrl from 'images/gruppe.svg';
import { getSakslistensSaksbehandlere, fetchAntallOppgaverForBehandlingsko, fetchSakslistensSaksbehandlere } from '../duck';
import { Saksbehandler } from '../saksbehandlerTsType';

import styles from './sakslisteVelgerForm.less';

interface TsProps {
  sakslister: Saksliste[];
  fetchSakslisteOppgaver: (sakslisteId: number) => void;
  fetchSakslistensSaksbehandlere: (sakslisteId: number) => void;
  fetchAntallOppgaverForBehandlingsko: (sakslisteId: number) => void;
  saksbehandlere?: Saksbehandler[];
}

const getDefaultSaksliste = (sakslister) => {
  const lagretSakslisteId = getValueFromLocalStorage('sakslisteId');
  if (lagretSakslisteId) {
    if (sakslister.some((s) => `${s.sakslisteId}` === lagretSakslisteId)) {
      return parseInt(lagretSakslisteId, 10);
    }
    removeValueFromLocalStorage('sakslisteId');
  }

  const sortertSakslister = sakslister.sort((saksliste1, saksliste2) => saksliste1.navn.localeCompare(saksliste2.navn));
  return sortertSakslister.length > 0 ? sortertSakslister[0].sakslisteId : undefined;
};

const getInitialValues = (sakslister) => {
  if (sakslister.length === 0) {
    return {
      sakslisteId: undefined,
    };
  }
  const defaultSaksliste = getDefaultSaksliste(sakslister);
  return {
    sakslisteId: defaultSaksliste ? `${defaultSaksliste}` : undefined,
  };
};

const getValgtSaksliste = (sakslister: Saksliste[], sakslisteId: string) => sakslister.find((s) => sakslisteId === `${s.sakslisteId}`);

const getStonadstyper = (saksliste?: Saksliste, intl: any) => (saksliste && saksliste.fagsakYtelseTyper.length > 0
  ? saksliste.fagsakYtelseTyper.map((type) => type.navn) : [intl.formatMessage({ id: 'SakslisteVelgerForm.Alle' })]);

const getBehandlingstyper = (saksliste?: Saksliste, intl: any) => (saksliste && saksliste.behandlingTyper.length > 0
  ? saksliste.behandlingTyper.map((type) => type.navn) : [intl.formatMessage({ id: 'SakslisteVelgerForm.Alle' })]);

const getAndreKriterier = (saksliste?: Saksliste, intl: any) => {
  if (saksliste && saksliste.andreKriterier.length > 0) {
    return saksliste.andreKriterier.map((ak) => (ak.inkluder ? ak.andreKriterierType.navn
      : intl.formatMessage({ id: 'SakslisteVelgerForm.Uten' }, { kriterie: ak.andreKriterierType.navn })));
  }
  return [intl.formatMessage({ id: 'SakslisteVelgerForm.Alle' })];
};

const getSorteringsnavn = (saksliste?: Saksliste) => {
  if (!saksliste || !saksliste.sortering) {
    return '';
  }

  const {
    erDynamiskPeriode, sorteringType, fra, til, fomDato, tomDato,
  } = saksliste.sortering;
  let values = {
    br: <br />,
  };
  if (!erDynamiskPeriode) {
    if (!fomDato && !tomDato) {
      return sorteringType.navn;
    }
    values = {
      navn: sorteringType.navn,
      fomDato: fomDato ? moment(fomDato).format(DDMMYYYY_DATE_FORMAT) : undefined,
      tomDato: tomDato ? moment(tomDato).format(DDMMYYYY_DATE_FORMAT) : undefined,
      br: <br />,
    };
  } else {
    if (!fra && !til) {
      return sorteringType.navn;
    }
    values = {
      navn: sorteringType.navn,
      fomDato: fra ? moment().add(fra, 'days').format(DDMMYYYY_DATE_FORMAT) : undefined,
      tomDato: til ? moment().add(til, 'days').format(DDMMYYYY_DATE_FORMAT) : undefined,
      br: <br />,
    };
  }

  if (!values.fomDato) {
    return <FormattedMessage id="SakslisteVelgerForm.SorteringsinfoTom" values={values} />;
  } if (!values.tomDato) {
    return <FormattedMessage id="SakslisteVelgerForm.SorteringsinfoFom" values={values} />;
  }
  return <FormattedMessage id="SakslisteVelgerForm.Sorteringsinfo" values={values} />;
};

/**
 * SakslisteVelgerForm
 *
 */
export class SakslisteVelgerForm extends Component<TsProps & WrappedComponentProps> {
  static defaultProps = {
    saksbehandlere: [],
  };

  componentDidMount = () => {
    const {
      sakslister, fetchSakslisteOppgaver, fetchSakslistensSaksbehandlere: fetchSaksbehandlere, fetchAntallOppgaverForBehandlingsko: fetchAntallOppgaver,
    } = this.props;
    if (sakslister.length > 0) {
      const defaultSakslisteId = getDefaultSaksliste(sakslister);
      if (defaultSakslisteId) {
        fetchSakslisteOppgaver(defaultSakslisteId);
        fetchSaksbehandlere(defaultSakslisteId);
        fetchAntallOppgaver(defaultSakslisteId);
      }
    }
  }

  createTooltip = (): ReactNode | undefined => {
    const {
      intl, saksbehandlere,
    } = this.props;
    if (!saksbehandlere || saksbehandlere.length === 0) {
      return undefined;
    }

    return (
      <div>
        <Element>{intl.formatMessage({ id: 'SakslisteVelgerForm.SaksbehandlerToolip' })}</Element>
        {saksbehandlere.map((s) => s.navn).sort((n1, n2) => n1.localeCompare(n2)).map((navn) => (<Normaltekst key={navn}>{navn}</Normaltekst>))}
      </div>
    );
  }

  render = () => {
    const {
      intl, sakslister, fetchSakslisteOppgaver, fetchSakslistensSaksbehandlere: fetchSaksbehandlere, fetchAntallOppgaverForBehandlingsko: fetchAntallOppgaver,
    } = this.props;
    return (
      <Form
        onSubmit={() => undefined}
        initialValues={getInitialValues(sakslister)}
        render={({ values = {} }) => (
          <form>
            <Element><FormattedMessage id="SakslisteVelgerForm.Utvalgskriterier" /></Element>
            <VerticalSpacer eightPx />
            <FormSpy
              onChange={(val) => {
                if (val && val.values.sakslisteId && val.dirtyFields.sakslisteId) {
                  setValueInLocalStorage('sakslisteId', val.values.sakslisteId);
                  const id = parseInt(val.values.sakslisteId, 10);
                  fetchSakslisteOppgaver(id);
                  fetchSaksbehandlere(id);
                  fetchAntallOppgaver(id);
                }
              }}
              subscription={{ values: true, dirtyFields: true }}
            />
            <FlexContainer>
              <FlexRow>
                <FlexColumn className={styles.navnInput}>
                  <SelectField
                    name="sakslisteId"
                    label={intl.formatMessage({ id: 'SakslisteVelgerForm.Saksliste' })}
                    selectValues={sakslister
                      .map((saksliste) => (<option key={saksliste.sakslisteId} value={`${saksliste.sakslisteId}`}>{saksliste.navn}</option>))}
                    bredde="l"
                  />
                </FlexColumn>
                {values.sakslisteId && (
                  <>
                    <FlexColumn>
                      <div className={styles.saksbehandlerIkon} />
                      <Image
                        alt={intl.formatMessage({ id: 'SakslisteVelgerForm.Saksbehandlere' })}
                        src={gruppeUrl}
                        srcHover={gruppeHoverUrl}
                        tooltip={this.createTooltip()}
                      />
                    </FlexColumn>
                    <FlexColumn className={styles.marginFilters}>
                      <LabelWithHeader
                        header={intl.formatMessage({ id: 'SakslisteVelgerForm.Stonadstype' })}
                        texts={getStonadstyper(getValgtSaksliste(sakslister, values.sakslisteId), intl)}
                      />
                    </FlexColumn>
                    <FlexColumn className={styles.marginFilters}>
                      <LabelWithHeader
                        header={intl.formatMessage({ id: 'SakslisteVelgerForm.Behandlingstype' })}
                        texts={getBehandlingstyper(getValgtSaksliste(sakslister, values.sakslisteId), intl)}
                      />
                    </FlexColumn>
                    <FlexColumn className={styles.marginFilters}>
                      <LabelWithHeader
                        header={intl.formatMessage({ id: 'SakslisteVelgerForm.AndreKriterier' })}
                        texts={getAndreKriterier(getValgtSaksliste(sakslister, values.sakslisteId), intl)}
                      />
                    </FlexColumn>
                    <FlexColumn className={styles.marginFilters}>
                      <LabelWithHeader
                        header={intl.formatMessage({ id: 'SakslisteVelgerForm.Sortering' })}
                        texts={[getSorteringsnavn(getValgtSaksliste(sakslister, values.sakslisteId))]}
                      />
                    </FlexColumn>
                  </>
                )}
              </FlexRow>
            </FlexContainer>
          </form>
        )}
      />
    );
  }
}

const mapStateToProps = (state) => ({
  saksbehandlere: getSakslistensSaksbehandlere(state),
});

const mapDispatchToProps = (dispatch: Dispatch) => ({
  ...bindActionCreators({
    fetchSakslistensSaksbehandlere,
    fetchAntallOppgaverForBehandlingsko,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(injectIntl(SakslisteVelgerForm));
