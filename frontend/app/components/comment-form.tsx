import { Form, Button, Container, Alert, Image, Row, Col } from "react-bootstrap";
import type { CommentDTO } from "~/dtos/CommentDTO";
import { InlineActionError } from "./inline-action-error";

interface CommentFormProps {
    comment?: Partial<CommentDTO>,
    actionState: [
        { success: boolean; error: string | null } | null,
        (formData: FormData) => void,
        boolean,
    ];
    onCancel: () => void;
}

export default function CommentForm({
    comment,
    actionState: [state, formAction, isPending],
    onCancel, }: CommentFormProps) {
    const isEditing = comment?.id;
    const errorComment = state?.error || null;
    return (
        <Form action={formAction} className="add-comment-form mt-4">
            {/* Si estamos editando o necesitamos el ID */}
            {isEditing && <input type="hidden" name="id" value={comment?.id} />}

            <Row className="g-3 align-items-end">
                <Col xs={12} md={9}>
                    <Form.Group controlId="new-comment">
                        <Form.Label className="form-label">
                            {isEditing ? "Edit your comment" : "Add your comment"}
                        </Form.Label>
                        <Form.Control
                            name="text"
                            type="text"
                            placeholder="Share your thoughts..."
                            defaultValue={comment?.text}
                            disabled={isPending}
                            required
                            className="form-control"
                        />
                    </Form.Group>
                </Col>

            
                <Col xs={12} md={3}>
                    <div className="d-flex gap-2">
                        <Button
                            type="submit"
                            className="btn w-100"
                            variant="light"
                            disabled={isPending}
                        >
                            {isPending ? "..." : (isEditing ? "Save" : "Comment")}
                        </Button>

                        
                        {isEditing && (
                            <Button variant="secondary" onClick={onCancel}>
                                <i className="bi bi-x"></i>
                            </Button>
                        )}
                    </div>
                </Col>
            </Row>
            <div className="action-error-stack mb-3">
                <InlineActionError message={errorComment} />
            </div>
        </Form>
    );
}
