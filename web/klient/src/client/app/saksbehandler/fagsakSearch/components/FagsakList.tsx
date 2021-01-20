import React, { Fragment, FunctionComponent, useMemo } from 'react';
import NavFrontendChevron from 'nav-frontend-chevron';

import Oppgave from 'saksbehandler/oppgaveTsType';
import kodeverkTyper from 'kodeverk/kodeverkTyper';
import Table from 'sharedComponents/table/Table';
import TableRow from 'sharedComponents/table/TableRow';
import TableColumn from 'sharedComponents/table/TableColumn';
import DateLabel from 'sharedComponents/DateLabel';
import fagsakStatus from 'kodeverk/fagsakStatus';
import useKodeverk from 'data/useKodeverk';
import Fagsak from '../fagsakTsType';

import styles from './fagsakList.less';

const headerTextCodes = [
  'FagsakList.Saksnummer',
  'FagsakList.Stonadstype',
  'FagsakList.Behandlingstype',
  'FagsakList.Status',
  'FagsakList.BarnFodt',
  'EMPTY_1',
];

interface OwnProps {
  fagsaker: Fagsak[];
  fagsakOppgaver: Oppgave[];
  selectFagsakCallback: (saksnummer: number) => void;
  selectOppgaveCallback: (oppgave: Oppgave) => void;
}

const getSelectOppgaveCallback = (oppgave, selectOppgaveCallback) => () => selectOppgaveCallback(oppgave);

const getFagsakCallback = (selectFagsakCallback) => (event: any, saksnummer: number) => selectFagsakCallback(saksnummer);

export const getSorterteFagsaker = (fagsaker: Fagsak[] = []) => fagsaker.concat().sort((fagsak1, fagsak2) => {
  if (fagsak1.status.kode === fagsakStatus.AVSLUTTET && fagsak2.status.kode !== fagsakStatus.AVSLUTTET) {
    return 1;
  } if (fagsak1.status.kode !== fagsakStatus.AVSLUTTET && fagsak2.status.kode === fagsakStatus.AVSLUTTET) {
    return -1;
  }
  const changeTimeFagsak1 = fagsak1.endret ? fagsak1.endret : fagsak1.opprettet;
  const changeTimeFagsak2 = fagsak2.endret ? fagsak2.endret : fagsak2.opprettet;
  return changeTimeFagsak1 > changeTimeFagsak2 ? 1 : -1;
});

/**
 * FagsakList
 *
 * Presentasjonskomponent. Formaterer fagsak-søkeresultatet for visning i tabell. Sortering av fagsakene blir håndtert her.
 */
const FagsakList: FunctionComponent<OwnProps> = ({
  fagsaker,
  fagsakOppgaver,
  selectFagsakCallback,
  selectOppgaveCallback,
}) => {
  const fagsakStatuser = useKodeverk(kodeverkTyper.FAGSAK_STATUS);
  const fagsakYtelseTyper = useKodeverk(kodeverkTyper.FAGSAK_YTELSE_TYPE);

  const sorterteFagsaker = useMemo(() => getSorterteFagsaker(fagsaker), [fagsaker]);

  return (
    <Table headerTextCodes={headerTextCodes} classNameTable={styles.table}>
      {sorterteFagsaker.map((fagsak) => {
        const fagsakStatusType = fagsakStatuser.find((type) => type.kode === fagsak.status.kode);
        const fagsakYtelseType = fagsakYtelseTyper.find((type) => type.kode === fagsak.sakstype.kode);

        const filtrerteOppgaver = fagsakOppgaver.filter((o) => o.saksnummer === fagsak.saksnummer);
        const oppgaver = filtrerteOppgaver.map((oppgave, index) => (
          <TableRow
            key={`oppgave${oppgave.id}`}
            id={oppgave.id}
            onMouseDown={getSelectOppgaveCallback(oppgave, selectOppgaveCallback)}
            onKeyDown={getSelectOppgaveCallback(oppgave, selectOppgaveCallback)}
            isDashedBottomBorder={filtrerteOppgaver.length > index + 1}
          >
            <TableColumn />
            <TableColumn>{oppgave.fagsakYtelseType.navn}</TableColumn>
            <TableColumn>{oppgave.behandlingstype.navn}</TableColumn>
            <TableColumn>{oppgave.behandlingStatus ? oppgave.behandlingStatus.navn : ''}</TableColumn>
            <TableColumn>{fagsak.barnFodt ? <DateLabel dateString={fagsak.barnFodt} /> : null}</TableColumn>
            <TableColumn><NavFrontendChevron /></TableColumn>
          </TableRow>
        ));

        return (
          <Fragment key={`fagsak${fagsak.saksnummer}`}>
            <TableRow
              id={fagsak.saksnummer}
              onMouseDown={getFagsakCallback(selectFagsakCallback)}
              onKeyDown={getFagsakCallback(selectFagsakCallback)}
              isDashedBottomBorder={oppgaver.length > 0}
            >
              <TableColumn>{fagsak.saksnummer}</TableColumn>
              <TableColumn>{fagsakYtelseType ? fagsakYtelseType.navn : ''}</TableColumn>
              <TableColumn />
              <TableColumn>{fagsakStatusType ? fagsakStatusType.navn : ''}</TableColumn>
              <TableColumn>{fagsak.barnFodt ? <DateLabel dateString={fagsak.barnFodt} /> : null}</TableColumn>
              <TableColumn><NavFrontendChevron /></TableColumn>
            </TableRow>
            {oppgaver.length > 0 && oppgaver}
          </Fragment>
        );
      })}
    </Table>
  );
};

export default FagsakList;
