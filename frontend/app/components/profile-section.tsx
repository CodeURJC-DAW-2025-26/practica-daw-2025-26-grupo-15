import { Button, Col, Form } from "react-bootstrap";
import { InlineActionError } from "./inline-action-error";
import type { UserDTO } from "~/dtos/UserDTO";
import { Link } from "react-router";
import { useState } from "react";

const API_IMAGES_URL = "/api/v1/images";

interface ProfileSectionProps {
    userProfile: UserDTO;
    isOwnProfile: boolean;
    actionStateAccept: [{errorAccept: string | null},(form: FormData) => void, boolean];
    actionStateDecline: [{errorDecline: string | null},(form: FormData) => void, boolean];
    lastRequestAction: "accept" | "decline" | null;
}


export default function ProfileSection({ userProfile, isOwnProfile, actionStateAccept:[{errorAccept},formAcceptRequestAction,isPendingAccept], actionStateDecline:[{errorDecline},formDeclineRequestAction,isPendingDecline], lastRequestAction }: ProfileSectionProps){
    const requestActionError =
    lastRequestAction === "accept"
      ? errorAccept
      : lastRequestAction === "decline"
        ? errorDecline
        : null;
    return (
    <>
    <Col as="aside" xs={12} lg={3} className="sidebar">
                <div className="profile-sidebar-header">
                  <div className="profile-avatar-preview">
                    {userProfile.photo.id && (
                      <img
                        src={`${API_IMAGES_URL}/${userProfile.photo.id}/media`}
                        alt="Profile photo"
                        className="avatar-image-cover"
                      ></img>
                    )}
                    {!userProfile.photo.id && (
                      <i className="bi bi-person-circle"></i>
                    )}
                  </div>
                </div>

                <div className="pill">{userProfile.name}</div>
                <div className="pill">{userProfile.bio ?? "No bio yet."}</div>
                <div className="pill">
                  {userProfile.name ?? "No specialty yet."}
                </div>

                {isOwnProfile && (
                  <>
                    <div className="sidebar-requests-section">
                      <div className="sidebar-requests-header">
                        <span className="sidebar-requests-title">
                          <i className="bi bi-person-plus-fill"></i> Follow
                          Requests
                        </span>
                        {userProfile.requestReceived.length > 0 && (
                          <span className="sidebar-requests-badge">
                            {userProfile.requestReceived.length}
                          </span>
                        )}
                      </div>
                      <div className="list">
                        {userProfile.requestReceived.length > 0 ? (
                          userProfile.requestReceived
                            .slice(0, 3)
                            .map((request) => (
                              <div className="list-item" key={request.id}>
                                <div className="req-identity">
                                  <div className="req-avatar">
                                    {request.photo && (
                                      <img
                                        src={`${API_IMAGES_URL}/${request.photo.id}/media`}
                                      ></img>
                                    )}
                                    {!request.photo && (
                                      <span>{request.name[0]}</span>
                                    )}
                                  </div>
                                  <span className="req-name">{request.name}</span>
                                </div>
                                <div className="actions">
                                  <Form action={formAcceptRequestAction}>
                                    <Form.Control
                                      type="hidden"
                                      name="targetId"
                                      value={request.id}>
                                    </Form.Control>
                                    <span
                                      className="tag tag-accept"
                                      title="Accept"
                                    >

                                      <Button
                                        type="submit"
                                        variant="link"
                                        className="p-0"
                                        disabled={isPendingAccept}
                                      >
                                        <i className="bi bi-check-lg"></i>
                                      </Button>
                                    </span>
                                  </Form>
                                  <Form action={formDeclineRequestAction}>
                                    <Form.Control
                                      type="hidden"
                                      name="targetId"
                                      value={request.id}
                                    />
                                    <span
                                      className="tag tag-decline"
                                      title="Decline"
                                    >
                                      <Button
                                        type="submit"
                                        variant="link"
                                        className="p-0"
                                        disabled={isPendingDecline}
                                      >
                                        <i className="bi bi-x-lg"></i>
                                      </Button>
                                    </span>
                                  </Form>
                                </div>
                              </div>
                            ))
                        ) : (
                          <p className="sidebar-requests-empty">
                            No pending requests.
                          </p>
                        )}
                      </div>
                      <div className="action-error-stack">
                        <InlineActionError message={requestActionError} />
                      </div>
                      <Link
                        to="/follow-requests"
                        className="btn secondary sidebar-requests-see-all"
                      >
                        <i className="bi bi-arrow-right-short"></i> See all
                        requests
                      </Link>
                    </div>
                  </>
                )}
              </Col>
    </> 
    );
}