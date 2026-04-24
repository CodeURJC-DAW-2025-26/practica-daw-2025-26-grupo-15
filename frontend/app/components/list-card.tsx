import { useState } from "react";
import { Link } from "react-router";
import { Modal, Button } from "react-bootstrap";
import type ListDTO from "~/dtos/ListDTO";
import type { UserDTO } from "~/dtos/UserDTO";
import "bootstrap-icons/font/bootstrap-icons.css";

interface ListCardProps {
    list: ListDTO;
    isOwnProfile: boolean;
    canDeleteLists: boolean;
    currentUser: UserDTO | null;
    onListDeleted: (listId: string) => void;
}

export default function ListCard({
    list,
    isOwnProfile,
    canDeleteLists,
    currentUser,
    onListDeleted,
}: ListCardProps) {
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);

    const handleDeleteConfirm = async () => {
        setIsDeleting(true);
        try {
            //TODO implement delete list method in service
            const response = await fetch(`/api/v1/lists/${list.id}`, {
                method: "DELETE",
            });
            if (response.ok) {
                onListDeleted(list.id);
                setShowDeleteModal(false);
            }
        } catch (error) {
            console.error("Error deleting list:", error);
        } finally {
            setIsDeleting(false);
        }
    };

    const handleDeleteClick = () => {
        setShowDeleteModal(true);
    };

    return (
        <>
            <article className="mt-0 feed-card">
                <header className="d-flex flex-column flex-md-row align-items-start align-items-md-center justify-content-between gap-0">
                    <div>
                        <h3>
                            <Link to={`/lists/${list.id}`} className="list-name">
                                {list.title}
                            </Link>
                        </h3>
                        <p className="meta">Last update · {/*{list.lastUpdated}*/}</p>
                    </div>

                    {isOwnProfile && (
                        <div className="card-actions d-flex flex-column align-items-end g-1">
                            <button className="btn-icon btn-icon-edit" type="button">
                                <Link to={`/lists/edit/${list.id}`}>
                                    <i className="bi bi-pencil icon-static"></i>
                                    <img
                                        src="/assets/pencilanimated.gif"
                                        alt="Edit"
                                        className="icon-gif"
                                    />
                                </Link>
                            </button>
                            {canDeleteLists && (
                                <button
                                    type="button"
                                    className="btn-icon"
                                    onClick={handleDeleteClick}
                                >
                                    <i className="bi bi-trash"></i>
                                </button>
                            )}
                        </div>
                    )}
                    {!isOwnProfile && canDeleteLists && (
                        <div className="card-actions d-flex flex-column align-items-end g-1">
                            <button
                                type="button"
                                className="btn-icon"
                                onClick={handleDeleteClick}
                            >
                                <i className="bi bi-trash"></i>
                            </button>
                        </div>
                    )}
                </header>
                <p className="muted">{list.description}</p>
            </article>

            <Modal
                show={showDeleteModal}
                onHide={() => setShowDeleteModal(false)}
                centered
                className="modal-content-themed"
            >
                <Modal.Header closeButton className="border-0">
                    <Modal.Title>Confirm deletion</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p className="mb-0">
                        Are you sure you want to delete the list "
                        <strong>{list.title}</strong>"?
                    </p>
                    <p className="text-muted mt-2 mb-0">
                        This action cannot be undone.
                    </p>
                </Modal.Body>
                <Modal.Footer className="border-0">
                    <Button
                        variant="secondary"
                        onClick={() => setShowDeleteModal(false)}
                    >
                        Cancel
                    </Button>
                    <Button
                        variant="danger"
                        onClick={handleDeleteConfirm}
                        disabled={isDeleting}
                    >
                        {isDeleting ? "Deleting..." : "Delete list"}
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}
