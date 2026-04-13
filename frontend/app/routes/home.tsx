import { Footer } from "../components/footer";
import type { Route } from "./+types/home";
import "../app.css";
import { Profile } from "./profile";
import Fyp from "./fyp";

export default function Home() {
  return (
    <>
    <Profile>
    </Profile>
      <Fyp/>
      <Footer/>
    </> 
  );
}
