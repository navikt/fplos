
import React, { Component } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import { createSelector } from 'reselect';

import { Form } from 'react-final-form';
import { FormattedMessage } from 'react-intl';
import Panel from 'nav-frontend-paneler';
import { Element } from 'nav-frontend-typografi';
import { Row, Column } from 'nav-frontend-grid';

import { getValgtAvdelingEnhet } from 'app/duck';
import VerticalSpacer from 'sharedComponents/VerticalSpacer';
import { CheckboxField } from 'form/FinalFields';
import { getAvdelingensSaksbehandlere } from 'avdelingsleder/saksbehandlere/duck';
import { Saksbehandler } from 'avdelingsleder/saksbehandlere/saksbehandlerTsType';
import saksbehandlerPropType from 'avdelingsleder/saksbehandlere/saksbehandlerPropType';
import { Saksliste } from '../../sakslisteTsType';
import sakslistePropType from '../../sakslistePropType';

import styles from './saksbehandlereForSakslisteForm.less';

interface TsProps {
  valgtSaksliste: Saksliste;
  avdelingensSaksbehandlere: Saksbehandler[];
  knyttSaksbehandlerTilSaksliste: (sakslisteId: number, brukerIdent: string, isChecked: boolean, avdelingEnhet: string) => void;
  valgtAvdelingEnhet: string;
}

/**
 * SaksbehandlereForSakslisteForm
 */
export class SaksbehandlereForSakslisteForm extends Component<TsProps> {
  static propTypes = {
    valgtSaksliste: sakslistePropType.isRequired,
    avdelingensSaksbehandlere: PropTypes.arrayOf(saksbehandlerPropType),
    knyttSaksbehandlerTilSaksliste: PropTypes.func.isRequired,
    valgtAvdelingEnhet: PropTypes.string.isRequired,
  };

  static defaultProps = {
    avdelingensSaksbehandlere: [],
  }

  buildInitialValues = () => {
    const {
      valgtSaksliste,
    } = this.props;

    const identer = valgtSaksliste.saksbehandlerIdenter.reduce((acc, brukerIdent) => ({ ...acc, [brukerIdent]: true }), {});
    return {
      ...identer,
    };
  }

  render = () => {
    const {
      avdelingensSaksbehandlere, knyttSaksbehandlerTilSaksliste, valgtSaksliste, valgtAvdelingEnhet,
    } = this.props;

    const pos = Math.ceil(avdelingensSaksbehandlere.length / 2);
    const avdelingensSaksbehandlereVenstreListe = avdelingensSaksbehandlere.slice(0, pos);
    const avdelingensSaksbehandlereHoyreListe = avdelingensSaksbehandlere.slice(pos);

    return (
      <Form
        onSubmit={() => undefined}
        initialValues={this.buildInitialValues()}
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
                {avdelingensSaksbehandlereVenstreListe.map(s => (
                  <CheckboxField
                    key={s.brukerIdent}
                    name={s.brukerIdent}
                    label={s.navn}
                    onChange={isChecked => knyttSaksbehandlerTilSaksliste(valgtSaksliste.sakslisteId, s.brukerIdent, isChecked, valgtAvdelingEnhet)}
                  />
                ))}
              </Column>
              <Column xs="6">
                {avdelingensSaksbehandlereHoyreListe.map(s => (
                  <CheckboxField
                    key={s.brukerIdent}
                    name={s.brukerIdent}
                    label={s.navn}
                    onChange={isChecked => knyttSaksbehandlerTilSaksliste(valgtSaksliste.sakslisteId, s.brukerIdent, isChecked, valgtAvdelingEnhet)}
                  />
                ))}
              </Column>
            </Row>
            )}
          </Panel>
        )}
      />
    );
  }
}

const sortSaksbehandlere = createSelector([getAvdelingensSaksbehandlere], saksbehandlere => (saksbehandlere && saksbehandlere instanceof Array
  ? saksbehandlere.sort((saksbehandler1, saksbehandler2) => saksbehandler1.navn.localeCompare(saksbehandler2.navn))
  : saksbehandlere));

const mapStateToProps = state => ({
  valgtAvdelingEnhet: getValgtAvdelingEnhet(state),
  avdelingensSaksbehandlere: sortSaksbehandlere(state),
});

export default connect(mapStateToProps)(SaksbehandlereForSakslisteForm);
