import React, { FunctionComponent } from 'react';
import { FormattedMessage, WrappedComponentProps } from 'react-intl';
import dayjs from 'dayjs';
import customParseFormat from 'dayjs/plugin/customParseFormat';
import { useFormContext } from 'react-hook-form';
import { Undertekst } from 'nav-frontend-typografi';

import { FlexColumn, FlexContainer, FlexRow } from 'sharedComponents/flexGrid';
import { hasValidDate, hasValidPosOrNegInteger } from 'utils/validation/validators';
import DateLabel from 'sharedComponents/DateLabel';
import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import { ISO_DATE_FORMAT } from 'utils/formats';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import ArrowBox from 'sharedComponents/ArrowBox';
import { InputField, CheckboxField, DatepickerField } from 'form/formIndex';

import useDebounce from 'form/useDebounce';
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

  const { watch } = useFormContext();
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
                validate={[hasValidPosOrNegInteger(intl)]}
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
                validate={[hasValidPosOrNegInteger(intl)]}
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
              <DatepickerField
                name="fomDato"
                label={intl.formatMessage({ id: 'SorteringVelger.Fom' })}
                validate={[hasValidDate(intl)]}
                onChange={lagreFomDatoDebounce}
              />
            </FlexColumn>
            <FlexColumn>
              <Undertekst className={styles.dager}>
                <FormattedMessage id="SorteringVelger.Bindestrek" />
              </Undertekst>
            </FlexColumn>
            <FlexColumn className={styles.tomDato}>
              <DatepickerField
                name="tomDato"
                label={intl.formatMessage({ id: 'SorteringVelger.Tom' })}
                validate={[hasValidDate(intl)]}
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
