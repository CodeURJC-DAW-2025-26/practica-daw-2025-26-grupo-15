import { useActionState } from "react";
import { useNavigate } from "react-router";
import ListForm from "~/components/list-form";


export function NewList() {
    const navigate = useNavigate();

    async function saveListAction(
        prevState: {
        success: boolean;
        error: string | null;
        } | null,
        formData: FormData,
    ) {
        const title = formData.get("title") as string;
        const topic = formData.get("topic") as string;
        const description = formData.get("description") as string;

        try {
        const newBook = await addList(title, topic,description);


        navigate(`/book/${newBook.id}`);
        return { success: true, error: null };
        } catch (error) {
            console.error(error);
            return {
                success: false,
                error: "Failed to save book. Please try again.",
            };
        }
    }

    
    const [state, formAction, isPending] = useActionState(saveListAction, null);

    return (
    <ListForm
        actionState={[state, formAction, isPending]}
        onCancel={() => navigate("/")}
    />
    );
}