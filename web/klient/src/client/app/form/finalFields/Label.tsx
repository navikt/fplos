import React, { Component, ReactNode, ReactType } from 'react';
import classnames from 'classnames/bind';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import styles from './label.less';

const classNames = classnames.bind(styles);

export type LabelType = string | ReactNode | {
  id: string;
  args?: any;
};

interface OwnProps {
  input: LabelType;
  typographyElement?: ReactType;
  readOnly?: boolean;
}

export class Label extends Component<OwnProps & WrappedComponentProps> {
  static defaultProps = {
    typographyElement: Undertekst,
    readOnly: false,
  };

  constructor(props) {
    super(props);
    this.format = this.format.bind(this);
  }

  format(label) {
    if (label && label.id) {
      const { intl } = this.props;
      return intl.formatMessage({ id: label.id }, label.args);
    }
    return label;
  }

  render() {
    const { input, readOnly, typographyElement: TypoElem } = this.props;
    if (!input) {
      return null;
    }
    return <span className={classNames('labelWrapper', { readOnly })}><TypoElem tag="span" className={styles.label}>{this.format(input)}</TypoElem></span>;
  }
}

export default injectIntl(Label);
