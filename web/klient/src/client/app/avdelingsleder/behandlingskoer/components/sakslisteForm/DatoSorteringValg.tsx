import React, { FunctionComponent } from 'react';
import { FormattedMessage, WrappedComponentProps } from 'react-intl';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { Undertekst } from 'nav-frontend-typografi';

import {
  DateLabel, FlexColumn, FlexContainer, FlexRow, VerticalSpacer, ArrowBox,
} from '@navikt/ft-ui-komponenter';
import { ISO_DATE_FORMAT } from '@navikt/ft-utils';
import { hasValidDate, hasValidPosOrNegInteger } from '@navikt/ft-form-validators';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import {
  InputField, CheckboxField, Datepicker, formHooks,
} from '@navikt/ft-form-hooks';

import useDebounce from 'data/useDebounce';
import styles from './sorteringVelger.less';

dayjs.extend(customParseFormat);

const finnDato = (antallDager: number) => dayjs().add(antallDager, 'd').format();

const getLagreDatoFn = (
  lagreSakslisteSorteringTidsintervallDato: (params?: any, keepData?: boolean | undefined) => Promise<unknown>,
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void,
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void,
  valgtSakslisteId: number,
  valgtAvdelingEnhet: string,
  erFomDato: boolean,
  annenDato?: string,
) => (inputdato: string) => {
  let dato;
  if (inputdato) {
    dato = dayjs(inputdato);
  }
  if (!dato || dato.isValid()) {
    const d = dato ? dato.format(ISO_DATE_FORMAT) : dato;

    const params = erFomDato ? {
      sakslisteId: valgtSakslisteId,
      avdelingEnhet: valgtAvdelingEnhet,
      fomDato: d,
      tomDato: annenDato,
    } : {
      sakslisteId: valgtSakslisteId,
      avdelingEnhet: valgtAvdelingEnhet,
      fomDato: annenDato,
      tomDato: d,
    };

    return lagreSakslisteSorteringTidsintervallDato(params)
      .then(() => {
        hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
        hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
      });
  }
  return undefined;
};
interface OwnProps {
  valgtSakslisteId: number;
  lagreSakslisteSorteringTidsintervallDager: (params: {sakslisteId: number, fra: number, til: number, avdelingEnhet: string}) => Promise<any>;
  valgtAvdelingEnhet: string;
  erDynamiskPeriode: boolean;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
  hentAntallOppgaver: (sakslisteId: number, avdelingEnhet: string) => void;
}

