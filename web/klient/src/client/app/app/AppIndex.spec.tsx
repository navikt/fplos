import React from 'react';
import { render, screen } from '@testing-library/react';
import { createBrowserHistory, Location, History } from 'history';
import { Router, match } from 'react-router-dom';
import { AppIndex } from './AppIndex';

const LocationMock = {
  search: '',
  state: {},
} as Location;

const history = createBrowserHistory<any>({
  basename: '/',
});

describe('<AppIndex>', () => {
  it('skal vise hjem-skjermbildet', async () => {
    render(
      <Router history={history}>
        <AppIndex
          location={LocationMock}
          history={{} as History}
          match={{} as match}
        />
      </Router>,
    );

    expect(await screen.findByText('Svangerskap, fødsel og adopsjon')).toBeInTheDocument();
  });

  it('skal vise hjem-skjermbildet med feilmelding som kommer via url', async () => {
    const location = {
      search: '?errormessage=Det+finnes+ingen+sak+med+denne+referansen%3A+266',
      state: {},
    } as Location;

    render(
      <Router history={history}>
        <AppIndex
          location={location}
          history={{} as History}
          match={{} as match}
        />
      </Router>,
    );

    expect(await screen.findByText('Det finnes ingen sak med denne referansen: 266')).toBeInTheDocument();
  });
});
