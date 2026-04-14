import { Link } from "react-router";
import type { Route } from "./+types/home";

type UserBasicInfo = {
  id: number;
  name: string;
  email: string;
};

type FollowingSuggestion = {
  suggestion: UserBasicInfo & { photoId?: number };
  contact: UserBasicInfo[];
  commonCount: number;
};

// Función helper equivalente a las condiciones de Mustache
function getContactText(contact: UserBasicInfo[]) {
  if (contact.length >= 3) return `Followed by ${contact.length} contacts`;
  if (contact.length >= 1) {
    return `Followed by ${contact[0].name}${contact[1] ? ` and ${contact[1].name}` : ""}`;
  }
  return "Suggested for you";
}

export default function Fyp() {
  // Variables mockeadas por ahora
  const isLogged = true;
  const currentUser = {
    name: "John Doe",
    nameInitial: "J",
    photoId: null as number | null,
  };

  const suggestions: FollowingSuggestion[] = [
    {
      suggestion: { id: 1, name: "Alice", email: "alice@example.com", photoId: 10 },
      contact: [
        { id: 2, name: "Bob", email: "bob@example.com" },
        { id: 3, name: "Charlie", email: "charlie@example.com" },
        { id: 4, name: "Dave", email: "dave@example.com" },
      ],
      commonCount: 3,
    },
    {
      suggestion: { id: 5, name: "Eve", email: "eve@example.com" },
      contact: [
        { id: 2, name: "Bob", email: "bob@example.com" },
      ],
      commonCount: 1,
    },
    {
      suggestion: { id: 6, name: "Frank", email: "frank@example.com" },
      contact: [],
      commonCount: 0,
    },
  ];

  return (
    <>
      <main className="page page--feed">
        <div className="brand">
          <a className="brand-mark-link" href="/"><img alt="DSGram logo" className="brand-mark" src="/assets/DSGram_LOGO.png" /></a>
          <a href="/"><span className="brand-title">DSGram</span></a>
        </div>

        <section className="app-shell feed">
          <div className="container-fluid px-0">
            <div className="row g-0">

              <aside className="sidebar col-12 col-lg-3">
                {/* Search Bar */}
                <label className="visually-hidden" htmlFor="sidebarSearch">Search</label>
                <div className="sidebar-search__wrap">
                  <input id="sidebarSearch" name="searchName" className="sidebar-search__input" type="search" placeholder="Search for users to conect with…" aria-label="Search" />
                  <button className="sidebar-search__btn" type="submit" >
                    <span className="sidebar-search__icon" aria-hidden="true">
                      <svg viewBox="0 0 24 24" width="18" height="18" fill="none" >
                        <path d="M10.5 18.5a8 8 0 1 1 0-16 8 8 0 0 1 0 16Z" stroke="currentColor" strokeWidth="2" />
                        <path d="M16.5 16.5 21 21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
                      </svg>
                    </span>
                  </button>
                </div>

                {/* Search results */}
                <div id="searchContainer" className="sidebar-search-results visually-hidden" aria-label="Search results">
                  <div className="sidebar-search-results__header">
                    <span className="sidebar-search-results__title">Search results</span>
                  </div>
                  <div id="searchResults" className="sidebar-search-results__list" role="list"></div>
                </div>

                {isLogged ? (
                  <div className="sidebar-section">
                    <h3 className="sidebar-section__title">Suggested for you</h3>
                    <div className="list suggestions-container">
                      
                      {suggestions.map((item) => (
                        <div key={item.suggestion.id} className="list-item sidebar-search-results__item d-flex align-items-center justify-content-between w-100">
                          <div className="d-flex align-items-center flex-grow-1 min-w-0">
                            
                            {item.suggestion.photoId ? (
                              <span className="sidebar-search-results__avatar avatar--img">
                                { /* TODO: enable when backend serves images */ }
                                <img
                                  alt="Profile picture"
                                  className="avatar-image-cover"
                                />
                              </span>
                            ) : (
                              <span className="sidebar-search-results__avatar">
                                {item.suggestion.name.charAt(0).toUpperCase()}
                              </span>
                            )}

                            <div className="ms-2 overflow-hidden">
                              <span className="sidebar-search-results__name d-block text-truncate">{item.suggestion.name}</span>
                              <span className={`text-muted-custom ${item.contact.length === 0 ? "opacity-50" : ""}`}>
                                {getContactText(item.contact)}
                              </span>
                            </div>
                          </div>

                          <a href={`/profile/${item.suggestion.id}`} className="suggestion-follow-btn text-decoration-none text-center ms-2 text-nowrap">
                            View profile
                          </a>
                        </div>
                      ))}

                    </div>
                  </div>
                ) : (
                  <div className="sidebar-cta mt-0">
                    <div className="sidebar-cta__icon" aria-hidden="true">
                      <svg viewBox="0 0 24 24" width="28" height="28" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <circle cx="12" cy="8" r="4" stroke="currentColor" strokeWidth="1.8" />
                        <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" />
                      </svg>
                    </div>
                    <p className="sidebar-cta__heading">Join DSGram</p>
                    <p className="sidebar-cta__sub">Follow users, share solutions and track your progress.</p>
                    <a className="sidebar-cta__btn sidebar-cta__btn--primary" href="/login">Log in</a>
                    <a className="sidebar-cta__btn sidebar-cta__btn--ghost" href="/register">Create account</a>
                  </div>
                )}
              </aside>

              {/* Main content */}
              <div className="content col-12 col-lg-9">
                <div className="topbar d-flex flex-column flex-md-row align-items-start align-items-md-center justify-content-between gap-3">
                  <div>
                    <h2 className="section-title">Home feed</h2>
                    <p className="muted">Recent changes of your interest</p>
                  </div>
                  
                  {isLogged && (
                    <div className="profile-image d-flex align-items-center gap-2">
                      <p className="p greeting mt-3">Welcome {currentUser.name}!</p>
                      <Link to="users/1">
                        {currentUser.photoId ? (
                          <div className="avatar avatar--img">
                            { /* TODO: enable when backend serves images AÑadir el src*/ }
                            <img
                              alt="Profile picture"
                            />
                          </div>
                        ) : (
                          <div className="avatar">{currentUser.nameInitial}</div>
                        )}
                      </Link>
                    </div>
                  )}

                </div>
                {/* Lists feed */}
                <div id="feedStream" className="feed-stream mt-0" data-petition="p">
                  <div id="feedEmpty" className="feed-empty visually-hidden">
                    <svg viewBox="0 0 24 24" width="40" height="40" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                      <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="1.5" />
                      <path d="M8 12h8M12 8v8" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
                    </svg>
                    <p className="feed-empty__title">Nothing here yet</p>
                    <p className="feed-empty__sub">Follow users to see their activity in your feed.</p>
                  </div>
                  <div id="feedSentinel"></div>
                  <div id="loadingSpinner" className="row mx-0 justify-content-center visually-hidden mb-5">
                    <div className="spinner"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>
    </>
  );
}