export const DatoSorteringValg: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  valgtSakslisteId,
  valgtAvdelingEnhet,
  erDynamiskPeriode,
  lagreSakslisteSorteringTidsintervallDager,
  hentAvdelingensSakslister,
  hentAntallOppgaver,
}) => {
  const { startRequest: lagreSakslisteSorteringErDynamiskPeriode } = restApiHooks
    .useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_DYNAMISK_PERIDE);
  const { startRequest: lagreSakslisteSorteringTidsintervallDato } = restApiHooks
    .useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SORTERING_TIDSINTERVALL_DATO);

  const { watch } = formHooks.useFormContext();
  const fraVerdi = watch('fra');
  const tilVerdi = watch('til');
  const fomDatoVerdi = watch('fomDato');
  const tomDatoVerdi = watch('tomDato');

  const lagreFra = (nyFraVerdi: number) => lagreSakslisteSorteringTidsintervallDager({
    sakslisteId: valgtSakslisteId, fra: nyFraVerdi, til: tilVerdi, avdelingEnhet: valgtAvdelingEnhet,
  })
    .then(() => {
      hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
      hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
    });
  const lagreTil = (nyTilVerdi: number) => lagreSakslisteSorteringTidsintervallDager({
    sakslisteId: valgtSakslisteId, fra: fraVerdi, til: nyTilVerdi, avdelingEnhet: valgtAvdelingEnhet,
  })
    .then(() => {
      hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
      hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
    });

  const lagreFomDato = getLagreDatoFn(lagreSakslisteSorteringTidsintervallDato, hentAntallOppgaver, hentAvdelingensSakslister,
    valgtSakslisteId, valgtAvdelingEnhet, true, tomDatoVerdi);
  const lagreTomDato = getLagreDatoFn(lagreSakslisteSorteringTidsintervallDato, hentAntallOppgaver, hentAvdelingensSakslister,
    valgtSakslisteId, valgtAvdelingEnhet, false, fomDatoVerdi);

  const lagreFraDebounce = useDebounce<number>('fra', lagreFra);
  const lagreTilDebounce = useDebounce<number>('til', lagreTil);
  const lagreFomDatoDebounce = useDebounce<string>('fomDato', lagreFomDato);
  const lagreTomDatoDebounce = useDebounce<string>('tomDato', lagreTomDato);

  return (
    <ArrowBox>
      <Undertekst>
        <FormattedMessage id="SorteringVelger.FiltrerPaTidsintervall" />
      </Undertekst>

      {erDynamiskPeriode && (
        <FlexContainer>
          <FlexRow>
            <FlexColumn>
              <InputField
                name="fra"
                className={styles.dato}
                label={intl.formatMessage({ id: 'SorteringVelger.Fom' })}
                validate={[hasValidPosOrNegInteger]}
                bredde="XS"
                onChange={lagreFraDebounce}
              />
              {(fraVerdi || fraVerdi === 0) && (
              <Undertekst>
                <DateLabel dateString={finnDato(fraVerdi)} />
              </Undertekst>
              )}
            </FlexColumn>
            <FlexColumn>
              <Undertekst className={styles.dager}>
                <FormattedMessage id="SorteringVelger.DagerMedBindestrek" />
              </Undertekst>
            </FlexColumn>
            <FlexColumn>
              <InputField
                name="til"
                className={styles.dato}
                label={intl.formatMessage({ id: 'SorteringVelger.Tom' })}
                validate={[hasValidPosOrNegInteger]}
                bredde="XS"
                onChange={lagreTilDebounce}
              />
              {(tilVerdi || tilVerdi === 0) && (
              <Undertekst>
                <DateLabel dateString={finnDato(tilVerdi)} />
              </Undertekst>
              )}
            </FlexColumn>
            <FlexColumn>
              <Undertekst className={styles.dagerMedBindestrek}>
                <FormattedMessage id="SorteringVelger.Dager" />
              </Undertekst>
            </FlexColumn>
          </FlexRow>
        </FlexContainer>
      )}
      {!erDynamiskPeriode && (
        <FlexContainer>
          <FlexRow>
            <FlexColumn>
              <Datepicker
                name="fomDato"
                label={intl.formatMessage({ id: 'SorteringVelger.Fom' })}
                validate={[hasValidDate]}
                onChange={lagreFomDatoDebounce}
              />
            </FlexColumn>
            <FlexColumn>
              <Undertekst className={styles.dager}>
                <FormattedMessage id="SorteringVelger.Bindestrek" />
              </Undertekst>
            </FlexColumn>
            <FlexColumn className={styles.tomDato}>
              <Datepicker
                name="tomDato"
                label={intl.formatMessage({ id: 'SorteringVelger.Tom' })}
                validate={[hasValidDate]}
                onChange={lagreTomDatoDebounce}
              />
            </FlexColumn>
          </FlexRow>
        </FlexContainer>
      )}
      <VerticalSpacer eightPx />
      <CheckboxField
        name="erDynamiskPeriode"
        label={intl.formatMessage({ id: 'SorteringVelger.DynamiskPeriode' })}
        onChange={() => lagreSakslisteSorteringErDynamiskPeriode({ sakslisteId: valgtSakslisteId, avdelingEnhet: valgtAvdelingEnhet })
          .then(() => {
            hentAntallOppgaver(valgtSakslisteId, valgtAvdelingEnhet);
            hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet });
          })}
      />
      <VerticalSpacer eightPx />
    </ArrowBox>
  );
};

export default DatoSorteringValg;
