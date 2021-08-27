import React from 'react';
import { render, screen } from '@testing-library/react';
import LanguageProvider from 'app/LanguageProvider';

import IkkeTilgangTilKode6AvdelingPanel from './IkkeTilgangTilKode6AvdelingPanel';

describe('<IkkeTilgangTilKode6AvdelingPanel>', () => {
  it('skal vise side for ikke tilgang grunnet kode 6', async () => {
    render(
      <LanguageProvider>
        <IkkeTilgangTilKode6AvdelingPanel />
      </LanguageProvider>,
    );
    expect(await screen.findByText('Du har ikke tilgang til denne avdelingen')).toBeInTheDocument();
  });
});
