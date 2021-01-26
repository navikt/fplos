import React, { FunctionComponent } from 'react';
import { Field } from 'react-final-form';
import { Textarea as NavTextarea, TextareaProps } from 'nav-frontend-skjema';
import EtikettFokus from 'nav-frontend-etiketter';
import { injectIntl, FormattedMessage, WrappedComponentProps } from 'react-intl';
import { FieldValidator } from 'final-form';

import renderNavField from './renderNavField';
import { LabelType } from './Label';

import styles from './textAreaField.less';
import ReadOnlyField from './ReadOnlyField';

type BadgesType = 'suksess' | 'info' | 'advarsel' | 'fokus';

interface Badges {
  textId: string;
  type: BadgesType;
  title: string;
}

interface TextAreaWithBadgeProps {
  badges?: Badges[];
}

const TextAreaWithBadge: FunctionComponent<TextAreaWithBadgeProps & WrappedComponentProps & TextareaProps> = ({
  badges,
  intl,
  ...otherProps
}) => (
  <div className={badges ? styles.textAreaFieldWithBadges : null}>
    { badges && (
      <div className={styles.etikettWrapper}>
        { badges.map(({ textId, type, title }) => (
          <EtikettFokus key={textId} type={type} title={intl.formatMessage({ id: title })}>
            <FormattedMessage id={textId} />
          </EtikettFokus>
        ))}
      </div>
    )}
    <NavTextarea
      {...otherProps}
    />
  </div>
);

const composeValidators = (validators: FieldValidator<any>[]): FieldValidator<any> => (
  value: any,
) => (validators ? validators.reduce((error, validator) => error || validator(value, undefined), undefined) : []);

const renderNavTextArea = renderNavField(injectIntl(TextAreaWithBadge));

interface OwnProps {
  name: string;
  label: LabelType;
  validate?: FieldValidator<any>[];
  readOnly?: boolean;
  maxLength?: number;
  badges?: Badges;
}

const TextAreaField: FunctionComponent<OwnProps> = ({
  name,
  label,
  validate,
  readOnly,
  ...otherProps
}) => (
  <Field
    name={name}
    validate={composeValidators(validate)}
    component={readOnly ? ReadOnlyField : renderNavTextArea}
    label={label}
    {...otherProps}
    readOnly={readOnly}
    readOnlyHideEmpty
    autoComplete="off"
    type="textarea"
  />
);

TextAreaField.defaultProps = {
  readOnly: false,
};

export default TextAreaField;
