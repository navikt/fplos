import React, { ReactNode, FunctionComponent } from 'react';
import { IntlProvider } from 'react-intl';

import data from '../sprak/nb_NO.json';

interface OwnProps {
  nbMessages: {[key: string]: string};
  children: ReactNode;
}

/**
 * LanguageProvider
 *
 * Container komponent. Har ansvar for å hente språkfilen.
 */
const LanguageProvider: FunctionComponent<OwnProps> = ({
  children,
}) => (
  <IntlProvider locale="nb-NO" messages={data}>
    {children}
  </IntlProvider>
);

export default LanguageProvider;
