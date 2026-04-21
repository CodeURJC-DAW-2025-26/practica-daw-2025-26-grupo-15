import { Form, Button, Container, Alert, Image, Row, Col } from "react-bootstrap";
import type { CommentDTO } from "~/dtos/CommentDTO";

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
    return (
        <Form action={formAction} className="add-comment-form mt-4">
            {/* Si estamos editando o necesitamos el ID */}
            {isEditing && <input type="hidden" name="id" value={comment?.id} />}
            
            <Row className="g-3 align-items-end">
                {/* Columna del Input (9 de 12 en MD) */}
                <Col xs={12} md={9}>
                    <Form.Group controlId="new-comment">
                        <Form.Label className="form-label">
                            {isEditing ? "Edit your comment" : "Add your comment"}
                        </Form.Label>
                        <Form.Control
                            name="text"
                            type="text" // Cambiado de textarea a text para que sea igual al tuyo
                            placeholder="Share your thoughts..."
                            defaultValue={comment?.text}
                            disabled={isPending}
                            required
                            className="form-control"
                        />
                    </Form.Group>
                </Col>

                {/* Columna del Botón (3 de 12 en MD) */}
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
                        
                        {/* Botón cancelar opcional por si quieres salir del modo edición */}
                        {isEditing && (
                            <Button variant="secondary" onClick={onCancel}>
                                <i className="bi bi-x"></i>
                            </Button>
                        )}
                    </div>
                </Col>
            </Row>

            {state?.error && (
                <Row className="mt-2">
                    <Col>
                        <Alert variant="danger" className="py-2 mb-0">
                            {state.error}
                        </Alert>
                    </Col>
                </Row>
            )}
        </Form>
    );
}
