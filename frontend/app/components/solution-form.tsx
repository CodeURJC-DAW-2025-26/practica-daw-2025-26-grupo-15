import { Form, Button, Container, Row, Col } from "react-bootstrap";
import type { SolutionDTO } from "~/dtos/SolutionDTO";
import { InlineActionError } from "./inline-action-error";

interface SolutionFormProps {
    solution?: Partial<SolutionDTO>,
    actionState: [
        { success: boolean; error: string | null } | null,
        (formData: FormData) => void,                      
        boolean,                                           
    ];
    onCancel: () => void;
}

export default function SolutionForm({
    solution,
    actionState: [state, formAction, isPending],
    onCancel,
}: SolutionFormProps) {

    const errorSolution = state?.error || null;

    return (
        <Container>
            <Row className="justify-content-center">
                <Col xs={12} lg={8} as="section" className="hero-card hero-card--full-width hero-card--exercise">
                
                    <Form action={formAction} className="form-block text-start">
                        <div className="text-center mb-5">
                            <h2>Add solution</h2>
                        </div>
                        <div className="action-error-stack mb-3">
                            <InlineActionError message={errorSolution} />
                        </div>
                        <Form.Group className="mb-4 w-100" controlId="solName">
                            <Form.Label className="form-label">Solution Title</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                className="form-control-custom"
                                placeholder="Briefly name your solution"
                                minLength={3}
                                maxLength={100}
                                defaultValue={solution?.name}
                                disabled={isPending}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-4 w-100" controlId="solDesc">
                            <Form.Label className="form-label">Description</Form.Label>
                            <Form.Control
                                as="textarea"
                                name="description"
                                rows={3}
                                minLength={10}
                                maxLength={10000}
                                className="form-control-custom"
                                placeholder="Explain your approach..."
                                defaultValue={solution?.description}
                                disabled={isPending}
                                required
                            />
                        </Form.Group>

                        <Form.Group className="mb-5 w-100" controlId="imageFile">
                            <Form.Label className="form-label">Upload your solution Image</Form.Label>
                            <div className="card w-100 bg-soft border-0 p-5 upload-dropzone">
                                <div className="text-center">
                                    <Form.Control
                                        type="file"
                                        name="imageFile"
                                        accept="image/*"
                                        className="form-control-custom mb-3"
                                        disabled={isPending}
                                        required
                                    />
                                    <p className="text-muted small mb-0">Select the file containing your data structure visualization</p>
                                </div>
                            </div>
                        </Form.Group>

                        
                        
                        <div className="d-flex justify-content-end gap-3 w-100 mt-4">
                            <Button 
                                variant="link" 
                                className="btn ghost text-decoration-none" 
                                onClick={onCancel}
                                disabled={isPending}
                            >
                                Back
                            </Button>
                            
                            <Button 
                                type="submit" 
                                className="btn"
                                disabled={isPending}
                            >
                                {isPending ? "Sending..." : "Send Solution"}
                            </Button>
                        </div>
                    </Form>
                </Col>
            </Row>
        </Container>
    );
}