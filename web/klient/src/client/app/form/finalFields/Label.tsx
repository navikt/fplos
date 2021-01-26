import React, { Component, ReactElement, ElementType } from 'react';
import classnames from 'classnames/bind';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import { Undertekst } from 'nav-frontend-typografi';

import styles from './label.less';

const classNames = classnames.bind(styles);

type Message = {
  id: string;
  args?: any;
}

export type LabelType = string | ReactElement | Message;

interface OwnProps {
  input: LabelType;
  typographyElement?: ElementType;
  readOnly?: boolean;
}

export class Label extends Component<OwnProps & WrappedComponentProps> {
  static defaultProps = {
    typographyElement: Undertekst,
    readOnly: false,
  };

  constructor(props: OwnProps & WrappedComponentProps) {
    super(props);
    this.format = this.format.bind(this);
  }

  format(label: LabelType): string | ReactElement {
    const labelAsMessage = label as Message;
    const labelAsReactNodeOrString = label as ReactElement | string;
    if (label && labelAsMessage.id) {
      const { intl } = this.props;
      return intl.formatMessage({ id: labelAsMessage.id }, labelAsMessage.args);
    }
    return labelAsReactNodeOrString;
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
