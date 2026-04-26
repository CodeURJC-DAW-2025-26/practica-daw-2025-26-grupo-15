import React, { useActionState, useRef, useEffect } from 'react';
import { Link } from 'react-router';
import type { UserDTO } from '~/dtos/UserDTO';
import { getUsersByName } from '~/services/user-service';

const initialState = { users: [], hasMore: true, query: '', showResults: false };

function makeSearchAction(activeUser: UserDTO | null, lockRef: React.MutableRefObject<boolean>) {
  return async function searchUsersAction(prevState: any, formData: FormData) {
    const query = formData.get('searchName')?.toString().trim() || '';
    const page = Number(formData.get('page') || 0);
    const isReset = formData.get('isReset') === 'true';

    if (!query) return { ...initialState };

    try {
      const response = await getUsersByName(query, activeUser, page);
      const newUsers = isReset ? response.data : [...prevState.users, ...response.data];
      return { users: newUsers, hasMore: response.hasMore, query, showResults: true };
    } catch (error) {
      console.error('Error fetching users:', error);
      return { ...prevState, hasMore: false };
    } finally {
      lockRef.current = false; // release synchronously when the fetch is done
    }
  };
}

export default function UserSearchbar({ activeUser }: { activeUser: UserDTO | null }) {
  const lockRef = useRef(false);
  const nextPageRef = useRef(0);
  const formRef = useRef<HTMLFormElement>(null);
  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const searchAction = React.useMemo(
    () => makeSearchAction(activeUser, lockRef),
    [activeUser]
  );
  const [state, formAction, isPending] = useActionState(searchAction, initialState);

  useEffect(() => () => { if (timeoutRef.current) clearTimeout(timeoutRef.current); }, []);

  const triggerSearch = (query: string, page: number, isReset: boolean) => {
    if (!formRef.current) return;
    lockRef.current = true; // acquire synchronously before formAction
    const formData = new FormData(formRef.current);
    formData.set('searchName', query);
    formData.set('page', String(page));
    formData.set('isReset', String(isReset));
    formAction(formData);
  };

  const handleInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    if (!value.trim()) { nextPageRef.current = 0; lockRef.current = false; formAction(new FormData()); return; }
    timeoutRef.current = setTimeout(() => {
      nextPageRef.current = 1;
      triggerSearch(value, 0, true);
    }, 400);
  };

  const handleScroll = (e: React.UIEvent<HTMLDivElement>) => {
    const { scrollTop, clientHeight, scrollHeight } = e.currentTarget;
    if (scrollTop + clientHeight < scrollHeight - 50) return;
    if (lockRef.current || !state.hasMore || !state.query) return;
    const page = nextPageRef.current;
    nextPageRef.current += 1;
    triggerSearch(state.query, page, false);
  };

  return (
    <>
      <form ref={formRef} onSubmit={(e) => e.preventDefault()}>
        <label className="visually-hidden" htmlFor="sidebarSearch">Search</label>
        <div className="sidebar-search__wrap">
          <input type="hidden" name="page" value="0" />
          <input type="hidden" name="isReset" value="true" />
          <input
            id="sidebarSearch"
            name="searchName"
            className="sidebar-search__input"
            type="search"
            placeholder="Search for users to connect with…"
            aria-label="Search"
            onChange={handleInput}
          />
          <button
            className="sidebar-search__btn"
            type="button"
            onClick={() => {
              const input = formRef.current?.elements.namedItem('searchName') as HTMLInputElement;
              if (input) { nextPageRef.current = 1; triggerSearch(input.value, 0, true); }
            }}
          >
            <span className="sidebar-search__icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="none">
                <path d="M10.5 18.5a8 8 0 1 1 0-16 8 8 0 0 1 0 16Z" stroke="currentColor" strokeWidth="2" />
                <path d="M16.5 16.5 21 21" stroke="currentColor" strokeWidth="2" strokeLinecap="round" />
              </svg>
            </span>
          </button>
        </div>
      </form>

      <div
        id="searchContainer"
        className={`sidebar-search-results ${!state.showResults ? 'visually-hidden' : ''}`}
        aria-label="Search results"
        aria-busy={isPending}
      >
        <div className="sidebar-search-results__header">
          <span className="sidebar-search-results__title">Search results</span>
          {isPending && <span style={{ fontSize: '12px', marginLeft: '10px' }} aria-live="polite">Loading…</span>}
        </div>
        <div id="searchResults" className="sidebar-search-results__list" role="list" onScroll={handleScroll}>
          {state.users.map((user: UserDTO) => (
            <Link key={user.id} className="sidebar-search-results__item" to={`users/${user.id}`} role="listitem">
              <span className="sidebar-search-results__avatar avatar--img">
                <img src={`api/v1/images/${user.photo?.id}/media`} alt={`${user.name}'s profile picture`} className="avatar-image-cover" />
              </span>
              <span className="sidebar-search-results__name">{user.name}</span>
            </Link>
          ))}
          {!isPending && state.showResults && state.users.length === 0 && (
            <p style={{ padding: '12px', color: 'var(--color-text-secondary)', fontSize: '14px' }}>No users found.</p>
          )}
          {!state.hasMore && state.users.length > 0 && (
            <p style={{ padding: '12px', color: 'var(--color-text-secondary)', fontSize: '14px' }}>No more results.</p>
          )}
        </div>
      </div>
    </>
  );
}