import { useEffect, useRef, useState, useCallback } from "react";
import { Link } from "react-router"; 
import type ListDTO from "~/dtos/ListDTO";
import type PostDTO from "~/dtos/PostDTO";
import type { UserDTO } from "~/dtos/UserDTO";
import { useUserStore } from "~/stores/user-store";
import ListCard from "./list-card";

interface FeedStreamProps {
    itemsType: "post" | "list";
    itemsSearch: (page: number, user: UserDTO | null) => Promise<{ hasMore: boolean; items: (PostDTO | ListDTO)[] }>;
    currentUser?: UserDTO | null;
}

export default function FeedStream({ itemsType, itemsSearch, currentUser }: FeedStreamProps) {
    const [items, setItems] = useState<(PostDTO | ListDTO)[]>([]);
    const [loading, setLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true);
    const [hasError, setHasError] = useState(false);

    const sentinelRef = useRef<HTMLDivElement | null>(null);
    const pageRef = useRef(0); // ← single source of truth for page

    const { user } = useUserStore();
    const activeUser = currentUser ?? user;

    // Subsequent page fetches (page 1+)
    const fetchPage = useCallback(async () => {
        setLoading(true);
        setHasError(false);
        try {
            const response = await itemsSearch(pageRef.current, activeUser);
            setHasMore(response.hasMore);
            setItems(prev => [...prev, ...response.items]);
            pageRef.current += 1; // ← increment directly, no state involved
        } catch {
            setHasError(true);
            setHasMore(false);
        } finally {
            setLoading(false);
        }
    }, [itemsSearch, activeUser]);

    const fetchPageRef = useRef(fetchPage);
    useEffect(() => { fetchPageRef.current = fetchPage; }, [fetchPage]);

    // Reset + first page fetch when feed type or user changes
    useEffect(() => {
        let cancelled = false;

        pageRef.current = 0;
        setHasMore(true);
        setHasError(false);
        setItems([]);

        const doFetch = async () => {
            setLoading(true);
            try {
                const response = await itemsSearch(0, activeUser);
                if (cancelled) return;
                setHasMore(response.hasMore);
                setItems(response.items);
                pageRef.current = 1; // ← set ref directly, no setPage()
            } catch {
                if (cancelled) return;
                setHasError(true);
                setHasMore(false);
            } finally {
                if (!cancelled) setLoading(false);
            }
        };

        doFetch();
        return () => { cancelled = true; };

    }, [itemsType, activeUser]); // itemsSearch deliberately excluded

    // Observer — re-runs when loading flips to false, re-observing sentinel
    useEffect(() => {
        if (!hasMore || hasError || loading) return;

        const handleObserver = (entries: IntersectionObserverEntry[]) => {
            if (entries[0].isIntersecting) {
                fetchPageRef.current();
            }
        };

        const observer = new IntersectionObserver(handleObserver, {
            root: null,
            rootMargin: "150px"
        });

        if (sentinelRef.current) observer.observe(sentinelRef.current);
        return () => observer.disconnect();

    }, [hasMore, hasError, loading]);

    return (
        <div id="feedStream" className="feed-stream mt-0" style={{ overflowY: 'auto', maxHeight: '80vh' }}>

            {items.length === 0 && !loading && !hasError && (
                <div id="feedEmpty" className="feed-empty">
                    <svg viewBox="0 0 24 24" width="40" height="40" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                        <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="1.5" />
                        <path d="M8 12h8M12 8v8" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" />
                    </svg>
                    <p className="feed-empty__title">Nothing here yet</p>
                    <p className="feed-empty__sub">Sigue a usuarios para ver su actividad en tu feed.</p>
                </div>
            )}

            {hasError && (
                <div className="alert alert-danger mx-3 mt-3 text-center">
                    Hubo un problema de conexión al cargar el feed.
                    <button className="btn btn-link" onClick={() => fetchPageRef.current()}>
                        Intentar de nuevo
                    </button>
                </div>
            )}

            <div className="feed-items-container d-flex flex-column gap-3 p-2">
                {itemsType === "post" ? (
                    items.map((item, index) => {
                        const post = item as PostDTO;
                        return (
                            <Link key={post.id || index} to={`/${post.contentLink}`} className="text-decoration-none text-reset">
                                <article className="feed-card">
                                    <header className="d-flex flex-column flex-md-row align-items-start align-items-md-center justify-content-between gap-0">
                                        <div>
                                            <h3>{post.actionType} · {post.header}</h3>
                                            <p className="meta">{post.ownerName} · {post.timeAgo}</p>
                                        </div>
                                    </header>
                                    <p className="muted">See more details</p>
                                </article>
                            </Link>
                        );
                    })
                ) : itemsType === "list" ? (
                    items.map((item, index) => {
                        const list = item as ListDTO;
                        return (
                            <ListCard
                                key={list.id || index}
                                list={list}
                                isOwnProfile={list.owner?.id === activeUser?.id}
                                canDeleteLists={list.owner?.id === activeUser?.id}
                                currentUser={activeUser}
                                onListDeleted={(listId) => {
                                    setItems(prev => prev.filter(l => (l as ListDTO).id !== listId));
                                }}
                            />
                        );
                    }) 
                ): (
                    <p>No posts to display</p>
                )}
            </div>

            <div ref={sentinelRef} id="feedSentinel" style={{ height: '20px' }} />

            {loading && (
                <div id="loadingSpinner" className="row mx-0 justify-content-center mb-5">
                    <div className="spinner">Cargando...</div>
                </div>
            )}
        </div>
    );
}
