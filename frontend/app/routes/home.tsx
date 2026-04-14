import { Footer } from "../components/footer";
import "../app.css"
import { Outlet } from "react-router";

export default function Home() {
  return (
    <>
      <Outlet />
      <Footer/>
    </> 
  );
}
