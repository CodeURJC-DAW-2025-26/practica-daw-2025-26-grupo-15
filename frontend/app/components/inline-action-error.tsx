type InlineActionErrorProps = {
  message: string | null;
};

export function InlineActionError({ message }: InlineActionErrorProps) {
  if (!message) {
    return null;
  }

  return (
    <p className="action-error-banner" role="alert" aria-live="polite">
      {message}
    </p>
  );
}
