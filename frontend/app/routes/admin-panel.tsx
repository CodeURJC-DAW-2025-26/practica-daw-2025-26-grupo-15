export function AdminPanel() {
  // ============================================================================
  // TODO: PHASE 2 - REACT MIGRATION (STATE & DATA FETCHING)
  // ============================================================================
  // Currently, this component uses mock variables to simulate the data
  // that was previously injected by the Spring Boot backend via Mustache (`{{currentUser}}`).
  //
  // Next steps for full migration:
  // 1. Remove static constants like `currentUser` and `token`.
  // 2. Fetch the admin profile data and tables data from the REST API using a standard React mechanism.
  // 3. Replace vanilla JS DOM manipulations (like `setAdminOption`, injecting HTML into `#feedStream`)
  //    with React state (e.g., `const [currentTab, setCurrentTab] = useState('u')`).
  // 4. Handle CSRF properly via HTTP Headers (`fetch`/`axios`) and turn actions into actual event handlers
  //    (`onSubmit`, `onClick`) without full page reloads.
  // ============================================================================

  // Mock indicators previously rendered with Mustache
  const currentUser: {
    name: string;
    nameInitial: string;
    photo: { id: string } | null;
  } = {
    name: "Admin User",
    nameInitial: "A",
    photo: null, // e.g. { id: "photo-id" }
  };
  const token = "mock-csrf-token";

  // Mocks for what used to be vanilla global JS functions.
  // These should later become state updates like `setTab('users')`.
  const setAdminOption = (option: string) => {
    console.log("Mock setAdminOption:", option);
  };

  const setFilter = (value: string) => {
    console.log("Mock setFilter:", value);
  };

  return (
    <>
      <div className="adm-wrap">
        <aside className="adm-sidebar">
          <div className="adm-sidebar-brand">
            <a href="/" className="brand-mark-link">
              <img
                src="/assets/DSGram_LOGO.png"
                alt="DSGram logo"
                className="brand-mark"
              />
            </a>
            <span className="adm-sidebar-brand-name">DSGram</span>
          </div>

          <div className="adm-sidebar-label">Admin Panel</div>

          <nav className="adm-nav">
            <button
              onClick={() => setAdminOption("u")}
              className="adm-nav-item"
            >
              <i className="bi bi-people-fill"></i>
              <span>Users</span>
            </button>
            <button
              onClick={() => setAdminOption("l")}
              className="adm-nav-item"
            >
              <i className="bi bi-collection-fill"></i>
              <span>Lists</span>
            </button>
            <button
              onClick={() => setAdminOption("e")}
              className="adm-nav-item"
            >
              <i className="bi bi-journal-code"></i>
              <span>Exercises</span>
            </button>
          </nav>

          <div className="adm-sidebar-bottom">
            <div className="adm-sidebar-user">
              <div className="adm-sidebar-avatar">
                {currentUser && currentUser.photo ? (
                  <img src={`/images/${currentUser.photo.id}`} alt="avatar" />
                ) : (
                  <span>{currentUser.nameInitial}</span>
                )}
              </div>
              <div className="adm-sidebar-user-info">
                <span className="adm-sidebar-user-name">
                  {currentUser.name}
                </span>
                <span className="adm-sidebar-user-role">Administrator</span>
              </div>
            </div>

            <div className="adm-sidebar-actions">
              <a href="/profile" className="adm-sidebar-action-link">
                <i className="bi bi-person"></i> Profile
              </a>
              {/* Logout form. In the future, this will become an Async `fetch`/`axios` call terminating the session inside an `onSubmit` handler */}
              <form action="/logout" method="post" className="m-0">
                <input type="hidden" name="_csrf" value={token} />
                <button
                  type="submit"
                  className="adm-sidebar-action-link adm-sidebar-action-link--logout"
                >
                  <i className="bi bi-box-arrow-right"></i> Log out
                </button>
              </form>
            </div>

            <a href="/" className="adm-exit-btn">
              <i className="bi bi-arrow-left-short"></i> Back to DSGram
            </a>
          </div>
        </aside>

        <main className="adm-main">
          {/* Page header */}
          <div className="adm-header">
            <div>
              <h1 className="adm-header-title">User Management</h1>
              <p className="adm-header-sub">
                View and remove registered accounts across DSGram.
              </p>
            </div>
          </div>

          {/* Search + table */}
          <div className="adm-card">
            <div className="adm-card-toolbar">
              <div className="adm-search-wrap">
                <i className="bi bi-search adm-search-icon"></i>
                <input
                  id="inputFilter"
                  type="search"
                  className="adm-search-input"
                  placeholder="Search by name…"
                  autoComplete="off"
                  onInput={() => setFilter("Filtro")}
                />
              </div>
            </div>

            <div id="feedScroll" className="table-scroll">
              <table className="adm-table">
                <thead id="tableHeader"></thead>
                <tbody
                  id="feedStream"
                  className="t-body-admin"
                  data-petition="a"
                >
                  {/* React Data Mapping goes here in the future instead of innerHTML injection */}
                </tbody>
              </table>
              <div id="feedSentinel" className="feed-sentinel"></div>
            </div>

            <div id="modalsContainer">
              {/* React Portals or State Modals go here in the future */}
            </div>
          </div>
        </main>
      </div>
    </>
  );
}
