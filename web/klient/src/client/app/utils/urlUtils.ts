export const parseQueryString = (queryString = ''): {[paramName: string]: string} => (
  queryString
    .replace(/^\?/, '') // Remove leading question mark
    .replace(/\+/g, '%20') // Replace plus signs with URL-encoded spaces
    .split(/&/) // Split on delimiter '&'
    .map((query) => query.split(/=/))
    .map(([key, value]) => ({ [key]: decodeURIComponent(value) })) // URL-decode value
    .reduce((a, b) => ({ ...a, ...b }), {})
);

export const formatQueryString = (queryParams: Record<string, any> = {}): string => (
  `?${( // Add leading question mark
    Object.entries(queryParams)
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
      .filter(([key, value]) => (value !== undefined && value !== null && value !== '')) // Filter out empty/null/undef values
      .map(([key, value]) => ([key, encodeURIComponent(value as string)])) // URL-encode value
      .map(([key, encodedValue]) => `${key}=${encodedValue}`)
      .join('&') // Join with delimiter '&'
      .replace('%20', '+') // Replace URL-encoded spaces with plus
  )}`
);
