import { addComment, getSolution } from '~/services/solution-service';
import type { Route } from './+types/solution';
import { useUserStore } from '~/stores/user-store';
import { findCommentsBySolutionId } from '~/services/solution-service';
import { Form, Link, useActionData, useNavigation, useRevalidator } from 'react-router';
import { useActionState } from 'react';
import CommentForm from '~/components/comment-form';
import { useNavigate } from "react-router";


export async function clientLoader({ params } : Route.ClientLoaderArgs) {
    const solution = await getSolution(params.id!);
    const comments = await findCommentsBySolutionId(params.id!);
    return { solution, comments };
}



export default function Solution({ loaderData }: Route.ComponentProps) {
    const { solution, comments } = loaderData;
    let { user } = useUserStore();
    const navigate = useNavigate();
    const revalidator = useRevalidator();
    
    const logged = user !== null; 
    const userOwner = solution.owner;
    const canDeleteSolution = logged && user === userOwner; 
    const hasComments = comments.length > 0;
    const token = "sample-csrf-token"; // CSRF token for forms
  
    async function saveCommentAction(
      prevState: {
        success: boolean;
        error: string | null;
        } | null,
        formData: FormData,
    )
    {
        const text = formData.get("text") as string;

        try {
            await addComment(solution.id, text);
            revalidator.revalidate();
            return { success: true, error: null };
        } catch (error) {
            console.error(error);
            return {
                success: false,
                error: "Failed to add comment. Please try again.",
            };
        }
    }
    const [state, formAction, isPending] = useActionState(saveCommentAction, null);

    return (
        <>
        <main className="page">
    <div className="d-flex flex align-items-center justify-content-between">
      <div className="brand">
        <a href="/" className="brand-mark-link"><img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" /></a>
      <a href="/"><span className="brand-title">DSGram</span></a>
      </div>
      {/* Header section: shows user profile if logged in, otherwise login button */}
      {logged ? (
      <div className="profile-image d-flex align-items-center gap-2">
        <a href="/profile">
          <div className="avatar avatar--img">
            {user.photo.id ? <img src={`/images/${user.photo.id}`} alt="Profile photo" /> : <span>{user.name.charAt(0)}</span>}
          </div>
        </a>
      </div>
      ) : (
      <a className="btn ghost" href="/login">Log in</a>
      )}
    </div>

    <div className="container">
      <div className="row justify-content-center">
        <div className="col-12 col-lg-10">
          <section className="content-section mb-4">
            <div className="content-section__header">
              <div
                className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3 mb-3">
                <div>
                  <h2 className="content-section__title mb-2">{solution.name}</h2>
                  <p className="content-section__meta text-muted mb-0">
                    By {solution.owner.name} · For Exercise: {solution.exercise.title}
                  </p>
                  <small className="text-secondary">Submitted: {solution.lastUpdate}</small>
                </div>
                {/* Actions section: export and delete buttons if logged in and can delete */}
                {logged && (
                <div className="card-actions d-flex flex-row align-items-center gap-2">
                  <a href={`/solution/${solution.id}/export/pdf`} className="btn solution-export-btn">
                    <i className="bi bi-file-earmark-pdf"></i>
                    Download PDF
                  </a>
                  {canDeleteSolution && (
                  <button type="button" className="btn-icon d-flex align-items-center justify-content-center"
                    data-bs-toggle="modal" data-bs-target={`#deleteSolutionModal${solution.id}`}>
                    <i className="bi bi-trash"></i>
                  </button>
                  )}
                </div>
                )}
              </div>

              <div className="solution-description mt-3">
                <p className="solution-description__label mb-2">Solution Description</p>
                <p className="solution-description__text mb-0">{solution.description}</p>
              </div>
            </div>

            {/* Delete solution modal: appears if user can delete */}
            {canDeleteSolution && (
            <div className="modal fade" id={`deleteSolutionModal${solution.id}`} tabIndex={-1}
              aria-labelledby={`deleteSolutionModalLabel${solution.id}`} aria-hidden="true">
              <div className="modal-dialog modal-dialog-centered">
                <div className="modal-content modal-content-themed">
                  <div className="modal-header border-0">
                    <h5 className="modal-title" id={`deleteSolutionModalLabel${solution.id}`}>Confirm deletion</h5>
                    <button type="button" className="btn-close btn-close-white" data-bs-dismiss="modal"
                      aria-label="Close"></button>
                  </div>
                  <div className="modal-body">
                    <p className="mb-0">Are you sure you want to delete the solution "<strong>{solution.name}</strong>"?
                    </p>
                    <p className="text-muted mt-2 mb-0">This action cannot be undone.</p>
                  </div>
                  <div className="modal-footer border-0">
                    <button type="button" className="btn secondary" data-bs-dismiss="modal">Cancel</button>
                    <form method="post" action={`/exercise/${solution.exercise.id}/solution/${solution.id}/delete`}
                      className="d-inline">
                      <input type="hidden" name="_csrf" value={token} />
                      <button type="submit" className="btn btn-danger-action">Delete
                        solution</button>
                    </form>
                  </div>
                </div>
              </div>
            </div>
            )}

            {/* Solution image section: shows image if exists and user is logged in, otherwise locked overlay */}
            {solution.solImage && (
            <div className="solution-content mt-4">
              {logged ? (
              <div className="solution-viewer text-center p-2 bg-dark rounded overflow-hidden">
                <img src={`/images/${solution.solImage.id}`} alt="Solution Visualization"
                  className="img-fluid rounded shadow-sm solution-image" />
              </div>
              ) : (
              <div className="solution-viewer solution-viewer--locked">
                <div className="solution-viewer__overlay">
                  <i className="bi bi-lock-fill solution-viewer__lock-icon" aria-hidden="true"></i>
                  <p className="solution-viewer__lock-title">Solution image locked</p>
                  <p className="solution-viewer__lock-sub">Log in to view the full solution image.</p>
                  <a className="btn solution-viewer__login-btn" href="/login">Log in</a>
                </div>
              </div>
              )}
            </div>
            )}
          </section>

          <section className="content-section">
            <h3 className="content-section__subtitle mb-4">Comments ({hasComments ? solution.numComments : 0})</h3>
            <div className="comment-list mb-4">
              {/* Deletable comments: comments that the user can delete */}
              {comments.map(comment => (
              <div key={comment.id} className="comment-item">
                <div className="comment-item__header d-flex justify-content-between align-items-center">
                  <div className="d-flex align-items-center gap-2">
                    {comment.owner.photo ? (
                    <div className="avatar-sm avatar--img">
                      <img src={`/images/${comment.owner.photo.id}`} alt="avatar"
                        className="avatar-image-cover" />
                    </div>
                    ) : (
                    <div className="avatar-sm">{comment.owner.name.charAt(0)}</div>
                    )}
                    <div>
                      <strong className="comment-item__author">{comment.owner.name}</strong>
                      <span className="comment-item__date">{comment.lastUpdate}</span>
                    </div>
                  </div>

                  <button type="button" className="btn-icon" data-bs-toggle="modal"
                    data-bs-target={`#deleteCommentModal${comment.id}`}>
                    <i className="bi bi-trash"></i>
                  </button>
                </div>
                <p className="comment-item__text mt-2">{comment.text}</p>
              </div>
              ))}

              {/* Delete comment modal for each deletable comment */}
              {comments.map(comment => (
              <div key={`modal-${comment.id}`} className="modal fade" id={`deleteCommentModal${comment.id}`} tabIndex={-1} aria-hidden="true">
                <div className="modal-dialog modal-dialog-centered">
                  <div className="modal-content modal-content-themed">
                    <div className="modal-header border-0">
                      <h5 className="modal-title">Delete Comment</h5>
                      <button type="button" className="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div className="modal-body">
                      <p>Are you sure you want to delete this comment?</p>
                    </div>
                    <div className="modal-footer border-0">
                      <button type="button" className="btn secondary" data-bs-dismiss="modal">Cancel</button>
                      <form method="post" action={`/solution/${solution.id}/comment/${comment.id}/delete`}>
                        <input type="hidden" name="_csrf" value={token} />
                        <button type="submit" className="btn btn-danger-action">Delete</button>
                      </form>
                    </div>
                  </div>
                </div>
              </div>
              ))}

              {/* No comments message if there are no comments */}
              {!hasComments && (
              <p className="text-muted">No comments yet. Be the first to help!</p>
              )}
            </div>

            {/* Add comment form: only if logged in */}
            {logged ? ( 
              <CommentForm
                    actionState={[state, formAction, isPending]}
                    onCancel={() => navigate(`/solution/${solution.id}`)}
                />
            ) : 
            (
              <div className="text-center">
                <p className="mb-3">Log in to add a comment.</p>
              </div>
            )}

            <div className="text-center mt-5">
              <Link className="btn ghost" to={`/exercise/${solution.exercise.id}`}>Back to Exercise</Link>
            </div>
          </section>
        </div>
      </div>
    </div>
  </main>

        </>
    );
}