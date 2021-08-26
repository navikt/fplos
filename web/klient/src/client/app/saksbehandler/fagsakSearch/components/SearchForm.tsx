import React, { FunctionComponent } from 'react';
import { FormattedMessage, injectIntl, WrappedComponentProps } from 'react-intl';
import { useForm } from 'react-hook-form';
import { Undertittel } from 'nav-frontend-typografi';
import { Knapp } from 'nav-frontend-knapper';

import advarselIcon from 'images/advarsel.svg';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { hasValidSaksnummerOrFodselsnummerFormat } from 'utils/validation/validators';
import { restApiHooks, RestApiGlobalStatePathsKeys } from 'data/fplosRestApi';
import { FlexColumn, FlexContainer, FlexRow } from 'sharedComponents/flexGrid';
import Image from 'sharedComponents/Image';
import { Form, InputField, CheckboxField } from 'form/formIndex';

import styles from './searchForm.less';

const isButtonDisabled = (
  searchString: string,
  searchStarted: boolean,
  searchResultAccessDenied?: {
    feilmelding?: string;
  },
) => (!searchResultAccessDenied?.feilmelding && searchStarted) || !searchString;

type FormValues = {
  skalReservere: boolean;
  searchString: string;
};

interface OwnProps {
  onSubmit: (values: { searchString: string, skalReservere: boolean }) => void;
  searchStarted: boolean;
  searchResultAccessDenied?: {
    feilmelding?: string;
  };
  resetSearch: () => void;
}

/**
 * SearchForm
 *
 * Presentasjonskomponent. Definerer søkefelt og tilhørende søkeknapp.
 */
const SearchForm: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  onSubmit,
  searchResultAccessDenied,
  searchStarted,
  resetSearch,
}) => {
  const { kanSaksbehandle } = restApiHooks.useGlobalStateRestApiData(RestApiGlobalStatePathsKeys.NAV_ANSATT);
  const formMethods = useForm<FormValues>();

  const searchStringValue = formMethods.watch('searchString');

  return (
    <Form<FormValues> onSubmit={onSubmit} formMethods={formMethods}>
      <div className={styles.container}>
        <Undertittel>{intl.formatMessage({ id: 'Search.SearchFagsakOrPerson' })}</Undertittel>
        {kanSaksbehandle && (
        <>
          <VerticalSpacer sixteenPx />
          <CheckboxField name="skalReservere" label={intl.formatMessage({ id: 'Search.ReserverBehandling' })} onClick={resetSearch} />
        </>
        )}
        <VerticalSpacer eightPx />
        <FlexContainer>
          <FlexRow>
            <FlexColumn>
              <InputField
                name="searchString"
                label={intl.formatMessage({ id: 'Search.SaksnummerOrPersonId' })}
                bredde="L"
                validate={[hasValidSaksnummerOrFodselsnummerFormat(intl)]}
              />
            </FlexColumn>
            <FlexColumn>
              <Knapp
                mini
                htmlType="submit"
                className={styles.button}
                spinner={!searchResultAccessDenied?.feilmelding && searchStarted}
                disabled={isButtonDisabled(searchStringValue, searchStarted, searchResultAccessDenied)}
              >
                <FormattedMessage id="Search.Search" />
              </Knapp>
            </FlexColumn>
          </FlexRow>
          {searchResultAccessDenied?.feilmelding && (
            <>
              <VerticalSpacer eightPx />
              <FlexRow>
                <FlexColumn>
                  <Image className={styles.advarselIcon} src={advarselIcon} />
                </FlexColumn>
                <FlexColumn>
                  <FormattedMessage id={searchResultAccessDenied.feilmelding} />
                </FlexColumn>
              </FlexRow>
            </>
          )}
        </FlexContainer>
      </div>
    </Form>
  );
};

export default injectIntl(SearchForm);
