import React, { FunctionComponent, useMemo } from 'react';
import { Form } from 'react-final-form';
import { FormattedMessage } from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Element } from 'nav-frontend-typografi';
import { Row, Column } from 'nav-frontend-grid';

import { restApiHooks, RestApiPathsKeys } from 'data/fplosRestApi';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { CheckboxField } from 'form/FinalFields';
import Saksbehandler from 'types/avdelingsleder/saksbehandlerTsType';
import Saksliste from 'types/avdelingsleder/sakslisteTsType';

import styles from './saksbehandlereForSakslisteForm.less';

const sortSaksbehandlere = (saksbehandlere: Saksbehandler[]) => (saksbehandlere && saksbehandlere instanceof Array
  ? saksbehandlere.sort((saksbehandler1, saksbehandler2) => saksbehandler1.navn.localeCompare(saksbehandler2.navn))
  : saksbehandlere);

interface OwnProps {
  valgtSaksliste: Saksliste;
  avdelingensSaksbehandlere: Saksbehandler[];
  valgtAvdelingEnhet: string;
  hentAvdelingensSakslister: (params: {avdelingEnhet: string}) => void;
}

/**
 * SaksbehandlereForSakslisteForm
 */
const SaksbehandlereForSakslisteForm: FunctionComponent<OwnProps> = ({
  avdelingensSaksbehandlere = [],
  valgtSaksliste,
  valgtAvdelingEnhet,
  hentAvdelingensSakslister,
}) => {
  const sorterteAvdelingensSaksbehandlere = useMemo(() => sortSaksbehandlere(avdelingensSaksbehandlere), [avdelingensSaksbehandlere]);
  const pos = Math.ceil(sorterteAvdelingensSaksbehandlere.length / 2);
  const avdelingensSaksbehandlereVenstreListe = sorterteAvdelingensSaksbehandlere.slice(0, pos);
  const avdelingensSaksbehandlereHoyreListe = sorterteAvdelingensSaksbehandlere.slice(pos);

  const { startRequest: knyttSaksbehandlerTilSaksliste } = restApiHooks.useRestApiRunner(RestApiPathsKeys.LAGRE_SAKSLISTE_SAKSBEHANDLER);

  return (
    <Form
      onSubmit={() => undefined}
      initialValues={{
        ...valgtSaksliste.saksbehandlerIdenter.reduce((acc, brukerIdent) => ({ ...acc, [brukerIdent]: true }), {}),
      }}
      render={() => (
        <Panel className={styles.panel}>
          <Element>
            <FormattedMessage id="SaksbehandlereForSakslisteForm.Saksbehandlere" />
          </Element>
          <VerticalSpacer sixteenPx />
          {sorterteAvdelingensSaksbehandlere.length === 0 && (
            <FormattedMessage id="SaksbehandlereForSakslisteForm.IngenSaksbehandlere" />
          )}
          {sorterteAvdelingensSaksbehandlere.length > 0 && (
          <Row>
            <Column xs="6">
              {avdelingensSaksbehandlereVenstreListe.map((s) => (
                <React.Fragment key={s.brukerIdent}>
                  <CheckboxField
                    name={s.brukerIdent}
                    label={s.navn}
                    onChange={(isChecked) => knyttSaksbehandlerTilSaksliste({
                      sakslisteId: valgtSaksliste.sakslisteId,
                      brukerIdent: s.brukerIdent,
                      checked: isChecked,
                      avdelingEnhet: valgtAvdelingEnhet,
                    }).then(() => hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet }))}
                  />
                  <VerticalSpacer fourPx />
                </React.Fragment>
              ))}
            </Column>
            <Column xs="6">
              {avdelingensSaksbehandlereHoyreListe.map((s) => (
                <React.Fragment key={s.brukerIdent}>
                  <CheckboxField
                    name={s.brukerIdent}
                    label={s.navn}
                    onChange={(isChecked) => knyttSaksbehandlerTilSaksliste({
                      sakslisteId: valgtSaksliste.sakslisteId,
                      brukerIdent: s.brukerIdent,
                      checked: isChecked,
                      avdelingEnhet: valgtAvdelingEnhet,
                    }).then(() => hentAvdelingensSakslister({ avdelingEnhet: valgtAvdelingEnhet }))}
                  />
                  <VerticalSpacer fourPx />
                </React.Fragment>
              ))}
            </Column>
          </Row>
          )}
        </Panel>
      )}
    />
  );
};

export default SaksbehandlereForSakslisteForm;
