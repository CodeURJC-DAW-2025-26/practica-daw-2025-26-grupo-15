import { useState } from 'react';
import { Link, useNavigate } from 'react-router';
import { Container, Row, Col, Modal, Button } from 'react-bootstrap';
import { getExerciseListById, deleteList } from '~/services/list-service';
import type { Route } from './+types/list-view';
import { useUserStore } from '~/stores/user-store';

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    return await getExerciseListById(params.id!);
}

export default function ListView({ loaderData }: Route.ComponentProps) {
    const list = loaderData;
    const userOwner = list.owner;
    const { user } = useUserStore();
    const navigate = useNavigate();

    // Estados para los Modales
    const [exerciseToDelete, setExerciseToDelete] = useState<any>(null);

    const logged = user !== null;
    const isOwner = user?.id === userOwner?.id;

    // Supongamos que estas variables vienen de tu lógica de permisos
    const canDeleteList = isOwner; 
    const canDeleteExercises = isOwner;


    const [showDeleteList, setShowDeleteList] = useState(false);
    function handleDeleteList() {
        setShowDeleteList(true);
    }

    function handleCloseDeleteModal() {
        setShowDeleteList(false);
    }

    const handleToDeleteList = async () => {
        try {
            await deleteList(list.id);
            
            // Cerramos el modal por si acaso (aunque vamos a navegar)
            setShowDeleteList(false);
            
            // Redirigimos al perfil del usuario
            navigate(`/users/${userOwner.id}`);
        } catch (error) {
            console.error("Error al borrar la lista:", error);
            alert("No se pudo borrar la lista. Inténtalo de nuevo.");
        }
    };

    return (
        <main className="page">
            {/* HEADER / NAV - Estructura exacta del template */}
            <div className="d-flex align-items-center justify-content-between p-3">
                <div className="brand">
                    <Link to="/" className="brand-mark-link">
                        <img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" />
                    </Link>
                    <Link to="/" className="text-decoration-none">
                        <span className="brand-title">DSGram</span>
                    </Link>
                </div>

                {logged ? (
                    <div className="profile-image d-flex align-items-center gap-2">
                        <Link to="/profile">
                            <div className="avatar avatar--img">
                                {user?.photo?.id ? (
                                    <img src={`/images/${user.photo.id}`} alt="Profile" />
                                ) : (
                                    <span>{user.name.charAt(0)}</span>
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
                    <Col xs={12} lg={10}>
                        {/* SECCIÓN CABECERA DE LA LISTA */}
                        <section className="content-section mb-4">
                            <div className="content-section__header">
                                <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3 mb-3">
                                    <div>
                                        <h2 className="content-section__title mb-2">{list.title}</h2>
                                        <p className="content-section__meta text-muted mb-0">
                                            Created by {list.owner.name} · Last update: {/*list.lastUpdate} */}
                                        </p>
                                    </div>
                                    
                                    {canDeleteList && (
                                        <div className="d-flex gap-2">
                                            <button 
                                                type="button" 
                                                className="btn secondary" 
                                                onClick={handleDeleteList}
                                            >
                                                Delete list
                                            </button>
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
                                            <div className="exercise-card position-relative">
                                                <div className="exercise-card__header">
                                                    <span className="exercise-card__badge">Exercise {index + 1}</span>
                                                    
                                                    {isOwner && (
                                                        <div className="d-flex position-relative z-3">
                                                            <Link to={`/edit-exercise/${exercise.id}`} className="btn-icon btn-icon-edit mx-1">
                                                                <i className="bi bi-pencil icon-static"></i>
                                                                <img src="/assets/pencilanimated.gif" alt="Edit" className="icon-gif" />
                                                            </Link>
                                                            {canDeleteExercises && (
                                                                <button 
                                                                    className="btn-icon" 
                                                                    onClick={() => setExerciseToDelete(exercise)}
                                                                >
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
                                                <p className="exercise-card__description">{exercise.description}</p>
                                                <div className="exercise-card__footer">
                                                    <span className="exercise-card__solutions">{exercise.numSolutions} solutions</span>
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

                            {/* BOTÓN AÑADIR (Plus Button) */}
                            {isOwner && (
                                <div className="row g-3 justify-content-center">
                                    <Link className="btn plus-btn rounded-circle col-12 col-sm-auto" to={`/lists/${list.id}/exercises/new`}>
                                        <i className="bi bi-plus"></i>
                                    </Link>
                                </div>
                            )}

                            {!logged && (
                                <div className="text-center">
                                    <p className="text-muted mb-0">Log in to create, edit or delete content.</p>
                                </div>
                            )}

                            <div className="text-center mt-4">
                                <Link className="btn ghost" to={`/users/${list.owner.id}`}>
                                    Back to {list.owner.name}'s profile
                                </Link>
                            </div>
                        </section>
                    </Col>
                </Row>
            </Container>

            {/* --- MODALES REPLICANDO LAS CLASES DEL TEMPLATE --- */}

            {/* Modal Borrar Lista */}
            <Modal show={showDeleteList} onHide={handleCloseDeleteModal} centered>
                <div className="modal-content-themed">
                    <Modal.Header className="border-0">
                        <Modal.Title className="h5">Confirm Delete List</Modal.Title>
                        <button type="button" className="btn-close btn-close-white" onClick={handleCloseDeleteModal}></button>
                    </Modal.Header>
                    <Modal.Body>
                        <p>Are you sure you want to delete the list "<strong>{list.title}</strong>"?</p>
                        <p className="text-muted mt-2 mb-0">This will delete all exercises and solutions within it. This action cannot be undone.</p>
                    </Modal.Body>
                    <Modal.Footer className="border-0">
                        <button className="btn secondary" onClick={handleCloseDeleteModal}>Cancel</button>
                        <button className="btn btn-danger-action" onClick={handleToDeleteList}>Delete everything</button>
                    </Modal.Footer>
                </div>
            </Modal>

            {/* Modal Borrar Ejercicio */}
            <Modal show={!!exerciseToDelete} onHide={() => setExerciseToDelete(null)} centered>
                <div className="modal-content-themed">
                    <Modal.Header className="border-0">
                        <Modal.Title className="h5">Delete Exercise</Modal.Title>
                        <button type="button" className="btn-close btn-close-white" onClick={() => setExerciseToDelete(null)}></button>
                    </Modal.Header>
                    <Modal.Body>
                        <p>Delete "<strong>{exerciseToDelete?.title}</strong>"?</p>
                    </Modal.Body>
                    <Modal.Footer className="border-0">
                        <button className="btn secondary" onClick={() => setExerciseToDelete(null)}>Cancel</button>
                        <button className="btn btn-danger-action">Delete</button>
                    </Modal.Footer>
                </div>
            </Modal>
        </main>
    );
}