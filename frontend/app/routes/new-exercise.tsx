import { useActionState } from "react";
import type { Route } from "./+types/home";
import ExerciseForm from "~/components/exercise-form";
import { useNavigate, useParams } from "react-router";
import { addExercise, uploadExercisePDF } from "~/services/exercise-service";

export default function NewExercise(){

    let {listId} = useParams();

    if (!listId){
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

      if (pdfFile) {
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

    return(
        <main className="page">
          <div className="brand">
            <a href="/" className="brand-mark-link"><img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" /></a>
            <a href="/"><span className="brand-title">DSGram</span></a>
          </div>

          <ExerciseForm 
            actionState={[state, formAction, isPending]}
            listId={Number(listId)}
          />

        </main>
  
    )
}