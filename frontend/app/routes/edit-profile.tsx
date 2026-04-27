import { useActionState } from "react";
import { Link, useNavigate, useParams } from "react-router";
import EditProfileForm from "~/components/edit-profile-form";
import { requireUser } from "~/services/route-guards-service";
import { updateProfile, updateProfilePhoto } from "~/services/user-service";
import { useUserStore } from "~/stores/user-store";
import type { Route } from "./+types/edit-profile";

export async function clientLoader({params}:Route.ClientLoaderArgs){
  let user = await requireUser();
  if(user.id !== Number(params.userId)){
    throw Error("You dont have access to edit this profile");
  }
}

export default function EditProfile() {
  const user = useUserStore((state) => state.user);
    const { userId } = useParams();
    const navigate = useNavigate();

    async function editProfileAction(
        prevState: {
            success: boolean;
            error: string | null;
        } | null,
        formData: FormData
    ) {
        const photoFile = formData.get("photoFile") as File | null;
        const name = formData.get("name") as string;
        const bio = formData.get("bio") as string;
        const specialty = formData.get("specialty") as string;

        try {
          await updateProfile({ name, bio, specialty }, user!.id);

          if (photoFile && photoFile.size > 0 && photoFile.name !== "") {
            await updateProfilePhoto(photoFile, user!.id);
          }

          navigate(`/users/${user!.id}`);
          return { success: true, error: null };

        } catch (error) {
          console.error(error);
          return {
            success: false,
            error: "Failed to update profile. Please try again.",
          };

        }
        

    }

    const [state, formAction, isPending] = useActionState(editProfileAction, null);

    return (
        <main className="page">
          <div className="brand">
            <Link to="/" className="brand-mark-link"><img src="/assets/DSGram_LOGO.png" alt="DSGram logo" className="brand-mark" /></Link>
            <Link to="/"><span className="brand-title">DSGram</span></Link>
          </div>

        {user && user.id === Number(userId) && (
          <EditProfileForm
            user={user}
            actionState={[state, formAction, isPending]}
            />
        )}
        </main>
    );
}