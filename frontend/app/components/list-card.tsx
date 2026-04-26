import { useActionState, useState } from "react";
import { Link } from "react-router";
import { Modal, Button, Form } from "react-bootstrap";
import type ListDTO from "~/dtos/ListDTO";
import type { UserDTO } from "~/dtos/UserDTO";
import "bootstrap-icons/font/bootstrap-icons.css";
import { deleteList } from "~/services/list-service";
import { formatDate } from "~/services/date-service";
import { InlineActionError } from "./inline-action-error";

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
    const formattedLastUpdate = formatDate(list.lastUpdate);

    const [{ errorDelete }, deleteListAction, isDeleting] = useActionState(
        deleteListFormAction,
        { errorDelete: null },
    );

    async function deleteListFormAction(
        _prevState: { errorDelete: string | null },
        _formData: FormData,
    ) {
        try {
            await deleteList(list.id);
            onListDeleted(list.id);
            setShowDeleteModal(false);
            return { errorDelete: null };
        }
        catch (_error) {
            return { errorDelete: "Error deleting list" };
        }
    }

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
                        <p className="meta">Last update · {formattedLastUpdate}</p>
                    </div>

                    {isOwnProfile && (
                        <div className="card-actions d-flex flex-column align-items-end g-1">
                            <Button className="btn-icon btn-icon-edit" type="button">
                                <Link to={`/lists/edit/${list.id}`}>
                                    <i className="bi bi-pencil icon-static"></i>
                                    <img
                                        src="/assets/pencilanimated.gif"
                                        alt="Edit"
                                        className="icon-gif"
                                    />
                                </Link>
                            </Button>
                            {canDeleteLists && (
                                <Button
                                    type="button"
                                    className="btn-icon"
                                    onClick={handleDeleteClick}
                                >
                                    <i className="bi bi-trash"></i>
                                </Button>
                            )}
                        </div>
                    )}
                    {!isOwnProfile && canDeleteLists && (
                        <div className="card-actions d-flex flex-column align-items-end g-1">
                            <Button
                                type="button"
                                className="btn-icon"
                                onClick={handleDeleteClick}
                            >
                                <i className="bi bi-trash"></i>
                            </Button>
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
                    <div className="mt-3">
                        <InlineActionError message={errorDelete} />
                    </div>
                </Modal.Body>
                <Modal.Footer className="border-0">
                    <Button
                        variant="secondary"
                        onClick={() => setShowDeleteModal(false)}
                    >
                        Cancel
                    </Button>
                    <Form action={deleteListAction}>
                        <Button
                            variant="danger"
                            type="submit"
                            disabled={isDeleting}
                        >
                            {isDeleting ? "Deleting..." : "Delete list"}
                        </Button>
                    </Form>
                </Modal.Footer>
            </Modal>
        </>
    );
}
