import { Component, ReactNode, ErrorInfo } from 'react';
import { captureException, withScope } from '@sentry/browser';

interface OwnProps {
  errorMessageCallback: (error: any) => void;
  children: ReactNode;
}

interface State {
  hasError: boolean;
}

export class ErrorBoundary extends Component<OwnProps, State> {
  static getDerivedStateFromError() {
    // Update state so the next render will show the fallback UI.
    return { hasError: true };
  }

  componentDidCatch(error: Error, info: ErrorInfo): void {
    const { errorMessageCallback } = this.props;

    withScope((scope) => {
      Object.keys(info).forEach((key) => {
        // @ts-ignore Fiks
        scope.setExtra(key, info[key]);
        captureException(error);
      });
    });

    errorMessageCallback([
      error.toString(),
      info.componentStack
        .split('\n')
        .map((line) => line.trim())
        .find((line) => !!line),
    ].join(' '));

    // eslint-disable-next-line no-console
    console.error(error);
  }

  render(): ReactNode {
    const { children } = this.props;
    return children;
  }
}

export default ErrorBoundary;
