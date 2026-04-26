import { useState } from 'react';
import { 
    addComment, 
    getSolution, 
    findCommentsBySolutionId, 
    deleteSolution,
    exportToPdf, 
} from '~/services/solution-service';
import { deleteComment } from '~/services/comment-service';
import type { Route } from './+types/solution';
import { useUserStore } from '~/stores/user-store';
import { Link, useRevalidator, useNavigate } from 'react-router';
import { useActionState } from 'react';
import { Container, Row, Col, Button, Modal, Image } from 'react-bootstrap';
import CommentForm from '~/components/comment-form';

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    const solution = await getSolution(params.id!);
    const comments = await findCommentsBySolutionId(params.id!);
    return { solution, comments };
}

export default function Solution({ loaderData }: Route.ComponentProps) {
    const { solution, comments } = loaderData;
    const { user } = useUserStore();
    const navigate = useNavigate();
    const revalidator = useRevalidator();

    const logged = user !== null;
    const userOwner = solution.owner;
    const canDeleteSolution = logged && user?.id === userOwner?.id;
    const hasComments = comments.length > 0;

    const [showDeleteSolution, setShowDeleteSolution] = useState(false);
    const [commentToDelete, setCommentToDelete] = useState<any>(null);

    const handleConfirmDeleteSolution = async () => {
        try {
            await deleteSolution(solution.id);
            setShowDeleteSolution(false);
            navigate(`/exercise/${solution.exercise.id}`);
        } catch (error) {
            alert("Failed to delete the solution. Please try again.");
        }
    };

    const handleConfirmDeleteComment = async () => {
        if (!commentToDelete) return;
        try {
            await deleteComment(commentToDelete.id);
            setCommentToDelete(null);
            revalidator.revalidate(); // Refresca los comentarios
        } catch (error) {
            alert("Failed to delete the comment.");
        }
    };

    async function saveCommentAction(prevState: any, formData: FormData) {
        const text = formData.get("text") as string;
        try {
            await addComment(solution.id, text);
            revalidator.revalidate();
            return { success: true, error: null };
        } catch (error) {
            return { success: false, error: "Failed to add comment." };
        }
    }
    const [state, formAction, isPending] = useActionState(saveCommentAction, null);

    const handlePdfDownload = async (solutionId: number, solutionName: string) => {
        try {
            const pdfBlob = await exportToPdf(solutionId);
            const url = window.URL.createObjectURL(pdfBlob);

            const link = document.createElement('a');
            link.href = url;
            link.download = `solution-${solutionId}-${solutionName}.pdf`;
            document.body.appendChild(link);
            link.click();

            // Cleanup
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error('PDF download failed:', error);
        }
    };

    return (
        <main className="page">
            {/* HEADER / NAVBAR */}
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
                                {user.photo?.id ? (
                                    <img src={`/api/v1/images/${user.photo.id}/media`} alt="Profile" />
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
                        {/* SOLUTION HEADER */}
                        <section className="content-section mb-4">
                            <div className="content-section__header">
                                <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3 mb-3">
                                    <div>
                                        <h2 className="content-section__title mb-2">{solution.name}</h2>
                                        <p className="content-section__meta text-muted mb-0">
                                            By {solution.owner.name} · For Exercise: {solution.exercise.title}
                                        </p>
                                        <small className="text-secondary">Submitted: {solution.lastUpdate}</small>
                                    </div>

                                    <div className="card-actions d-flex align-items-center gap-2">
                                       
                                         <Button
                                            onClick={() => handlePdfDownload(solution.id, solution.name)}
                                            className="solution-export-btn"
                                        >
                                            <i className="bi bi-file-earmark-pdf me-1"></i> PDF
                                        </Button>
                                        {canDeleteSolution && (
                                            <Button 
                                                variant="link" 
                                                className="btn-icon text-danger"
                                                onClick={() => setShowDeleteSolution(true)}
                                            >
                                                <i className="bi bi-trash"></i>
                                            </Button>
                                        )}
                                    </div>
                                </div>

                                <div className="solution-description mt-3">
                                    <p className="solution-description__label mb-2">Solution Description</p>
                                    <p className="solution-description__text mb-0">{solution.description}</p>
                                </div>
                            </div>

                            {/*SOLUTION IMAGE */}
                            {solution.solImage && (
                                <div className="solution-content mt-4">
                                    {logged ? (
                                        <div className="solution-viewer text-center p-2 bg-dark rounded overflow-hidden">
                                            <Image 
                                                src={`/api/v1/images/${solution.solImage.id}/media`} 
                                                fluid 
                                                className="rounded shadow-sm solution-image" 
                                            />
                                        </div>
                                    ) : (
                                        <div className="solution-viewer solution-viewer--locked">
                                            <div className="solution-viewer__overlay text-center">
                                                <i className="bi bi-lock-fill solution-viewer__lock-icon"></i>
                                                <p className="solution-viewer__lock-title">Solution image locked</p>
                                                <p className="solution-viewer__lock-sub small">Log in to view the full image.</p>
                                                <Link className="btn solution-viewer__login-btn mt-2" to="/login">Log in</Link>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            )}
                        </section>

                        {/* COMMENTS SECTION */}
                        <section className="content-section">
                            <h3 className="content-section__subtitle mb-4">
                                Comments ({hasComments ? comments.length : 0})
                            </h3>
                            
                            <div className="comment-list mb-4">
                                {comments.map(comment => (
                                    <div key={comment.id} className="comment-item">
                                        <div className="comment-item__header d-flex justify-content-between align-items-center">
                                            <div className="d-flex align-items-center gap-2">
                                                <div className={`avatar-sm ${comment.owner.photo ? 'avatar--img' : ''}`}>
                                                    {comment.owner.photo ? (
                                                        <img src={`/api/v1/images/${comment.owner.photo.id}/media`} alt="avatar" />
                                                    ) : (
                                                        <span>{comment.owner.name.charAt(0)}</span>
                                                    )}
                                                </div>
                                                <div>
                                                    <strong className="comment-item__author">{comment.owner.name}</strong>
                                                    <span className="comment-item__date ms-2">{comment.lastUpdate}</span>
                                                </div>
                                            </div>

                                            {/* Comment owner */}
                                            {(logged && (user.id === comment.owner.id || canDeleteSolution)) && (
                                                <Button 
                                                    variant="link" 
                                                    className="btn-icon"
                                                    onClick={() => setCommentToDelete(comment)}
                                                >
                                                    <i className="bi bi-trash"></i>
                                                </Button>
                                            )}
                                        </div>
                                        <p className="comment-item__text mt-2">{comment.text}</p>
                                    </div>
                                ))}

                                {!hasComments && (
                                    <p className="text-muted">No comments yet. Be the first to help!</p>
                                )}
                            </div>

                            {/* COMMENT FORM */}
                            {logged ? (
                                <CommentForm
                                    actionState={[state, formAction, isPending]}
                                    onCancel={() => navigate(`/solution/${solution.id}`)}
                                />
                            ) : (
                                <div className="text-center p-4 border rounded dashed">
                                    <p className="mb-0 text-muted">Log in to add a comment.</p>
                                </div>
                            )}

                            <div className="text-center mt-5">
                                <Link className="btn ghost" to={`/exercise/${solution.exercise.id}`}>
                                    Back to Exercise
                                </Link>
                            </div>
                        </section>
                    </Col>
                </Row>
            </Container>

            {/* DELETE SOLUTION MODAL */}
            <Modal show={showDeleteSolution} onHide={() => setShowDeleteSolution(false)} centered>
                <div className="modal-content-themed">
                    <Modal.Header className="border-0">
                        <Modal.Title className="h5">Confirm Deletion</Modal.Title>
                        <Button variant="white" onClick={() => setShowDeleteSolution(false)} />
                    </Modal.Header>
                    <Modal.Body>
                        <p>Are you sure you want to delete "<strong>{solution.name}</strong>"?</p>
                        <p className="text-muted mt-2 mb-0">This action cannot be undone.</p>
                    </Modal.Body>
                    <Modal.Footer className="border-0">
                        <Button className="secondary" onClick={() => setShowDeleteSolution(false)}>Cancel</Button>
                        <Button className="btn-danger-action" onClick={handleConfirmDeleteSolution}>Delete Solution</Button>
                    </Modal.Footer>
                </div>
            </Modal>

            {/* DELETE COMMENT MODAL */}
            <Modal show={commentToDelete !== null} onHide={() => setCommentToDelete(null)} centered>
                <div className="modal-content-themed">
                    <Modal.Header className="border-0">
                        <Modal.Title className="h5">Delete Comment</Modal.Title>
                        <Button variant="white" onClick={() => setCommentToDelete(null)} />
                    </Modal.Header>
                    <Modal.Body>
                        <p>Are you sure you want to delete this comment?</p>
                    </Modal.Body>
                    <Modal.Footer className="border-0">
                        <Button className="secondary" onClick={() => setCommentToDelete(null)}>Cancel</Button>
                        <Button className="btn-danger-action" onClick={handleConfirmDeleteComment}>Delete</Button>
                    </Modal.Footer>
                </div>
            </Modal>
        </main>
    );
}