import { useActionState } from "react";
import type { Route } from "./+types/home";
import ExerciseForm from "~/components/exercise-form";
import { Link, useNavigate, useParams } from "react-router";
import { addExercise, uploadExercisePDF } from "~/services/exercise-service";
import { requireUser } from "~/services/route-guards-service";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  let user = await requireUser();
  if (!user.exerciseLists.some(l => Number(l.id) === Number(params.listId))) {
    throw new Error("This listID doesnt belong to your user");
  }
}
export default function NewExercise() {

  let { listId } = useParams();

  if (!listId) {
    throw new Error("Error adding exercise, must need listId in URL params");
  }

  const navigate = useNavigate();

  async function saveExerciseAction(
    prevState: {
      success: boolean;
      error: string | null;
    } | null,
    formData: FormData,
  ) {
    const exname = formData.get("exname") as string;
    const exdesc = formData.get("exdesc") as string;
    const pdfFile = formData.get("pdfFile") as File | null;

    try {
      const newExercise = await addExercise(exname, exdesc, listId!);

      if (pdfFile && pdfFile.size > 0 && pdfFile.name !== "") {
        await uploadExercisePDF(newExercise.id, pdfFile);
      }

      navigate(`/exercise/${newExercise.id}`);
      return { success: true, error: null };
    } catch (error) {
      console.error(error);
      return {
        success: false,
        error: "Failed to save exercise. Please try again.",
      };
    }
  }


  const [state, formAction, isPending] = useActionState(saveExerciseAction, null);

  return (
    <main className="page">
      <div className="brand">
        <Link to="/" className="brand-mark-link">
          <img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" />
        </Link>
        <Link to="/" className="text-decoration-none">
          <span className="brand-title fs-4 fw-bold text-dark">DSGram</span>
        </Link>
      </div>

      <ExerciseForm
        actionState={[state, formAction, isPending]}
        onCancel={() => navigate(`/lists/${listId}`)}
      />

    </main>

  )
}
