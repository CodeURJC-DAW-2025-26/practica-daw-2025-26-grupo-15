import { useState } from 'react';
import { Link, useLoaderData, useNavigate } from 'react-router';
import { Container, Row, Col, Modal, Button } from 'react-bootstrap';

// NOTA: Para que esto funcione, 'list' y 'user' deberían venir de:
// 1. useLoaderData() si usas React Router Data APIs
// 2. Props pasadas al componente
// 3. Un Store (Zustand/Redux)
// 4. Un estado local (useState) para pruebas.

export default function ListView() {
    // --- DATOS PROVISIONALES ---
    const user = {
        photo: null, 
        nameInitial: "JD",
        name: "John Doe",
        id: "123"
    };

    const list = {
        id: "list-001",
        title: "Lista de Ejercicios React",
        description: "Una descripción de prueba para la lista de ejercicios.",
        owner: { id: "123", name: "John Doe" },
        lastUpdate: "13/04/2026",
        exercises: [
            { id: "ex1", title: "Ejercicio 1", description: "Descripción corta", numSolutions: 5 },
            { id: "ex2", title: "Ejercicio 2", description: "Otra descripción", numSolutions: 2 }
        ]
    };

    const token = "fake-csrf-token"; //Placeholder para seguridad

    // --- LÓGICA DE ESTADO (Antes gestionada por el servidor/Mustache) ---
    const logged = true; 
    const isOwner = true;
    const canDeleteList = true;
    const canDeleteExercises = true;

    return (
        <main className="page">
            {/* HEADER / NAV */}
            <div className="d-flex align-items-center justify-content-between p-3">
                <div className="brand">
                    <Link to="/" className="brand-mark-link">
                        <img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" />
                    </Link>
                    <Link to="/" className="text-decoration-none">
                        <span className="brand-title ms-2">DSGram</span>
                    </Link>
                </div>

                {logged ? (
                    <div className="profile-image d-flex align-items-center gap-2">
                        <Link to="/profile">
                            <div className="avatar avatar--img">
                                {/* Lógica: Si hay foto, img. Si no, iniciales */}
                                {user.photo ? (
                                    <img src={`/images/${user.photo}`} alt="Profile" />
                                ) : (
                                    <span className="p-2 border rounded-circle bg-light">{user.nameInitial}</span>
                                )}
                            </div>
                        </Link>
                    </div>
                ) : (
                    <Link className="btn ghost" to="/login">Log in</Link>
                )}
            </div>

            <Container>
                <Row className="justify-content-center">
                    <Col xs={12} md={10} lg={8}>
                        {/* SECCIÓN CABECERA DE LA LISTA */}
                        <section className="content-section mb-4">
                            <div className="content-section__header">
                                <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3 mb-3">
                                    <div>
                                        <h2 className="content-section__title mb-2">{list.title}</h2>
                                        <p className="content-section__meta text-muted mb-0">
                                            Created by {list.owner.name} · Last update: {list.lastUpdate}
                                        </p>
                                    </div>

                                    {canDeleteList && (
                                        <div className="d-flex gap-2">
                                            {/* Nota: En React es mejor usar Modales de React-Bootstrap que data-bs-toggle */}
                                            <Button variant="outline-danger" size="sm">Delete list</Button>
                                        </div>
                                    )}
                                </div>
                                <p className="content-section__description">{list.description}</p>
                            </div>
                        </section>

                        {/* SECCIÓN EJERCICIOS */}
                        <section className="content-section">
                            <h3 className="content-section__subtitle mb-4">Exercises</h3>
                            <div className="row g-4 mb-4">
                                {list.exercises && list.exercises.length > 0 ? (
                                    list.exercises.map((exercise, index) => (
                                        <div key={exercise.id} className="col-12 col-md-6">
                                            <div className="exercise-card position-relative border p-3 rounded shadow-sm">
                                                <div className="exercise-card__header d-flex justify-content-between">
                                                    <span className="badge bg-secondary mb-2">Exercise {index + 1}</span>
                                                    {/* Acciones de edición/borrado */}
                                                    {(isOwner || canDeleteExercises) && (
                                                        <div className="d-flex gap-2 position-relative z-3">
                                                            {isOwner && (
                                                                <Link to={`/edit-exercise/${exercise.id}`} className="text-primary">
                                                                    <i className="bi bi-pencil"></i>
                                                                </Link>
                                                            )}
                                                            {canDeleteExercises && (
                                                                <button className="btn btn-link p-0 text-danger text-decoration-none">
                                                                    <i className="bi bi-trash"></i>
                                                                </button>
                                                            )}
                                                        </div>
                                                    )}
                                                </div>

                                                <h4 className="exercise-card__title">
                                                    <Link to={`/exercise/${exercise.id}`} className="text-decoration-none text-reset stretched-link">
                                                        {exercise.title}
                                                    </Link>
                                                </h4>
                                                <p className="exercise-card__description small text-muted">{exercise.description}</p>
                                                <div className="exercise-card__footer mt-3">
                                                    <span className="exercise-card__solutions text-info">{exercise.numSolutions} solutions</span>
                                                </div>
                                            </div>
                                        </div>
                                    ))
                                ) : (
                                    <div className="col-12 text-center">
                                        <p className="text-muted">No exercises in this list yet. {isOwner && "Click the + button to add one!"}</p>
                                    </div>
                                )}
                            </div>

                            {/* BOTÓN AÑADIR */}
                            {isOwner && (
                                <div className="row g-3 justify-content-center mb-4">
                                    <Link className="btn btn-primary rounded-circle" to={`/list-view/${list.id}/new-exercise`} style={{width: '50px', height: '50px', display: 'flex', alignItems: 'center', justifyContent: 'center'}}>
                                        <i className="bi bi-plus-lg"></i>
                                    </Link>
                                </div>
                            )}

                            {!logged && (
                                <div className="text-center">
                                    <p className="text-muted mb-0">Log in to create, edit or delete content.</p>
                                </div>
                            )}

                            <div className="text-center mt-4">
                                <Link className="btn btn-link text-decoration-none" to={`/profile/${list.owner.id}`}>
                                    Back to {list.owner.name}'s profile
                                </Link>
                            </div>
                        </section>
                    </Col>
                </Row>
            </Container>
        </main>
    );
}