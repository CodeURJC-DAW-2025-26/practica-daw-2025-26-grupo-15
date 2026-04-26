import Alert from "react-bootstrap/esm/Alert";
import Button from "react-bootstrap/esm/Button";
import Form from "react-bootstrap/esm/Form";
import Container from "react-bootstrap/esm/Container";
import Row from "react-bootstrap/esm/Row";
import Col from "react-bootstrap/esm/Col";
import Stack from "react-bootstrap/esm/Stack";
import { Link, useParams } from "react-router";
import type { ExerciseDTO } from "~/dtos/ExerciseDTO";
import { useNavigate } from "react-router";
import { InlineActionError } from "./inline-action-error";


interface ExerciseFormProps {
    exercise?: Partial<ExerciseDTO>,
    actionState: [
        { success: boolean, error: string | null } | null,
        (formData: FormData) => void,
        boolean
    ];
    onCancel?: () => void;
}

export default function ExerciseForm({ exercise, actionState: [state, formAction, isPending], onCancel }: ExerciseFormProps) {
    const isEditing = !!exercise?.id;
    const errorExercise = state?.error || null;

    return (
        <Container>
            <Row className="justify-content-center">
                <Col as="section" xs={12} lg={8} className="hero-card hero-card--full-width hero-card--exercise">

                    <Form action={formAction} className="form-block text-start">

                        <h2 className="text-center mb-5">
                            {isEditing ? "Edit exercise" : "New exercise page"}
                        </h2>

                        <div className="action-error-stack mb-3">
                            <InlineActionError message={errorExercise} />
                        </div>

                        {isEditing && <input type="hidden" name="id" value={exercise.id} />}

                        <Form.Group className="mb-4 w-100" controlId="exname">
                            <Form.Label>Name</Form.Label>
                            <Form.Control
                                type="text"
                                name="exname"
                                placeholder="Name of the exercise"
                                className="form-control-custom"
                                required
                                disabled={isPending}
                                defaultValue={isEditing ? exercise.title : ""}
                            />
                            <Form.Text className="text-danger mt-1" id="nameError" />
                        </Form.Group>

                        <Form.Group className="mb-4 w-100" controlId="exdesc">
                            <Form.Label>Description</Form.Label>
                            <Form.Control
                                as="textarea"
                                name="exdesc"
                                placeholder="Describe the exercise"
                                className="form-control-custom"
                                rows={5}
                                required
                                disabled={isPending}
                                defaultValue={isEditing ? exercise.description : ""} // 2. Rellenamos datos
                            />
                            <Form.Text className="text-danger mt-1" id="descError" />
                        </Form.Group>

                        <Form.Group className="mb-5 w-100" controlId="pdfFile">
                            <Form.Label>
                                {isEditing ? "Update statement (optional)" : "Upload statement"}
                            </Form.Label>
                            <Form.Control
                                type="file"
                                name="pdfFile"
                                className="form-control-custom"
                                accept="application/pdf"
                                disabled={isPending}
                            />
                            {isEditing && (
                                <Form.Text className="text-muted mt-1">
                                    Leave empty to keep the current PDF file.
                                </Form.Text>
                            )}
                        </Form.Group>

                        <Stack direction="horizontal" gap={3} className="justify-content-end w-100 mt-4">
                            <Link
                                to={`/lists/${exercise?.exerciseList!.id}`}
                                className="btn ghost"
                            >
                                Back
                            </Link>
                            <Button
                                type="submit"
                                bsPrefix="btn"
                                disabled={isPending}
                            >

                                {isPending ? "Saving..." : (isEditing ? "Update exercise" : "Save exercise")}
                            </Button>
                        </Stack>
                    </Form>
                </Col>
            </Row>
        </Container>
    );
}