import { useState } from "react";
import { Button, Col, Modal } from "react-bootstrap";
import { Link, useNavigate } from "react-router";
import type { SolutionBasicInfoDTO } from "~/dtos/SolutionBasicInfoDTO";
import { formatDate } from "~/services/date-service";
import { deleteSolution } from "~/services/solution-service";


export default function SolutionCard({ solution, index, deletable }: { solution: SolutionBasicInfoDTO, index: number, deletable: boolean }) {
    const navigate = useNavigate();


    const [solutionToDelete, setSolutionToDelete] = useState<SolutionBasicInfoDTO | null>(null);

    function handleOpenDeleteSolution(solution: SolutionBasicInfoDTO) {
        setSolutionToDelete(solution);
    }

    function handleCloseDeleteSolutionModal() {
        setSolutionToDelete(null);
    }

    async function handleConfirmDeleteSolution() {
        if (!solutionToDelete) return;

        try {
            await deleteSolution(solutionToDelete.id);
            setSolutionToDelete(null);
            navigate(".");
        } catch (error) {
            alert("Failed to delete the solution. Please try again later.");
        }
    }
    return (
        <Col key={solution.id} xs={12} md={6}>
            <div className="solution-card position-relative">
                <div className="solution-card__header d-flex justify-content-between align-items-start gap-2">
                    <div>
                        <span className="solution-card__badge">Solution {index + 1}</span>
                        <span className="solution-card__date">{formatDate(solution.lastUpdate)}</span>
                    </div>
                    {deletable && (
                        <button
                            type="button"
                            className="btn-icon position-relative z-3"
                            onClick={(e) => {
                                e.preventDefault();
                                e.stopPropagation();
                                handleOpenDeleteSolution(solution);
                            }}
                        >
                            <i className="bi bi-trash"></i>
                        </button>
                    )}
                </div>

                <div className="solution-card__author">
                    {solution.owner.photo ? (
                        <div className="avatar-sm avatar--img"><img src={`/api/v1/images/${solution.owner.photo.id}/media`} alt="avatar" className="avatar-image-cover" /></div>
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

            {deletable && (
                <Modal show={solutionToDelete !== null} onHide={handleCloseDeleteSolutionModal} centered>
                    <div className="modal-content-themed">
                        <Modal.Header className="border-0">
                            <Modal.Title className="h5">Confirm deletion</Modal.Title>
                            <button
                                type="button"
                                className="btn-close btn-close-white"
                                onClick={handleCloseDeleteSolutionModal}
                            />
                        </Modal.Header>

                        <Modal.Body>
                            <p className="mb-0">Are you sure you want to delete solution <span className="fw-bold">"{solutionToDelete?.name}"</span>?</p>
                            <p className="text-muted mt-2 mb-0">This action cannot be undone.</p>
                        </Modal.Body>

                        <Modal.Footer className="border-0">
                            <button
                                type="button"
                                className="btn secondary"
                                onClick={handleCloseDeleteSolutionModal}
                            >
                                Cancel
                            </button>

                            <button
                                type="button"
                                className="btn btn-danger-action"
                                onClick={handleConfirmDeleteSolution}
                            >
                                Delete solution
                            </button>
                        </Modal.Footer>
                    </div>
                </Modal>
            )}
        </Col>
    );
}