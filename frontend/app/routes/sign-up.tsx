import { useActionState, useEffect, useRef, useState, type ChangeEvent } from "react";
import { Button, Col, Container, Form, Row } from "react-bootstrap";
import { Link, useNavigate } from "react-router";
import { isUsernameAvailableBySearch, registerUser } from "~/services/user-service";
import { InlineActionError } from "~/components/inline-action-error";

type FeedbackTone = "neutral" | "ok" | "error";

type FeedbackState = {
  tone: FeedbackTone;
  message: string;
};

const NAME_REGEX = /^[a-zA-Z0-9_]{3,30}$/;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_REGEX =
  /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&#_\-])(?=\S{8,64}$).+$/;

export default function SignUp() {
  const navigate = useNavigate();
  const usernameCheckAbortRef = useRef<AbortController | null>(null);
  const [usernameInput, setUsernameInput] = useState("");
  const [isCheckingUsername, setIsCheckingUsername] = useState(false);

  const [nameFeedback, setNameFeedback] = useState<FeedbackState>({
    tone: "neutral",
    message: "",
  });
  
  const [emailFeedback, setEmailFeedback] = useState<FeedbackState>({
    tone: "neutral",
    message: "",
  });
  const [passwordFeedback, setPasswordFeedback] = useState<FeedbackState>({
    tone: "neutral",
    message: "",
  });

  useEffect(() => {
    if (!NAME_REGEX.test(usernameInput)) {
      usernameCheckAbortRef.current?.abort();
      setIsCheckingUsername(false);
      return;
    }

    const timeoutId = setTimeout(async () => {
      usernameCheckAbortRef.current?.abort();
      const controller = new AbortController();
      usernameCheckAbortRef.current = controller;
      setIsCheckingUsername(true);

      try {
        const isAvailable = await isUsernameAvailableBySearch(
          usernameInput,
          controller.signal,
        );

        if (isAvailable) {
          setNameFeedback({ tone: "ok", message: "Username available." });
        } else {
          setNameFeedback({ tone: "error", message: "Username is already taken." });
        }
      } catch {
        if (controller.signal.aborted) {
          return;
        }

        setNameFeedback({
          tone: "error",
          message: "Could not verify username now. Try again.",
        });
      } finally {
        if (!controller.signal.aborted) {
          setIsCheckingUsername(false);
        }
      }
    }, 400);

    return () => {
      clearTimeout(timeoutId);
    };
  }, [usernameInput]);

  useEffect(() => {
    return () => {
      usernameCheckAbortRef.current?.abort();
    };
  }, []);

  function onNameChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;
    setUsernameInput(value);

    if (value.length === 0) {
      setIsCheckingUsername(false);
      setNameFeedback({ tone: "neutral", message: "Type a username." });
      return;
    }

    if (value.length < 3) {
      setIsCheckingUsername(false);
      setNameFeedback({
        tone: "error",
        message: "Username must be at least 3 characters.",
      });
      return;
    }

    if (value.length > 30) {
      setIsCheckingUsername(false);
      setNameFeedback({
        tone: "error",
        message: "Username cannot be longer than 30 characters.",
      });
      return;
    }

    if (/[^a-zA-Z0-9_]/.test(value)) {
      setIsCheckingUsername(false);
      setNameFeedback({
        tone: "error",
        message: "Use only letters, numbers, and underscore (_).",
      });
      return;
    }

    if (NAME_REGEX.test(value)) {
      setIsCheckingUsername(true);
      setNameFeedback({ tone: "neutral", message: "Checking..." });
      return;
    }

    

    setIsCheckingUsername(false);
    setNameFeedback({
      tone: "error",
      message: "Please review the username format.",
    });
  }
  const [{ error: registerError }, formRegisterAction, isPending] =
    useActionState(registerUserAction, { error: null });

  async function registerUserAction(
    _prevState: { error: string | null },
    formData: FormData,
  ) {
    const name = formData.get("name") as string;
    const email = formData.get("email") as string;
    const password = formData.get("encodedPassword") as string;

    try {
      await registerUser(name, email, password);
      navigate("/login");
      return { error: null };
      
    } catch (err: any ) {
      
      setNameFeedback((prev) =>
        prev.tone === "ok" ? { tone: "neutral", message: "" } : prev,
      );
      setEmailFeedback((prev) =>
        prev.tone === "ok" ? { tone: "neutral", message: "" } : prev,
      );
      setPasswordFeedback((prev) =>
        prev.tone === "ok" ? { tone: "neutral", message: "" } : prev,
      );
      return { error: err.message };
    }
  }

  function onEmailChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;

    if (value.length === 0) {
      setEmailFeedback({ tone: "neutral", message: "Type your email." });
      return;
    }

    if (value.includes(" ")) {
      setEmailFeedback({
        tone: "error",
        message: "Email cannot contain spaces.",
      });
      return;
    }

    if (!value.includes("@")) {
      setEmailFeedback({
        tone: "error",
        message: "Email must include @.",
      });
      return;
    }

    if (EMAIL_REGEX.test(value)) {
      setEmailFeedback({ tone: "ok", message: "Valid email." });
      return;
    }

    setEmailFeedback({
      tone: "error",
      message: "Use format username@domain.com.",
    });
  }

  function onPasswordChange(event: ChangeEvent<HTMLInputElement>) {
    const value = event.target.value;

    if (value.length === 0) {
      setPasswordFeedback({ tone: "neutral", message: "Type a password." });
      return;
    }

    const missingRules: string[] = [];

    if (value.length < 8) {
      missingRules.push("at least 8 characters");
    }
    if (value.length > 64) {
      missingRules.push("maximum 64 characters");
    }
    if (!/[A-Z]/.test(value)) {
      missingRules.push("one uppercase letter");
    }
    if (!/[a-z]/.test(value)) {
      missingRules.push("one lowercase letter");
    }
    if (!/\d/.test(value)) {
      missingRules.push("one number");
    }
    if (!/[@$!%*?&#_\-]/.test(value)) {
      missingRules.push("one symbol (@$!%*?&#_-)");
    }
    if (/\s/.test(value)) {
      missingRules.push("no whitespace");
    }

    if (missingRules.length > 0) {
      setPasswordFeedback({
        tone: "error",
        message: `Missing: ${missingRules.join(", ")}.`,
      });
      return;
    }

    if (PASSWORD_REGEX.test(value)) {
      setPasswordFeedback({ tone: "ok", message: "Valid password." });
      return;
    }

    setPasswordFeedback({
      tone: "error",
      message: "Please review the password format.",
    });
  }

  const hasValidationError =
    nameFeedback.tone === "error" ||
    emailFeedback.tone === "error" ||
    passwordFeedback.tone === "error";

  const isFormValid =
    nameFeedback.tone === "ok" &&
    emailFeedback.tone === "ok" &&
    passwordFeedback.tone === "ok";

  const nameInputClass = isCheckingUsername
    ? "inputForm inputForm--checking"
    : `inputForm inputForm--${nameFeedback.tone}`;

  const nameFeedbackClass = isCheckingUsername
    ? "input-feedback input-feedback--checking"
    : `input-feedback input-feedback--${nameFeedback.tone}`;

  return (
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
            <h2>Join in to learn and share solutions with the community.</h2>
          </Col>
          <Col xs={10} lg={5}>
            <div className="card-container">
              <Form className="form" action={formRegisterAction}>
                <div className="flex-column">
                  <Form.Label htmlFor="name" className="form-label">
                    Username
                  </Form.Label>
                </div>
                <div className={nameInputClass}>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                  >
                    <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z" />
                  </svg>
                  <Form.Control
                    id="name"
                    name="name"
                    className="input"
                    type="text"
                    onChange={onNameChange}
                    placeholder="Enter your user name"
                    required
                    minLength={3}
                    maxLength={30}
                    pattern="^[a-zA-Z0-9_]{3,30}$"
                    title="3–30 characters, only letters, numbers and underscores"
                  />
                </div>
                <p className={nameFeedbackClass}>{nameFeedback.message}</p>
                <div className="flex-column">
                  <Form.Label htmlFor="email" className="form-label">
                    Email
                  </Form.Label>
                </div>
                <div className={`inputForm inputForm--${emailFeedback.tone}`}>
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="0 0 24 24"
                  >
                    <path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z" />
                  </svg>
                  <Form.Control
                    id="email"
                    name="email"
                    className="input"
                    type="email"
                    onChange={onEmailChange}
                    placeholder="Enter your email: user@email.com..."
                    required
                  />
                </div>
                <p
                  className={`input-feedback input-feedback--${emailFeedback.tone}`}
                >
                  {emailFeedback.message}
                </p>
                <div className="flex-column">
                  <Form.Label htmlFor="password" className="form-label">
                    Password
                  </Form.Label>
                </div>
                <div
                  className={`inputForm inputForm--${passwordFeedback.tone}`}
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    viewBox="-64 0 512 512"
                  >
                    <path d="m336 512h-288c-26.453125 0-48-21.523438-48-48v-224c0-26.476562 21.546875-48 48-48h288c26.453125 0 48 21.523438 48 48v224c0 26.476562-21.546875 48-48 48zm-288-288c-8.8125 0-16 7.167969-16 16v224c0 8.832031 7.1875 16 16 16h288c8.8125 0 16-7.167969 16-16v-224c0-8.832031-7.1875-16-16-16zm0 0"></path>
                    <path d="m304 224c-8.832031 0-16-7.167969-16-16v-80c0-52.929688-43.070312-96-96-96s-96 43.070312-96 96v80c0 8.832031-7.167969 16-16 16s-16-7.167969-16-16v-80c0-70.59375 57.40625-128 128-128s128 57.40625 128 128v80c0 8.832031-7.167969 16-16 16zm0 0"></path>
                  </svg>
                  <Form.Control
                    id="password"
                    name="encodedPassword"
                    className="input"
                    type="password"
                    onChange={onPasswordChange}
                    placeholder="********"
                    required
                    minLength={8}
                    maxLength={64}
                    pattern="^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*?&#_\-])(?=\S{8,64}$).+$"
                    title="8-64 characters, no spaces, at least one uppercase, one lowercase, one number and one special character (@$!%*?&#_-)"
                  />
                </div>
                <p
                  className={`input-feedback input-feedback--${passwordFeedback.tone}`}
                >
                  {passwordFeedback.message}
                </p>
                <Button
                  type="submit"
                  className="button-submit"
                  disabled={isPending || hasValidationError || !isFormValid}
                >
                  {isPending ? "Creating account..." : "Create account"}
                </Button>
                <InlineActionError message={registerError} />
                <p className="p">
                  Already have an account?
                  <span>
                    <Link to="/login">Log In</Link>
                  </span>
                </p>
              </Form>
            </div>
          </Col>
        </Row>
      </Container>
    </main>
  );
}
