
import React, { FunctionComponent } from 'react';
import { connect } from 'react-redux';
import { createSelector } from 'reselect';

import { Form } from 'react-final-form';
import { FormattedMessage } from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Element } from 'nav-frontend-typografi';
import { Row, Column } from 'nav-frontend-grid';

import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { CheckboxField } from 'form/FinalFields';
import { getAvdelingensSaksbehandlere } from 'avdelingsleder/saksbehandlere/duck';
import Saksbehandler from 'avdelingsleder/saksbehandlere/saksbehandlerTsType';
import Saksliste from '../../sakslisteTsType';

import styles from './saksbehandlereForSakslisteForm.less';

interface OwnProps {
  valgtSaksliste: Saksliste;
  avdelingensSaksbehandlere: Saksbehandler[];
  knyttSaksbehandlerTilSaksliste: (sakslisteId: number, brukerIdent: string, isChecked: boolean, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
}

/**
 * SaksbehandlereForSakslisteForm
 */
export const SaksbehandlereForSakslisteForm: FunctionComponent<OwnProps> = ({
  avdelingensSaksbehandlere = [],
  knyttSaksbehandlerTilSaksliste,
  valgtSaksliste,
  valgtAvdelingEnhet,
}) => {
  const pos = Math.ceil(avdelingensSaksbehandlere.length / 2);
  const avdelingensSaksbehandlereVenstreListe = avdelingensSaksbehandlere.slice(0, pos);
  const avdelingensSaksbehandlereHoyreListe = avdelingensSaksbehandlere.slice(pos);

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
          {avdelingensSaksbehandlere.length === 0 && (
            <FormattedMessage id="SaksbehandlereForSakslisteForm.IngenSaksbehandlere" />
          )}
          {avdelingensSaksbehandlere.length > 0 && (
          <Row>
            <Column xs="6">
              {avdelingensSaksbehandlereVenstreListe.map((s) => (
                <React.Fragment key={s.brukerIdent}>
                  <CheckboxField
                    name={s.brukerIdent}
                    label={s.navn}
                    onChange={(isChecked) => knyttSaksbehandlerTilSaksliste(valgtSaksliste.sakslisteId, s.brukerIdent, isChecked, valgtAvdelingEnhet)}
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
                    onChange={(isChecked) => knyttSaksbehandlerTilSaksliste(valgtSaksliste.sakslisteId, s.brukerIdent, isChecked, valgtAvdelingEnhet)}
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

const sortSaksbehandlere = createSelector([getAvdelingensSaksbehandlere], (saksbehandlere) => (saksbehandlere && saksbehandlere instanceof Array
  ? saksbehandlere.sort((saksbehandler1, saksbehandler2) => saksbehandler1.navn.localeCompare(saksbehandler2.navn))
  : saksbehandlere));

const mapStateToProps = (state) => ({
  avdelingensSaksbehandlere: sortSaksbehandlere(state),
});

export default connect(mapStateToProps)(SaksbehandlereForSakslisteForm);
