
// Provisional constants to replace Mustache variables
// These represent data that would come from the backend/server-side rendering
const logged = true; // Boolean indicating if user is logged in
const user = { photo: { id: 123 }, nameInitial: 'A' }; // User object with photo and initial
const solution = { // Solution object with all properties
  id: 1,
  name: "Sample Solution Name",
  owner: { name: "Solution Owner" },
  exercise: { title: "Exercise Title", id: 2 },
  lastUpdate: "2023-01-01",
  description: "This is the solution description.",
  solImage: { id: 456 },
  numComments: 5
};
const canDeleteSolution = true; // Boolean for delete permission
const token = "sample-csrf-token"; // CSRF token for forms
const deletableComments = [ // Array of comments that can be deleted
  {
    id: 10,
    owner: { photo: { id: 789 }, nameInitial: 'B', name: "Commenter Name" },
    lastUpdate: "2023-01-02",
    text: "This is a sample comment text."
  }
];
const readonlyComments = [ // Array of comments that are read-only
  {
    id: 11,
    owner: { photo: { id: 790 }, nameInitial: 'C', name: "Another Commenter" },
    lastUpdate: "2023-01-03",
    text: "Another comment."
  }
];
const hasComments = true; // Boolean indicating if there are any comments

export function Solution() {
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
            {user.photo ? <img src={`/images/${user.photo.id}`} alt="Profile photo" /> : <span>{user.nameInitial}</span>}
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
            <h3 className="content-section__subtitle mb-4">Comments ({solution.numComments})</h3>
            <div className="comment-list mb-4">
              {/* Deletable comments: comments that the user can delete */}
              {deletableComments.map(comment => (
              <div key={comment.id} className="comment-item">
                <div className="comment-item__header d-flex justify-content-between align-items-center">
                  <div className="d-flex align-items-center gap-2">
                    {comment.owner.photo ? (
                    <div className="avatar-sm avatar--img">
                      <img src={`/images/${comment.owner.photo.id}`} alt="avatar"
                        className="avatar-image-cover" />
                    </div>
                    ) : (
                    <div className="avatar-sm">{comment.owner.nameInitial}</div>
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
              {deletableComments.map(comment => (
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

              {/* Read-only comments: comments that cannot be deleted */}
              {readonlyComments.map(comment => (
              <div key={comment.id} className="comment-item">
                <div className="comment-item__header d-flex justify-content-between align-items-center">
                  <div className="d-flex align-items-center gap-2">
                    {comment.owner.photo ? (
                    <div className="avatar-sm avatar--img">
                      <img src={`/images/${comment.owner.photo.id}`} alt="avatar"
                        className="avatar-image-cover" />
                    </div>
                    ) : (
                    <div className="avatar-sm">{comment.owner.nameInitial}</div>
                    )}
                    <div>
                      <strong className="comment-item__author">{comment.owner.name}</strong>
                      <span className="comment-item__date">{comment.lastUpdate}</span>
                    </div>
                  </div>
                </div>
                <p className="comment-item__text mt-2">{comment.text}</p>
              </div>
              ))}

              {/* No comments message if there are no comments */}
              {!hasComments && (
              <p className="text-muted">No comments yet. Be the first to help!</p>
              )}
            </div>

            {/* Add comment form: only if logged in */}
            {logged ? (
            <form className="add-comment-form" action={`/solution/${solution.id}/comment`} method="post">
              <div className="row g-3 align-items-end">
                <div className="col-12 col-md-9">
                  <label htmlFor="new-comment" className="form-label">Add your comment</label>
                  <input id="new-comment" name="text" className="form-control" type="text"
                    placeholder="Share your thoughts..." required />
                </div>
                <div className="col-12 col-md-3">
                  <button className="btn w-100" type="submit">Comment</button>
                </div>
              </div>
              <input type="hidden" name="_csrf" value={token} />
            </form>
            ) : (
            <div className="text-center">
              <p className="text-muted mb-0">Log in to comment, export PDF or manage this solution.</p>
            </div>
            )}

            <div className="text-center mt-5">
              <a className="btn ghost" href={`/exercise/${solution.exercise.id}`}>Back to Exercise</a>
            </div>
          </section>
        </div>
      </div>
    </div>
  </main>

        </>
    );
}