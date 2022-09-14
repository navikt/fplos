import React, { FunctionComponent, useEffect, useCallback } from 'react';
import {
  injectIntl, WrappedComponentProps, FormattedMessage, IntlShape,
} from 'react-intl';
import { useForm } from 'react-hook-form';
import {
  Heading, BodyShort, Panel, Label,
} from '@navikt/ds-react';

import { Row, Column } from 'nav-frontend-grid';
import {
  required, minLength, maxLength, hasValidName,
} from '@navikt/ft-form-validators';
import { VerticalSpacer } from '@navikt/ft-ui-komponenter';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import { Form, InputField } from '@navikt/ft-form-hooks';
import Saksliste from 'types/avdelingsleder/sakslisteAvdelingTsType';
import useDebounce from 'data/useDebounce';
import BehandlingstypeVelger from './BehandlingstypeVelger';
import AndreKriterierVelger from './AndreKriterierVelger';
import FagsakYtelseTypeVelger from './FagsakYtelseTypeVelger';
import SorteringVelger from './SorteringVelger';

import styles from './utvalgskriterierForSakslisteForm.less';

const minLength3 = minLength(3);
const maxLength100 = maxLength(100);

type FormValues = {
  sakslisteId: number
  navn: string;
  fagsakYtelseType: string;
  sortering?: string;
  erDynamiskPeriode?: boolean;
  fra?: string;
  til?: string;
  fomDato?: string;
  tomDato?: string;
}

const buildInitialValues = (intl: IntlShape, valgtSaksliste: Saksliste): FormValues => {
  const behandlingTypes = valgtSaksliste.behandlingTyper ? valgtSaksliste.behandlingTyper.reduce((acc, bt) => ({ ...acc, [bt]: true }), {}) : {};
  const fagsakYtelseType = valgtSaksliste.fagsakYtelseTyper && valgtSaksliste.fagsakYtelseTyper.length > 0
    ? valgtSaksliste.fagsakYtelseTyper[0] : '';

  const andreKriterierTyper = valgtSaksliste.andreKriterier
    ? valgtSaksliste.andreKriterier.reduce((acc, ak) => ({ ...acc, [ak.andreKriterierType]: true }), {}) : {};
  const andreKriterierInkluder = valgtSaksliste.andreKriterier
    ? valgtSaksliste.andreKriterier.reduce((acc, ak) => ({ ...acc, [`${ak.andreKriterierType}_inkluder`]: ak.inkluder }), {}) : {};

  return {
    sakslisteId: valgtSaksliste.sakslisteId,
    navn: valgtSaksliste.navn ? valgtSaksliste.navn : intl.formatMessage({ id: 'UtvalgskriterierForSakslisteForm.NyListe' }),
    sortering: valgtSaksliste.sortering ? valgtSaksliste.sortering.sorteringType : undefined,
    fomDato: valgtSaksliste.sortering ? valgtSaksliste.sortering.fomDato : undefined,
    tomDato: valgtSaksliste.sortering ? valgtSaksliste.sortering.tomDato : undefined,
    fra: valgtSaksliste.sortering ? valgtSaksliste.sortering.fra?.toString() : undefined,
    til: valgtSaksliste.sortering ? valgtSaksliste.sortering.til?.toString() : undefined,
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

  const hentAntallOppgaver = useCallback((sakslisteId: number, avdelingEnhet: string) => {
    hentAntallOppgaverForSaksliste({ sakslisteId, avdelingEnhet });
    hentOppgaverForAvdelingAntall({ avdelingEnhet });
  }, []);

  const { startRequest: lagreSakslisteNavn } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_NAVN);

  const tranformValues = useCallback((nyttNavn: string): void => {
    lagreSakslisteNavn({ sakslisteId: valgtSaksliste.sakslisteId, navn: nyttNavn, avdelingEnhet: valgtAvdelingEnhet })
      .then(() => hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet }));
  }, [valgtAvdelingEnhet, valgtSaksliste]);

  const defaultValues = buildInitialValues(intl, valgtSaksliste);

  const formMethods = useForm<FormValues>({
    defaultValues,
  });

  const lagreNavn = useDebounce<string>('navn', tranformValues, formMethods.trigger);

  useEffect(() => {
    formMethods.reset(defaultValues);
  }, [valgtSaksliste.sakslisteId]);

  const values = formMethods.watch();

  return (
    <Form<FormValues> formMethods={formMethods}>
      <Panel className={styles.panel}>
        <Label size="small">
          <FormattedMessage id="UtvalgskriterierForSakslisteForm.Utvalgskriterier" />
        </Label>
        <VerticalSpacer eightPx />
        <Row>
          <Column xs="9">
            <InputField
              name="navn"
              label={intl.formatMessage({ id: 'UtvalgskriterierForSakslisteForm.Navn' })}
              validate={[required, minLength3, maxLength100, hasValidName]}
              onChange={lagreNavn}
              className={styles.bredde}
            />
          </Column>
          <Column xs="3">
            <div className={styles.grayBox}>
              <BodyShort size="small"><FormattedMessage id="UtvalgskriterierForSakslisteForm.AntallSaker" /></BodyShort>
              <Heading size="small">{antallOppgaver ? `${antallOppgaver}` : '0'}</Heading>
            </div>
          </Column>
        </Row>
        <VerticalSpacer eightPx />
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
        <VerticalSpacer sixteenPx />
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
              erDynamiskPeriode={!!values.erDynamiskPeriode}
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
