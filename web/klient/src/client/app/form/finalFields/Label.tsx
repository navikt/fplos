import React, { ReactElement, ElementType, FunctionComponent, useMemo } from 'react';
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
  input?: LabelType;
  typographyElement?: ElementType;
  readOnly?: boolean;
}

const Label: FunctionComponent<OwnProps & WrappedComponentProps> = ({
  intl,
  input,
  readOnly = false,
  typographyElement: TypoElem = Undertekst,
}) => {
  const formattedInput = useMemo(() => (): string | ReactElement | null => {
    if (!input) {
      return null;
    }

    const labelAsMessage = input as Message;
    const labelAsReactNodeOrString = input as ReactElement | string;
    if (input && labelAsMessage.id) {
      return intl.formatMessage({ id: labelAsMessage.id }, labelAsMessage.args);
    }
    return labelAsReactNodeOrString;
  }, [input])

  if (!input) {
    return null;
  }

  return (
    <span className={classNames('labelWrapper', { readOnly })}>
      <TypoElem tag="span" className={styles.label}>{formattedInput}</TypoElem>
    </span>
  );
} 

export default injectIntl(Label);
