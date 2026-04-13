import { Footer } from "../components/footer";
import { Link } from "react-router"; // O 'react-router-dom' según tu versión

export default function FollowingFollowers() {
    // --- DATOS PROVISIONALES (Simulan el backend) ---
    const followersPage = true; // Cambia a 'false' para ver la vista de "Following"
    const isOwnProfile = true;  // Cambia a 'false' para ver la vista de otro usuario
    const token = "fake-csrf-token";
    const loggedUserId = "me123";

    const user = {
        id: "user456",
        name: "Dev Master"
    };

    const userList = [
        { id: "1", name: "Alice", nameInitial: "A", photo: null },
        { id: "2", name: "Bob", nameInitial: "B", photo: { id: "avatar2.jpg" } }
    ];

    const numFollowers = 10;
    const numFollowing = 5;

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
                                            {userList && userList.length > 0 ? (
                                                userList.map((item) => (
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
                                                                        <input type="hidden" name="requesterId" value={loggedUserId} />
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