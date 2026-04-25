import { useNavigate } from "react-router";
import { useActionState } from "react";
import type { Route } from "./+types/edit-list";
import ListForm from "~/components/list-form";
import {
  getExerciseListById,
  updateList,
} from "~/services/list-service";
import { requireUser } from "~/services/route-guards-service";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  let user = await requireUser();
  const list = await getExerciseListById(params.id!);
  if (!user.exerciseLists.some(l => l.id === list.id)){
    throw Error("You dont have access to this list ")
  }

  return { list };
}

export default function EditList({ loaderData }: Route.ComponentProps) {
  const { list } = loaderData;
  const navigate = useNavigate();

  async function saveListAction(
    prevState: { success: boolean; error: string | null } | null,
    formData: FormData,
  ) {
    const id = formData.get("id") as string;
    const title = formData.get("title") as string;
    const topic = formData.get("topic") as string;
    const description = formData.get("description") as string;

    try {
      await updateList(id, title, topic, description);

      navigate(`/lists/${list.id}`);
      return { success: true, error: null };
    } catch (error) {
      console.error(error);
      return {
        success: false,
        error: "Failed to update list. Please try again.",
      };
    }
  }

  const [state, formAction, isPending] = useActionState(saveListAction, null);

  return (
    <ListForm
      list={list}
      actionState={[state, formAction, isPending]}
      onCancel={() => navigate(-1)}
    />
  );
}