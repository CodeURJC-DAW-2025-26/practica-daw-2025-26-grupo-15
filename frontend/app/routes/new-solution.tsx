import { useActionState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import { Container, Row, Col, Image, Button, Stack } from "react-bootstrap";
import SolutionForm from "~/components/solution-form";
import { addSolution, uploadSolutionImage } from "~/services/solution-service";
import { useUserStore } from "~/stores/user-store";
import { requireUser } from "~/services/route-guards-service";

export async function clientLoader() {
    await requireUser();
}

export default function NewSolution() {
    const { exerciseId } = useParams();
    const user = useUserStore((state) => state.user);
    const logged = user !== null;
    const navigate = useNavigate();

    if (!exerciseId) {
        throw new Error("Error adding solution, must need exerciseId in URL params");
    }

    async function saveSolutionAction(
        prevState: { success: boolean; error: string | null } | null,
        formData: FormData,
    ) {
        const name = formData.get("name") as string;
        const description = formData.get("description") as string;
        const imageFile = formData.get("imageFile") as File;

        try {
            const newSolution = await addSolution(exerciseId!, name, description);
           
            if (imageFile && imageFile.size > 0) {
                await uploadSolutionImage(newSolution.id, imageFile);
            }

            navigate(`/solutions/${newSolution.id}`);
            return { success: true, error: null };
        } catch (error) {
            console.error(error);
            return {
                success: false,
                error: "Failed to save solution. Please try again.",
            };
        }
    }

    const [state, formAction, isPending] = useActionState(saveSolutionAction, null);

    return (
        <Container as="main" className="py-4">
            {/* Header Section */}
            <Row className="align-items-center mb-5">
                <Col>
                    <Stack direction="horizontal" gap={2} className="brand">
                        <Link to="/" className="brand-mark-link">
                            <Image 
                                src="/assets/DSGram_LOGO.png" 
                                alt="DSGram logo" 
                                width={40}
                                height={40}
                            />
                        </Link>
                        <Link to="/" className="text-decoration-none">
                            <span className="brand-title fs-4 fw-bold text-dark">DSGram</span>
                        </Link>
                    </Stack>
                </Col>

                <Col xs="auto">
                    {logged ? (
                        <Link to="/profile">
                            <div className="avatar avatar--img overflow-hidden rounded-circle" style={{ width: '40px', height: '40px' }}>
                                {user?.photo ? (
                                    <Image 
                                        src={`/api/v1/images/${user.photo.id}/media`} 
                                        alt="Profile photo" 
                                        fluid 
                                    />
                                ) : (
                                    <div className="bg-primary text-white d-flex align-items-center justify-content-center h-100">
                                        {user?.name.charAt(0).toUpperCase()}
                                    </div>
                                )}
                            </div>
                        </Link>
                    ) : (
                        <Button variant="outline-secondary" onClick={() => navigate("/login")}>
                            Log in
                        </Button>
                    )}
                </Col>
            </Row>

            <Row className="justify-content-center">
                <Col xs={12} md={10} lg={8}>
                    <SolutionForm
                        actionState={[state, formAction, isPending]}
                        onCancel={() => navigate("/")}
                    />
                </Col>
            </Row>
        </Container>
    );
}