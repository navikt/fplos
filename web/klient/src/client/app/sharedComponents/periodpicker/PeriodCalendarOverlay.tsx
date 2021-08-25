import React, { Component } from 'react';
import { injectIntl, WrappedComponentProps } from 'react-intl';
import DayPicker, { AfterModifier, BeforeModifier, Modifier } from 'react-day-picker';
import dayjs from 'dayjs';

import { getRelatedTargetIE11, isIE11 } from 'utils/browserUtils';

const getRelatedTarget = (e: React.FocusEvent): Promise<any> => {
  if (isIE11()) {
    return getRelatedTargetIE11();
  }
  return Promise.resolve(e.relatedTarget);
};

interface OwnProps {
  onDayChange: (dato: Date) => void;
  className: string;
  dayPickerClassName: string;
  elementIsCalendarButton: (target: EventTarget) => boolean;
  startDate?: Date;
  endDate?: Date;
  disabled?: boolean;
  onClose?: () => void;
  disabledDays?: Modifier | Modifier[];
}

class PeriodCalendarOverlay extends Component<OwnProps & WrappedComponentProps> {
  calendarRootRef: HTMLDivElement

  static defaultProps = {
    startDate: undefined,
    endDate: undefined,
    disabled: false,
  };

  constructor(props: OwnProps & WrappedComponentProps) {
    super(props);
    this.onBlur = this.onBlur.bind(this);
    this.onKeyDown = this.onKeyDown.bind(this);
    this.setCalendarRootRef = this.setCalendarRootRef.bind(this);
    this.onDayClick = this.onDayClick.bind(this);
    this.getDayPickerLocalization = this.getDayPickerLocalization.bind(this);
    this.targetIsCalendarOrCalendarButton = this.targetIsCalendarOrCalendarButton.bind(this);
  }

  onBlur(e: React.FocusEvent): void {
    const { targetIsCalendarOrCalendarButton, props: { onClose } } = this;
    getRelatedTarget(e)
      .then((relatedTarget: HTMLDivElement) => {
        if (targetIsCalendarOrCalendarButton(relatedTarget)) {
          return;
        }
        if (onClose) {
          onClose();
        }
      });
  }

  onKeyDown({ keyCode }: React.KeyboardEvent): void {
    if (keyCode === 27) {
      const { onClose } = this.props;
      if (onClose) {
        onClose();
      }
    }
  }

  onDayClick(selectedDate: Date): void {
    let isSelectable = true;
    const { disabledDays, onDayChange } = this.props;
    if (disabledDays) {
      const { before: intervalStart } = disabledDays as BeforeModifier;
      if (intervalStart) {
        isSelectable = dayjs(selectedDate).isSameOrAfter(dayjs(intervalStart).startOf('day'));
      }
      const { after: intervalEnd } = disabledDays as AfterModifier;
      if (isSelectable && intervalEnd) {
        isSelectable = dayjs(selectedDate).isSameOrBefore(dayjs(intervalEnd).endOf('day'));
      }
    }
    if (isSelectable) {
      onDayChange(selectedDate);
    }
  }

  setCalendarRootRef(calendarRootRef: HTMLDivElement): void {
    if (calendarRootRef) {
      this.calendarRootRef = calendarRootRef;
      calendarRootRef.focus();
    }
  }

  getDayPickerLocalization() {
    const { intl: { formatMessage, locale } } = this.props;
    return {
      locale,
      months: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11].map((monthNum) => formatMessage({ id: `Calendar.Month.${monthNum}` })),
      weekdaysLong: [0, 1, 2, 3, 4, 5, 6].map((dayNum) => formatMessage({ id: `Calendar.Day.${dayNum}` })),
      weekdaysShort: [0, 1, 2, 3, 4, 5, 6].map((dayName) => formatMessage({ id: `Calendar.Day.Short.${dayName}` })),
      firstDayOfWeek: 1,
    };
  }

  targetIsCalendarOrCalendarButton(target: HTMLDivElement): boolean {
    const { calendarRootRef, props: { elementIsCalendarButton } } = this;

    const targetIsInsideCalendar = calendarRootRef && calendarRootRef.contains(target);
    const targetIsCalendarButton = elementIsCalendarButton(target);

    return targetIsInsideCalendar || targetIsCalendarButton;
  }

  render() {
    const {
      disabled, className, dayPickerClassName, disabledDays, startDate, endDate,
    } = this.props;
    if (disabled) {
      return null;
    }

    return (
      <div
        className={className}
        ref={this.setCalendarRootRef}
        onBlur={this.onBlur}
        tabIndex={0} // eslint-disable-line jsx-a11y/no-noninteractive-tabindex
        onKeyDown={this.onKeyDown}
        role="link"
      >
        <DayPicker
          {...this.getDayPickerLocalization()}
          className={dayPickerClassName}
          numberOfMonths={2}
          selectedDays={startDate && endDate ? [{ from: startDate, to: endDate }] : undefined}
          onDayClick={this.onDayClick}
          onKeyDown={this.onKeyDown}
          disabledDays={disabledDays}
          initialMonth={endDate || dayjs().toDate()}
        />
      </div>
    );
  }
}

export default injectIntl(PeriodCalendarOverlay);
