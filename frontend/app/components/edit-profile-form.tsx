import { Alert, Button, Form } from "react-bootstrap";
import { Link } from "react-router";
import type { UserDTO } from "~/dtos/UserDTO";

interface EditProfileFormProps{
    user: UserDTO,
    actionState: [
        {success: boolean, error: string | null} | null,
        (formData: FormData) => void,
        boolean
    ]
}


export default function EditProfileForm({user, actionState: [state, formAction, isPending]}: EditProfileFormProps){
    const photoInputId = "edit-profile-photo";
    const nameInputId = "edit-profile-name";
    const bioInputId = "edit-profile-bio";
    const specialtyInputId = "edit-profile-specialty";

    return(
    <div className="container">
      <div className="row justify-content-center">
        <section className="hero-card col-12 col-lg-10">

          <div className="form-block mb-5">
            <h2>Edit profile</h2>
            <p className="muted mb-0">Update your account details and profile photo.</p>
          </div>
          <Form action={formAction} method="post" encType="multipart/form-data">

          {state?.error && <Alert variant="danger">{state.error}</Alert>}
          
          <div className="layout-edit-profile row g-4"> 
            
            <Form.Group className="photo-column col-12 col-md-4">
              <div className="profile-avatar-preview" title="Current profile photo">
                {user.photo ? (
                  <img src={`/api/v1/images/${user.photo.id}/media`} alt="Profile photo" className="avatar-image-cover" />
                ) : (
                  <span>{user.name.charAt(0).toUpperCase()}</span>
                )}
              </div>
              <Form.Label htmlFor={photoInputId} className="btn secondary mt-3 cursor-pointer">Upload photo</Form.Label>
              <Form.Control id={photoInputId} name="photoFile" type="file" accept="image/*" className="visually-hidden" disabled={isPending}/>
              <Form.Text className="text-danger mt-1" id="photoError" />
            </Form.Group>

            <div className="info-column col-12 col-md-8">
              <Form.Group className="field-group">
                <Form.Label htmlFor={nameInputId}>User</Form.Label>
                <Form.Control id={nameInputId} name="name" defaultValue={user.name} type="text" disabled={isPending}/>
                <Form.Text className="text-danger mt-1" id="nameError" />
              </Form.Group>

              <Form.Group className="field-group">
                <Form.Label htmlFor={bioInputId}>Bio</Form.Label>
                <Form.Control id={bioInputId} name="bio" as="textarea" disabled={isPending} defaultValue={user.bio} />
                <Form.Text className="text-danger mt-1" id="bioError" />
              </Form.Group>

              <Form.Group className="field-group">
                <Form.Label htmlFor={specialtyInputId}>Speciality</Form.Label>
                <Form.Control
                  id={specialtyInputId}
                  name="specialty"
                  defaultValue={user.specialty}
                  type="text"
                  disabled={isPending}
                />
                <Form.Text className="text-danger mt-1" id="specialtyError" />
              </Form.Group>
            </div>

          </div>

          <div className="splitprof d-flex gap-3 mt-5">
            <Link className="btn secondary" to={`/users/${user.id}`}>Cancel</Link>
            <Button
              type="submit"
              bsPrefix="btn"
              disabled={isPending}>
              {isPending ? "Saving..." : "Save profile"}
            </Button>
          </div>

        </Form>

        </section>
      </div>
    </div>
    )

}