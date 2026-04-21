import { Col } from "react-bootstrap";
import { Link } from "react-router";
import type { SolutionBasicInfoDTO } from "~/dtos/SolutionBasicInfoDTO";


export default function SolutionCard({ solution, index, deletable }: { solution: SolutionBasicInfoDTO, index: number, deletable: boolean }) {

    return (
        <Col key={solution.id} xs={12} md={6}>
            <div className="solution-card position-relative">
                <div className="solution-card__header d-flex justify-content-between align-items-start gap-2">
                    <div>
                        <span className="solution-card__badge">Solution {index + 1}</span>
                        <span className="solution-card__date">{/* {solution.lastUpdate.toLocaleDateString()} */}</span>
                    </div>
                    {deletable && (
                        <button type="button" className="btn-icon position-relative z-3" data-bs-toggle="modal" data-bs-target={`#deleteSolutionModal${solution.id}`}>
                            <i className="bi bi-trash"></i>
                        </button>
                    )}
                </div>

                <div className="solution-card__author">
                    {solution.owner.photo ? (
                        <div className="avatar-sm avatar--img"><img src={`/images/${solution.owner.photo.id}`} alt="avatar" className="avatar-image-cover" /></div>
                    ) : (
                        <div className="avatar-sm">{solution.owner.name.charAt(0).toLocaleUpperCase()}</div>
                    )}
                    <span>{solution.owner.name}</span>
                </div>

                <div className="solution-card__footer">
                    <span className="solution-card__comments">{solution.numComments} comments</span>
                </div>

                <Link to={`/solutions/${solution.id}`} className="stretched-link"></Link>
            </div>
            
            {/* Delete solution modal 
            TODO: Implement the delete solution modal form*/}

            {deletable && (
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
                                
                                <form method="delete" action={`/solution/${solution.id}`} className="d-inline">
                                    <button type="submit" className="btn btn-danger-action">Delete solution</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            )}
        </Col>
    );
}