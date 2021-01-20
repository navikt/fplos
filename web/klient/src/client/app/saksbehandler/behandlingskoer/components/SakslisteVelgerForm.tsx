import React, {
  ReactNode, FunctionComponent, useEffect, useMemo,
} from 'react';
import moment from 'moment';
import { Form, FormSpy } from 'react-final-form';
import {
  injectIntl, WrappedComponentProps, FormattedMessage, IntlShape,
} from 'react-intl';
import { Element, Normaltekst } from 'nav-frontend-typografi';

import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import Image from 'sharedComponents/Image';
import { FlexContainer, FlexRow, FlexColumn } from 'sharedComponents/flexGrid';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import LabelWithHeader from 'sharedComponents/LabelWithHeader';
import Saksliste from 'saksbehandler/behandlingskoer/sakslisteTsType';
import { SelectField } from 'form/FinalFields';
import gruppeHoverUrl from 'images/gruppe_hover.svg';
import gruppeUrl from 'images/gruppe.svg';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import Saksbehandler from '../saksbehandlerTsType';

import styles from './sakslisteVelgerForm.less';

interface OwnProps {
  sakslister: Saksliste[];
  setValgtSakslisteId: (sakslisteId: number) => void;
  fetchAntallOppgaver: (data: {sakslisteId: number}) => void;
  getValueFromLocalStorage: (key: string) => string;
  setValueInLocalStorage: (key: string, value: string) => void;
  removeValueFromLocalStorage: (key: string) => void;
}

