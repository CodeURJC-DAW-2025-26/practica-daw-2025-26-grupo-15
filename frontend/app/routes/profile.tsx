import { acceptFollowRequest, declineFollowRequest, deleteProfile, getUser, sendFollowRequest, unFollowUser } from "~/services/user-service";
import type { Route } from "./+types/profile";
import { useUserStore } from "~/stores/user-store";
import { reqIsLogged } from "~/services/login-service";
import { Link, useNavigate } from "react-router";
import type { UserDTO } from "~/dtos/UserDTO";
import { Button, Col, Container, Form, Modal, Row } from "react-bootstrap";
import { useActionState, useCallback, useState } from "react";
import { InlineActionError } from "~/components/inline-action-error";
import FeedStream from "~/components/feed-stream";
import { getExerciseListsFromUser, getListsForUserProfile } from "~/services/list-service";
import { optionalUser } from "~/services/route-guards-service";
import ProfileSection from "~/components/profile-section";

const API_IMAGES_URL = "/api/v1/images";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {

  let userLogged = await optionalUser();
  const userVisited: UserDTO = await getUser(Number(params.id));
  let isOwnProfile = userLogged && userVisited.id === userLogged.id;
  let isAdmin = userLogged && userLogged.roles.includes("ADMIN");

  return { userLogged, userVisited, isOwnProfile, isAdmin };
}

