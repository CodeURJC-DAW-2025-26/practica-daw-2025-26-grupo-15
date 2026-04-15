import { getUser } from "~/services/user-service";
import { Footer } from "../components/footer";
import { Link } from "react-router"; // O 'react-router-dom' según tu versión
import type { Route } from "./+types/followers-following";


export async function clientLoader({ request }: Route.ClientLoaderArgs) {
    const url = new URL(request.url);
    const userId = url.searchParams.get("userId");

    if (!userId) {
        throw new Error("No se proporcionó un ID de usuario");
    }

    return await getUser(Number(userId));
}


export default function FollowingFollowers({loaderData}: Route.ComponentProps) {
    // --- DATOS PROVISIONALES (Simulan el backend) ---
    const followersPage = true; // Cambia a 'false' para ver la vista de "Following"
    const isOwnProfile = true;  // Cambia a 'false' para ver la vista de otro usuario
    const token = "fake-csrf-token";

    const user = loaderData;

    const numFollowers = loaderData.followers.length;
    const numFollowing = loaderData.following.length;

    return (
        <>
            <main className="page page--feed">
                <div className="brand">
                    <Link to="/" className="brand-mark-link">
                        <img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" />
                    </Link>
                    <Link to="/">
                        <span className="brand-title">DSGram</span>
                    </Link>
                </div>

                <section className="app-shell feed">
                    <div className="container">
                        <div className="row">
                            <div className="content col-12 mx-0 g-0">
                                {/* BARRA SUPERIOR: Títulos dinámicos */}
                                <div className="topbar d-flex flex-column flex-md-row align-items-start align-items-md-center justify-content-between gap-0 mt-3">
                                    <div>
                                        <h2 className="section-title">
                                            {followersPage ? "Followers" : "Following"}
                                        </h2>
                                        <p className="muted">
                                            {followersPage 
                                                ? (isOwnProfile ? "Users that follow you" : `Users that follow ${user.name}`)
                                                : (isOwnProfile ? "Users you are following" : `Users that ${user.name} is following`)
                                            }
                                        </p>
                                    </div>
                                    <Link className="btn ghost" to={`/profile/${user.id}`}>Back</Link>
                                </div>

                                <div className="row align-items-start justify-content-between">
                                    {/* COLUMNA IZQUIERDA: Lista de usuarios */}
                                    <div className="col-md-8 col-12 followers-card mx-0 mt-5">
                                        <h3>{followersPage ? "Followers" : "Following"}</h3>
                                        <div className="followers-list">
                                            {user && user.followers.length > 0 ? (
                                                user.followers.map((item: typeof user) => (
                                                    <div key={item.id} className="followers-item">
                                                        <div className="followers-left">
                                                            <div className="followers-avatar avatar--img">
                                                                {item.photo ? (
                                                                    <img src={`/images/${item.photo.id}`} alt={item.name} className="avatar-image-cover" />
                                                                ) : (
                                                                    <span>{item.nameInitial}</span>
                                                                )}
                                                            </div>
                                                            <span>{item.name}</span>
                                                        </div>
                                                        <div className="d-flex gap-2">
                                                            <Link className="btn secondary followers-action p-0" to={`/profile/${item.id}`}>View</Link>
                                                            
                                                            {isOwnProfile && (
                                                                followersPage ? (
                                                                    <form method="post" action="/removeFollower">
                                                                        <input type="hidden" name="followerId" value={item.id} />
                                                                        <input type="hidden" name="_csrf" value={token} />
                                                                        <button className="btn followers-action btn-danger-action" type="submit">Remove</button>
                                                                    </form>
                                                                ) : (
                                                                    <form method="post" action="/unfollow">
                                                                        <input type="hidden" name="requesterId" value={user.id} />
                                                                        <input type="hidden" name="targetId" value={item.id} />
                                                                        <input type="hidden" name="_csrf" value={token} />
                                                                        <button className="btn followers-action btn-danger-action" type="submit">Unfollow</button>
                                                                    </form>
                                                                )
                                                            )}
                                                        </div>
                                                    </div>
                                                ))
                                            ) : (
                                                <p>
                                                    {followersPage 
                                                        ? (isOwnProfile ? "You don't have any followers yet." : "This user doesn't have any followers yet.")
                                                        : (isOwnProfile ? "You're not following anyone yet." : "This user isn't following anyone yet.")
                                                    }
                                                </p>
                                            )}
                                        </div>
                                    </div>

                                    {/* COLUMNA DERECHA: Gráfico (Canvas) */}
                                    {/* TODO: El gráfico se mostrará más adelante    */}
                                    <div className="col-md-4 col-12 chart-container mx-0 mt-5">
                                        <canvas 
                                            id="comparisonChart" 
                                            data-num-followers={numFollowers} 
                                            data-num-following={numFollowing}
                                        ></canvas>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </main>
            <Footer />
        </>
    );
}