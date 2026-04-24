import { useActionState } from "react";
import { Link, useNavigate } from "react-router";
import SolutionForm from "~/components/solution-form";
import { addList } from "~/services/list-service";
import { useParams } from "react-router";
import { addSolution, uploadSolutionImage } from "~/services/solution-service";
import { useUserStore } from "~/stores/user-store";
import { requireUser } from "~/services/route-guards-service";


export async function clientLoader() {
    await requireUser();
}

export default function NewSolution() {

    const { exerciseId } = useParams();
    const { user } = useUserStore();
    const logged = user !== null;

    if (!exerciseId) {
        throw new Error("Error adding solution, must need exerciseId in URL params");
    }

    const navigate = useNavigate();

    async function saveSolutionAction(
        prevState: {
            success: boolean;
            error: string | null;
        } | null,
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

            <SolutionForm
                actionState={[state, formAction, isPending]}
                onCancel={() => navigate("/")}
            />
        </main>
        </>
    );
}