export default function Profile({ loaderData }: Route.ComponentProps) {
  
  const navigate = useNavigate();

  let { logoutUser } = useUserStore();


  const isUserLogged = loaderData.userLogged != null;
  const userLogged = loaderData.userLogged;
  const isOwnProfile = loaderData.isOwnProfile;
  const userProfile = loaderData.userVisited;
  const isAdmin = loaderData.isAdmin;
  const isFollowing =
    isUserLogged &&
    userProfile.followers.some((user) => user.id === userLogged!.id);

  async function logoutUserAction() {
    await logoutUser();
    navigate("/", { replace: true });
  }

  const [, formLogoutAction, isPending] = useActionState(
    logoutUserAction,
    null,
  );

  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [isAdminDeleteModal, setIsAdminDeleteModal] = useState(false);
  const [lastRequestAction, setLastRequestAction] = useState<"accept" | "decline" | null>(null);

  function handleShowDeleteModal() {
    setIsAdminDeleteModal(false);
    setShowDeleteModal(true);
  }

  function handleCloseDeleteModal() {
    setShowDeleteModal(false);
  }

  function handleShowAdminDeleteModal() {
    setIsAdminDeleteModal(true);
    setShowDeleteModal(true);
  }

  const [{ errorRequest }, formRequestFollowAction, isPendingRequest] = useActionState(sendActionFollowRequest, { errorRequest: null });

  async function sendActionFollowRequest(_prevState: { errorRequest: string | null }, formData: FormData) {
    const targetId = formData.get("targetId") as string;

    try {
      await sendFollowRequest(targetId);
      navigate(".", { replace: true })
      return { errorRequest: null };
    } catch (error) {
      return { errorRequest: "Failed to send follow request" }
    }
  }

  const [{ errorAccept }, formAcceptRequestAction, isPendingAccept] = useActionState(acceptFollowRequestAction, { errorAccept: null })

  async function acceptFollowRequestAction(_prevState: { errorAccept: string | null }, formData: FormData) {
    setLastRequestAction("accept");
    const targetId = formData.get("targetId") as string;
    try {
      await acceptFollowRequest(targetId)
      navigate(".", { replace: true })
      return { errorAccept: null };
    } catch (error) {
      return { errorAccept: "Failed to accept follow request" }
    }

  }

  const [{ errorDecline }, formDeclineRequestAction, isPendingDecline] = useActionState(declineRequestAction, { errorDecline: null })

  async function declineRequestAction(_prevState: { errorDecline: string | null }, formData: FormData) {
    setLastRequestAction("decline");
    const targetId = formData.get("targetId") as string;
    try {
      await declineFollowRequest(targetId);
      navigate(".", { replace: true });
      return { errorDecline: null };
    } catch (error) {
      return { errorDecline: "Failed to decline follow request" }
    }

  }

  const requestActionError =
    lastRequestAction === "accept"
      ? errorAccept
      : lastRequestAction === "decline"
        ? errorDecline
        : null;


  const [{ errorUnfollow }, formUnFollowAction, isPendingUnfollow] = useActionState(unFollowAction, { errorUnfollow: null });
  async function unFollowAction(_prevState: { errorUnfollow: string | null }, formData: FormData) {
    const targetId = formData.get("targetId") as string;
    try {
      await unFollowUser(targetId);
      navigate(".", { replace: true });
      return { errorUnfollow: null };
    } catch (error) {
      return { errorUnfollow: "Failed to unfollow user" }
    }

  }

  const [{ errorDelete }, deleteUserProfileAction, isPendingDeleteUserProfile] =
    useActionState(deleteUserProfile, { errorDelete: null });

  async function deleteUserProfile(
    _prevState: { errorDelete: string | null },
    formData: FormData,
  ) {
    const targetId = formData.get("targetId") as string;

    try {
      await deleteProfile(targetId);
      if (isOwnProfile) {
        useUserStore.setState({ user: null, loginError: null });
      }
      navigate("/", { replace: true });
      return { errorDelete: null };
    } catch (_error) {
      return { errorDelete: "Failed to delete profile" };
    }
  }

  return (
    <>
      <main className="page page--feed">
        <div className="brand brand--full">
          <div className="brand-left">
            <Link to="/" className="brand-mark-link">
              <img
                src="/assets/DSGram_LOGO.png"
                alt="DSGram logo"
                className="brand-mark"
              />
            </Link>
            <Link to="/">
              <span className="brand-title">DSGram</span>
            </Link>
          </div>
          {isOwnProfile && (
            <>
              <Form action={formLogoutAction} className="brand-logout">
                <Button type="submit" className="btn-logout" disabled={isPending}>
                  <i className="bi bi-box-arrow-right"></i> Log out
                </Button>
              </Form>
            </>
          )}
        </div>

        <section className="app-shell feed">
          <Container fluid className="px-0">
            <Row className="g-0">
              <ProfileSection
                userProfile={userProfile}
                isOwnProfile={isOwnProfile ?  true : false}
                actionStateAccept={[{ errorAccept }, formAcceptRequestAction, isPendingAccept]}
                actionStateDecline={[{ errorDecline }, formDeclineRequestAction, isPendingDecline]}
                lastRequestAction={lastRequestAction}
              />
              <Col xs={12} lg={9} className="content">
                <div className="topbar d-flex flex-column flex-md-row align-items-start align-items-md-center justify-content-between gap-3">
                  <div className="profile-title-block">
                    <h2 className="section-title">
                      {userProfile.name}'s profile
                    </h2>

                    <div className="profile-actions d-flex align-items-center flex-nowrap gap-3">
                      <div className="followers-cta">
                        <Link
                          className="followers-cta-link"
                          to={`/followers-following/followers?userId=${userProfile.id}`}
                        >
                          <span className="followers-cta-value">
                            {userProfile.followers.length}
                          </span>
                          <span className="followers-cta-label">
                            Followers
                          </span>
                        </Link>

                        <div className="followers-cta-divider"></div>

                        <Link
                          className="followers-cta-link"
                          to={`/followers-following/following?userId=${userProfile.id}`}
                        >
                          <span className="followers-cta-value">
                            {userProfile.following.length}
                          </span>
                          <span className="followers-cta-label">
                            Following
                          </span>
                        </Link>
                      </div>

                      {isOwnProfile && (
                        <Link
                          className="btn plus-btn-labeled d-flex align-items-center gap-2"
                          to="/lists/new"
                        >
                          <i className="bi bi-plus-lg"></i>
                          <span>Create list</span>
                        </Link>
                      )}
                      {isOwnProfile && isAdmin && (
                        <Link className="btn admin-panel-btn" to="/admin">
                          <i className="bi bi-shield-lock"></i> Admin panel
                        </Link>
                      )}
                    </div>
                  </div>
                  {isOwnProfile && (
                    <div className="dropdown">
                      <div className="avatar avatar--img">
                        {userProfile.photo && (
                          <img
                            src={`${API_IMAGES_URL}/${userProfile.photo.id}/media`}
                            alt="Profile photo"
                          />
                        )}
                        {!userProfile.photo && (
                          <i className="bi bi-person-circle"></i>
                        )}
                      </div>
                      <div className="dropdown-content">
                        <Link to={`/users/${userProfile.id}/edit`}>Edit profile</Link>
                        <div className="divider"></div>
                        <Button
                          className="dropdown-action-danger"
                          onClick={handleShowDeleteModal}

                        >
                          Delete profile
                        </Button>
                      </div>
                    </div>
                  )}
                  {!isOwnProfile && isUserLogged && isFollowing && (
                    <div className="profile-action-with-feedback">
                      <Form action={formUnFollowAction}>
                        <Form.Control
                          type="hidden"
                          name="requesterId"
                          disabled={isPendingUnfollow}
                          value={userLogged!.id}
                        />
                        <Form.Control
                          type="hidden"
                          name="targetId"
                          disabled={isPendingUnfollow}
                          value={userProfile!.id}
                        />
                        <Button
                          className="btn followers-action btn-danger-action"
                          type="submit"
                          disabled={isPendingUnfollow}
                        >
                          Unfollow
                        </Button>
                      </Form>
                      <InlineActionError message={errorUnfollow} />
                    </div>
                  )}
                  {!isOwnProfile &&
                    isUserLogged &&
                    !isFollowing &&
                    userLogged!.requestedFriends.some(
                      (p) => p.id === userProfile.id,
                    ) && (
                      <Button className="btn secondary" disabled>
                        Requested
                      </Button>
                    )}
                  {!isOwnProfile &&
                    isUserLogged &&
                    !isFollowing &&
                    !userLogged!.requestedFriends.some(
                      (p) => p.id === userProfile.id,
                    ) && (
                      <div className="profile-action-with-feedback">
                        <Form action={formRequestFollowAction}>
                          <Form.Control
                            type="hidden"
                            name="requesterId"
                            value={userLogged!.id}
                            disabled={isPendingRequest}
                          />
                          <Form.Control
                            type="hidden"
                            name="targetId"
                            value={userProfile!.id}
                            disabled={isPendingRequest}
                          />
                          <Button className="btn secondary" type="submit">
                            Follow
                          </Button>
                        </Form>
                        <InlineActionError message={errorRequest} />
                      </div>
                    )}
                  {isAdmin && !isOwnProfile && (
                    <Button
                      className="btn btn-delete-profile-admin"
                      onClick={handleShowAdminDeleteModal}
                    >
                      Delete profile
                    </Button>
                  )}
                </div>
                <FeedStream itemsType="list" itemsSearch={useCallback(
                  (page: number) => getListsForUserProfile(page, userProfile),
                  [userProfile.id]
                )} />

              </Col>
            </Row>
          </Container>
        </section>

        <Modal
          show={showDeleteModal}
          onHide={() => {
            if (!isPendingDeleteUserProfile) {
              handleCloseDeleteModal();
            }
          }}
          centered
          backdrop="static"
          contentClassName="adm-modal modal-content-themed"
        >
          <Modal.Header
            closeButton={!isPendingDeleteUserProfile}
            className="border-0 pb-0"
          >
            <Modal.Title className="adm-modal-title">
              <i
                className={`bi ${isAdminDeleteModal ? "bi-shield-exclamation" : "bi-exclamation-triangle-fill"} text-danger me-2`}
              ></i>
              {isAdminDeleteModal
                ? `Delete ${userProfile.name}'s profile?`
                : "Delete your profile?"}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p className="adm-modal-body">
              {isAdminDeleteModal
                ? "You are deleting this account as administrator. This action is permanent and cannot be undone."
                : "This action permanently removes your profile, your lists, and your exercises. You will be logged out immediately."}
            </p>
            <InlineActionError message={errorDelete} />
          </Modal.Body>
          <Modal.Footer className="border-0 pt-0">
            <Button
              className="adm-modal-cancel"
              onClick={handleCloseDeleteModal}
              disabled={isPendingDeleteUserProfile}
            >
              Cancel
            </Button>
            <Form action={deleteUserProfileAction}>
              <Form.Control type="hidden" name="targetId" value={userProfile.id} />
              <Button
                type="submit"
                className="adm-modal-confirm"
                disabled={isPendingDeleteUserProfile}
              >
                {isPendingDeleteUserProfile ? "Deleting profile..." : "Delete profile"}
              </Button>
            </Form>

          </Modal.Footer>
        </Modal>
      </main>
    </>
  );
}
