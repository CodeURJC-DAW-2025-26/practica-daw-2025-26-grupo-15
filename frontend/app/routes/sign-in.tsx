import { useActionState, useState } from "react";
import { Col, Container, Form, Modal, Row } from "react-bootstrap";
import { Link, useNavigate } from "react-router";
import { useUserStore } from "~/stores/user-store";

export default function SigIn() {
  const [isErrorDialogOpen, setErrorDialog] = useState(false);

  const { loginUser, loginError } = useUserStore();
  
  const navigate = useNavigate();

  async function loginUserAction(_prevState: void | null, formData: FormData) {
    const email = formData.get("email") as string;
    const password = formData.get("password") as string;

    await loginUser(email, password);

    const error = useUserStore.getState().loginError;

    if (error) {
      handleShowErrorLoginDialog();
    }
    else{
      navigate("/")
    }
  }

  function handleShowErrorLoginDialog() {
    setErrorDialog(true);
  }

  function handleCloseErrorLoginDialog() {
    setErrorDialog(false);
  }

  const [_, formLoginAction, isPending] = useActionState(loginUserAction, null);

  return (
    <>
      <body>
        <main className="body-login">
          <Container>
            <Row className="align-items-center justify-content-around g-4 main-container">
              <Col
                xs={12}
                lg={5}
                className="text-container text-center text-lg-start"
              >
                <img
                  src="/assets/DSGram_LOGO_BW.png"
                  alt="DSGram logo"
                  className="brand-mark sign-in-special-logo"
                />
                <div className="display-flex">
                  <h1>DSGram</h1>
                </div>
                <h2>Solve exercises, share solutions.</h2>
              </Col>
              <Col xs={10} lg={5} className="col-10">
                <div className="card-container">
                  <Form className="form" action={formLoginAction}>
                    <div className="flex-column">
                      <Form.Label htmlFor="email" className="form-label">
                        Email
                      </Form.Label>
                    </div>
                    <div className="inputForm">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        height="20"
                        viewBox="0 0 24 24"
                      >
                        <path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4-8 5-8-5V6l8 5 8-5v2z" />
                      </svg>
                      <Form.Control
                        id="email"
                        name="email"
                        className="input"
                        type="email"
                        placeholder="Enter your email"
                        disabled={isPending}
                      />
                    </div>
                    <div className="flex-column">
                      <Form.Label className="form-label" />
                    </div>
                    <div className="inputForm">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="20"
                        viewBox="-64 0 512 512"
                        height="20"
                      >
                        <path d="m336 512h-288c-26.453125 0-48-21.523438-48-48v-224c0-26.476562 21.546875-48 48-48h288c26.453125 0 48 21.523438 48 48v224c0 26.476562-21.546875 48-48 48zm-288-288c-8.8125 0-16 7.167969-16 16v224c0 8.832031 7.1875 16 16 16h288c8.8125 0 16-7.167969 16-16v-224c0-8.832031-7.1875-16-16-16zm0 0"></path>
                        <path d="m304 224c-8.832031 0-16-7.167969-16-16v-80c0-52.929688-43.070312-96-96-96s-96 43.070312-96 96v80c0 8.832031-7.167969 16-16 16s-16-7.167969-16-16v-80c0-70.59375 57.40625-128 128-128s128 57.40625 128 128v80c0 8.832031-7.167969 16-16 16zm0 0"></path>
                      </svg>
                      <Form.Control
                        id="password"
                        name="password"
                        className="input"
                        type="password"
                        placeholder="Enter your password"
                        disabled={isPending}
                      />
                    </div>

                    <button type="submit" className="button-submit">
                      Log In
                    </button>

                    <div className="separator">
                      <hr />
                      <span>or continue</span>
                      <hr />
                    </div>

                    <p className="p mt-20">
                      No account yet?
                      <span>
                        <Link to="/register"> Create account</Link>
                      </span>
                    </p>
                    <p className="p">
                      Not feeling like signing up?
                      <span>
                        <Link to="/"> Access anonymously</Link>
                      </span>
                    </p>
                  </Form>
                </div>
              </Col>
            </Row>
          </Container>
          <Modal
            show={isErrorDialogOpen}
            onHide={handleCloseErrorLoginDialog}
            centered
            dialogClassName="login-error-modal-dialog"
            contentClassName="login-error-modal-content"
          >
            <Modal.Header className="login-error-modal-header" closeButton>
              <Modal.Title className="login-error-modal-title">
                <span className="login-error-badge">Login</span>
                Sign-in failed
              </Modal.Title>
            </Modal.Header>
            <Modal.Body className="login-error-modal-body">
              <p className="login-error-message">
                {loginError ||
                  "Your session could not be created. Please verify your credentials and try again."}
              </p>
              <p className="login-error-help">
                Check that your email is correct and Caps Lock is turned off.
              </p>
            </Modal.Body>
            <Modal.Footer className="login-error-modal-footer">
              <button
                type="button"
                className="login-error-close-btn"
                onClick={handleCloseErrorLoginDialog}
              >
                Try again
              </button>
            </Modal.Footer>
          </Modal>
        </main>
      </body>
    </>
  );
}
