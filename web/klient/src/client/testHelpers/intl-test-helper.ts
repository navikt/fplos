import { createIntl, createIntlCache } from 'react-intl';
import messages from '../app/sprak/nb_NO.json';

const cache = createIntlCache();

const getIntlObject = (moduleMessages?: any) => {
  const selectedMessages = moduleMessages || messages;

  return createIntl({
    locale: 'nb-NO',
    defaultLocale: 'nb-NO',
    messages: selectedMessages,
  }, cache);
};

const intlMock = getIntlObject(messages);

export default intlMock;