const getDefaultSaksliste = (sakslister, getValueFromLocalStorage, removeValueFromLocalStorage) => {
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

const getInitialValues = (sakslister, getValueFromLocalStorage, removeValueFromLocalStorage) => {
  if (sakslister.length === 0) {
    return {
      sakslisteId: undefined,
    };
  }
  const defaultSaksliste = getDefaultSaksliste(sakslister, getValueFromLocalStorage, removeValueFromLocalStorage);
  return {
    sakslisteId: defaultSaksliste ? `${defaultSaksliste}` : undefined,
  };
};

const getValgtSaksliste = (sakslister: Saksliste[], sakslisteId: string) => sakslister.find((s) => sakslisteId === `${s.sakslisteId}`);

const getStonadstyper = (intl: IntlShape, saksliste?: Saksliste) => (saksliste && saksliste.fagsakYtelseTyper.length > 0
  ? saksliste.fagsakYtelseTyper.map((type) => type.navn) : [intl.formatMessage({ id: 'SakslisteVelgerForm.Alle' })]);

const getBehandlingstyper = (intl: IntlShape, saksliste?: Saksliste) => (saksliste && saksliste.behandlingTyper.length > 0
  ? saksliste.behandlingTyper.map((type) => type.navn) : [intl.formatMessage({ id: 'SakslisteVelgerForm.Alle' })]);

const getAndreKriterier = (intl: IntlShape, saksliste?: Saksliste) => {
  if (saksliste && saksliste.andreKriterier.length > 0) {
    return saksliste.andreKriterier.map((ak) => (ak.inkluder ? ak.andreKriterierType.navn
      : intl.formatMessage({ id: 'SakslisteVelgerForm.Uten' }, { kriterie: ak.andreKriterierType.navn })));
  }
  return [intl.formatMessage({ id: 'SakslisteVelgerForm.Alle' })];
};

const getSorteringsnavn = (intl: IntlShape, saksliste?: Saksliste): string => {
  if (!saksliste || !saksliste.sortering) {
    return '';
  }

  const {
    erDynamiskPeriode, sorteringType, fra, til, fomDato, tomDato,
  } = saksliste.sortering;
  let values = {
    br: <br />,
    fomDato: undefined,
    tomDato: undefined,
    navn: undefined,
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
    return intl.formatMessage({ id: 'SakslisteVelgerForm.SorteringsinfoTom' }, values) as string;
  } if (!values.tomDato) {
    return intl.formatMessage({ id: 'SakslisteVelgerForm.SorteringsinfoFom' }, values) as string;
  }
  return intl.formatMessage({ id: 'SakslisteVelgerForm.Sorteringsinfo' }, values) as string;
};

const createTooltip = (saksbehandlere: Saksbehandler[]): ReactNode | undefined => {
  if (!saksbehandlere || saksbehandlere.length === 0) {
    return undefined;
  }

  return (
    <div>
      <Element><FormattedMessage id="SakslisteVelgerForm.SaksbehandlerToolip" /></Element>
      {saksbehandlere.map((s) => s.navn).sort((n1, n2) => n1.localeCompare(n2)).map((navn) => (<Normaltekst key={navn}>{navn}</Normaltekst>))}
    </div>
  );
};

/**
 * SakslisteVelgerForm
 *
 */
export const SakslisteVelgerForm: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  sakslister,
  setValgtSakslisteId,
  fetchAntallOppgaver,
  getValueFromLocalStorage,
  setValueInLocalStorage,
  removeValueFromLocalStorage,
}) => {
  const { data: saksbehandlere, startRequest: fetchSaksbehandlere } = restApiHooks.useRestApiRunner<Saksbehandler[]>(RestApiPathsKeys.SAKSLISTE_SAKSBEHANDLERE);

  useEffect(() => {
    if (sakslister.length > 0) {
      const defaultSakslisteId = getDefaultSaksliste(sakslister, getValueFromLocalStorage, removeValueFromLocalStorage);
      if (defaultSakslisteId) {
        setValgtSakslisteId(defaultSakslisteId);
        fetchSaksbehandlere({ sakslisteId: defaultSakslisteId });
        fetchAntallOppgaver({ sakslisteId: defaultSakslisteId });
      }
    }
  }, []);

  const tooltip = useMemo(() => createTooltip(saksbehandlere), [saksbehandlere]);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={getInitialValues(sakslister, getValueFromLocalStorage, removeValueFromLocalStorage)}
      render={({ values = {} }) => (
        <form>
          <Element><FormattedMessage id="SakslisteVelgerForm.Utvalgskriterier" /></Element>
          <VerticalSpacer eightPx />
          <FormSpy
            onChange={(val) => {
              if (val && val.values.sakslisteId && val.dirtyFields.sakslisteId) {
                setValueInLocalStorage('sakslisteId', val.values.sakslisteId);
                const id = parseInt(val.values.sakslisteId, 10);
                setValgtSakslisteId(id);
                fetchSaksbehandlere({ sakslisteId: id });
                fetchAntallOppgaver({ sakslisteId: id });
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
                      tooltip={tooltip}
                    />
                  </FlexColumn>
                  <FlexColumn className={styles.marginFilters}>
                    <LabelWithHeader
                      header={intl.formatMessage({ id: 'SakslisteVelgerForm.Stonadstype' })}
                      texts={getStonadstyper(intl, getValgtSaksliste(sakslister, values.sakslisteId))}
                    />
                  </FlexColumn>
                  <FlexColumn className={styles.marginFilters}>
                    <LabelWithHeader
                      header={intl.formatMessage({ id: 'SakslisteVelgerForm.Behandlingstype' })}
                      texts={getBehandlingstyper(intl, getValgtSaksliste(sakslister, values.sakslisteId))}
                    />
                  </FlexColumn>
                  <FlexColumn className={styles.marginFilters}>
                    <LabelWithHeader
                      header={intl.formatMessage({ id: 'SakslisteVelgerForm.AndreKriterier' })}
                      texts={getAndreKriterier(intl, getValgtSaksliste(sakslister, values.sakslisteId))}
                    />
                  </FlexColumn>
                  <FlexColumn className={styles.marginFilters}>
                    <LabelWithHeader
                      header={intl.formatMessage({ id: 'SakslisteVelgerForm.Sortering' })}
                      texts={[getSorteringsnavn(intl, getValgtSaksliste(sakslister, values.sakslisteId))]}
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
};

export default injectIntl(SakslisteVelgerForm);
