import { Footer } from "../components/footer";
import type { Route } from "./+types/home";
import "../app.css";
import Fyp from "./fyp";

export default function Home() {
  return (
    <>
      <Fyp/>
      <Footer/>
    </> 
   
  );
}
