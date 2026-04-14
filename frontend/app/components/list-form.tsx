import { Form, Button, Container, Alert, Image } from "react-bootstrap";
import type ListDTO from "~/dtos/ListDTO";


interface ListFormProps {
    list?: Partial<ListDTO>,
    actionState: [
        { success: boolean; error: string | null } | null,
        (formData: FormData) => void,
        boolean,
    ];
    onCancel: () => void;
}

export default function ListForm({ 
    list, 
    actionState: [state, formAction, isPending],
    onCancel, }: ListFormProps) {

        const isEditing = list?.id;
        return(
            <main className="page">
                <div className="brand">
                    <a href="/" className="brand-mark-link"><img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" /></a>
                    <a href="/"><span className="brand-title">DSGram</span></a>
                </div>

                <div className="container">
                    <div className="row justify-content-center">
                        <section className="hero-card hero-card--full-width hero-card--list col-12 col-lg-8">

                            <div className="form-block text-start">
                                {state?.error && <Alert variant="danger">{state.error}</Alert>}

                                <Form method="post" action= {formAction}>
                                    {isEditing && <input type="hidden" name="id" value={list?.id} />}
                                    <div className="text-center mb-5">
                                        {isEditing ? <h2>Edit list {list.title}</h2> : <h2>Add new list</h2>}
                                    </div>


                                    <Form.Group className="mb-4 w-100" controlId="title">
                                        <Form.Label className="form-label">List name</Form.Label>
                                        <Form.Control 
                                            id="title" 
                                            type="text" 
                                            name="title" 
                                            required minLength={3} maxLength={100} 
                                            className="form-control-custom" 
                                            placeholder="List A"
                                            defaultValue={list?.title || ''}
                                            disabled={isPending}
                                            />
                                    </Form.Group>

                                    <Form.Group className="mb-4 w-100" controlId="topic">
                                        <Form.Label className="form-label">Main topic</Form.Label>
                                        <Form.Control
                                            id="topic"
                                            type="text"
                                            name="topic"
                                            required
                                            minLength={3}
                                            maxLength={100}
                                            className="form-control-custom"
                                            placeholder="Graphs, trees..."
                                            defaultValue={list?.topic || ''}
                                            disabled={isPending}
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-4 w-100" controlId="description">
                                        <Form.Label className="form-label">Description</Form.Label>
                                        <Form.Control
                                            as="textarea"
                                            id="description"
                                            rows={4}
                                            name="description"
                                            required
                                            minLength={10}
                                            maxLength={10000}
                                            className="form-control-custom"
                                            placeholder="Brief description"
                                            defaultValue={list?.description || ''}
                                            disabled={isPending}
                                        />
                                    </Form.Group>

                                    <div className="d-flex justify-content-end gap-3 w-100 mt-4">
                                        <Button 
                                            type="button" 
                                            className="btn ghost" 
                                            onClick={onCancel}
                                            disabled={isPending}>
                                                Back
                                        </Button>
                                        <Button 
                                            type="submit" 
                                            className="btn">
                                                {isPending ? "Saving..." : "Save"}
                                        </Button>
                                    </div>
                                </Form>
                            </div>
                        </section>
                    </div>
                </div>
            </main>
        );
}