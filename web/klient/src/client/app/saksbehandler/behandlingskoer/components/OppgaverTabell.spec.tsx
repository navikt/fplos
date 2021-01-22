import React, { ReactElement } from 'react';
import { expect } from 'chai';
import { FormattedMessage, IntlShape } from 'react-intl';
import sinon from 'sinon';
import NavFrontendChevron from 'nav-frontend-chevron';

import { requestApi, RestApiPathsKeys } from 'data/fplosRestApi';
import { shallowWithIntl, intlMock } from 'testHelpers/intl-enzyme-test-helper';
import behandlingType from 'kodeverk/behandlingType';
import behandlingStatus from 'kodeverk/behandlingStatus';
import fagsakYtelseType from 'kodeverk/fagsakYtelseType';
import DateLabel from 'sharedComponents/DateLabel';
import TableRow from 'sharedComponents/table/TableRow';
import TableColumn from 'sharedComponents/table/TableColumn';
import Image from 'sharedComponents/Image';
import { OppgaverTabell } from './OppgaverTabell';

describe('<OppgaverTabell>', () => {
  const intl: Partial<IntlShape> = {
    ...intlMock,
  };

  it.skip('skal vise kriterievelger og liste over neste oppgaver', () => {
    const oppgaverTilBehandling = [{
      id: 1,
      status: {
        erReservert: false,
      },
      saksnummer: 1,
      behandlingId: '2',
      personnummer: '123456789',
      navn: 'Espen Utvikler',
      system: 'FPSAK',
      behandlingstype: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      opprettetTidspunkt: '2019-01-02',
      behandlingsfrist: '2019-03-03',
      erTilSaksbehandling: true,
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'FP',
      },
      behandlingStatus: {
        kode: behandlingStatus.OPPRETTET,
        navn: '',
      },
      href: '',
    }, {
      id: 2,
      status: {
        erReservert: false,
      },
      saksnummer: 2,
      behandlingId: '2',
      personnummer: '657643535',
      navn: 'Espen Solstråle',
      system: 'FPSAK',
      behandlingstype: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad far',
      },
      opprettetTidspunkt: '2018-01-02',
      behandlingsfrist: '2018-03-03',
      erTilSaksbehandling: true,
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'FP',
      },
      behandlingStatus: {
        kode: behandlingStatus.OPPRETTET,
        navn: '',
      },
      href: '',
    }];

    requestApi.mock(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, undefined);
    requestApi.mock(RestApiPathsKeys.RESERVERTE_OPPGAVER, []);
    requestApi.mock(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, oppgaverTilBehandling);
    const wrapper = shallowWithIntl(<OppgaverTabell
      intl={intl as IntlShape}
      reserverOppgave={sinon.spy()}
      valgtSakslisteId={1}
    />);

    const tableRows = wrapper.find(TableRow);
    expect(tableRows).has.length(2);

    const columnsRow1 = tableRows.first().find(TableColumn);
    expect(columnsRow1.first().childAt(0).text()).is.eql('Espen Utvikler 123456789');
    expect(columnsRow1.at(1).childAt(0).text()).is.eql('Førstegangssøknad');
    expect(columnsRow1.at(2).find(DateLabel).prop('dateString')).is.eql('2019-01-02');
    expect(columnsRow1.at(3).find(DateLabel).prop('dateString')).is.eql('2019-03-03');

    const columnsRow2 = tableRows.last().find(TableColumn);
    expect(columnsRow2.first().childAt(0).text()).is.eql('Espen Solstråle 657643535');
    expect(columnsRow2.at(1).childAt(0).text()).is.eql('Førstegangssøknad far');
    expect(columnsRow2.at(2).find(DateLabel).prop('dateString')).is.eql('2018-01-02');
    expect(columnsRow2.at(3).find(DateLabel).prop('dateString')).is.eql('2018-03-03');

    const message = wrapper.find(FormattedMessage);
    expect(message).has.length(1);
    expect(message.prop('id')).is.eql('OppgaverTabell.DineNesteSaker');
  });

  it.skip('skal vise de behandlingene som fremdeles er valgt av saksbehandler først i listen samt et menyikon for disse', () => {
    const oppgaverTilBehandling = [{
      id: 1,
      status: {
        erReservert: false,
      },
      saksnummer: 1,
      behandlingId: '2',
      personnummer: '123456789',
      navn: 'Espen Utvikler',
      system: 'FPSAK',
      behandlingstype: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad',
      },
      opprettetTidspunkt: '2019-01-02',
      behandlingsfrist: '2019-03-03',
      erTilSaksbehandling: true,
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'FP',
      },
      behandlingStatus: {
        kode: behandlingStatus.OPPRETTET,
        navn: '',
      },
      href: '',
    }];
    const reserverteOppgaver = [{
      id: 2,
      status: {
        erReservert: true,
      },
      saksnummer: 2,
      behandlingId: '2',
      personnummer: '657643535',
      navn: 'Espen Solstråle',
      system: 'FPSAK',
      behandlingstype: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad far',
      },
      opprettetTidspunkt: '2018-01-02',
      behandlingsfrist: '2018-03-03',
      erTilSaksbehandling: true,
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'FP',
      },
      behandlingStatus: {
        kode: behandlingStatus.OPPRETTET,
        navn: '',
      },
      href: '',
    }];

    requestApi.mock(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, undefined);
    requestApi.mock(RestApiPathsKeys.RESERVERTE_OPPGAVER, reserverteOppgaver);
    requestApi.mock(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, oppgaverTilBehandling);

    const wrapper = shallowWithIntl(<OppgaverTabell
      intl={intl as IntlShape}
      reserverOppgave={sinon.spy()}
      valgtSakslisteId={1}
    />);

    const tableRows = wrapper.find(TableRow);
    expect(tableRows).has.length(2);

    const columnsRow1 = tableRows.first().find(TableColumn);
    expect(columnsRow1.first().childAt(0).text()).is.eql('Espen Solstråle 657643535');
    expect(columnsRow1.at(1).childAt(0).text()).is.eql('Førstegangssøknad far');
    expect(columnsRow1.at(2).find(DateLabel).prop('dateString')).is.eql('2018-01-02');
    expect(columnsRow1.at(3).find(DateLabel).prop('dateString')).is.eql('2018-03-03');
    expect(columnsRow1.at(4).find(Image)).has.length(0);
    expect(columnsRow1.at(5).find(Image)).has.length(1);

    const columnsRow2 = tableRows.last().find(TableColumn);
    expect(columnsRow2.first().childAt(0).text()).is.eql('Espen Utvikler 123456789');
    expect(columnsRow2.at(1).childAt(0).text()).is.eql('Førstegangssøknad');
    expect(columnsRow2.at(2).find(DateLabel).prop('dateString')).is.eql('2019-01-02');
    expect(columnsRow2.at(3).find(DateLabel).prop('dateString')).is.eql('2019-03-03');
    expect(columnsRow2.at(4).find(Image)).has.length(0);
    expect(columnsRow2.at(5).find(NavFrontendChevron)).has.length(1);
  });

  it('skal ikke vise liste når en ikke har oppgaver', () => {
    const wrapper = shallowWithIntl(<OppgaverTabell
      intl={intl as IntlShape}
      reserverOppgave={sinon.spy()}
      valgtSakslisteId={1}
    />);

    const message = wrapper.find(FormattedMessage);
    expect(message).has.length(2);
    expect(message.first().prop('id')).is.eql('OppgaverTabell.DineNesteSaker');
    expect(message.last().prop('id')).is.eql('OppgaverTabell.IngenOppgaver');

    expect(wrapper.find(TableRow)).has.length(0);
  });

  it.skip('skal vise tooltip for reserverte oppgaver som er flyttet', () => {
    const reserverteOppgaver = [{
      id: 2,
      status: {
        erReservert: true,
        flyttetReservasjon: {
          tidspunkt: '2018-01-02',
          uid: '1234556',
          navn: 'Auto Joachim',
          begrunnelse: 'Har flytta til deg',
        },
      },
      saksnummer: 2,
      behandlingId: '2',
      personnummer: '657643535',
      navn: 'Espen Solstråle',
      system: 'FPSAK',
      behandlingstype: {
        kode: behandlingType.FORSTEGANGSSOKNAD,
        navn: 'Førstegangssøknad far',
      },
      opprettetTidspunkt: '2018-01-02',
      behandlingsfrist: '2018-03-03',
      erTilSaksbehandling: true,
      fagsakYtelseType: {
        kode: fagsakYtelseType.FORELDREPRENGER,
        navn: 'FP',
      },
      behandlingStatus: {
        kode: behandlingStatus.OPPRETTET,
        navn: '',
      },
      href: '',
    }];

    requestApi.mock(RestApiPathsKeys.FORLENG_OPPGAVERESERVASJON, undefined);
    requestApi.mock(RestApiPathsKeys.RESERVERTE_OPPGAVER, reserverteOppgaver);
    requestApi.mock(RestApiPathsKeys.OPPGAVER_TIL_BEHANDLING, []);

    const wrapper = shallowWithIntl(<OppgaverTabell
      intl={intl as IntlShape}
      reserverOppgave={sinon.spy()}
      valgtSakslisteId={1}
    />);

    const tableRows = wrapper.find(TableRow);
    expect(tableRows).has.length(1);

    const columnsRow1 = tableRows.first().find(TableColumn);
    expect(columnsRow1.first().childAt(0).text()).is.eql('Espen Solstråle 657643535');
    expect(columnsRow1.at(1).childAt(0).text()).is.eql('Førstegangssøknad far');
    expect(columnsRow1.at(2).find(DateLabel).prop('dateString')).is.eql('2018-01-02');
    expect(columnsRow1.at(3).find(DateLabel).prop('dateString')).is.eql('2018-03-03');
    expect(columnsRow1.at(4).find(Image)).has.length(1);
    expect(columnsRow1.at(5).find(Image)).has.length(1);

    const tooltip = shallowWithIntl(columnsRow1.at(4).find(Image).prop('tooltip') as ReactElement);
    const values = tooltip.find(FormattedMessage).prop('values') as { dato: string; tid: string; uid: string; navn: string; beskrivelse: string};

    expect(values.dato).is.eql('02.01.2018');
    expect(values.tid).is.eql('00:00');
    expect(values.uid).is.eql('1234556');
    expect(values.navn).is.eql('Auto Joachim');
    expect(values.beskrivelse).is.eql('Har flytta til deg');
  });
});
