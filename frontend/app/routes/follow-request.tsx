import { useActionState } from "react";
import { Link, useNavigate } from "react-router";
import { Button, Col, Container, Form } from "react-bootstrap";
import {
  acceptFollowRequest,
  declineFollowRequest,
  getFollowRequests,
} from "~/services/user-service";
import { InlineActionError } from "~/components/inline-action-error";
import type { Route } from "./+types/follow-request";
import { requireUser } from "~/services/route-guards-service";
import { useUserStore } from "~/stores/user-store";

export async function clientLoader() {
  const currentUser = await requireUser();
  const followRequests = await getFollowRequests();

  return { currentUser, followRequests };
}

export default function FollowRequest({ loaderData }: Route.ComponentProps) {
  const logoutUser = useUserStore((state) => state.logoutUser);
  const { currentUser, followRequests } = loaderData;
  const navigate = useNavigate();
  const pendingCount = followRequests.length;
  const followersNumber = currentUser.followers.length;
  const followingNumber = currentUser.following.length;

  const [{ errorAccept }, onAcceptFormAction, isAccepting] = useActionState(
    acceptRequest,
    { errorAccept: null },
  );
  const [{ errorDecline }, onDeclineFormAction, isDeclining] = useActionState(
    declineRequest,
    { errorDecline: null },
  );
  const [, formLogoutAction, isPending] = useActionState(
    logoutUserAction,
    null,
  );
  async function logoutUserAction(){
    await logoutUser();
    navigate("/", { replace: true });
  }

  async function acceptRequest(
    _prevState: { errorAccept: string | null },
    formData: FormData,
  ) {
    const targetId = formData.get("targetId") as string;

    try {
      await acceptFollowRequest(targetId);
      navigate(".", { replace: true });
      return { errorAccept: null };
    } catch (_error) {
      return { errorAccept: "Failed to accept the follow request" };
    }
  }

  async function declineRequest(
    _prevState: { errorDecline: string | null },
    formData: FormData,
  ) {
    const targetId = formData.get("targetId") as string;

    try {
      await declineFollowRequest(targetId);
      navigate(".", { replace: true });
      return { errorDecline: null };
    } catch (_error) {
      return { errorDecline: "Failed to decline the follow request" };
    }
  }

  return (
    <div className="fr-page">
      <main className="page page--feed flex-grow-1">
        <div className="brand brand--full mb-4">
          <Link to="/" className="brand-mark-link">
            <img
              src="/assets/DSGram_LOGO.png"
              alt="DSGram logo"
              className="brand-mark"
            />
          </Link>
          <Link to="/" className="text-decoration-none">
            <span className="brand-title">DSGram</span>
          </Link>

          <Form action={formLogoutAction} className="brand-logout">
            <Button type="submit" className="btn-logout">
              <i className="bi bi-box-arrow-right"></i> Log out
            </Button>
          </Form>
        </div>

        <Container fluid className="px-0 fr-content-container">
          <Col className="d-flex flex-column gap-4">
            <div className="fr-identity">
              <div className="fr-identity-avatar">
                {currentUser.photo.id ? (
                  <img
                    src={`/api/v1/images/${currentUser.photo.id}/media`}
                    alt="Profile photo"
                  />
                ) : (
                  <i className="bi bi-person-circle"></i>
                )}
              </div>
              <div className="fr-identity-info">
                <p className="fr-username">{currentUser.name}</p>
                <p className="fr-handle">Your follow requests</p>
              </div>
              <div className="fr-stats">
                <span className="fr-stat-chip">
                  <i className="bi bi-hourglass-split"></i>{" "}
                  <span className="fr-stat-num">{pendingCount}</span> Pending
                </span>
                <span className="fr-stat-chip">
                  <i className="bi bi-people-fill"></i>{" "}
                  <span className="fr-stat-num">{followersNumber}</span>{" "}
                  Followers
                </span>
                <span className="fr-stat-chip">
                  <i className="bi bi-person-check-fill"></i>{" "}
                  <span className="fr-stat-num">{followingNumber}</span>{" "}
                  Following
                </span>
              </div>
            </div>

            <div className="fr-section-header">
              <h2 className="fr-section-title">
                <i className="bi bi-bell-fill"></i> Pending requests
              </h2>
              <Link className="fr-back-btn" to={`/users/${currentUser.id}`}>
                <i className="bi bi-arrow-left-short"></i> Back to profile
              </Link>
            </div>

            <div className="fr-list">
              {followRequests.length > 0 ? (
                followRequests.map((req) => (
                  <article key={req.id} className="fr-card">
                    <div className="fr-card-avatar">
                      {req.photo ? (
                        <img
                          src={`/api/v1/images/${req.photo.id}/media`}
                          alt="Profile photo"
                        />
                      ) : (
                        <i className="bi bi-person-fill"></i>
                      )}
                    </div>
                    <div className="fr-card-body">
                      <p className="fr-card-name">{req.name}</p>
                      <p className="fr-card-meta">
                        <i className="bi bi-clock"></i> Wants to follow you
                      </p>
                    </div>
                    <div className="fr-card-actions">
                      <Form action={onAcceptFormAction} className="d-inline">
                        <Form.Control
                          name="targetId"
                          type="hidden"
                          value={req.id}
                        />
                        <Button
                          disabled={isAccepting}
                          type="submit"
                          className="fr-btn-accept"
                        >
                          <i className="bi bi-check-lg"></i> Accept
                        </Button>
                      </Form>
                      <Form action={onDeclineFormAction} className="d-inline">
                        <Form.Control
                          name="targetId"
                          type="hidden"
                          value={req.id}
                        />
                        <Button
                          disabled={isDeclining}
                          type="submit"
                          className="fr-btn-decline"
                        >
                          <i className="bi bi-x-lg"></i> Decline
                        </Button>
                      </Form>
                    </div>
                  </article>
                ))
              ) : (
                <div className="fr-empty">
                  <i className="bi bi-inbox fr-empty-icon"></i>
                  <p className="fr-empty-title">All caught up!</p>
                  <p className="fr-empty-sub">
                    You have no pending follow requests right now.
                  </p>
                </div>
              )}
            </div>

            <InlineActionError message={errorAccept ?? errorDecline} />
          </Col>
        </Container>
      </main>
    </div>
  );
}
