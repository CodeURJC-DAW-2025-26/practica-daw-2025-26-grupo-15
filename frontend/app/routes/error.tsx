import { useEffect } from "react";
import { Link } from "react-router";
// import "../assets/sign-in.css"; 

export default function Error() {
  const errorMessage = "This is a custom error message for demonstration purposes.";

  return (
    <main className="container">
      <div className="row align-items-center justify-content-around g-4 main-container">
        <div className="col-12 col-lg-5 text-container text-center text-lg-start">
          <h1>Error</h1>
          <h2>Wow, this shouldn't be happening...</h2>
          <p>
            {errorMessage ? errorMessage : "An unexpected error occurred."}
          </p>
          <h6>
            <Link to="/">Back</Link>
          </h6>
        </div>
      </div>
    </main>
  );
}