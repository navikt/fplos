import React, { Component } from 'react';
import { Element } from 'nav-frontend-typografi';
import { FormattedMessage } from 'react-intl';

interface TsProps {
}

interface StateTsProps {
}

export class ReservasjonerTabell extends Component<TsProps, StateTsProps> {
  render = () => {
    const {
    } = this.props;
    const {
    } = this.state;
    return (
      <>
        <Element><FormattedMessage id="ReservsajonerTabell.Reservasjoner" /></Element>
      </>
    );
  }
}
