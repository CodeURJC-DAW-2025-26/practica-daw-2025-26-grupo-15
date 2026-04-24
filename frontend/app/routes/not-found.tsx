import { Link, useLocation, useNavigate } from "react-router";

export default function NotFound() {
  const location = useLocation();
  const navigate = useNavigate();

  return (
    <main className="page not-found-page">
      <section className="not-found-card" role="status" aria-live="polite">
        <p className="not-found-code">404</p>
        <h1 className="not-found-title">Page not found</h1>
        <p className="not-found-copy">
          We could not find <span className="not-found-path">{location.pathname}</span>.
          The address might be wrong, or the page may have been moved.
        </p>
        <div className="not-found-actions">
          <Link to="/" className="not-found-btn not-found-btn--primary">
            Go to home
          </Link>
          <button
            type="button"
            className="not-found-btn not-found-btn--ghost"
            onClick={() => navigate(-1)}
          >
            Go back
          </button>
        </div>
      </section>
    </main>
  );
}
