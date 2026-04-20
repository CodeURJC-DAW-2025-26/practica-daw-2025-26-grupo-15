import Alert from "react-bootstrap/esm/Alert";
import Button from "react-bootstrap/esm/Button";
import Form from "react-bootstrap/esm/Form";
import Container from "react-bootstrap/esm/Container";
import Row from "react-bootstrap/esm/Row";
import Col from "react-bootstrap/esm/Col";
import Stack from "react-bootstrap/esm/Stack";
import { Link } from "react-router";
import type { ExercisePostDTO } from "~/dtos/ExercisePostDTO";

interface ExerciseFormProps {
    exercise?: ExercisePostDTO
    actionState: [
        {success: boolean, error: string | null} | null,
        (formData: FormData) => void,
        boolean];
    listId: number;
}

export default function ExerciseForm({exercise, actionState: [state, formAction, isPending], listId}: ExerciseFormProps) {

    return (
        <Container>
            <Row className="justify-content-center">
                <Col as="section" xs={12} lg={8} className="hero-card hero-card--full-width hero-card--exercise">
                    <Form action={formAction} method="post" encType="multipart/form-data" className="form-block text-start">
                        
                        <h2 className="text-center mb-5">{exercise ? "Edit exercise" : "New exercise page"}</h2>

                        {state?.error && <Alert variant="danger">{state.error}</Alert>}

                        <Form.Group className="mb-4 w-100" controlId="exname">
                            <Form.Label>Name</Form.Label>
                            <Form.Control
                                type="text"
                                name="exname"
                                placeholder="Name of the exercise"
                                defaultValue={exercise ? exercise.title : ""}
                                className="form-control-custom"
                                required
                                disabled={isPending}
                            />
                            <Form.Text className="text-danger mt-1" id="nameError" />
                        </Form.Group>

                        <Form.Group className="mb-4 w-100" controlId="exdesc">
                            <Form.Label>Description</Form.Label>
                            <Form.Control
                                as="textarea"
                                name="exdesc"
                                placeholder="Describe the exercise"
                                defaultValue={exercise ? exercise.description : ""}
                                className="form-control-custom"
                                rows={5}
                                required
                                disabled={isPending}
                            />
                            <Form.Text className="text-danger mt-1" id="descError" />
                        </Form.Group>

                        <Form.Group className="mb-5 w-100" controlId="pdfFile">
                            <Form.Label>Upload statement</Form.Label>
                            <Form.Control
                                type="file"
                                name="pdfFile"
                                className="form-control-custom"
                                accept="application/pdf"
                                disabled={isPending}
                            />
                        </Form.Group>

                        <Stack direction="horizontal" gap={3} className="justify-content-end w-100 mt-4">
                            <Link
                                to={`/lists/${listId}`}
                                className="btn ghost"
                            >
                                Back
                            </Link>
                            <Button
                                type="submit"
                                bsPrefix="btn"
                                disabled={isPending}
                            >
                                {isPending ? "Saving..." : "Save exercise"}
                            </Button>
                        </Stack>
                    </Form>     
                </Col>
            </Row>
        </Container>
    );
}

