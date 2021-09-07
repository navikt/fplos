import React, { ReactNode, Component, ChangeEvent } from 'react';
import { Input } from 'nav-frontend-skjema';
import classnames from 'classnames/bind';
import dayjs from 'dayjs';
import { Modifier } from 'react-day-picker';

import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import CalendarOverlay from './CalendarOverlay';
import CalendarToggleButton from './CalendarToggleButton';

import styles from './datepicker.less';

const classNames = classnames.bind(styles);

interface OwnProps {
  label?: ReactNode;
  placeholder?: string;
  feil?: { feilmelding?: string };
  disabled?: boolean;
  onChange: (dato: string | ChangeEvent) => void;
  onBlur: (event: any) => void;
  value?: string;
  initialMonth?: Date;
  numberOfMonths?: number;
  disabledDays?: Modifier | Modifier[];
  alwaysShowCalendar?: boolean;
}

interface StateProps {
  showCalendar?: boolean;
  inputOffsetTop?: number;
  inputOffsetWidth?: number;
}

class Datepicker extends Component<OwnProps, StateProps> {
  buttonRef: HTMLButtonElement;

  inputRef: HTMLDivElement;

  static defaultProps = {
    label: '',
    placeholder: 'dd.mm.åååå',
    value: '',
    feil: null,
    disabled: false,
    initialMonth: new Date(),
    numberOfMonths: 1,
    disabledDays: {},
  };

  constructor(props: OwnProps) {
    super(props);
    this.state = { showCalendar: false };
    this.handleInputRef = this.handleInputRef.bind(this);
    this.handleButtonRef = this.handleButtonRef.bind(this);
    this.handleUpdatedRefs = this.handleUpdatedRefs.bind(this);
    this.toggleShowCalendar = this.toggleShowCalendar.bind(this);
    this.hideCalendar = this.hideCalendar.bind(this);
    this.elementIsCalendarButton = this.elementIsCalendarButton.bind(this);
    this.handleDayChange = this.handleDayChange.bind(this);
  }

  handleButtonRef(buttonRef: HTMLButtonElement): void {
    if (buttonRef) {
      this.buttonRef = buttonRef;
      this.handleUpdatedRefs();
    }
  }

  handleInputRef(inputRef: HTMLInputElement | null): void {
    if (inputRef) {
      this.inputRef = inputRef;
      this.handleUpdatedRefs();
    }
  }

  handleUpdatedRefs(): void {
    const { inputRef, buttonRef } = this;
    if (inputRef) {
      this.setState({
        inputOffsetTop: inputRef.offsetTop,
        inputOffsetWidth: inputRef.offsetWidth,
      });
      if (buttonRef) {
        inputRef.style.paddingRight = `${buttonRef.offsetWidth}px`;
      }
    }
  }

  handleDayChange(selectedDay: Date): void {
    if (selectedDay) {
      const parsed = dayjs(selectedDay);
      if (parsed.isValid()) {
        const { onChange } = this.props;
        onChange(parsed.format(DDMMYYYY_DATE_FORMAT));
        this.setState({ showCalendar: false });
        this.inputRef.focus();
      }
    }
  }

  toggleShowCalendar(): void {
    const { showCalendar } = this.state;
    this.setState({ showCalendar: !showCalendar });
  }

  hideCalendar(): void {
    this.setState({ showCalendar: false });
  }

  elementIsCalendarButton(element: EventTarget): boolean {
    return element === this.buttonRef;
  }

  render() {
    const {
      label,
      placeholder,
      onChange,
      onBlur,
      value,
      feil,
      disabled,
      disabledDays,
      initialMonth,
      numberOfMonths,
      alwaysShowCalendar = false,
    } = this.props;
    const {
      inputOffsetTop, inputOffsetWidth, showCalendar,
    } = this.state;

    return (
      <>
        <div className={styles.inputWrapper}>
          <Input
            className={styles.dateInput}
            inputRef={this.handleInputRef}
            autoComplete="off"
            bredde="S"
            placeholder={placeholder}
            label={label}
            onChange={onChange}
            onBlur={onBlur}
            value={value || ''}
            feil={feil}
            disabled={disabled}
          />
          {!alwaysShowCalendar && (
            <CalendarToggleButton
              inputOffsetTop={inputOffsetTop}
              inputOffsetWidth={inputOffsetWidth}
              className={styles.calendarToggleButton}
              toggleShowCalendar={this.toggleShowCalendar}
              buttonRef={this.handleButtonRef}
              disabled={disabled}

            />
          )}
        </div>
        {(showCalendar || alwaysShowCalendar) && (
        <CalendarOverlay
          disabled={disabled}
          value={value}
          onDayChange={this.handleDayChange}
          onClose={this.hideCalendar}
          elementIsCalendarButton={this.elementIsCalendarButton}
          className={styles.calendarRoot}
          dayPickerClassName={classNames(`calendarWrapper calendarWrapper--${numberOfMonths}`)}
          disabledDays={disabledDays}
          numberOfMonths={numberOfMonths}
          initialMonth={initialMonth}
        />
        )}
      </>
    );
  }
}

export default Datepicker;
