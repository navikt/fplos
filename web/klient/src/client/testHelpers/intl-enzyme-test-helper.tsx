/**
 * https://github.com/yahoo/react-intl/wiki/Testing-with-React-Intl
 *
 * Components using the react-intl module require access to the intl context.
 * This is not available when mounting single components in Enzyme.
 * These helper functions aim to address that.
 */

import React, { ReactElement } from 'react';
import { createIntl, createIntlCache, IntlProvider } from 'react-intl';
import { mount, shallow, ShallowRendererProps } from 'enzyme';
// You can pass your messages to the IntlProvider. Optional: remove if unneeded.
import messages from '../app/sprak/nb_NO.json';

// Create the IntlProvider to retrieve context for wrapping around.
const cache = createIntlCache();

const getIntlObject = (moduleMessages?: any) => {
  const selectedMessages = moduleMessages || messages;

  return createIntl({
    locale: 'nb-NO',
    defaultLocale: 'nb-NO',
    messages: selectedMessages,
  }, cache);
};

/**
 * When using React-Intl `injectIntl` on components, props.intl is required.
 */
function nodeWithIntlProp(node: ReactElement, moduleMessages?: any): ReactElement {
  const selectedMessages = moduleMessages || messages;
  return React.cloneElement(node, { intl: getIntlObject(selectedMessages) });
}

const getOptions = (moduleMessages?: any): ShallowRendererProps => {
  const selectedMessages = moduleMessages || messages;

  return {
    wrappingComponent: IntlProvider,
    wrappingComponentProps: {
      locale: 'nb-NO',
      defaultLocale: 'nb-NO',
      messages: selectedMessages,
    },
  };
};

export function shallowWithIntl(node: ReactElement) {
  return shallow(nodeWithIntlProp(node, undefined), { ...getOptions(undefined) });
}

export function mountWithIntl(node: ReactElement) {
  return mount(nodeWithIntlProp(node, undefined), { ...getOptions(undefined) });
}

/* Lagt til for a hindre warnings i tester */
export const intlMock = getIntlObject(messages);
