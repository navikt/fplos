import React from 'react';
import { render, screen } from '@testing-library/react';
import { FormattedMessage } from 'react-intl';

import LanguageProvider from './LanguageProvider';

describe('<AppIndex>', () => {
  it('skal vise bruke tekst fra tekstfil', async () => {
    render(
      <LanguageProvider>
        <FormattedMessage id="Header.Foreldrepenger" tagName="span" />
      </LanguageProvider>,
    );

    expect(await screen.findByText('Svangerskap, fødsel og adopsjon')).toBeInTheDocument();
  });
});
