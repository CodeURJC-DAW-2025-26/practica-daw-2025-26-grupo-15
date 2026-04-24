import {
  isRouteErrorResponse,
  useLocation,
  useNavigate,
} from "react-router";

type ErrorBoundaryScreenProps = {
  error: unknown;
};

export function ErrorBoundaryScreen({ error }: ErrorBoundaryScreenProps) {
  const navigate = useNavigate();
  const location = useLocation();

  let eyebrow = "Unexpected interruption";
  let headline = "This corner of DSGram hit a rough edge.";
  let detail =
    error instanceof Error ? error.message : "An unexpected error occurred.";

  if (isRouteErrorResponse(error)) {
    eyebrow = `Error ${error.status}`;
    headline =
      error.status === 404
        ? "The page vanished before it could load."
        : "We couldn't finish loading this screen.";
    detail = error.statusText || detail;
  }

  return (
    <main className="page error-boundary-page">
      <section className="error-boundary-card" role="alert" aria-live="assertive">
        <div
          className="error-boundary-aurora error-boundary-aurora--top"
          aria-hidden="true"
        />
        <div
          className="error-boundary-aurora error-boundary-aurora--bottom"
          aria-hidden="true"
        />

        <div className="error-boundary-grid">
          <div className="error-boundary-copy">
            <p className="error-boundary-eyebrow">{eyebrow}</p>
            <h1 className="error-boundary-title">{headline}</h1>
            <p className="error-boundary-text">
              Something in this view broke while the app was trying to render it.
              You can jump back to a safe route, return to the previous page, or
              reload and try again.
            </p>

            <div className="error-boundary-actions">
              <button
                type="button"
                className="error-boundary-btn error-boundary-btn--primary"
                onClick={() => navigate("/")}
              >
                Back to home
              </button>
              <button
                type="button"
                className="error-boundary-btn error-boundary-btn--ghost"
                onClick={() => navigate(-1)}
              >
                Go back
              </button>
              <button
                type="button"
                className="error-boundary-btn error-boundary-btn--soft"
                onClick={() => window.location.reload()}
              >
                Reload page
              </button>
            </div>
          </div>

          <aside className="error-boundary-panel">
            <div className="error-boundary-pulse" aria-hidden="true">
              <span />
              <span />
              <span />
            </div>

            <div className="error-boundary-meta">
              <p className="error-boundary-label">Location</p>
              <p className="error-boundary-value">{location.pathname}</p>
            </div>

            <div className="error-boundary-meta">
              <p className="error-boundary-label">Details</p>
              <p className="error-boundary-detail">{detail}</p>
            </div>
          </aside>
        </div>
      </section>
    </main>
  );
}
