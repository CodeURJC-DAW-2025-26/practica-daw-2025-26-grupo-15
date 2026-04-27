import { Link, useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/edit-exercise";
import ExerciseForm from "~/components/exercise-form";
import { getExercise, updateExercise, uploadExercisePDF } from "~/services/exercise-service";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  const exercise = await getExercise(params.id!);
  return { exercise };
}

export default function EditExercise({ loaderData }: Route.ComponentProps) {
  const { exercise } = loaderData;
  const navigate = useNavigate();

  async function saveExerciseAction(
    prevState: { success: boolean; error: string | null } | null,
    formData: FormData,
  ) {
    const id = formData.get("id") as string;
    const name = formData.get("exname") as string;
    const description = formData.get("exdesc") as string;
    const pdfFile = formData.get("pdfFile") as File | null;

    try {
      await updateExercise(id, name, description);

      if (pdfFile && pdfFile.size > 0 && pdfFile.name !== "") {
        await uploadExercisePDF(Number(id), pdfFile);
      }

      navigate(`/exercise/${exercise.id}`);
      return { success: true, error: null };
    } catch (error) {
      console.error(error);
      return {
        success: false,
        error: "Failed to update exercise. Please try again.",
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
            <Link to="/"><span className="brand-title">DSGram</span></Link>
          </div>

    <ExerciseForm
      exercise={exercise}
      actionState={[state, formAction, isPending]}
      onCancel={() => navigate(`/lists/${exercise.exerciseList.id}`)}
    />
    </main>
  );
}
