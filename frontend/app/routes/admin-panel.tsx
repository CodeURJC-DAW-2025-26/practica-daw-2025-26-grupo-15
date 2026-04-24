import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Modal } from "react-bootstrap";
import { Link, redirect, useNavigate } from "react-router";
import type { ExerciseDTO } from "~/dtos/ExerciseDTO";
import type ListDTO from "~/dtos/ListDTO";
import type { UserDTO } from "~/dtos/UserDTO";
import { deleteExercise } from "~/services/exercise-service";
import { deleteList } from "~/services/list-service";
import { reqIsLogged } from "~/services/login-service";
import { deleteProfile } from "~/services/user-service";
import { useUserStore } from "~/stores/user-store";
import type { Route } from "./+types/admin-panel";
import { requireRole } from "~/services/route-guards-service";

const API_IMAGES_URL = "/api/v1/images";
const DEFAULT_PAGE_SIZE = 15;
const MIN_SPINNER_TIME_MS = 700;

type AdminOption = "u" | "l" | "e";

type AdminRow =
  | { kind: "u"; value: UserDTO }
  | { kind: "l"; value: ListDTO }
  | { kind: "e"; value: ExerciseDTO };

interface PagedResponse<T> {
  content: T[];
  page?: {
    number: number;
    totalPages: number;
  };
  number?: number;
  totalPages?: number;
}

const OPTION_COPY: Record<
  AdminOption,
  {
    title: string;
    subtitle: string;
    searchPlaceholder: string;
    emptyLabel: string;
    icon: string;
  }
> = {
  u: {
    title: "User Management",
    subtitle: "View and remove registered accounts across DSGram.",
    searchPlaceholder: "Search by user name...",
    emptyLabel: "No users found",
    icon: "bi-people-fill",
  },
  l: {
    title: "List Management",
    subtitle: "Review and remove exercise lists from the whole platform.",
    searchPlaceholder: "Search by list title...",
    emptyLabel: "No lists found",
    icon: "bi-collection-fill",
  },
  e: {
    title: "Exercise Management",
    subtitle: "Inspect and remove exercises created by the community.",
    searchPlaceholder: "Search by exercise title...",
    emptyLabel: "No exercises found",
    icon: "bi-journal-code",
  },
};

export async function clientLoader() {
  return await requireRole("ADMIN");
}

