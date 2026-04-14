import { Footer } from "../components/footer";
import "../app.css"
import { Outlet } from "react-router";
import { useEffect } from "react";
import { useUserStore } from "~/stores/user-store";


export default function Home() {
  let {loadLoggedUser}  = useUserStore();
  useEffect(() => { loadLoggedUser() }, [loadLoggedUser]);
  return (
    <>
      <Outlet />
      <Footer/>
    </> 
  );
}
