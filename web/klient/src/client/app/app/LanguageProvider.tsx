import React, { ReactNode, FunctionComponent } from 'react';
import { connect } from 'react-redux';
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
export const LanguageProvider: FunctionComponent<OwnProps> = ({
  nbMessages,
  children,
}) => (
  <IntlProvider locale="nb-NO" messages={nbMessages}>
    {children}
  </IntlProvider>
);

const mapStateToProps = () => ({
  nbMessages: data,
});

export default connect(mapStateToProps)(LanguageProvider);
