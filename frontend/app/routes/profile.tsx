import { getUser } from "~/services/user-service";
import type { Route } from "./+types/profile";
import type { UserBasicInfoDTO } from "~/dtos/UserBasicInfoDTO";
import { useUserStore } from "~/stores/user-store";
import { reqIsLogged } from "~/services/login-service";


export async function clientLoader({ params }: Route.ClientLoaderArgs) {
  let user 
  if (params.id == "me"){
    user = await reqIsLogged();
  }
  else{
    user =  await getUser(Number(params.id!));
  }
  user.isAdmin = user.roles?.some( (role:string) => role === "ADMIN") ?? false;
  user.isOwnProfile = (params.id === "me" || Number(params.id) === user.id);
  
  return user;

}

export default function Profile({ loaderData }: Route.ComponentProps) {

  let { user } = useUserStore();

  {/* Logica necesaria a comntrolar:
    -Saber si es mi perfil
    -Saber si es admin
    -Saber si estoy logeado o no y si estoy viendo mi propio perfil o no */}



  const userLoaderData = loaderData;
  const isOwnProfile = userLoaderData.isOwnProfile;
  const firstTreeRequests = userLoaderData.requestReceived?.slice(0, 3) ?? [];
  const logged = user != null;
  const isFollowing = false;
  const hasRequested = false;
  const showFollowButton = true;
  

  {
    /*Parte ya importante */
  }

  return (
    <>
      <main className="page page--feed">
        <div className="brand brand--full">
          <div className="brand-left">
            <a href="/" className="brand-mark-link">
              <img
                src="/assets/DSGram_LOGO.png"
                alt="DSGram logo"
                className="brand-mark"
              />
            </a>
            <a href="/">
              <span className="brand-title">DSGram</span>
            </a>
          </div>

          
          <form action="/logout" method="post" className="brand-logout">
            <button type="submit" className="btn-logout">
              <i className="bi bi-box-arrow-right"></i> Log out
            </button>
          </form>
        </div>

        <section className="app-shell feed">
          <div className="container-fluid px-0">
            <div className="row g-0">
              <aside className="sidebar col-12 col-lg-3">
                <div className="profile-sidebar-header">
                  <div className="profile-avatar-preview">
                    {userLoaderData.photo ? (
                      <img
                        src={`/api/v1/images/${userLoaderData.photo.id}/media`}
                        alt="Profile photo"
                        className="avatar-image-cover"
                      />
                    ) : (
                      <i className="bi bi-person-circle"></i>
                    )}
                  </div>
                </div>

                <div className="pill">{userLoaderData.name}</div>
                <div className="pill">
                  {userLoaderData.bio ? userLoaderData.bio : "No bio yet."}
                </div>
                <div className="pill">
                  {userLoaderData.specialty ? userLoaderData.specialty : "No specialty yet."}
                </div>

                {isOwnProfile && (
                  <div className="sidebar-requests-section">
                    <div className="sidebar-requests-header">
                      <span className="sidebar-requests-title">
                        <i className="bi bi-person-plus-fill"></i> Follow
                        Requests
                      </span>
                      {userLoaderData.pendingFollowRequests && (
                        <span className="sidebar-requests-badge">
                          {userLoaderData.pendingFollowRequests}
                        </span>
                      )}
                    </div>

                    <div className="list">
                      {firstTreeRequests.length > 0 ? (
                        firstTreeRequests.map((req: UserBasicInfoDTO) => (
                          <div className="list-item" key={req.id}>
                            <div className="req-identity">
                              <div className="req-avatar">
                                {req.photo ? (
                                  <img
                                    src={`/api/v1/images/${req.photo.id}/media`}
                                    alt={req.name}
                                  />
                                ) : (
                                  <span>{req.name.charAt(0)}</span>
                                )}
                              </div>
                              <span className="req-name">{req.name}</span>
                            </div>
                            <div className="actions">
                              <form
                                action={`/acceptRequest/${req.id}`}
                                method="post"
                              >
                                <input
                                  type="hidden"
                                  name="srcPage"
                                  value="/profile"
                                />
                                <span className="tag tag-accept" title="Accept">
                                  <button
                                    type="submit"
                                    className="btn btn-link p-0"
                                  >
                                    <i className="bi bi-check-lg"></i>
                                  </button>
                                </span>
                              </form>
                              <form
                                action={`/declineRequest/${req.id}`}
                                method="post"
                              >
                                <input
                                  type="hidden"
                                  name="srcPage"
                                  value="/profile"
                                />
                                <span
                                  className="tag tag-decline"
                                  title="Decline"
                                >
                                  <button
                                    type="submit"
                                    className="btn btn-link p-0"
                                  >
                                    <i className="bi bi-x-lg"></i>
                                  </button>
                                </span>
                              </form>
                            </div>
                          </div>
                        ))
                      ) : (
                        <p className="sidebar-requests-empty">
                          No pending requests.
                        </p>
                      )}
                    </div>

                    <a
                      className="btn secondary sidebar-requests-see-all"
                      href="/follow-requests"
                    >
                      <i className="bi bi-arrow-right-short"></i> See all
                      requests
                    </a>
                  </div>
                )}
              </aside>

              <div className="content col-12 col-lg-9">
                <div className="topbar d-flex flex-column flex-md-row align-items-start align-items-md-center justify-content-between gap-3">
                  <div className="profile-title-block">
                    <h2 className="section-title">{userLoaderData.name}'s profile</h2>

                    <div className="profile-actions d-flex align-items-center flex-nowrap gap-3">
                      <div className="followers-cta">
                        <a
                          className="followers-cta-link"
                          href={`/followers-following/followers?userId=${userLoaderData.id}`}
                        >
                          <span className="followers-cta-value">
                            {userLoaderData.followers?.length}
                          </span>
                          <span className="followers-cta-label">Followers</span>
                        </a>

                        <div className="followers-cta-divider"></div>

                        <a
                          className="followers-cta-link"
                          href={`/followers-following/following?userId=${userLoaderData.id}`}
                        >
                          <span className="followers-cta-value">
                            {userLoaderData.following?.length}
                          </span>
                          <span className="followers-cta-label">Following</span>
                        </a>
                      </div>
                      {isOwnProfile && (
                        <a
                          className="btn plus-btn-labeled d-flex align-items-center gap-2"
                          href="/new-list"
                        >
                          <i className="bi bi-plus-lg"></i>
                          <span>Create list</span>
                        </a>
                      )}
                      {isOwnProfile && userLoaderData.isAdmin && (
                        <a className="btn admin-panel-btn" href="/admin">
                          <i className="bi bi-shield-lock"></i>
                          Admin panel
                        </a>
                      )}
                    </div>
                  </div>
                  {isOwnProfile ? (
                    <div className="dropdown">
                      <div className="avatar avatar--img">
                        {userLoaderData.photo ? (
                          <img
                            src={`/api/v1/images/${userLoaderData.photo.id}/media`}
                            alt="Profile photo"
                          />
                        ) : (
                          <i className="bi bi-person-circle"></i>
                        )}
                      </div>
                      <div className="dropdown-content">
                        <a href="/edit-profile">Edit profile</a>
                        <div className="divider"></div>
                        <a
                          href="#"
                          className="dropdown-action-danger"
                          data-bs-toggle="modal"
                          data-bs-target="#deleteProfileModal"
                        >
                          Delete profile
                        </a>
                      </div>
                    </div>
                  ) : (
                    logged && (
                      <>
                        {isFollowing ? (
                          <form method="post" action="/unfollow">
                            <input
                              type="hidden"
                              name="requesterId"
                              value={userLoaderData.id}
                            />
                            <input
                              type="hidden"
                              name="targetId"
                              value={userLoaderData .id}
                            />

                            <button
                              className="btn followers-action btn-danger-action"
                              type="submit"
                            >
                              Unfollow
                            </button>
                          </form>
                        ) : hasRequested ? (
                          <button
                            className="btn secondary"
                            type="button"
                            disabled
                          >
                            Requested
                          </button>
                        ) : showFollowButton ? (
                          <form method="post" action="/requestToFollow">
                            <input
                              type="hidden"
                              name="requesterId"
                              value={user.id}
                            />
                            <input
                              type="hidden"
                              name="targetId"
                              value={user.id}
                            />

                            <button className="btn secondary" type="submit">
                              Follow
                            </button>
                          </form>
                        ) : null}
                        {userLoaderData.isAdmin && (
                          <button
                            className="btn btn-delete-profile-admin"
                            type="button"
                            data-bs-toggle="modal"
                            data-bs-target="#deleteProfileModalAdmin"
                          >
                            Delete profile
                          </button>
                        )}
                      </>
                    )
                  )}
                </div>

                {/* User lists */}
                <div
                  id="feedStream"
                  className="feed-stream"
                  data-profile-id={userLoaderData.id}
                  data-petition="l"
                >
                  <div id="feedEmpty" className="feed-empty visually-hidden">
                    <svg
                      viewBox="0 0 24 24"
                      width="40"
                      height="40"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                      aria-hidden="true"
                    >
                      <circle
                        cx="12"
                        cy="12"
                        r="9"
                        stroke="currentColor"
                        strokeWidth="1.5"
                      />
                      <path
                        d="M8 12h8M12 8v8"
                        stroke="currentColor"
                        strokeWidth="1.5"
                        strokeLinecap="round"
                      />
                    </svg>
                    <p className="feed-empty__title">Nothing here yet</p>
                    <p className="feed-empty__sub">
                      {isOwnProfile
                        ? "You haven't created any lists yet."
                        : "This user hasn't created any lists yet."}
                    </p>
                  </div>
                  <div id="feedSentinel"></div>
                  <div
                    id="loadingSpinner"
                    className="row mx-0 justify-content-center visually-hidden mb-5"
                  >
                    <div className="spinner"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>

      {isOwnProfile && (
        <div
          className="modal fade"
          id="deleteProfileModal"
          tabIndex={-1}
          aria-labelledby="deleteProfileModalLabel"
          aria-hidden="true"
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content modal-content-themed">
              <div className="modal-header border-0">
                <h5 className="modal-title" id="deleteProfileModalLabel">
                  Delete profile
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  data-bs-dismiss="modal"
                  aria-label="Close"
                ></button>
              </div>
              <div className="modal-body">
                <p className="mb-0">
                  Are you sure you want to delete your profile?
                </p>
                <p className="text-muted mt-2 mb-0">
                  This action cannot be undone.
                </p>
              </div>
              <div className="modal-footer border-0">
                <button
                  type="button"
                  className="btn secondary"
                  data-bs-dismiss="modal"
                >
                  Cancel
                </button>
                <form
                  method="post"
                  action={`/delete-profile/${userLoaderData.id}`}
                  className="d-inline"
                >
                  <button type="submit" className="btn btn-danger-action">
                    Delete profile
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>
      )}

      {!isOwnProfile && userLoaderData.isAdmin && (
        <div
          className="modal fade"
          id="deleteProfileModalAdmin"
          tabIndex={-1}
          aria-labelledby="deleteProfileModalAdminLabel"
          aria-hidden="true"
        >
          <div className="modal-dialog modal-dialog-centered">
            <div className="modal-content modal-content-themed">
              <div className="modal-header border-0">
                <h5 className="modal-title" id="deleteProfileModalAdminLabel">
                  Delete profile
                </h5>
                <button
                  type="button"
                  className="btn-close btn-close-white"
                  data-bs-dismiss="modal"
                  aria-label="Close"
                ></button>
              </div>
              <div className="modal-body">
                <p className="mb-0">
                  Are you sure you want to delete{" "}
                  <strong>{userLoaderData.name}</strong>'s profile?
                </p>
                <p className="text-muted mt-2 mb-0">
                  This action cannot be undone.
                </p>
              </div>
              <div className="modal-footer border-0">
                <button
                  type="button"
                  className="btn secondary"
                  data-bs-dismiss="modal"
                >
                  Cancel
                </button>
                <form
                  method="post"
                  action={`/delete-profile/${userLoaderData.id}`}
                  className="d-inline"
                >
                  <button type="submit" className="btn btn-danger-action">
                    Delete profile
                  </button>
                </form>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
