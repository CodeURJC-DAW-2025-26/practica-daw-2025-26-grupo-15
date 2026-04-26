function parseDateValue(value: string | null | undefined): Date | null {
  if (!value) {
    return null;
  }

  if (/^\d+$/.test(value)) {
    const parsedFromTimestamp = new Date(Number(value));
    return Number.isFinite(parsedFromTimestamp.getTime())
      ? parsedFromTimestamp
      : null;
  }

  const parsedFromString = new Date(value);
  return Number.isFinite(parsedFromString.getTime())
    ? parsedFromString
    : null;
}

export function formatDate(
  value: string | null | undefined,
  locale = "es-ES",
  fallback = "Unknown",
): string {
  const parsedDate = parseDateValue(value);

  if (!parsedDate) {
    return fallback;
  }

  return new Intl.DateTimeFormat(locale, {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }).format(parsedDate);
}
