

// Provisional constants to replace Mustache variables
// These represent data that would come from the backend/server-side rendering
const logged = true; // Boolean indicating if user is logged in
const user = { photo: { id: 123 }, nameInitial: 'A' }; // User object with photo and initial
const exercise = { // Exercise object with all properties
    id: 1,
    title: "Sample Exercise Title",
    description: "This is the exercise description.",
    pdfImage: true // Boolean indicating if PDF exists
};
const list = { // List object containing the exercise
    id: 2,
    title: "Sample List Title",
    owner: { name: "List Owner" }
};
const token = "sample-csrf-token"; // CSRF token for forms
const deletableSolutions = [ // Array of solutions that can be deleted
    {
        id: 10,
        lastUpdate: "2023-01-02",
        owner: { photo: { id: 456 }, nameInitial: 'B', name: "Solution Owner" },
        numComments: 3
    }
];
const readonlySolutions = [ // Array of solutions that are read-only
    {
        id: 11,
        lastUpdate: "2023-01-03",
        owner: { photo: { id: 457 }, nameInitial: 'C', name: "Another Owner" },
        numComments: 1
    }
];
const hasSolutions = true; // Boolean indicating if there are any solutions

export function Exercise() {
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
                                    <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3 mb-3">
                                        <div>
                                            <h2 className="content-section__title mb-2">Exercise {exercise.id}</h2>
                                            <p className="content-section__meta text-muted mb-0">From {list.title} · Created by {list.owner.name}</p>
                                        </div>
                                    </div>
                                </div>

                                <div className="row g-4 mt-2">
                                    <div className="col-12">
                                        <div className="detail-block">
                                            <h4 className="detail-block__label">Name</h4>
                                            <p className="detail-block__value">{exercise.title}</p>
                                        </div>
                                    </div>
                                    <div className="col-12">
                                        <div className="detail-block">
                                            <h4 className="detail-block__label">Description</h4>
                                            <p className="detail-block__value">{exercise.description}</p>
                                        </div>
                                    </div>
                                    {/* PDF link: only shown if logged in and PDF exists */}
                                    {logged && exercise.pdfImage && (
                                        <div className="col-12">
                                            <div className="detail-block">
                                                <h4 className="detail-block__label">PDF statement</h4>
                                                <a href={`/exercise/${exercise.id}/pdf`} className="detail-block__link">
                                                    <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                                        <path d="M14 4.5V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h5.5L14 4.5zm-3 0A1.5 1.5 0 0 1 9.5 3V1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V4.5h-2z" />
                                                    </svg>
                                                    document.pdf
                                                </a>
                                            </div>
                                        </div>
                                    )}
                                </div>
                            </section>

                            <section className="content-section">
                                <h3 className="content-section__subtitle mb-4">Solutions</h3>
                                <div className="row g-4 mb-4">
                                    {/* Deletable solutions: solutions that the user can delete */}
                                    {deletableSolutions.map((solution, index) => (
                                        <div key={solution.id} className="col-12 col-md-6">
                                            <div className="solution-card position-relative">
                                                <div className="solution-card__header d-flex justify-content-between align-items-start gap-2">
                                                    <div>
                                                        <span className="solution-card__badge">Solution {index + 1}</span>
                                                        <span className="solution-card__date">{solution.lastUpdate}</span>
                                                    </div>

                                                    <button type="button" className="btn-icon position-relative z-3" data-bs-toggle="modal" data-bs-target={`#deleteSolutionModal${solution.id}`}>
                                                        <i className="bi bi-trash"></i>
                                                    </button>
                                                </div>

                                                <div className="solution-card__author">
                                                    {solution.owner.photo ? (
                                                        <div className="avatar-sm avatar--img"><img src={`/images/${solution.owner.photo.id}`} alt="avatar" className="avatar-image-cover" /></div>
                                                    ) : (
                                                        <div className="avatar-sm">{solution.owner.nameInitial}</div>
                                                    )}
                                                    <span>{solution.owner.name}</span>
                                                </div>

                                                <div className="solution-card__footer">
                                                    <span className="solution-card__comments">{solution.numComments} comments</span>
                                                </div>

                                                <a href={`/solution/${solution.id}`} className="stretched-link"></a>
                                            </div>

                                            {/* Delete solution modal */}
                                            <div className="modal fade" id={`deleteSolutionModal${solution.id}`} tabIndex={-1} aria-hidden="true">
                                                <div className="modal-dialog modal-dialog-centered">
                                                    <div className="modal-content modal-content-themed">
                                                        <div className="modal-header border-0">
                                                            <h5 className="modal-title">Confirm deletion</h5>
                                                            <button type="button" className="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                                                        </div>
                                                        <div className="modal-body">
                                                            <p className="mb-0">Are you sure you want to delete this solution?</p>
                                                            <p className="text-muted mt-2 mb-0">This action cannot be undone.</p>
                                                        </div>
                                                        <div className="modal-footer border-0">
                                                            <button type="button" className="btn secondary" data-bs-dismiss="modal">Cancel</button>
                                                            <form method="post" action={`/exercise/${exercise.id}/solution/${solution.id}/delete`} className="d-inline">
                                                                <input type="hidden" name="_csrf" value={token} />
                                                                <button type="submit" className="btn btn-danger-action">Delete solution</button>
                                                            </form>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    ))}

                                    {/* Read-only solutions: solutions that cannot be deleted */}
                                    {readonlySolutions.map((solution, index) => (
                                        <div key={solution.id} className="col-12 col-md-6">
                                            <div className="solution-card position-relative">
                                                <div className="solution-card__header d-flex justify-content-between align-items-start gap-2">
                                                    <div>
                                                        <span className="solution-card__badge">Solution {deletableSolutions.length + index + 1}</span>
                                                        <span className="solution-card__date">{solution.lastUpdate}</span>
                                                    </div>
                                                </div>

                                                <div className="solution-card__author">
                                                    {solution.owner.photo ? (
                                                        <div className="avatar-sm avatar--img"><img src={`/images/${solution.owner.photo.id}`} alt="avatar" className="avatar-image-cover" /></div>
                                                    ) : (
                                                        <div className="avatar-sm">{solution.owner.nameInitial}</div>
                                                    )}
                                                    <span>{solution.owner.name}</span>
                                                </div>

                                                <div className="solution-card__footer">
                                                    <span className="solution-card__comments">{solution.numComments} comments</span>
                                                </div>

                                                <a href={`/solution/${solution.id}`} className="stretched-link"></a>
                                            </div>
                                        </div>
                                    ))}

                                    {/* No solutions message if there are no solutions */}
                                    {!hasSolutions && (
                                        <div className="col-12 text-center">
                                            <p className="text-muted">No solutions for this exercise yet. Click the + button to add one!</p>
                                        </div>
                                    )}
                                </div>
                                {/* Add solution button: only if logged in */}
                                {logged ? (
                                    <div className="row g-3 justify-content-center">
                                        <a className="btn plus-btn rounded-circle col-12 col-sm-auto" href={`/add-solution/${exercise.id}`}>
                                            <i className="bi bi-plus"></i>
                                        </a>
                                    </div>
                                ) : (
                                    <div className="text-center">
                                        <p className="text-muted mb-0">Log in to add, edit or delete content.</p>
                                    </div>
                                )}

                                <div className="text-center mt-5">
                                    <a className="btn ghost" href={`/list-view/${list.id}`}>Back to {list.title}</a>
                                </div>
                            </section>
                        </div>
                    </div>
                </div>
            </main>
        </>
    );
}