export default function AdminPanel({ loaderData }: Route.ComponentProps) {
  const user = loaderData;
  const { logoutUser } = useUserStore();
  const navigate = useNavigate();

  const [currentOption, setCurrentOption] = useState<AdminOption>("u");
  const [filterText, setFilterText] = useState("");
  const [rows, setRows] = useState<AdminRow[]>([]);
  const [feedHasMore, setFeedHasMore] = useState(true);
  const [feedLoading, setFeedLoading] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState<AdminRow | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  const feedScrollRef = useRef<HTMLDivElement | null>(null);
  const sentinelRef = useRef<HTMLDivElement | null>(null);

  const pageRef = useRef(0);
  const queryVersionRef = useRef(0);
  const loadingRef = useRef(false);
  const hasMoreRef = useRef(true);

  const optionCopy = OPTION_COPY[currentOption];

  const tableHeader = useMemo(() => {
    if (currentOption === "u") {
      return (
        <tr>
          <th>User</th>
          <th>Email</th>
          <th>Roles</th>
          <th className="text-center">Followers</th>
          <th className="text-center">Following</th>
          <th></th>
        </tr>
      );
    }

    if (currentOption === "l") {
      return (
        <tr>
          <th>Title</th>
          <th>Topic</th>
          <th className="text-center">Owner</th>
          <th></th>
        </tr>
      );
    }

    return (
      <tr>
        <th>Title</th>
        <th>List</th>
        <th className="text-center">Owner</th>
        <th></th>
      </tr>
    );
  }, [currentOption]);

  const loadMoreFeed = useCallback(async () => {
    if (loadingRef.current || !hasMoreRef.current) {
      return;
    }

    const requestVersion = queryVersionRef.current;
    const pageToLoad = pageRef.current;
    const loadingStartedAt = performance.now();

    loadingRef.current = true;
    setFeedLoading(true);

    try {
      const params = new URLSearchParams();
      params.set("page", String(pageToLoad));
      params.set("size", String(DEFAULT_PAGE_SIZE));

      const normalizedFilter = filterText.trim();
      if (normalizedFilter) {
        params.set("nameFilter", normalizedFilter);
      }

      let endpoint = "";
      if (currentOption === "u") {
        endpoint = "/api/v1/users/";
        params.set("excludedId", String(user.id));
      } else if (currentOption === "l") {
        endpoint = "/api/v1/exerciselists/";
      } else {
        endpoint = "/api/v1/exercises/";
      }

      const response = await fetch(`${endpoint}?${params.toString()}`);
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }

      if (queryVersionRef.current !== requestVersion) {
        return;
      }

      if (currentOption === "u") {
        const page = (await response.json()) as PagedResponse<UserDTO>;
        const mappedRows = page.content.map((entry) => ({
          kind: "u" as const,
          value: entry,
        }));
        setRows((prev) => [...prev, ...mappedRows]);

        const pageNumber = page.page?.number ?? page.number ?? pageToLoad;
        const totalPages = page.page?.totalPages ?? page.totalPages ?? 0;
        const hasMore = pageNumber + 1 < totalPages;

        setFeedHasMore(hasMore);
        hasMoreRef.current = hasMore;
      } else if (currentOption === "l") {
        const page = (await response.json()) as PagedResponse<ListDTO>;
        const mappedRows = page.content.map((entry) => ({
          kind: "l" as const,
          value: entry,
        }));
        setRows((prev) => [...prev, ...mappedRows]);

        const pageNumber = page.page?.number ?? page.number ?? pageToLoad;
        const totalPages = page.page?.totalPages ?? page.totalPages ?? 0;
        const hasMore = pageNumber + 1 < totalPages;

        setFeedHasMore(hasMore);
        hasMoreRef.current = hasMore;
      } else {
        const page = (await response.json()) as PagedResponse<ExerciseDTO>;
        const mappedRows = page.content.map((entry) => ({
          kind: "e" as const,
          value: entry,
        }));
        setRows((prev) => [...prev, ...mappedRows]);

        const pageNumber = page.page?.number ?? page.number ?? pageToLoad;
        const totalPages = page.page?.totalPages ?? page.totalPages ?? 0;
        const hasMore = pageNumber + 1 < totalPages;

        setFeedHasMore(hasMore);
        hasMoreRef.current = hasMore;
      }

      pageRef.current = pageToLoad + 1;
      setLoadError(null);
    } catch (error) {
      console.error("Error loading admin feed", error);
      if (queryVersionRef.current === requestVersion) {
        setLoadError("The admin feed could not be loaded. Please try again.");
      }
    } finally {
      if (queryVersionRef.current === requestVersion) {
        const elapsed = performance.now() - loadingStartedAt;
        const remaining = MIN_SPINNER_TIME_MS - elapsed;
        if (remaining > 0) {
          await new Promise((resolve) => setTimeout(resolve, remaining));
        }

        loadingRef.current = false;
        setFeedLoading(false);
      }
    }
  }, [currentOption, filterText, user.id]);

  const reloadFeed = useCallback(() => {
    queryVersionRef.current += 1;
    pageRef.current = 0;
    loadingRef.current = false;
    hasMoreRef.current = true;

    setRows([]);
    setFeedHasMore(true);
    setFeedLoading(false);
    setLoadError(null);

    if (feedScrollRef.current) {
      feedScrollRef.current.scrollTop = 0;
    }

    void loadMoreFeed();
  }, [loadMoreFeed]);

  useEffect(() => {
    reloadFeed();
  }, [reloadFeed]);

  useEffect(() => {
    const root = feedScrollRef.current;
    const sentinel = sentinelRef.current;

    if (!root || !sentinel) {
      return;
    }

    const onScroll = () => {
      const nearBottom =
        root.scrollTop + root.clientHeight >= root.scrollHeight - 80;

      if (nearBottom) {
        void loadMoreFeed();
      }
    };

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0]?.isIntersecting) {
          void loadMoreFeed();
        }
      },
      {
        root,
        rootMargin: "150px",
      },
    );

    observer.observe(sentinel);
    root.addEventListener("scroll", onScroll, { passive: true });
    onScroll();

    return () => {
      observer.disconnect();
      root.removeEventListener("scroll", onScroll);
    };
  }, [loadMoreFeed]);

  useEffect(() => {
    if (feedLoading || !feedHasMore) {
      return;
    }

    const root = feedScrollRef.current;
    if (!root) {
      return;
    }

    const rafId = window.requestAnimationFrame(() => {
      const doesNotOverflowYet = root.scrollHeight <= root.clientHeight + 8;
      const nearBottom =
        root.scrollTop + root.clientHeight >= root.scrollHeight - 80;

      if (doesNotOverflowYet || nearBottom) {
        void loadMoreFeed();
      }
    });

    return () => window.cancelAnimationFrame(rafId);
  }, [rows.length, feedLoading, feedHasMore, loadMoreFeed]);

  async function handleLogout() {
    setIsLoggingOut(true);
    await logoutUser();
    navigate("/");
  }

  function setAdminOption(option: AdminOption) {
    if (option === currentOption) {
      return;
    }
    setCurrentOption(option);
  }

  function setFilter(newValue: string) {
    setFilterText(newValue);
  }

  function openDeleteModal(row: AdminRow) {
    setDeleteTarget(row);
  }

  function closeDeleteModal() {
    if (isDeleting) {
      return;
    }
    setDeleteTarget(null);
  }

  async function confirmDelete() {
    if (!deleteTarget) {
      return;
    }

    setIsDeleting(true);

    try {
      if (deleteTarget.kind === "u") {
        await deleteProfile(String(deleteTarget.value.id));
      } else if (deleteTarget.kind === "l") {
        await deleteList(String(deleteTarget.value.id));
      } else {
        await deleteExercise(deleteTarget.value.id);
      }

      setDeleteTarget(null);
      reloadFeed();
    } catch (error) {
      console.error("Error deleting admin item", error);
    } finally {
      setIsDeleting(false);
    }
  }

  function getDeleteModalBody() {
    if (!deleteTarget) {
      return {
        title: "Delete item?",
        body: "This action cannot be undone.",
      };
    }

    if (deleteTarget.kind === "u") {
      return {
        title: "Delete user?",
        body: `Permanently remove ${deleteTarget.value.name} and all their content. This cannot be undone.`,
      };
    }

    if (deleteTarget.kind === "l") {
      return {
        title: "Delete list?",
        body: `Permanently remove ${deleteTarget.value.title} and all exercises and solutions in it. This cannot be undone.`,
      };
    }

    return {
      title: "Delete exercise?",
      body: `Permanently remove ${deleteTarget.value.title} and all its solutions and comments. This cannot be undone.`,
    };
  }

  const deleteModalText = getDeleteModalBody();

  return (
    <>
      <div className="adm-wrap">
        <aside className="adm-sidebar">
          <div className="adm-sidebar-brand">
            <Link to="/" className="brand-mark-link">
              <img
                src="/assets/DSGram_LOGO.png"
                alt="DSGram logo"
                className="brand-mark"
              />
            </Link>
            <span className="adm-sidebar-brand-name">DSGram</span>
          </div>

          <div className="adm-sidebar-label">Admin Panel</div>
          <nav className="adm-nav">
            <button
              type="button"
              onClick={() => setAdminOption("u")}
              className={`adm-nav-item ${currentOption === "u" ? "adm-nav-item--active" : ""}`}
            >
              <i className="bi bi-people-fill"></i>
              <span>Users</span>
            </button>
            <button
              type="button"
              onClick={() => setAdminOption("l")}
              className={`adm-nav-item ${currentOption === "l" ? "adm-nav-item--active" : ""}`}
            >
              <i className="bi bi-collection-fill"></i>
              <span>Lists</span>
            </button>
            <button
              type="button"
              onClick={() => setAdminOption("e")}
              className={`adm-nav-item ${currentOption === "e" ? "adm-nav-item--active" : ""}`}
            >
              <i className="bi bi-journal-code"></i>
              <span>Exercises</span>
            </button>
          </nav>

          <div className="adm-sidebar-bottom">
            <div className="adm-sidebar-user">
              <div className="adm-sidebar-avatar">
                {user?.photo?.id && (
                  <img
                    src={`${API_IMAGES_URL}/${user.photo.id}/media`}
                    alt="avatar"
                  />
                )}
                {!user?.photo?.id && <span>{user?.name.charAt(0)}</span>}
              </div>
              <div className="adm-sidebar-user-info">
                <span className="adm-sidebar-user-name">{user.name}</span>
                <span className="adm-sidebar-user-role">Admin</span>
              </div>
            </div>

            <div className="adm-sidebar-actions">
              <Link to={`/users/${user.id}`} className="adm-sidebar-action-link">
                <i className="bi bi-person"></i>
                Profile
              </Link>
              <button
                type="button"
                onClick={handleLogout}
                disabled={isLoggingOut}
                className="adm-sidebar-action-link adm-sidebar-action-link--logout"
              >
                <i className="bi bi-box-arrow-right"></i>
                {isLoggingOut ? "Logging out..." : "Log Out"}
              </button>
            </div>
            <Link to="/" className="adm-exit-btn">
              <i className="bi bi-arrow-left-short"></i> Back to DSGram
            </Link>
          </div>
        </aside>
        <main className="adm-main">
          <div className="adm-header">
            <div>
              <h1 className="adm-header-title">{optionCopy.title}</h1>
              <p className="adm-header-sub">{optionCopy.subtitle}</p>
            </div>
          </div>

          <div className="adm-card">
            <div className="adm-card-toolbar">
              <div className="adm-search-wrap">
                <i className="bi bi-search adm-search-icon"></i>
                <input
                  id="inputFilter"
                  type="search"
                  className="adm-search-input"
                  placeholder={optionCopy.searchPlaceholder}
                  autoComplete="off"
                  value={filterText}
                  onChange={(event) => setFilter(event.currentTarget.value)}
                />
              </div>
            </div>

            {loadError && (
              <div className="adm-load-error" role="alert">
                <i className="bi bi-exclamation-triangle-fill"></i>
                <span>{loadError}</span>
              </div>
            )}

            <div id="feedScroll" ref={feedScrollRef} className="table-scroll">
              <table className="adm-table">
                <thead id="tableHeader">{tableHeader}</thead>
                <tbody
                  id="feedStream"
                  className="t-body-admin"
                  data-petition={currentOption}
                >
                  {rows.map((row) => {
                    const rowId = row.value.id;
                    const key = `${row.kind}-${String(rowId)}`;

                    if (row.kind === "u") {
                      const profileImageId = row.value.photo?.id;
                      return (
                        <tr
                          key={key}
                          className="adm-user-row"
                          data-entity-id={row.value.id}
                        >
                          <td>
                            <div className="adm-user-cell">
                              <div className="adm-user-avatar">
                                {profileImageId ? (
                                  <img
                                    src={`${API_IMAGES_URL}/${profileImageId}/media`}
                                    alt={row.value.name}
                                  />
                                ) : (
                                  <span>{row.value.name.charAt(0).toUpperCase()}</span>
                                )}
                              </div>
                              <div>
                                <Link to={`/users/${row.value.id}`}>
                                  <div className="adm-user-name">{row.value.name}</div>
                                </Link>
                                <div className="adm-user-id">ID #{row.value.id}</div>
                              </div>
                            </div>
                          </td>
                          <td className="adm-email">{row.value.email}</td>
                          <td>
                            {row.value.roles.map((role) => (
                              <span
                                key={`${row.value.id}-${role}`}
                                className={`adm-role-badge adm-role-badge--${role}`}
                              >
                                {role}
                              </span>
                            ))}
                          </td>
                          <td className="text-center adm-stat-cell">
                            {row.value.followers?.length ?? 0}
                          </td>
                          <td className="text-center adm-stat-cell">
                            {row.value.following?.length ?? 0}
                          </td>
                          <td>
                            <button
                              className="adm-delete-btn"
                              type="button"
                              onClick={() => openDeleteModal(row)}
                              title={`Delete ${row.value.name}`}
                            >
                              <i className="bi bi-trash3"></i>
                            </button>
                          </td>
                        </tr>
                      );
                    }

                    if (row.kind === "l") {
                      return (
                        <tr
                          key={key}
                          className="adm-user-row"
                          data-entity-id={row.value.id}
                        >
                          <td>
                            <div className="adm-user-cell">
                              <div>
                                <Link to={`/lists/${row.value.id}`}>
                                  <div className="adm-user-name">{row.value.title}</div>
                                </Link>
                                <div className="adm-user-id">ID #{row.value.id}</div>
                              </div>
                            </div>
                          </td>
                          <td className="adm-email">{row.value.topic}</td>
                          <td className="text-center adm-stat-cell">
                            {row.value.owner?.name ?? "-"}
                          </td>
                          <td>
                            <button
                              className="adm-delete-btn"
                              type="button"
                              onClick={() => openDeleteModal(row)}
                              title={`Delete ${row.value.title}`}
                            >
                              <i className="bi bi-trash3"></i>
                            </button>
                          </td>
                        </tr>
                      );
                    }

                    return (
                      <tr
                        key={key}
                        className="adm-user-row"
                        data-entity-id={row.value.id}
                      >
                        <td>
                          <div className="adm-user-cell">
                            <div>
                              <Link to={`/exercise/${row.value.id}`}>
                                <div className="adm-user-name">{row.value.title}</div>
                              </Link>
                              <div className="adm-user-id">ID #{row.value.id}</div>
                            </div>
                          </div>
                        </td>
                        <td className="adm-email">
                          {row.value.exerciseList?.title ?? "-"}
                        </td>
                        <td className="text-center adm-stat-cell">
                          {row.value.owner?.name ?? "-"}
                        </td>
                        <td>
                          <button
                            className="adm-delete-btn"
                            type="button"
                            onClick={() => openDeleteModal(row)}
                            title={`Delete ${row.value.title}`}
                          >
                            <i className="bi bi-trash3"></i>
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>

              {!feedLoading && !feedHasMore && rows.length === 0 && (
                <div id="feedEmpty" className="adm-no-results">
                  <i className={`bi ${optionCopy.icon}`}></i>
                  <p>{optionCopy.emptyLabel}</p>
                </div>
              )}

              <div
                id="loadingSpinner"
                className={`adm-loading-wrap ${feedLoading ? "" : "visually-hidden"}`}
              >
                <div className="spinner-border" role="status" aria-hidden="true"></div>
                <span className="visually-hidden">Loading</span>
              </div>

              <div
                id="feedSentinel"
                ref={sentinelRef}
                className="feed-sentinel"
              ></div>
            </div>
          </div>
        </main>
      </div>

      <Modal show={deleteTarget !== null} onHide={closeDeleteModal} centered>
        <div className="modal-content adm-modal">
          <Modal.Header className="border-0 pb-1" closeButton>
            <Modal.Title className="adm-modal-title">
              <i className="bi bi-exclamation-circle-fill me-2 text-sky"></i>
              {deleteModalText.title}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body className="py-1">
            <p className="adm-modal-body">{deleteModalText.body}</p>
          </Modal.Body>
          <Modal.Footer className="border-0 pt-1">
            <button
              className="adm-modal-cancel"
              onClick={closeDeleteModal}
              disabled={isDeleting}
            >
              Cancel
            </button>
            <button
              className="adm-modal-confirm"
              onClick={confirmDelete}
              disabled={isDeleting}
            >
              {isDeleting ? "Deleting..." : "Delete"}
            </button>
          </Modal.Footer>
        </div>
      </Modal>
    </>
  );
}
