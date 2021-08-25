import React, { ReactNode, Component } from 'react';
import dayjs from 'dayjs';
import { Input } from 'nav-frontend-skjema';
import { DateUtils, Modifier } from 'react-day-picker';

import { haystack } from 'utils/arrayUtils';
import { DDMMYYYY_DATE_FORMAT } from 'utils/formats';
import CalendarToggleButton from '../datepicker/CalendarToggleButton';
import PeriodCalendarOverlay from './PeriodCalendarOverlay';

import styles from './periodpicker.less';

interface OwnProps {
  names: string[];
  label?: ReactNode;
  placeholder?: string;
  feil?: { feilmelding?: string };
  disabled?: boolean;
  disabledDays?: Modifier | Modifier[];
}

interface StateProps {
  showCalendar: boolean;
  period: string;
  inputOffsetTop?: number;
  inputOffsetWidth?: number;
}

const getStartDateInput = (props: OwnProps) => haystack(props, props.names[0]).input;
const getEndDateInput = (props: OwnProps) => haystack(props, props.names[1]).input;
const isValidDate = (date: Date): boolean => dayjs(date, DDMMYYYY_DATE_FORMAT, true).isValid();
const createPeriod = (startDay?: Date | null, endDay?: Date | null): string => `${dayjs(startDay)
  .format(DDMMYYYY_DATE_FORMAT)} - ${dayjs(endDay).format(DDMMYYYY_DATE_FORMAT)}`;

class Periodpicker extends Component<OwnProps, StateProps> {
  buttonRef: HTMLButtonElement;

  inputRef: HTMLDivElement;

  static defaultProps = {
    label: '',
    placeholder: 'dd.mm.åååå - dd.mm.åååå',
    feil: null,
    disabled: false,
  };

  constructor(props: OwnProps) {
    super(props);
    this.handleInputRef = this.handleInputRef.bind(this);
    this.handleButtonRef = this.handleButtonRef.bind(this);
    this.handleUpdatedRefs = this.handleUpdatedRefs.bind(this);
    this.toggleShowCalendar = this.toggleShowCalendar.bind(this);
    this.hideCalendar = this.hideCalendar.bind(this);
    this.elementIsCalendarButton = this.elementIsCalendarButton.bind(this);
    this.handleDayChange = this.handleDayChange.bind(this);
    this.onBlur = this.onBlur.bind(this);
    this.onChange = this.onChange.bind(this);
    this.parseToDate = this.parseToDate.bind(this);

    const startDate = getStartDateInput(props).value;
    const endDate = getEndDateInput(props).value;
    let period = '';
    if (startDate) {
      period = endDate ? `${startDate} - ${endDate}` : startDate;
    }

    this.state = {
      showCalendar: false,
      period,
    };
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
    if (!isValidDate(selectedDay)) {
      return;
    }
    const startInput = getStartDateInput(this.props);
    const endInput = getEndDateInput(this.props);
    const currentStartDate = startInput.value;
    const currentEndDate = endInput.value;

    if (isValidDate(currentStartDate)) {
      const range = {
        from: dayjs(currentStartDate, DDMMYYYY_DATE_FORMAT).toDate(),
        to: dayjs(currentEndDate, DDMMYYYY_DATE_FORMAT).toDate(),
      };

      const newRange = DateUtils.addDayToRange(selectedDay, range);
      const period = createPeriod(newRange.from, newRange.to);
      this.setState({ period });

      if (newRange.from === selectedDay) {
        startInput.onChange(period);
        if (isValidDate(currentEndDate)) {
          this.setState({ showCalendar: false });
          this.inputRef.focus();
        }
      } else {
        endInput.onChange(period);
        this.setState({ showCalendar: false });
        this.inputRef.focus();
      }
    } else {
      const period = createPeriod(selectedDay, selectedDay);
      this.setState({ period });
      startInput.onChange(period);
      endInput.onChange(period);
    }
  }

  onBlur(e: React.FocusEvent): void {
    getStartDateInput(this.props).onBlur(e);
    getEndDateInput(this.props).onBlur(e);
  }

  onChange(e: React.ChangeEvent): void {
    // @ts-ignore Fiks
    this.setState({ period: e.target.value });
    getStartDateInput(this.props).onChange(e);
    getEndDateInput(this.props).onChange(e);
  }

  parseToDate(name: string): Date | undefined {
    const nameFromProps = haystack(this.props, name);
    const day = nameFromProps.input.value;
    return isValidDate(day) ? dayjs(day, DDMMYYYY_DATE_FORMAT).toDate() : undefined;
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
      label, placeholder, feil, names, disabled, disabledDays,
    } = this.props;
    const {
      period, inputOffsetTop, inputOffsetWidth, showCalendar,
    } = this.state;

    return (
      <>
        <div className={styles.inputWrapper}>
          <Input
            className={styles.dateInput}
            inputRef={this.handleInputRef}
            autoComplete="off"
            bredde="L"
            placeholder={placeholder}
            label={label}
            value={period}
            feil={feil}
            disabled={disabled}
            onBlur={this.onBlur}
            onChange={this.onChange}
          />
          <CalendarToggleButton
            inputOffsetTop={inputOffsetTop}
            inputOffsetWidth={inputOffsetWidth}
            className={styles.calendarToggleButton}
            toggleShowCalendar={this.toggleShowCalendar}
            buttonRef={this.handleButtonRef}
            disabled={disabled}
          />
        </div>
        {showCalendar
        && (
        <PeriodCalendarOverlay
          disabled={disabled}
          startDate={this.parseToDate(names[0])}
          endDate={this.parseToDate(names[1])}
          onDayChange={this.handleDayChange}
          elementIsCalendarButton={this.elementIsCalendarButton}
          className={styles.calendarRoot}
          dayPickerClassName={styles.calendarWrapper}
          onClose={this.hideCalendar}
          disabledDays={disabledDays}
        />
        )}
      </>
    );
  }
}

export default Periodpicker;
