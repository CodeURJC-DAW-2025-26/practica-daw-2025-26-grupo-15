
import type { Route } from "./+types/exercise";
import { Container, Row, Col } from 'react-bootstrap';
import { Link } from 'react-router';
import type ListDTO from "~/dtos/ListDTO";
import type { ExerciseDTO } from "~/dtos/ExerciseDTO";
import type { SolutionBasicInfoDTO } from "~/dtos/SolutionBasicInfoDTO";
import { getExercise, getExercisePdf } from '~/services/exercise-service';
import { getExerciseListById } from "~/services/list-service";
import { useUserStore } from "~/stores/user-store";
import SolutionCard from "~/components/solution-card";


export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  let exercise: ExerciseDTO = await getExercise(params.id!); 
  let list: ListDTO = await getExerciseListById(exercise.exerciseList.id.toString()!);
  return { exercise, list };

}

export default function Exercise({ loaderData }: Route.ComponentProps) {

    const { exercise, list } = loaderData;
    const { user } = useUserStore();
    const userId = user?.id ?? null;
    const logged = userId !== null;

    const hasSolutions = exercise.solutions.length > 0;
    const readonlySolutions = !hasSolutions
        ? []
        : logged
        ? exercise.solutions.filter((solution: SolutionBasicInfoDTO) => solution.owner.id !== userId)
        : exercise.solutions;

    const showDeletableSolutions = hasSolutions && readonlySolutions.length < exercise.solutions.length;
    const deletableSolutions = showDeletableSolutions
        ? exercise.solutions.filter((solution: SolutionBasicInfoDTO) => solution.owner.id === userId)
        : [];

    async function downloadPdf() {
        try {
            const blob = await getExercisePdf(exercise.id);
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            const safeTitle = exercise.title.replace(/[^\w\d-]/g, "_");
            a.download = `exercise-${safeTitle}-${exercise.id}.pdf`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error("Error downloading PDF:", error);
            alert("Sorry, there was an error downloading the PDF. Please try again later.");
        }    
    }

   
                        
    return (
        <>
            <main className="page">
                <div className="d-flex flex align-items-center justify-content-between">
                    <div className="brand">
                        <Link to="/" className="brand-mark-link"><img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" /></Link>
                        <Link to="/"><span className="brand-title">DSGram</span></Link>
                    </div>
                    {/* Header section: shows user profile if logged in, otherwise login button */}
                    {logged ? (
                        <div className="profile-image d-flex align-items-center gap-2">
                            <Link to="/profile">
                                <div className="avatar avatar--img">
                                    {user?.photo ? <img src={`/images/${user.photo.id}/media`} alt="Profile photo" /> : <span>{user?.name.charAt(0).toLocaleUpperCase() ?? ""}</span>}
                                </div>
                            </Link>
                        </div>
                    ) : (
                        <a className="btn ghost" href="/login">Log in</a>
                    )}
                </div>

                <Container>
                    <Row className="justify-content-center">
                        <Col xs={12} lg={10}>
                            <section className="content-section mb-4">
                                <div className="content-section__header">
                                    <div className="d-flex flex-column flex-md-row justify-content-between align-items-start align-items-md-center gap-3 mb-3">
                                        <div>
                                            <h2 className="content-section__title mb-2">Exercise {exercise.id}</h2>
                                            <p className="content-section__meta text-muted mb-0">From {list.title} · Created by {list.owner.name}</p>
                                        </div>
                                    </div>
                                </div>

                                <Row className="g-4 mt-2">
                                    <Col xs={12}>
                                        <div className="detail-block">
                                            <h4 className="detail-block__label">Name</h4>
                                            <p className="detail-block__value">{exercise.title}</p>
                                        </div>
                                    </Col>
                                    <Col xs={12}>
                                        <div className="detail-block">
                                            <h4 className="detail-block__label">Description</h4>
                                            <p className="detail-block__value">{exercise.description}</p>
                                        </div>
                                    </Col>
                                     
                                    {logged && exercise.hasPdf && (
                                        <Col xs={12}>
                                            <div className="detail-block">
                                                <h4 className="detail-block__label">PDF statement</h4>
                                                <a onClick={(e) => { e.preventDefault(); downloadPdf();}} className="detail-block__link">
                                                    <svg width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                                        <path d="M14 4.5V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h5.5L14 4.5zm-3 0A1.5 1.5 0 0 1 9.5 3V1H4a1 1 0 0 0-1 1v12a1 1 0 0 0 1 1h8a1 1 0 0 0 1-1V4.5h-2z" />
                                                    </svg>
                                                    document.pdf
                                                </a>
                                            </div>
                                        </Col>
                                    )}
                                    
                                </Row>
                            </section>

                            <section className="content-section">
                                <h3 className="content-section__subtitle mb-4">Solutions</h3>
                                <Row className="g-4 mb-4">
                                    {/* Deletable solutions: solutions that the user can delete */}
                                    {showDeletableSolutions && deletableSolutions.map((solution: SolutionBasicInfoDTO, index: number) => (
                                        <SolutionCard key={solution.id} solution={solution} index={index} deletable={true} />
                                    ))}

                                    {/* Read-only solutions: solutions that cannot be deleted */}
                                    {hasSolutions &&readonlySolutions.map((solution: SolutionBasicInfoDTO, index: number) => (
                                        <SolutionCard key={solution.id} solution={solution} index={index} deletable={false} />
                                    ))}

                                    {/* No solutions message if there are no solutions */}
                                    {!hasSolutions && (
                                        <Col xs={12} className="text-center">
                                            <p className="text-muted">No solutions for this exercise yet. Click the + button to add one!</p>
                                        </Col>
                                    )}
                                </Row>
                                {/* Add solution button: only if logged in */}
                                {logged ? (
                                    <Row className="g-3 justify-content-center">
                                        <Col xs={12} sm="auto">
                                            <Link className="btn plus-btn rounded-circle" to={`/exercises/${exercise.id}/solutions/new`}>
                                                <i className="bi bi-plus"></i>
                                            </Link>
                                        </Col>
                                    </Row>
                                ) : (
                                    <div className="text-center">
                                        <p className="text-muted mb-0">Log in to add, edit or delete content.</p>
                                    </div>
                                )}

                                <div className="text-center mt-5">
                                    <Link className="btn ghost" to={`/lists/${list.id}`}>Back to {list.title}</Link>
                                </div>
                            </section>
                        </Col>
                    </Row>
                </Container>
            </main>
        </>
    );
}
