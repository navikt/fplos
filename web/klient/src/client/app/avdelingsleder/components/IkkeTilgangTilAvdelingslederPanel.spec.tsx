import React from 'react';
import { render, screen } from '@testing-library/react';
import LanguageProvider from 'app/LanguageProvider';

import IkkeTilgangTilAvdelingslederPanel from './IkkeTilgangTilAvdelingslederPanel';

describe('<IkkeTilgangTilAvdelingslederPanel>', () => {
  it('skal vise side for ikke tilgang til avdelingsleder', async () => {
    render(
      <LanguageProvider>
        <IkkeTilgangTilAvdelingslederPanel />
      </LanguageProvider>,
    );
    expect(await screen.findByText('Du har ikke tilgang til å bruke dette programmet')).toBeInTheDocument();
  });
});
