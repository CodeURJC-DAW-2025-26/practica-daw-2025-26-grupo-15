import { useState } from 'react';
import { Link } from 'react-router';
import { Container, Row, Col, Modal, Button } from 'react-bootstrap';
import { Footer } from '../components/footer';

export default function FollowRequest() {
    // --- DATOS PROVISIONALES  ---
    const token = "fake-csrf-token";
    const user = {
        name: "Usuario de Prueba",
        photo: { id: "profile-pic.jpg" } 
    };
    const pendingCount = 2;
    const followersNumber = 120;
    const followingNumber = 85;

    const followRequests = [
        { id: "req-1", name: "Ana Gómez", photo: null },
        { id: "req-2", name: "Carlos Ruiz", photo: { id: "carlos-pic.jpg" } }
    ];

    return (
        <>

            <div className="fr-page">
                <main className="page page--feed flex-grow-1">

                    {/* Brand bar */}
                    <div className="brand brand--full mb-4">
                        <Link to="/" className="brand-mark-link">
                            <img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" />
                        </Link>
                        <Link to="/" className="text-decoration-none">
                            <span className="brand-title">DSGram</span>
                        </Link>
                        
                        <form action="/logout" method="post" className="brand-logout">
                            <button type="submit" className="btn-logout">
                                <i className="bi bi-box-arrow-right"></i> Log out
                            </button>
                            <input type="hidden" name="_csrf" value={token} />
                        </form>
                    </div>

                    <div className="container-fluid px-0 fr-content-container">
                        <div className="d-flex flex-column gap-4">
                            <div className="fr-identity">
                                <div className="fr-identity-avatar">
                                    {/* Funcionalidad Mustache: Condicional {{#user.photo}} y {{^user.photo}} */}
                                    {/* En React: Verificamos si existe user.photo. Si sí, la mostramos. Si no, icono por defecto. */}
                                    {user.photo ? (
                                        <img src={`/images/${user.photo}`} alt="Profile photo" />
                                    ) : (
                                        <i className="bi bi-person-circle"></i>
                                    )}
                                </div>
                                <div className="fr-identity-info">
                                    {/* Funcionalidad Mustache: Inyectar texto simple {{user.name}} */}
                                    <p className="fr-username">{user.name}</p>
                                    <p className="fr-handle">Your follow requests</p>
                                </div>
                                <div className="fr-stats">
                                    <span className="fr-stat-chip"><i className="bi bi-hourglass-split"></i> <span className="fr-stat-num">{pendingCount}</span> Pending</span>
                                    <span className="fr-stat-chip"><i className="bi bi-people-fill"></i> <span className="fr-stat-num">{followersNumber}</span> Followers</span>
                                    <span className="fr-stat-chip"><i className="bi bi-person-check-fill"></i> <span className="fr-stat-num">{followingNumber}</span> Following</span>
                                </div>
                            </div>

                            <div className="fr-section-header">
                                <h2 className="fr-section-title">
                                    <i className="bi bi-bell-fill"></i> Pending requests
                                </h2>
                                <Link className="fr-back-btn" to="/profile">
                                    <i className="bi bi-arrow-left-short"></i> Back to profile
                                </Link>
                            </div>

                            <div className="fr-list">
                                {/* Funcionalidad Mustache: Bucle {{#followRequests}} y estado vacío {{^followRequests}} */}
                                {/* En React: Comprobamos si el array tiene elementos. Si tiene, hacemos un .map(). Si no, mostramos el mensaje de vacío. */}
                                {followRequests && followRequests.length > 0 ? (
                                    followRequests.map((req) => (
                                        <article key={req.id} className="fr-card">
                                            <div className="fr-card-avatar">
                                                {/* Funcionalidad Mustache: Condicional de foto dentro del bucle */}
                                                {req.photo ? (
                                                    <img src={`/images/${req.photo.id}`} alt="Profile photo" />
                                                ) : (
                                                    <i className="bi bi-person-fill"></i>
                                                )}
                                            </div>
                                            <div className="fr-card-body">
                                                <p className="fr-card-name">{req.name}</p>
                                                <p className="fr-card-meta"><i className="bi bi-clock"></i> Wants to follow you</p>
                                            </div>
                                            <div className="fr-card-actions">
                                                {/* Botón Aceptar */}
                                                <form action={`/acceptRequest/${req.id}`} method="post" className="d-inline">
                                                    <input type="hidden" name="_csrf" value={token} />
                                                    <input type="hidden" name="srcPage" value="/follow-requests" />
                                                    <button type="submit" className="fr-btn-accept"><i className="bi bi-check-lg"></i> Accept</button>
                                                </form>
                                                {/* Botón Rechazar */}
                                                <form action={`/declineRequest/${req.id}`} method="post" className="d-inline">
                                                    <input type="hidden" name="_csrf" value={token} />
                                                    <input type="hidden" name="srcPage" value="/follow-requests" />
                                                    <button type="submit" className="fr-btn-decline"><i className="bi bi-x-lg"></i> Decline</button>
                                                </form>
                                            </div>
                                        </article>
                                    ))
                                ) : (
                                    /* Estado vacío si no hay peticiones (lo que antes era {{^followRequests}}) */
                                    <div className="fr-empty">
                                        <i className="bi bi-inbox fr-empty-icon"></i>
                                        <p className="fr-empty-title">All caught up!</p>
                                        <p className="fr-empty-sub">You have no pending follow requests right now.</p>
                                    </div>
                                )}
                            </div>

                        </div>
                    </div>
                </main>
                <Footer />
            </div>
        </>
    );
}